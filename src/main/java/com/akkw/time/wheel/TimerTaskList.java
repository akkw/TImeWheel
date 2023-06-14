package com.akkw.time.wheel;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.ReentrantLock;

public class TimerTaskList {

    private TimerTaskEntry head;

    private TimerTaskEntry tail;

    private volatile long expiration;

    private static final AtomicLongFieldUpdater<TimerTaskList> EXPIRATION;

    private static final VarHandle HEAD;
    private static final VarHandle TAIL;

    private final ReentrantLock mainLock = new ReentrantLock();

    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            HEAD = l.findVarHandle(TimerTaskList.class, "head", TimerTaskEntry.class);
            TAIL = l.findVarHandle(TimerTaskList.class, "tail", TimerTaskEntry.class);
            EXPIRATION = AtomicLongFieldUpdater.newUpdater(TimerTaskList.class, "expiration");

        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    TimerTaskEntry getHead() {
        return head;
    }

    TimerTaskEntry getTail() {
        return tail;
    }


    public long getExpiration() {
        return expiration;
    }

    public boolean setExpiration(long expirationMs) {
        return EXPIRATION.getAndSet(this, expirationMs) != expirationMs;
    }

    public boolean add(TimerTaskEntry entry) {
        if (entry == null) {
            return false;
        }


        for (; ; ) {
            TimerTaskEntry t = tail;
            if (t != null) {
                boolean isLocked = t.addWriteLock();
                if (!isLocked) {
                    continue;
                }
                try {
                    entry.setPrevRelaxed(t);
                    if (compareAndSetTail(t, entry)) {
                        t.next = entry;
                        return true;
                    }
                } finally {
                    t.writeUnlock();
                }
            } else {
                initializeSyncList();
            }
        }
    }

    /**
     * ____________         __________       __________       ___________         __________        _________
     * |     X      | ----->|          |---->|   X      |----->|          |------>|    X     |----->|         |
     * |____________| <-----|__________|<----|__________|<-----|__________|<------|__________|<-----|_________|
     *
     * @param entry
     */
    public void remove(TimerTaskEntry entry) throws Exception {
        if (entry != null && entry.getTimerTaskList() == this) {

            if (entry == tail) {
                lockTailEntry(entry);
                if (entry != tail) {
                    entry.next.addReadLock();
                }
            } else {
                lockEntry(entry);
            }

            TimerTaskEntry prev = entry.prev;
            TimerTaskEntry next = entry.next;
            try {
                if (entry == tail) {
                    if (compareAndSetTail(entry, prev)) {
                        prev.next = null;
                    }
                } else {
                    next.prev = prev;
                    prev.next = next;
                }
            } finally {
                if (next != null) {
                    next.readUnlock();
                }
                prev.readUnlock();
            }
            entry.next = entry;
            entry.prev = entry;
        }
    }

    private void lockTailEntry(TimerTaskEntry entry) throws InterruptedException {

        boolean success;

        do {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            success = entry.addWriteLock();
            if (!success) {
                continue;
            }
            TimerTaskEntry prev = entry.prev;
            success = prev.addReadLock();
            if (!success) {
                entry.writeUnlock();
            }
        } while (!success);
    }


    private void lockEntry(TimerTaskEntry entry) throws InterruptedException {
        boolean success;
        boolean prevLockSuccess;
        boolean nextLockSuccess;
        do {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            success = entry.addWriteLock();
            if (!success) {
                continue;
            }
            TimerTaskEntry prev = entry.prev;
            TimerTaskEntry next = entry.next;

            prevLockSuccess = prev.addReadLock();
            nextLockSuccess = next.addReadLock();
            success = prevLockSuccess && nextLockSuccess;

            if (!success) {
                if (prevLockSuccess) {
                    prev.readUnlock();
                }

                if (nextLockSuccess) {
                    next.readUnlock();
                }

                entry.writeUnlock();
            }
        } while (!success);
    }


    private void initializeSyncList() {
        TimerTaskEntry h;
        if (HEAD.compareAndSet(this, null, (h = new TimerTaskEntry(-1, null)))) tail = h;
    }


    private boolean compareAndSetTail(TimerTaskEntry expect, TimerTaskEntry update) {
        return TAIL.compareAndSet(this, expect, update);
    }
}
