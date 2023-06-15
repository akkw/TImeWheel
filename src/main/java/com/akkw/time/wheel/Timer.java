package com.akkw.time.wheel;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

public class Timer {

    private final ThreadPoolExecutor taskExecutor;

    private final DelayQueue<TimerTaskList> delayQueue = new DelayQueue<>();

    private int taskCount;

    private final TimingWheel timingWheel;

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private TimeAdvanceExecutor defaultTimeAdvanceClockExecutor;

    Consumer<TimerTaskEntry> function = this::addTimerTaskEntry;

    public Timer(String executorName, long tickMs, int wheelSize, long startMs) {
        this.taskExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, executorName);
            }
        });
        this.timingWheel = new TimingWheel(tickMs, wheelSize, startMs, delayQueue);
        this.defaultTimeAdvanceClockExecutor.advanceClock(timingWheel);
    }


    public void add(TimerTask timerTask) {
        addTimerTaskEntry(new TimerTaskEntry(timerTask.getDelayMs() + TimeUtils.hiResClockMs(), timerTask));
    }

    private void addTimerTaskEntry(TimerTaskEntry timerTaskEntry) {
        if (!timingWheel.add(timerTaskEntry) && !timerTaskEntry.isCancelled()) {
            taskExecutor.submit(timerTaskEntry.getTimerTask());
        }
    }


    public void advanceClock(long timeoutMs) throws Exception {
        TimerTaskList bucket = delayQueue.poll(timeoutMs, TimeUnit.MILLISECONDS);

        if (bucket != null) {

            while (bucket != null) {
                timingWheel.advanceClock(bucket.getExpiration());
                bucket.flush(function);
                bucket = delayQueue.poll();
            }

        }
    }

}
