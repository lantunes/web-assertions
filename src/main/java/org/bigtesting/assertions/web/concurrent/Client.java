package org.bigtesting.assertions.web.concurrent;

import org.bigtesting.WebAssertions;

public abstract class Client implements Runnable {

    private Exception caught;
    
    public void run() {
        
        try {
            
            onRequest();
            WebAssertions.closeWebClient();
            
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
