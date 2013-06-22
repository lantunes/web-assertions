package org.bigtesting.web.assertions.concurrent;

import java.util.concurrent.CountDownLatch;

public class SyncedThread extends Thread {

    private CountDownLatch startLatch;
    private CountDownLatch stopLatch;

    public SyncedThread(Runnable runnable, CountDownLatch startLatch, CountDownLatch stopLatch) {
        super(runnable);
        this.startLatch = startLatch;
        this.stopLatch = stopLatch;
    }

    @Override
    public void run() {
        try {
            startLatch.await(); /* wait for other threads */
            super.run();
            stopLatch.countDown(); /* signal that this thread is finished */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
