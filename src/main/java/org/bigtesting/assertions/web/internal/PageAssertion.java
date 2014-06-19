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
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 
 * @author Luis Antunes
 */
public class PageAssertion {

    private final Page page;
    
    public PageAssertion(Page page) {
        this.page = page;
    }
    
    public PageAssertion withTag(String element, 
            PageElementContentAssertion pageElementAssertion) {
        
        DomNodeList<HtmlElement> elements = ((HtmlPage)page).getElementsByTagName(element);
        assertEquals(1, elements.size());
        HtmlElement htmlElement = elements.get(0);
        pageElementAssertion.doAssertion(htmlElement);
        return this;
    }
    
    public PageAssertion withElement(String elementId, 
            PageElementContentAssertion pageElementAssertion) {
        
        HtmlElement htmlElement = ((HtmlPage)page).getElementById(elementId);
        pageElementAssertion.doAssertion(htmlElement);
        return this;
    }
    
    public PageAssertion withElement(String elementId, 
            ElementAttributeAssertion elementAttrAssertion) {
        
        HtmlElement htmlElement = ((HtmlPage)page).getElementById(elementId);
        elementAttrAssertion.doAssertion(htmlElement);
        return this;
    }
    
    public PageAssertion withFlagElement(PageElementContentAssertion pageElementAssertion) {
        return withElement("flag", pageElementAssertion);
    }
    
    public PageAssertion withContentType(String contentType) {
        assertEquals("unexpected content-type:", contentType, page.getWebResponse().getContentType());
        return this;
    }
    
    public PageAssertion withContent(String content) {
        assertEquals("unexpected content:", content, page.getWebResponse().getContentAsString());
        return this;
    }
    
    public PageAssertion withH1Tag(PageElementContentAssertion pageElementAssertion) {
        return withTag("h1", pageElementAssertion);
    }
    
    public PageAssertion withH2Tag(PageElementContentAssertion pageElementAssertion) {
        return withTag("h2", pageElementAssertion);
    }
}
