package com.akkw.time.wheel;

public class TimerTask implements Runnable {
    private long delayMs;


    private TimerTaskEntry timerTaskEntry;


    @Override
    public void run() {

    }


    public void setTimerTaskEntry(TimerTaskEntry timerTaskEntry) {
        this.timerTaskEntry = timerTaskEntry;
    }
}
