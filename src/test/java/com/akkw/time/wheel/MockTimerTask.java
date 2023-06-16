package com.akkw.time.wheel;

public class MockTimerTask extends TimerTask<String> {


    @Override
    public String call() throws Exception {
        return "mock";
    }
}
