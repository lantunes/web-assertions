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

/**
 * 
 * @author Luis Antunes
 */
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
