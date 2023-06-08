package com.akkw.time.wheel;

public class TimingWheel {
    /**
     * 向前推进一格Buckets所需的时间
     */
    private long tickMs;


    private int wheelSize;


    private int startMS;

    /**
     * 本层时间轮上任务总数
     */
    private int taskCounter;


    private final long interval;

    /**
     * 时间格
     */
    private final TimerTaskList buckets[];



    private long currentTime;


    private TimingWheel overflowWheel;


    public TimingWheel(long tickMs, int wheelSize, long startMs) {
        this.tickMs = tickMs;
        this.wheelSize = wheelSize;
        this.buckets = new TimerTaskList[wheelSize];
        this.currentTime = startMS - (startMs % tickMs);
        this.interval = tickMs * wheelSize;
    }




    private synchronized void createOverflowWheel() {
        this.overflowWheel = new TimingWheel(
                interval,
                wheelSize,
                currentTime
        );
    }
}
