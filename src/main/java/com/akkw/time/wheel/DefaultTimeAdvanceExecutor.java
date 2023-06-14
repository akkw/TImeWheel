package com.akkw.time.wheel;

public class DefaultTimeAdvanceExecutor extends AbstractTimeAdvanceExecutor {

    @Override
    long callable() {
        return 200;
    }
}
