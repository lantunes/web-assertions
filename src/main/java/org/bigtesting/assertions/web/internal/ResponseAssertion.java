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
package org.bigtesting.assertions.web.internal;

import static junit.framework.Assert.*;

import java.util.List;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * 
 * @author Luis Antunes
 */
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