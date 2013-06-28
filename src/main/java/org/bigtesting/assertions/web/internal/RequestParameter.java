package org.bigtesting.assertions.web.internal;

public class RequestParameter {
    
    private final String name;
    private final String value;
    
    public RequestParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
}