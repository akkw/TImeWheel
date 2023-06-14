package com.akkw.time.wheel;

import java.util.concurrent.Callable;

public abstract class AbstractTimeAdvanceExecutor implements TimeAdvanceExecutor {
    private TimingWheel timingWheel;

    public Thread worker;

    public void advanceClock(TimingWheel timingWheel) {
        this.timingWheel = timingWheel;
    }

    abstract long callable();


    public void start() {
        this.worker = new Thread(()-> {
            try {
                while (!Thread.currentThread().isInterrupted()) {

                    timingWheel.advanceClock(callable());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void shutdown() {
        if (worker == null) {
            return ;
        }
        worker.interrupt();
    }
}
