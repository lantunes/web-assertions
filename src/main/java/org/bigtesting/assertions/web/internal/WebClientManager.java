package org.bigtesting.assertions.web.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;

public class WebClientManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WebClientManager.class);

    private final ThreadLocal<WebClient> threadClient = new ThreadLocal<WebClient>();
    
    public WebClient getWebClient() {
        
        WebClient client = threadClient.get();
        if (client == null) {
            //TODO can use various different browser versions, etc. 
            client = new WebClient();
            threadClient.set(client);
        }
        
        logger.debug("using client: " + client + " : " + 
                Thread.currentThread().getName());
        
        return client;
    }
    
    public void newWebClient() {
        
        closeWebClient();
        threadClient.remove();
        threadClient.set(new WebClient());
    }
    
    public void closeWebClient() {
        
        WebClient client = threadClient.get();
        if (client != null) {
            logger.debug("closing client: " + client + " : " + 
                    Thread.currentThread().getName());
            client.closeAllWindows();
        }
    }
}
