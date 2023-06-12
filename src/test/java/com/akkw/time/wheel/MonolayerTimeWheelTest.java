package com.akkw.time.wheel;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.ReentrantLock;

public class MonolayerTimeWheelTest {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock reentrantLock = new ReentrantLock();

        reentrantLock.lock();


        new Thread(reentrantLock::lock).start();
        new Thread(reentrantLock::lock).start();



        new CountDownLatch(1).await();
    }


    @Test
    public void AtomicMarkableReferenceTest() {
        Object o = new Object();
        AtomicMarkableReference<Object> atomicMarkableReference = new AtomicMarkableReference<>(o, true);
        new
        atomicMarkableReference.compareAndSet(o, )
    }
}