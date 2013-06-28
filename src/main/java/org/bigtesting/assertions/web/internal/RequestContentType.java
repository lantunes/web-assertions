package org.bigtesting.assertions.web.internal;

public class RequestContentType {
    
    private final String contentType;
    
    public RequestContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getContentType() {
        return contentType;
    }
}
