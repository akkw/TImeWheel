package com.akkw.time.wheel;

import java.util.concurrent.*;

public class ThreadPoolExecutorTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        Future<String> submit = threadPoolExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "s";
            }
        });

        System.out.println(submit.get());
    }
}
