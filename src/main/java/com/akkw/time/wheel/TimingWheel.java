package com.akkw.time.wheel;

import java.util.concurrent.DelayQueue;

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


    private DelayQueue<TimerTaskList> delayQueue;

    public TimingWheel(long tickMs, int wheelSize, long startMs, DelayQueue<TimerTaskList> delayQueue) {
        this.tickMs = tickMs;
        this.wheelSize = wheelSize;
        this.buckets = new TimerTaskList[wheelSize];
        this.currentTime = startMS - (startMs % tickMs);
        this.interval = tickMs * wheelSize;
        this.delayQueue = delayQueue;
    }


    private synchronized void createOverflowWheel() {
        if (this.overflowWheel != null) {
            return;
        }
        this.overflowWheel = new TimingWheel(interval, wheelSize, currentTime, delayQueue);
    }


    public boolean add(TimerTaskEntry timerTaskEntry) {
        long expiration = timerTaskEntry.getExpiration();
        if (timerTaskEntry.isCancelled()) {
            return false;
        } else if (expiration < currentTime + tickMs) {
            // 添加时在当前格子已经过期
            // 返回 false 立即执行
            return false;
        } else if (expiration < currentTime + interval) {
            int virtualId = (int) (expiration / tickMs);
            TimerTaskList bucket = buckets[virtualId];

            bucket.add(timerTaskEntry);

            if (bucket.setExpiration(virtualId * tickMs)) {
                delayQueue.offer(bucket);
            }
            return true;
        } else {
            createOverflowWheel();

            return overflowWheel.add(timerTaskEntry);
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
