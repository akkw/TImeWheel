package com.akkw.time.wheel;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TimerTaskEntry {
    private long expiration;

    private boolean cancelled;

    private TimerTask timerTask;

    private TimerTaskList timerTaskList;

    TimerTaskEntry next;

    TimerTaskEntry prev;

    private final ReadWriteLock readWriteLock;


    private static final VarHandle NEXT;
    private static final VarHandle PREV;


    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            NEXT = l.findVarHandle(TimerTaskEntry.class, "next", TimerTaskEntry.class);
            PREV = l.findVarHandle(TimerTaskEntry.class, "prev", TimerTaskEntry.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public TimerTaskEntry(long expiration, TimerTask timerTask) {
        this.expiration = expiration;
        this.timerTask = timerTask;
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public TimerTaskList getTimerTaskList() {
        return timerTaskList;
    }

    public void setTimerTaskList(TimerTaskList timerTaskList) {
        this.timerTaskList = timerTaskList;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }


    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }


    public void remove() {

    }


    void setPrevRelaxed(TimerTaskEntry entry) {
        PREV.set(this, entry);
    }

    boolean compareAndSetNext(TimerTaskEntry expect, TimerTaskEntry update) {
        return NEXT.compareAndSet(this, expect, update);
    }

    public boolean addWriteLock() {
        return this.readWriteLock.writeLock().tryLock();
    }

    public boolean addReadLock() {
        return this.readWriteLock.readLock().tryLock();
    }



    public void writeUnlock() {
        this.readWriteLock.writeLock().unlock();
    }

    public void readUnlock() {
        this.readWriteLock.readLock().unlock();
    }
}
