package com.akkw.time.wheel;

import java.util.concurrent.Callable;

public interface TimeAdvanceExecutor {
    void advanceClock(TimingWheel timingWheel);
}
