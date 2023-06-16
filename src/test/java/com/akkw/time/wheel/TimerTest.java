package com.akkw.time.wheel;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TimerTest {

    Timer timer = new Timer("ex", 1, 20, TimeUtils.hiResClockMs());
    @Test
    public void add() throws Exception {
        new Thread(()-> {
            while (true) {
                try {
                    timer.advanceClock(200);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        TimerTask timerTask = new TimerTask();

        timerTask.setDelayMs(10000);
        timer.add(timerTask);


        new CountDownLatch(1).await();
    }
}