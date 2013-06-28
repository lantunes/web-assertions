package org.bigtesting.assertions.web.internal;

import static junit.framework.Assert.*;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class ElementAttributeAssertion {
    
    private final String attribute;
    private final String expected;
    
    public ElementAttributeAssertion(String attribute, String expected) {
        this.attribute = attribute;
        this.expected = expected;
    }
    
    public void doAssertion(HtmlElement element) {
        assertEquals(expected, element.getAttribute(attribute));
    }   
}
