package org.bigtesting.fixtures.http;

public class Upon {

    private Method method;
    private String resource;
    private String contentType;
    
    public Upon(Method method, String resource) {
        this(method, resource, null);
    }
    
    public Upon(Method method, String resource, String contentType) {
        
        this.method = method;
        this.resource = resource;
        this.contentType = contentType;
    }

    public Method getMethod() {
        return method;
    }
    
    public String getResource() {
        return resource;
    }
    
    public String getContentType() {
        return contentType;
    }
}
