package org.bigtesting.assertions.web.test;

import static org.bigtesting.WebAssertions.*;
import static org.bigtesting.html.HtmlWriter.*;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bigtesting.assertions.web.concurrent.Client;
import org.bigtesting.fixd.Method;
import org.bigtesting.fixd.ServerFixture;
import org.bigtesting.fixd.session.PathParamSessionHandler;
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
        
        assertRequest(localhost + "/")
            .producesPage()
            .withH1Tag(withContent("Hello"));
    }
    
    @Test
    public void testSimpleGetWithPathParam() {
        
       server.handle(Method.GET, "/name/:name")
             .with(200, "text/html", 
                     html(body(h1("Hello :name"))));
        
        assertRequest(localhost + "/name/Tim")
            .producesPage()
            .withH1Tag(withContent("Hello Tim"));
    }
    
    @Test
    public void testConcurrentRequests() {
        
        server.handle(Method.GET, "/name/:name")
              .with(200, "text/html", 
                      html(body(h1("Hello :name"))));
        
        assertClients(
                new Client("client-1") {
                    public void onRequest() {
                        assertRequest(localhost + "/name/Joe")
                            .producesPage()
                            .withH1Tag(withContent("Hello Joe"));
                    }
                }, 
                new Client("client-2") {
                    public void onRequest() {
                        assertRequest(localhost + "/name/Tim")
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
       
        server.handle(Method.PUT, "/name/:name")
              .with(200, "text/html", 
                      html(body(h1("OK"))))
              .withSessionHandler(new PathParamSessionHandler());
        
        server.handle(Method.GET, "/name")
              .with(200, "text/html", 
                      html(body(h1("Name: {name}"))));
        
        assertClients(
                new Client("client-1") {
                    public void onRequest() {
                        assertRequest(PUT, localhost + "/name/Joe")
                            .producesPage()
                            .withH1Tag(withContent("OK"));
                        
                        assertRequest(GET, localhost + "/name")
                            .producesPage()
                            .withH1Tag(withContent("Name: Joe"));
                    }
                },
                new Client("client-2") {
                    public void onRequest() {
                        assertRequest(PUT, localhost + "/name/Tim")
                            .producesPage()
                            .withH1Tag(withContent("OK"));
                        
                        assertRequest(GET, localhost + "/name")
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
        
        assertRequest(localhost + "/suspend")
            .producesPage()
            .withH1Tag(withContent("ok"));
    }
    
    @Test
    public void testAsyncRequestResponse() throws Exception {
        
        server.handle(Method.GET, "/echo/:message")
              .with(200, "text/html", html(body(h1("message: :message"))))
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
    public void testUpon() throws Exception {
        
        /*
         * NOTE: for this to work, a client must first make
         * a request to "/subscribe", otherwise no handler will
         * be available for "/broadcast"
         * 
         * TODO
         * ability to include request body in response body, i.e.:
         * server.handle(Method.GET, "/subscribe")
         *    .with(200, "text/html", html(body(h1("message: [request.body]"))))
         *    .upon(Method.GET, "/broadcast");
         */
        server.handle(Method.GET, "/subscribe")
              .with(200, "text/html", html(body(h1("message: :message"))))
              .upon(Method.GET, "/broadcast/:message");
        
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Future<Integer> f = asyncHttpClient.prepareGet(localhost + "/subscribe").execute(
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
            
            assertRequest(localhost + "/broadcast/hello" + i)
                .producesPage();
        }
        
        /*
         * NOTE: this will throw an exception if the response is not complete after 5 seconds:
         * int statusCode = f.get(5, TimeUnit.SECONDS);
         */
        
        f.cancel(true);
    }
}
