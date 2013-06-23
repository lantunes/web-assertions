package org.bigtesting.assertions.web.internal;

import static junit.framework.Assert.*;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class PageElementContentAssertion {
    
    private final String expected;
    
    public PageElementContentAssertion(String expected) {
        this.expected = expected;
    }
    
    public void doAssertion(HtmlElement element) {
        assertEquals(expected, element.getTextContent());
    }
}
