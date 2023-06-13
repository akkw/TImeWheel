package com.akkw.time.wheel;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimerTaskListTest {


    private TimerTaskList timerTaskList = new TimerTaskList();


    @Test
    public void addList() {
        int size = 10;
        for (int i = 1; i <= size; i++) {
            timerTaskList.add(new TimerTaskEntry(i, new TimerTask()));
        }

        TimerTaskEntry head = timerTaskList.getHead();
        TimerTaskEntry current = head.next;
        for (int i = 1; current != null; i++) {
            Assert.assertEquals(i, current.getExpiration());
            current = current.next;
        }

        TimerTaskEntry tail = timerTaskList.getTail();
        current = tail;

        for (int i = size; current != head; i--) {
            Assert.assertEquals(i, current.getExpiration());
            current = current.prev;
        }

    }


    @Test
    public void removeList() {
        TimerTaskEntry timerTaskEntry = new TimerTaskEntry(4, new TimerTask());
        TimerTaskEntry timerTaskEntry1 = new TimerTaskEntry(6, new TimerTask());
        TimerTaskEntry timerTaskEntry2 = new TimerTaskEntry(7, new TimerTask());
        timerTaskEntry.setTimerTaskList(timerTaskList);
        timerTaskEntry1.setTimerTaskList(timerTaskList);
        timerTaskEntry2.setTimerTaskList(timerTaskList);

        timerTaskList.add(new TimerTaskEntry(1, new TimerTask()));
        timerTaskList.add(new TimerTaskEntry(2, new TimerTask()));
        timerTaskList.add(new TimerTaskEntry(3, new TimerTask()));
        timerTaskList.add(timerTaskEntry);
        timerTaskList.add(new TimerTaskEntry(5, new TimerTask()));
        timerTaskList.add(timerTaskEntry1);
        timerTaskList.add(timerTaskEntry2);

        timerTaskList.remove(timerTaskEntry);
        timerTaskList.remove(timerTaskEntry1);
        timerTaskList.remove(timerTaskEntry2);
    }
}