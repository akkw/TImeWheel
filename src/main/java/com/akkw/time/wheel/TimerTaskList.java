package com.akkw.time.wheel;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class TimerTaskList {

    private TimerTaskEntry head;

    private TimerTaskEntry tail;

    private volatile long expiration;

    private static final AtomicLongFieldUpdater<TimerTaskList> EXPIRATION;

    private static final VarHandle HEAD;
    private static final VarHandle TAIL;


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


    public boolean setExpiration(long expirationMs) {
        return EXPIRATION.getAndSet(this, expirationMs) != expirationMs;
    }

    public void add(TimerTaskEntry entry) {
        if (entry == null) {
            return;
        }
        for (; ; ) {
            TimerTaskEntry t = tail;
            if (t != null) {
                entry.setPrevRelaxed(t);
                if (compareAndSetTail(t, entry)) {
                    t.next = entry;
                    return;
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
    public void remove(TimerTaskEntry entry) {
        if (entry != null && entry.getTimerTaskList() == this) {
            boolean success = lockEntry(entry);



        }
    }

    private boolean lockEntry(TimerTaskEntry entry) {
        boolean success;
        boolean prevLockSuccess;
        boolean nextLockSuccess;
        try {
            do {
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

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            } while (!success);
        } catch (InterruptedException e) {
            success = false;
        }
        return success;
    }


    private void initializeSyncList() {
        TimerTaskEntry h;
        if (HEAD.compareAndSet(this, null, (h = new TimerTaskEntry(-1, null)))) tail = h;
    }


    private boolean compareAndSetTail(TimerTaskEntry expect, TimerTaskEntry update) {
        return TAIL.compareAndSet(this, expect, update);
    }
}
