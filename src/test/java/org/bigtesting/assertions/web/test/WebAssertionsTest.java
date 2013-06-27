package org.bigtesting.assertions.web.test;

import static org.bigtesting.WebAssertions.*;
import static org.bigtesting.html.HtmlWriter.*;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bigtesting.assertions.web.concurrent.Client;
import org.bigtesting.fixtures.http.Method;
import org.bigtesting.fixtures.http.PathParamSessionHandler;
import org.bigtesting.fixtures.http.ServerFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;

public class WebAssertionsTest {
    
    private static final int PORT = 9090;
    private static final String localhost = "http://localhost:" + PORT;
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
        server = new ServerFixture(PORT);
        server.start();
        newWebClient();
    }
    
    @After
    public void afterEachTest() throws Exception {
        
        closeWebClient();
        server.stop();
    }
    
    @Test
    public void testSimpleGet() {
        
       server.handle(Method.GET, "/")
             .with(200, "text/html", 
                     html(body(h1("Hello"))));
        
        assertThatRequestFor(localhost + "/")
            .producesPage()
            .withH1Tag(withContent("Hello"));
    }
    
    @Test
    public void testSimpleGetWithPathParam() {
        
       server.handle(Method.GET, "/name/:name")
             .with(200, "text/html", 
                     html(body(h1("Hello :name"))));
        
        assertThatRequestFor(localhost + "/name/Tim")
            .producesPage()
            .withH1Tag(withContent("Hello Tim"));
    }
    
    @Test
    public void testConcurrentRequests() {
        
        server.handle(Method.GET, "/name/:name")
              .with(200, "text/html", 
                      html(body(h1("Hello :name"))));
        
        assertThatClients(
                new Client("client-1") {
                    public void onRequest() {
                        assertThatRequestFor(localhost + "/name/Joe")
                            .producesPage()
                            .withH1Tag(withContent("Hello Joe"));
                    }
                }, 
                new Client("client-2") {
                    public void onRequest() {
                        assertThatRequestFor(localhost + "/name/Tim")
                            .producesPage()
                            .withH1Tag(withContent("Hello Tim"));
                    }
                })
                .canMakeConcurrentRequests();
    }
    
    @Test
    public void testStatefulConcurrentRequests() {
        
        /*
         * tests that assertThatRequestFor()
         * really does associate a new WebClient
         * with the current thread consistently
         */
       
        server.handle(Method.GET, "/set/name/:name")
              .with(200, "text/html", 
                      html(body(h1("OK"))))
              .withNewSession(new PathParamSessionHandler());
        
        server.handle(Method.GET, "/get/name")
              .with(200, "text/html", 
                      html(body(h1("Name: {name}"))));
        
        assertThatClients(
                new Client("client-1") {
                    public void onRequest() {
                        assertThatRequestFor(localhost + "/set/name/Joe")
                            .producesPage()
                            .withH1Tag(withContent("OK"));
                        
                        assertThatRequestFor(localhost + "/get/name")
                            .producesPage()
                            .withH1Tag(withContent("Name: Joe"));
                    }
                },
                new Client("client-2") {
                    public void onRequest() {
                        assertThatRequestFor(localhost + "/set/name/Tim")
                            .producesPage()
                            .withH1Tag(withContent("OK"));
                        
                        assertThatRequestFor(localhost + "/get/name")
                            .producesPage()
                            .withH1Tag(withContent("Name: Tim"));
                    }
                })
                .canMakeConcurrentRequests(10);
    }
    
    @Test
    public void testDelay() {
        
        server.handle(Method.GET, "/suspend")
              .with(200, "text/html", html(body(h1("ok"))))
              .after(1, TimeUnit.SECONDS);
        
        assertThatRequestFor(localhost + "/suspend")
            .producesPage()
            .withH1Tag(withContent("ok"));
    }
    
    @Test
    public void testAsyncRequestResponse() throws Exception {
        
        server.handle(Method.GET, "/echo/:message")
              .with(200, "text/html", html(body(h1("message: :message"))))
              .every(1, TimeUnit.SECONDS, 3);
        
        /* TODO
        assertThatAsyncRequestFor(localhost + "/suspend")
            .producesResponse()
            .withH1Tag(withContent("OK"))
            .every(2, TimeUnit.SECONDS);
            
        assertThatRequestFor(localhost + "/suspend")
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
        Future<Integer> f = asyncHttpClient.prepareGet(localhost + "/echo/hello").execute(
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
    
    @Test
    public void testUpon() {
        
        /*
         * NOTE: for this to work, a client must first make
         * a request to "/subscribe", otherwise no handler will
         * be available for "/broadcast"
         */
        server.handle(Method.GET, "/subscribe")
              .with(200, "text/html", html(body(h1("message: :message"))))
              .upon(Method.GET, "/broadcast/:message");
    }
}
