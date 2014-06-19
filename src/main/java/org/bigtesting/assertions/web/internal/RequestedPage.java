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

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 
 * @author Luis Antunes
 */
public class RequestedPage {
    private Page page;
    
    public RequestedPage(WebClient client, String url) throws Exception {
        page = client.getPage(url);
        assertNotNull(page);
    }
    
    public String andGetTagContent(String name) {
        DomNodeList<HtmlElement> elements = ((HtmlPage)page).getElementsByTagName(name);
        assertEquals(1, elements.size());
        HtmlElement tag = elements.get(0);
        return tag.getTextContent();
    }
    
    public String andGetH2TagContent() {
        return andGetTagContent("h2");
    }
}