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
package org.bigtesting.assertions.web.test;

import static junit.framework.Assert.*;
import static org.bigtesting.WebAssertions.*;
import static org.bigtesting.html.HTMLWriter.*;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bigtesting.assertions.web.concurrent.Client;
import org.bigtesting.fixd.Method;
import org.bigtesting.fixd.ServerFixture;
import org.bigtesting.fixd.session.PathParamSessionHandler;
import org.bigtesting.html.HTMLForm;
import org.bigtesting.html.HTMLPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;

/**
 * 
 * @author Luis Antunes
 */
public class WebAssertionsTest {
    
    private ServerFixture server;
    
    @Before
    public void beforeEachTest() throws Exception {

        /*
         * NOTE: the server could be initialized as part
         * of the 'server' class field declaration, as JUnit
         * will instantiate the Test class for each test; we're
         * doing it here to emphasize that every test has its
         * own server
         */
        server = new ServerFixture(9090);
        server.start();
        newWebClient();
    }
    
    @After
    public void afterEachTest() throws Exception {
        
        closeWebClient();
        server.stop();
    }
    
    @Test
    public void testProducesPage_WithH1Tag() {
        
       server.handle(Method.GET, "/")
             .with(200, "text/html", 
                     render(new HTMLPage().withH1Content("Hello")));
        
        assertRequest("http://localhost:9090/")
            .producesPage()
            .withH1Tag(withContent("Hello"));
    }
    
    @Test
    public void testProducesPage_WithH2Tag() {
        
       server.handle(Method.GET, "/name/:name")
             .with(200, "text/html", 
                     render(new HTMLPage().withH1Content("Hello :name")));
        
        assertRequest("http://localhost:9090/name/Tim")
            .producesPage()
            .withH1Tag(withContent("Hello Tim"));
    }
    
    @Test
    public void testProducesPage_ContentType() {
        
        server.handle(Method.GET, "/")
              .with(200, "application/xml", 
                      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>");
        
        assertRequest("http://localhost:9090/")
            .producesPage()
            .withContentType("application/xml")
            .withContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>" + LF);
    }
    
    @Test
    public void testProducesResponse_WithStatus() {
        
        server.handle(Method.GET, "/")
              .with(200, "text/plain", "ok");
        
        assertRequest("http://localhost:9090/")
            .producesResponse()
            .withStatus(200);
    }
    
    @Test
    public void testProducesResponse_WithHeader() {
        
        server.handle(Method.GET, "/")
              .with(200, "text/plain", "ok")
              .withHeader("MyHeader", "Hello");
        
        assertRequest("http://localhost:9090/")
            .producesResponse()
            .withHeader("MyHeader", "Hello");
    }
    
    @Test
    public void testAssertGETWithContentType() throws Exception {
        
        server.handle(Method.GET, "/", "text/plain")
              .with(200, "application/json", "{\"val\":\"ok\"}");
        
        assertRequest("http://localhost:9090/", 
                withContentType("text/plain"))
            .producesResponse()
            .withContentType("application/json")
            .withContent("{\"val\":\"ok\"}" + LF);
    }
    
    @Test
    public void testAssertPOSTWithContentType() throws Exception {
        
        server.handle(Method.POST, "/", "text/plain")
              .with(200, "application/json", "{\"val\":\"[request.body]\"}");
        
        assertRequest(POST, "http://localhost:9090/", 
                withBody("param2"), 
                withContentType("text/plain"))
            .producesResponse()
            .withContentType("application/json")
            .withContent("{\"val\":\"param2\"}" + LF);
    }
    
    @Test
    public void testAssertPOSTWithContentType_NoBody() throws Exception {
        
        server.handle(Method.POST, "/", "text/plain")
              .with(200, "application/json", "{\"val\":\"ok\"}");
        
        assertRequest(POST, "http://localhost:9090/", 
                withContentType("text/plain"))
            .producesResponse()
            .withContentType("application/json")
            .withContent("{\"val\":\"ok\"}" + LF);
    }
    
    @Test
    public void testAssertPUTWithContentType() throws Exception {
        
        server.handle(Method.PUT, "/", "text/plain")
              .with(200, "application/json", "{\"val\":\"[request.body]\"}");
        
        assertRequest("http://localhost:9090/", 
                withBody("param2"), 
                withContentType("text/plain"))
            .producesResponse()
            .withContentType("application/json")
            .withContent("{\"val\":\"param2\"}" + LF);
    }
    
    @Test
    public void testMakePageRequest_andGetH2TagContent() throws Exception {
        
        server.handle(Method.GET, "/")
              .with(200, "text/html", 
                      render(new HTMLPage().withH2Content("Hello")));
        
        assertEquals("Hello", 
                makePageRequestFor("http://localhost:9090/").andGetH2TagContent());
    }
    
    @Test
    public void testMakePageRequest_andGetTagContent() throws Exception {
        
        server.handle(Method.GET, "/")
              .with(200, "text/html", 
                      render(new HTMLPage().withH1Content("Hello")));
        
        assertEquals("Hello", 
                makePageRequestFor("http://localhost:9090/").andGetTagContent("h1"));
    }
    
    @Test
    public void testSubmittingForm() throws Exception {
        
        server.handle(Method.GET, "/form")
              .with(200, "text/html", 
                      render(new HTMLForm()
                        .withAction("/process")
                        .withTextInput("userName")
                        .withPasswordInput("password")));
        
        server.handle(Method.POST, "/process", "application/x-www-form-urlencoded")
              .with(200, "text/html", 
                      render(new HTMLPage()
                        .withParagraph("userName", "[request?userName]")
                        .withParagraph("password", "[request?password]")));
        
        assertRequest("http://localhost:9090/form")
            .afterSubmittingForm("test-form", 
                    withValueFor("userName").setTo("john"),
                    withValueFor("password").setTo("doe"))
            .producesPage()
            .withElement("userName", withContent("john"))
            .withElement("password", withContent("doe"));
    }
    
    @Test
    public void testSubmittingForm_WithAttribute() throws Exception {
        
        server.handle(Method.GET, "/form")
              .with(200, "text/html", 
                      render(new HTMLForm()
                        .withAction("/process")
                        .withTextInput("userName", "userName", "john")
                        .withPasswordInput("password", "password", "doe")));
        
        assertRequest("http://localhost:9090/form")
            .producesPage()
            .withElement("userName", withAttribute("value").setTo("john"))
            .withElement("password", withAttribute("value").setTo("doe"));
    }
    
    @Test
    public void testSubmittingFormWithCheckbox() throws Exception {
        
        server.handle(Method.GET, "/form")
              .with(200, "text/html", 
                      render(new HTMLForm()
                        .withAction("/process")
                        .withCheckboxInput("someFlag", true, "Flag")));
        
        server.handle(Method.POST, "/process", "application/x-www-form-urlencoded")
              .with(200, "text/html", 
                      render(new HTMLPage()
                        .withParagraph("flag", "[request?someFlag]")));
        
        assertRequest("http://localhost:9090/form")
            .afterSubmittingForm("test-form", 
                    withCheckBox("someFlag").checked())
            .producesPage()
            .withFlagElement(withContent("true"));
    }
    
    /*
     * TODO revisit when we can access parts (i.e. file part)
     * in the fixd request 
     */
    /*
    @Test
    public void testSubmittingFormWithFileUpload() throws Exception {
        
        server.handle(Method.GET, "/form")
        .with(200, "text/html", 
                render(new HTMLForm()
                  .withAction("/upload?p1=someVal")
                  .withFileInput()
                  .withTextInput("userName", "userName")));
        
        server.handle(Method.POST, "/process", "application/x-www-form-urlencoded")
              .with(new HttpRequestHandler() {
                  public void handle(HttpRequest request, HttpResponse response) {
                      fileUpload(request, response);                
                  }
              });
        
        assertRequest("http://localhost:9090/form")
            .afterSubmittingForm("test-form",
                    withFileNameFor("file").setTo("src/test/resources/upload-test.txt"),
                    withValueFor("userName").setTo("John"))
            .producesPage()
            .withElement("file-name", withContent("upload-test.txt"))
            .withElement("file-size", withContent("13"))
            .withElement("file-contenttype", withContent("text/plain"))
            .withElement("file-inmemory", withContent("true"))
            .withElement("file-username", withContent("John"))
            .withElement("file-content", withContent("Uploaded File"))
            .withElement("query-param", withContent("someVal"));
    }
    */
    
    @Test
    public void testConcurrentRequests() {
        
        server.handle(Method.GET, "/name/:name")
              .with(200, "text/html", 
                      render(new HTMLPage().withH1Content("Hello :name")));
        
        assertClients(
                new Client("client-1") {
                    public void onRequest() {
                        assertRequest("http://localhost:9090/name/Joe")
                            .producesPage()
                            .withH1Tag(withContent("Hello Joe"));
                    }
                }, 
                new Client("client-2") {
                    public void onRequest() {
                        assertRequest("http://localhost:9090/name/Tim")
                            .producesPage()
                            .withH1Tag(withContent("Hello Tim"));
                    }
                })
                .canMakeConcurrentRequests();
    }
    
    @Test
    public void testConcurrentRequests_Stateful() {
        
        /*
         * tests that assertRequest()
         * really does associate a new WebClient
         * with the current thread consistently
         */
       
        server.handle(Method.PUT, "/name/:name")
              .with(200, "text/html", 
                      render(new HTMLPage().withH1Content("OK")))
              .withSessionHandler(new PathParamSessionHandler());
        
        server.handle(Method.GET, "/name")
              .with(200, "text/html", 
                      render(new HTMLPage().withH1Content("Name: {name}")));
        
        assertClients(
                new Client("client-1") {
                    public void onRequest() {
                        assertRequest(PUT, "http://localhost:9090/name/Joe")
                            .producesPage()
                            .withH1Tag(withContent("OK"));
                        
                        assertRequest(GET, "http://localhost:9090/name")
                            .producesPage()
                            .withH1Tag(withContent("Name: Joe"));
                    }
                },
                new Client("client-2") {
                    public void onRequest() {
                        assertRequest(PUT, "http://localhost:9090/name/Tim")
                            .producesPage()
                            .withH1Tag(withContent("OK"));
                        
                        assertRequest(GET, "http://localhost:9090/name")
                            .producesPage()
                            .withH1Tag(withContent("Name: Tim"));
                    }
                })
                .canMakeConcurrentRequests(10);
    }
    
    /*-------------------------------------------------*/
    
    private static final String LF = System.getProperty("line.separator");
    
    /*
     * TODO revisit when we can access parts (i.e. file part)
     * in the fixd request 
     */
    /*
    private void fileUpload(HttpRequest request, HttpResponse response) {
        
        String content = "null";
        InputStream in = null;
        try {
            in = file.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) != -1) {
               out.write(buffer, 0, length);
            }
            content = new String(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close(); 
            } catch (Exception e) {
            }
        }
        
        String fileName = file.getFileName();
        if (fileName != null && fileName.trim().length() != 0) {
            int lastIdx = fileName.lastIndexOf(File.separatorChar);
            if (lastIdx != -1) {
                fileName = fileName.substring(lastIdx + 1);
            }
        }
        
        return new HTMLPage()
            .withParagraph("file-name", fileName)
            .withParagraph("file-size", String.valueOf(file.getSize()))
            .withParagraph("file-contenttype", file.getContentType())
            .withParagraph("file-inmemory", String.valueOf(file.isInMemory()))
            .withParagraph("file-username", request.getRequestParameter("userName"))
            .withParagraph("file-content", content)
            .withParagraph("query-param", request.getRequestParameter("p1"));
    }
    */
    
    @Ignore
    public void testAsyncRequestResponse() throws Exception {
        
        server.handle(Method.GET, "/echo/:message")
              .with(200, "text/html", 
                      render(new HTMLPage().withH1Content("message: :message")))
              .every(1, TimeUnit.SECONDS, 3);
        
        /* TODO
        assertAsyncRequest(localhost + "/suspend")
            .producesResponse()
            .withH1Tag(withContent("OK"))
            .every(2, TimeUnit.SECONDS);
            
        assertRequest(localhost + "/suspend")
            .producesPage()
            .withH1Tag(withContent("OK"))
            .within(5, TimeUnit.SECONDS);
       */
        
        /*
         * TODO take the body of an async response and convert it
         * into an HtmlUnit Page, so that we can make our typical
         * assertions on it
         */
        
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Future<Integer> f = asyncHttpClient.prepareGet("http://localhost:9090/echo/hello").execute(
                new AsyncCompletionHandler<Integer>() {
                    @Override
                    public Integer onCompleted(Response r) throws Exception {
                        if (r != null) {
                            System.out.println("COMPLETED: " + r.getResponseBody());
                            return r.getStatusCode();
                        }
                        return 200;
                    }
                    
                    @Override
                    public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                        System.out.println("HEADERS: " + h.getHeaders().toString());
                        return STATE.CONTINUE;
                    }
                    
                    @Override
                    public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                        System.out.println("STATUS: " + status.getStatusCode());
                        return STATE.CONTINUE;
                    }
                    
                    @Override
                    public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                        System.out.println("CHUNK: " + new String(bodyPart.getBodyPartBytes()));
                        return STATE.CONTINUE;
                    }
                    
                    @Override
                    public void onThrowable(Throwable t) {
                        System.out.println("ERROR");
                        t.printStackTrace();
                    }
                });
        
        int statusCode = f.get();
        System.out.println("status code: " + statusCode);
    }
    
    @Ignore
    public void testUpon() throws Exception {
        
        /*
         * NOTE: for this to work, a client must first make
         * a request to "/subscribe", otherwise no handler will
         * be available for "/broadcast"
         * ** There is a test in fixd that shows you can make a
         * request for the upon handler and it's fine
         * 
         * TODO
         * ability to include request body in response body, i.e.:
         * server.handle(Method.GET, "/subscribe")
         *    .with(200, "text/html", html(body(h1("message: [request.body]"))))
         *    .upon(Method.GET, "/broadcast");
         */
        server.handle(Method.GET, "/subscribe")
              .with(200, "text/html", 
                      render(new HTMLPage().withH1Content("message: :message")))
              .upon(Method.GET, "/broadcast/:message");
        
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Future<Integer> f = asyncHttpClient.prepareGet("http://localhost:9090/subscribe").execute(
                new AsyncCompletionHandler<Integer>() {
                    @Override
                    public Integer onCompleted(Response r) throws Exception {
                        if (r != null) {
                            System.out.println("COMPLETED: " + r.getResponseBody());
                            return r.getStatusCode();
                        }
                        return 200;
                    }
                    
                    @Override
                    public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                        System.out.println("HEADERS: " + h.getHeaders().toString());
                        return STATE.CONTINUE;
                    }
                    
                    @Override
                    public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                        System.out.println("STATUS: " + status.getStatusCode());
                        return STATE.CONTINUE;
                    }
                    
                    @Override
                    public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                        System.out.println("CHUNK: " + new String(bodyPart.getBodyPartBytes()));
                        return STATE.CONTINUE;
                    }
                    
                    @Override
                    public void onThrowable(Throwable t) {
                        if (!(t instanceof CancellationException)) {
                            System.out.println("ERROR");
                            t.printStackTrace();
                        }
                    }
                });
        
        for (int i = 0; i < 3; i++) {
            
            /* the sleep call is before the broadcast request
             * because sometimes the first request is made
             * before the async client has made its request */
            Thread.sleep(1000);
            
            assertRequest("http://localhost:9090/broadcast/hello" + i)
                .producesPage();
        }
        
        /*
         * NOTE: this will throw an exception if the response is not complete after 5 seconds:
         * int statusCode = f.get(5, TimeUnit.SECONDS);
         */
        
        f.cancel(true);
    }
}
