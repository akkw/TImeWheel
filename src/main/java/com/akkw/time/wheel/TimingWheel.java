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
     * 时间范围左闭右开[n,m)
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
        if (this.overflowWheel != null) {
            return;
        }
        this.overflowWheel = new TimingWheel(
                interval,
                wheelSize,
                currentTime
        );
    }


    public void add(TimerTaskEntry timerTaskEntry) {
        long expiration = timerTaskEntry.getExpiration();
        if (timerTaskEntry.isCancelled()) {

        } else if (expiration < currentTime + tickMs) {

        } else if (expiration < currentTime + interval) {
            int virtualId = (int) (expiration / tickMs);
            TimerTaskList bucket = buckets[virtualId];

            bucket.add(timerTaskEntry);

            // TODO 加入延迟队列
        } else {
            createOverflowWheel();

            overflowWheel.add(timerTaskEntry);
        }

    }

    public void advanceClock(long timeMs) {
        if (timeMs >= currentTime + tickMs) {
            currentTime = timeMs - (timeMs % tickMs);

            if (overflowWheel != null) {
                overflowWheel.advanceClock(currentTime);
            }
        }
    }

}
