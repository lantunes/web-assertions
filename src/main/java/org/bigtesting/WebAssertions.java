package org.bigtesting;

import org.bigtesting.assertions.web.concurrent.Client;
import org.bigtesting.assertions.web.concurrent.ConcurrentRequest;
import org.bigtesting.assertions.web.internal.Method;
import org.bigtesting.assertions.web.internal.PageElementContentAssertion;
import org.bigtesting.assertions.web.internal.RequestAssertion;
import org.bigtesting.assertions.web.internal.WebClientManager;

import com.gargoylesoftware.htmlunit.WebClient;

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
}
