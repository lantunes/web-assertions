package org.bigtesting.web.assertions;

import org.bigtesting.web.assertions.concurrent.Client;
import org.bigtesting.web.assertions.concurrent.ConcurrentRequest;
import org.bigtesting.web.assertions.internal.PageElementContentAssertion;
import org.bigtesting.web.assertions.internal.RequestAssertion;

import com.gargoylesoftware.htmlunit.WebClient;

public class WebAssertions {

    public static void startServer() {
        /*
         * start an embedded Jetty server
         */
    }
    
    public static void stopServer() {
        /*
         * stop an embedded Jetty server
         */
    }
    
    public static void setServer() {
        /*
         * indicate that an existing server will be used
         */
    }
    
    private static final ThreadLocal<WebClient> threadClient = new ThreadLocal<WebClient>();
    
    public static RequestAssertion assertThatRequestFor(String path) {
        
        WebClient client = threadClient.get();
        if (client == null) {
            //TODO can use various different browser versions, etc. 
            client = new WebClient();
            threadClient.set(client);
        }
        
        return new RequestAssertion(client, path);
    }
    
    public static void newWebClient() {
        
        threadClient.remove();
        threadClient.set(new WebClient());
    }
    
    public static PageElementContentAssertion withContent(String expected) {
        return new PageElementContentAssertion(expected);
    }
    
    public static ConcurrentRequest assertThatClients(Client ... clients) {
        return new ConcurrentRequest(clients);
    }
    
}
