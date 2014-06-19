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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * 
 * @author Luis Antunes
 */
public class RequestAssertion {
    
    private static final Map<Method, HttpMethod> methodMap = 
            new HashMap<Method, HttpMethod>();
    
    static {
        methodMap.put(Method.GET, HttpMethod.GET);
        methodMap.put(Method.POST, HttpMethod.POST);
        methodMap.put(Method.PUT, HttpMethod.PUT); 
        methodMap.put(Method.TRACE, HttpMethod.TRACE); 
        methodMap.put(Method.OPTIONS, HttpMethod.OPTIONS); 
        methodMap.put(Method.HEAD, HttpMethod.HEAD);
        methodMap.put(Method.DELETE, HttpMethod.DELETE);
    }
    
    private Page page;
    
    public RequestAssertion(WebClient client, String url) {
        
        try {
            page = client.getPage(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public RequestAssertion(WebClient client, Method method, String url, 
            RequestParameter...params) {
        
        try {
            WebRequest wr = new WebRequest(new URL(url), methodMap.get(method));
            setRequestParameters(wr, params);
            page = client.getPage(wr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public RequestAssertion(WebClient client, Method method, RequestBody content, 
            RequestContentType contentType, String url) {
        
        try {
            WebRequest wr = new WebRequest(new URL(url));
            wr.setHttpMethod(methodMap.get(method));
            wr.setRequestBody(content.getBody());
            wr.setAdditionalHeader("Content-Type", contentType.getContentType());
            page = client.getPage(wr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public RequestAssertion(WebClient client, Method method, 
            RequestContentType contentType, String url) {
        
        try {
            WebRequest wr = new WebRequest(new URL(url));
            wr.setHttpMethod(methodMap.get(method));
            wr.setAdditionalHeader("Content-Type", contentType.getContentType());
            page = client.getPage(wr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void setRequestParameters(WebRequest wr, RequestParameter... params) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (RequestParameter pair : params) {
            pairs.add(new NameValuePair(pair.getName(), pair.getValue()));
        }
        wr.setRequestParameters(pairs);
    }
    
    public PageAssertion producesPage() {
        assertNotNull("page was not produced:", page);
        return new PageAssertion(page);
    }
    
    public ResponseAssertion producesResponse() {
        assertNotNull(page);
        return new ResponseAssertion(page.getWebResponse());
    }
    
    public RequestAssertion afterSubmittingForm(String formName, 
            FormInputValue...formVals) throws Exception {
        
        HtmlForm form = ((HtmlPage)page).getFormByName(formName);
        for (FormInputValue formVal : formVals) {
            HtmlInput input = form.getInputByName(formVal.getInputName());
            formVal.handleInput(input);
        }
        HtmlSubmitInput submit = form.getInputByValue("submit");
        page = submit.click();
        return this;
    }
}
