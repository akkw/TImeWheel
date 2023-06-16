package com.akkw.time.wheel;

import java.util.concurrent.*;

public class TimerFuture<T> extends CompletableFuture<T> implements Runnable {


    private final Callable<T> callable;

    public TimerFuture(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    public void run() {
        try {
            complete(callable.call());
        } catch (Exception e) {
            completeExceptionally(e);

        }

    }
}
