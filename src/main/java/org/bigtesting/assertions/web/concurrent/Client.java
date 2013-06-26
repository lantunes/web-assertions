package org.bigtesting.assertions.web.concurrent;

import org.bigtesting.WebAssertions;

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
