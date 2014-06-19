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
package org.bigtesting;

import org.bigtesting.assertions.web.concurrent.Client;
import org.bigtesting.assertions.web.concurrent.ConcurrentRequest;
import org.bigtesting.assertions.web.internal.CheckBoxInput;
import org.bigtesting.assertions.web.internal.ElementAttribute;
import org.bigtesting.assertions.web.internal.FileInput;
import org.bigtesting.assertions.web.internal.Method;
import org.bigtesting.assertions.web.internal.PageElementContentAssertion;
import org.bigtesting.assertions.web.internal.RequestAssertion;
import org.bigtesting.assertions.web.internal.RequestBody;
import org.bigtesting.assertions.web.internal.RequestContentType;
import org.bigtesting.assertions.web.internal.RequestedPage;
import org.bigtesting.assertions.web.internal.StandardInput;
import org.bigtesting.assertions.web.internal.WebClientManager;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * 
 * @author Luis Antunes
 */
public class WebAssertions {

    private static final WebClientManager webClientManager = new WebClientManager();
    
    public static final Method GET = Method.GET;
    public static final Method POST = Method.POST;
    public static final Method PUT = Method.PUT; 
    public static final Method TRACE = Method.TRACE; 
    public static final Method OPTIONS = Method.OPTIONS; 
    public static final Method HEAD = Method.HEAD;
    public static final Method DELETE = Method.DELETE;
    
    public static RequestAssertion assertRequest(String url) {
        
        WebClient client = webClientManager.getWebClient();
        return new RequestAssertion(client, url);
    }
    
    public static RequestAssertion assertRequest(Method method, String url) {
        
        WebClient client = webClientManager.getWebClient();
        return new RequestAssertion(client, method, url);
    }
    
    public static RequestAssertion assertRequest(String url, RequestContentType contentType) {
        
        WebClient client = webClientManager.getWebClient();
        return new RequestAssertion(client, GET, contentType, url);
    }
    
    public static RequestAssertion assertRequest(Method method, String url, RequestContentType contentType) {
        
        WebClient client = webClientManager.getWebClient();
        return new RequestAssertion(client, method, contentType, url);
    }
    
    public static RequestAssertion assertRequest(Method method, String url, 
            RequestBody body, RequestContentType contentType) throws Exception {
        
        WebClient client = webClientManager.getWebClient();
        return new RequestAssertion(client, method, body, contentType, url);
    }
    
    public static RequestAssertion assertRequest(String url, 
            RequestBody body, RequestContentType contentType) throws Exception {
        
        WebClient client = webClientManager.getWebClient();
        return new RequestAssertion(client, PUT, body, contentType, url);
    }
    
    public static RequestedPage makePageRequestFor(String url) throws Exception {
        
        WebClient client = webClientManager.getWebClient();
        return new RequestedPage(client, url);
    }
    
    public static void newWebClient() {
        
        webClientManager.newWebClient();
    }
    
    public static void closeWebClient() {
        
        webClientManager.closeWebClient();
    }
    
    public static PageElementContentAssertion withContent(String expected) {
        
        return new PageElementContentAssertion(expected);
    }
    
    public static ConcurrentRequest assertClients(Client ... clients) {
        
        return new ConcurrentRequest(clients);
    }

    public static StandardInput withValueFor(String inputName) {
        
        return new StandardInput(inputName);
    }
    
    public static CheckBoxInput withCheckBox(String name) {
        
        return new CheckBoxInput(name);
    }
    
    public static FileInput withFileNameFor(String inputName) {
        
        return new FileInput(inputName);
    }
    
    public static ElementAttribute withAttribute(String name) {
        
        return new ElementAttribute(name);
    }
    
    public static RequestBody withBody(String body) {
        
        return new RequestBody(body);
    }
    
    public static RequestContentType withContentType(String contentType) {
        
        return new RequestContentType(contentType);
    }
}
