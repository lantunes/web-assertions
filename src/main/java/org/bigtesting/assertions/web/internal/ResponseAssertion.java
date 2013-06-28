package org.bigtesting.assertions.web.internal;

import static junit.framework.Assert.*;

import java.util.List;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class ResponseAssertion {
    
    private WebResponse resp;
    
    public ResponseAssertion(WebResponse resp) {
        assertNotNull(resp);
        this.resp = resp;
    }
    
    public ResponseAssertion withStatus(int statusCode) {
        assertEquals(statusCode, resp.getStatusCode());
        return this;
    }
    
    public ResponseAssertion withContent(String content) {
        assertEquals(content, resp.getContentAsString());
        return this;
    }
    
    public ResponseAssertion withContentType(String contentType) {
        assertEquals(contentType, resp.getContentType());
        return this;
    }
    
    public ResponseAssertion withHeader(String name, String value) {
        List<NameValuePair> headers = resp.getResponseHeaders();
        boolean found = false;
        for (NameValuePair header : headers) {
            if (header.getName().equals(name) && header.getValue().equals(value)) {
                found = true;
                break;
            }
        }
        if (!found) {
            fail("could not find header with name " + name + " and value " + value);
        }
        return this;
    }
}