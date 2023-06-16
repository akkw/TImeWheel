package com.akkw.time.wheel;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimerTaskListTest {


    private TimerTaskList timerTaskList = new TimerTaskList();


    @Test
    public void addList() {
        int size = 10;
        for (int i = 1; i <= size; i++) {
            timerTaskList.add(new TimerTaskEntry(i, new TimerFuture<>(new MockTimerTask())));
        }
        headForEach(timerTaskList, new ArrayList<>());
        tailForEach(timerTaskList, size, new ArrayList<>());
    }


    @Test
    public void removeList() throws Exception {
        TimerTaskEntry timerTaskEntry = new TimerTaskEntry(4,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry1 = new TimerTaskEntry(6,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry2 = new TimerTaskEntry(7,  new TimerFuture<>(new MockTimerTask()));
        timerTaskEntry.setTimerTaskList(timerTaskList);
        timerTaskEntry1.setTimerTaskList(timerTaskList);
        timerTaskEntry2.setTimerTaskList(timerTaskList);

        timerTaskList.add(new TimerTaskEntry(1,  new TimerFuture<>(new MockTimerTask())));
        timerTaskList.add(new TimerTaskEntry(2,  new TimerFuture<>(new MockTimerTask())));
        timerTaskList.add(new TimerTaskEntry(3,  new TimerFuture<>(new MockTimerTask())));
        timerTaskList.add(timerTaskEntry);
        timerTaskList.add(new TimerTaskEntry(5,  new TimerFuture<>(new MockTimerTask())));
        timerTaskList.add(timerTaskEntry1);
        timerTaskList.add(timerTaskEntry2);

        timerTaskList.remove(timerTaskEntry);
        timerTaskList.remove(timerTaskEntry1);
        timerTaskList.remove(timerTaskEntry2);
    }


    @Test
    public void currentAddAndRemoveTail() throws InterruptedException {
        TimerTaskEntry timerTaskEntry1 = new TimerTaskEntry(1,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry2 = new TimerTaskEntry(2,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry3 = new TimerTaskEntry(3,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry4 = new TimerTaskEntry(4,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry5 = new TimerTaskEntry(5,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry6 = new TimerTaskEntry(6,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry7 = new TimerTaskEntry(7,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry8 = new TimerTaskEntry(8,  new TimerFuture<>(new MockTimerTask()));
        timerTaskEntry1.setTimerTaskList(timerTaskList);
        timerTaskEntry2.setTimerTaskList(timerTaskList);
        timerTaskEntry3.setTimerTaskList(timerTaskList);
        timerTaskEntry4.setTimerTaskList(timerTaskList);
        timerTaskEntry5.setTimerTaskList(timerTaskList);
        timerTaskEntry6.setTimerTaskList(timerTaskList);
        timerTaskEntry7.setTimerTaskList(timerTaskList);
        timerTaskEntry8.setTimerTaskList(timerTaskList);
        AtomicBoolean add = new AtomicBoolean(false);
        AtomicBoolean add1 = new AtomicBoolean(false);

        Thread addThread = new Thread(() -> {
            timerTaskList.add(timerTaskEntry1);
            timerTaskList.add(timerTaskEntry2);
            timerTaskList.add(timerTaskEntry3);
            timerTaskList.add(timerTaskEntry4);
            add.set(true);
            timerTaskList.add(timerTaskEntry5);
            timerTaskList.add(timerTaskEntry6);
            add1.set(true);
            timerTaskList.add(timerTaskEntry7);
            timerTaskList.add(timerTaskEntry8);
        });
        Thread removeThread = new Thread(() -> {
            try {
                while (true) {
                    if (add.get()) {
                        timerTaskList.remove(timerTaskEntry4);
                        System.out.println("remove entry 4");
                        break;
                    }
                }
                while (true) {
                    if (add1.get()) {
                        timerTaskList.remove(timerTaskEntry6);
                        System.out.println("remove entry 6");
                        break;
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        addThread.start();
        removeThread.start();

        addThread.join();
        removeThread.join();
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(4);
        arrayList.add(6);
        headForEach(timerTaskList, arrayList);


        tailForEach(timerTaskList, 8, arrayList);
    }


    @Test
    public void currentAddAndRemove() throws InterruptedException {
        TimerTaskEntry timerTaskEntry1 = new TimerTaskEntry(1,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry2 = new TimerTaskEntry(2,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry3 = new TimerTaskEntry(3,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry4 = new TimerTaskEntry(4,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry5 = new TimerTaskEntry(5,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry6 = new TimerTaskEntry(6,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry7 = new TimerTaskEntry(7,  new TimerFuture<>(new MockTimerTask()));
        TimerTaskEntry timerTaskEntry8 = new TimerTaskEntry(8,  new TimerFuture<>(new MockTimerTask()));
        timerTaskEntry1.setTimerTaskList(timerTaskList);
        timerTaskEntry2.setTimerTaskList(timerTaskList);
        timerTaskEntry3.setTimerTaskList(timerTaskList);
        timerTaskEntry4.setTimerTaskList(timerTaskList);
        timerTaskEntry5.setTimerTaskList(timerTaskList);
        timerTaskEntry6.setTimerTaskList(timerTaskList);
        timerTaskEntry7.setTimerTaskList(timerTaskList);
        timerTaskEntry8.setTimerTaskList(timerTaskList);
        AtomicBoolean add = new AtomicBoolean(false);
        AtomicBoolean add1 = new AtomicBoolean(false);

        Thread addThread = new Thread(() -> {
            timerTaskList.add(timerTaskEntry1);
            timerTaskList.add(timerTaskEntry2);
            timerTaskList.add(timerTaskEntry3);
            timerTaskList.add(timerTaskEntry4);
            timerTaskList.add(timerTaskEntry5);
            timerTaskList.add(timerTaskEntry6);
            timerTaskList.add(timerTaskEntry7);
            timerTaskList.add(timerTaskEntry8);
        });
        Thread removeThread1 = new Thread(() -> {
            try {
                timerTaskList.remove(timerTaskEntry3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread removeThread2 = new Thread(() -> {
            try {
                timerTaskList.remove(timerTaskEntry4);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Thread removeThread3 = new Thread(() -> {
            try {
                timerTaskList.remove(timerTaskEntry5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        addThread.start();
        addThread.join();

        removeThread1.start();
        removeThread2.start();
        removeThread3.start();
        removeThread1.join();
        removeThread2.join();
        removeThread3.join();
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(3);
        arrayList.add(4);
        arrayList.add(5);
        headForEach(timerTaskList, arrayList);


        tailForEach(timerTaskList, 8, arrayList);
    }

    @Test
    public void circulationBigCurrentAddAndRemove() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            bigCurrentAddAndRemove();
        }
    }

    @Test
    public void bigCurrentAddAndRemove() throws InterruptedException {
        TimerTaskList timerTaskList = new TimerTaskList();
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            arrayList.add(i);
        }
        for (int i = 32; i < 35; i++) {
            arrayList.add(i);
        }

        for (int i = 50; i < 56; i++) {
            arrayList.add(i);
        }

        for (int i = 100; i < 120; i++) {
            arrayList.add(i);
        }


        for (int i = 150; i < 152; i++) {
            arrayList.add(i);
        }

        arrayList.add(159);
        arrayList.add(180);
        arrayList.add(190);
        arrayList.add(195);
        arrayList.add(199);

        LinkedBlockingQueue<TimerTaskEntry> queue = new LinkedBlockingQueue<>();


        int size = 200;
        for (int i = 1; i <= size; i++) {
            TimerTaskEntry timerTaskEntry = new TimerTaskEntry(i,  new TimerFuture<>(new MockTimerTask()));
            timerTaskEntry.setTimerTaskList(timerTaskList);
            timerTaskList.add(timerTaskEntry);
            if (arrayList.contains(i)) {
                queue.add(timerTaskEntry);
            }
        }
        TimeUnit.NANOSECONDS.sleep(1000);

        final Thread[] removeThread = new Thread[6];
        for (int i = 0; i < removeThread.length; i++) {
            removeThread[i] = new Thread(() -> {
                TimerTaskEntry take = null;
                try {

                    while (queue.peek() != null) {
                        take = queue.poll();
                        if (take != null)
                            timerTaskList.remove(take);
                    }
                } catch (Exception e) {
                    assert take != null;
                    System.out.println("Exception: " + take.getExpiration());
                }
            }, "remove-" + i);
        }

        for (int i = 0; i < 6; i++) {
            removeThread[i].start();
        }

        for (int i = 0; i < 6; i++) {
            removeThread[i].join();
        }

        headForEach(timerTaskList, arrayList);


        tailForEach(timerTaskList, size, arrayList);
    }


    public void headForEach(TimerTaskList timerTaskList, List<Integer> exclude) {
        TimerTaskEntry head = timerTaskList.getHead();
        TimerTaskEntry current = head.next;
        for (int i = 1; current != null; i++) {
            if (exclude.contains(i)) {
                continue;
            }
            Assert.assertEquals(i, current.getExpiration());
            current = current.next;
        }
    }

    public void tailForEach(TimerTaskList timerTaskList, int size, List<Integer> exclude) {
        TimerTaskEntry head = timerTaskList.getHead();
        TimerTaskEntry current = timerTaskList.getTail();

        for (int i = size; current != head; i--) {
            if (exclude.contains(i)) {
                continue;
            }
            Assert.assertEquals(i, current.getExpiration());
            current = current.prev;
        }
    }


}