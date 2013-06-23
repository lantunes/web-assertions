package org.bigtesting.assertions.web.concurrent;

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

public class ConcurrentRequest {

    private final Client[] clients;
    
    public ConcurrentRequest(Client[] clients) {
        this.clients = clients;
    }
    
    public ConcurrentRequest canMakeConcurrentRequests() {
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(clients.length);

        for (Client cl : clients) {
            new SyncedThread(cl, startLatch, stopLatch).start();
        }

        startLatch.countDown(); /* let all threads proceed */
        try {
            stopLatch.await(); /* wait for all to finish */
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        for (Client cl : clients) {
            if (cl.hasException()) {
                cl.getCaught().printStackTrace();
                Assert.fail("a client reported an exception: " + cl.getCaught().getMessage());
            }
        }
        
        return this;
    }
}
