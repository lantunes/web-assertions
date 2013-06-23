package org.bigtesting;

import org.bigtesting.assertions.web.concurrent.Client;
import org.bigtesting.assertions.web.concurrent.ConcurrentRequest;
import org.bigtesting.assertions.web.internal.PageElementContentAssertion;
import org.bigtesting.assertions.web.internal.RequestAssertion;

import com.gargoylesoftware.htmlunit.WebClient;

public class WebAssertions {

    private static final ThreadLocal<WebClient> threadClient = new ThreadLocal<WebClient>();
    
    public static RequestAssertion assertThatRequestFor(String path) {
        
        WebClient client = threadClient.get();
        if (client == null) {
            //TODO can use various different browser versions, etc. 
            client = new WebClient();
            threadClient.set(client);
        }
        
        //TODO debugging
        System.out.println("using client: " + client + " : " + Thread.currentThread().getName());
        //end debugging
        
        return new RequestAssertion(client, path);
    }
    
    public static void newWebClient() {
        
        closeWebClient();
        threadClient.remove();
        threadClient.set(new WebClient());
    }
    
    public static void closeWebClient() {
        
        WebClient client = threadClient.get();
        if (client != null) {
            //TODO debugging
            System.out.println("closing client: " + client + " : " + Thread.currentThread().getName());
            //end debugging
            client.closeAllWindows();
        }
    }
    
    public static PageElementContentAssertion withContent(String expected) {
        return new PageElementContentAssertion(expected);
    }
    
    public static ConcurrentRequest assertThatClients(Client ... clients) {
        return new ConcurrentRequest(clients);
    }
    
}
