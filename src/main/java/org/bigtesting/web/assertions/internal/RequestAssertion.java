package org.bigtesting.web.assertions.internal;

import static junit.framework.Assert.*;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

public class RequestAssertion {
    
    private Page page;
    private WebClient client;
    
    public RequestAssertion(WebClient client, String path) {
        this.client = client;
        
        //TODO debugging
        System.out.println("client: " + client + " : " + Thread.currentThread().getName());
        //end debugging
        
        try {
            page = client.getPage(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
//    public RequestAssertion(HttpMethod method, String path, 
//            RequestParameter...params) throws Exception {
//        
//        WebRequest wr = new WebRequest(new URL(toUrl(path)), method);
//        setRequestParameters(wr, params);
//        page = client.getPage(wr);
//    }
//    
//    public RequestAssertion(RequestBody content, RequestContentType contentType, 
//            String path) throws Exception {
//        
//        WebRequest wr = new WebRequest(new URL(toUrl(path)));
//        wr.setHttpMethod(HttpMethod.POST);
//        wr.setRequestBody(content.getBody());
//        wr.setAdditionalHeader("Content-Type", contentType.getContentType());
//        page = client.getPage(wr);
//    }
//    
//    private void setRequestParameters(WebRequest wr, RequestParameter... params) {
//        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//        for (RequestParameter pair : params) {
//            pairs.add(new NameValuePair(pair.getName(), pair.getValue()));
//        }
//        wr.setRequestParameters(pairs);
//    }
//    
    public PageAssertion producesPage() {
        assertNotNull(page);
        return new PageAssertion(page);
    }
//    
//    public void producesErrorPage() {
//        producesPage().withTag("h1", withContent("Error"));
//    }
//    
//    public ResponseAssertion producesResponse() {
//        assertNotNull(page);
//        return new ResponseAssertion(page.getWebResponse());
//    }
//    
//    public RequestAssertion afterSubmittingForm(String formName, 
//            FormInputValue...formVals) throws Exception {
//        
//        HtmlForm form = ((HtmlPage)page).getFormByName(formName);
//        for (FormInputValue formVal : formVals) {
//            HtmlInput input = form.getInputByName(formVal.getInputName());
//            formVal.handleInput(input);
//        }
//        HtmlSubmitInput submit = form.getInputByValue("submit");
//        page = submit.click();
//        return this;
//    }
}
