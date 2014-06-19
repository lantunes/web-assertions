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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * 
 * @author Luis Antunes
 */
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
