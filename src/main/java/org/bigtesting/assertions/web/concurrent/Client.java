package org.bigtesting.assertions.web.concurrent;

import org.bigtesting.WebAssertions;

public abstract class Client implements Runnable {

    private String name;
    private Throwable caught;
    
    public Client() {}
    
    public Client(String name) {
        this.name = name;
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
}
