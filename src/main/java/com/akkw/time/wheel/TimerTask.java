package com.akkw.time.wheel;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public abstract class TimerTask<T>  implements Callable<T> {
    private long delayMs;

    private TimerTaskEntry timerTaskEntry;



    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }

    public long getDelayMs() {
        return delayMs;
    }

    public synchronized void setTimerTaskEntry(TimerTaskEntry timerTaskEntry) {
        if (this.timerTaskEntry != null && timerTaskEntry != this.timerTaskEntry) {
            timerTaskEntry.remove();
        }
        this.timerTaskEntry = timerTaskEntry;
    }
}
