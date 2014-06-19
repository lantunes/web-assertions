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

import org.bigtesting.WebAssertions;

/**
 * 
 * @author Luis Antunes
 */
public abstract class Client implements Runnable {

    private String name;
    private Throwable caught;
    private int copies;
    
    public Client() {
        this(null);
    }
    
    public Client(String name) {
        this(name, 1);
    }
    
    public Client(int copies) {
        this(null, copies);
    }
    
    public Client(String name, int copies) {
        this.name = name;
        this.copies = copies;
    }
    
    public void run() {
        
        try {
            
            onRequest();
            WebAssertions.closeWebClient();
            
        } catch (Throwable e) {
            caught = e;
        }
    }
    
    public abstract void onRequest();
    
    public String getName() {
        return name;
    }
    
    public Throwable getCaught() {
        return caught;
    }
    
    public boolean hasException() {
        return getCaught() != null;
    }
    
    public int getCopies() {
        return copies;
    }
}
