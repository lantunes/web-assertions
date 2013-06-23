package org.bigtesting.assertions.web.internal;

import static junit.framework.Assert.*;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

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
    
//    public PageAssertion withElement(String elementId, 
//            ElementAttributeAssertion elementAttrAssertion) {
//        
//        HtmlElement htmlElement = ((HtmlPage)page).getElementById(elementId);
//        elementAttrAssertion.doAssertion(htmlElement);
//        return this;
//    }
    
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
