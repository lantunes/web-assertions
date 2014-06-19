/*
 * Copyright (C) 2014 BigTesting.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bigtesting.assertions.web.concurrent;

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/**
 * 
 * @author Luis Antunes
 */
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
                for (int c = 0; c < cl.getCopies(); c++) {
                    new SyncedThread(cl, startLatch, stopLatch).start();
                }
            }
    
            startLatch.countDown(); /* let all threads proceed */
            try {
                stopLatch.await(); /* wait for all to finish */
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            
            failIfAClientHasError(i);
        }
    }

    private void failIfAClientHasError(int runNumber) {
        
        for (Client cl : clients) {
            if (cl.hasException()) {
                String message = "client named " + cl.getName() + 
                        " reported a problem on run #" + (runNumber + 1) + 
                        ": " + cl.getCaught().getMessage();
                
                if (!(cl.getCaught() instanceof AssertionFailedError)) {
                    throw new RuntimeException(message, cl.getCaught());
                }
                Assert.fail(message);
            }
        }
    }
}
