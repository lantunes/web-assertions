package org.bigtesting.assertions.web.concurrent;

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

public class ConcurrentRequest {

    private final Client[] clients;
    
    public ConcurrentRequest(Client[] clients) {
        this.clients = clients;
    }
    
    public void canMakeConcurrentRequests() {
        canMakeConcurrentRequests(1);
    }
    
    public void canMakeConcurrentRequests(int times) {

        int n = times;
        if (n < 1) {
            n = 1;
        }
        
        for (int i = 0; i < n; i++) {
        
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
            
            failIfAClientHasError();
        }
    }

    private void failIfAClientHasError() {
        
        for (Client cl : clients) {
            if (cl.hasException()) {
                String message = "client named " + cl.getName() + 
                        " reported a problem: " + cl.getCaught().getMessage();
                
                if (!(cl.getCaught() instanceof AssertionFailedError)) {
                    throw new RuntimeException(message, cl.getCaught());
                }
                Assert.fail(message);
            }
        }
    }
}
