package org.bigtesting.web.assertions.concurrent;

public abstract class Client implements Runnable {

    private Exception caught;
    
    public void run() {
        
        try {
            
            onRequest();
            
        } catch (Exception e) {
            caught = e;
        }
    }
    
    public abstract void onRequest();
    
    public Exception getCaught() {
        return caught;
    }
    
    public boolean hasException() {
        return getCaught() != null;
    }
}
