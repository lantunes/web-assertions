package org.bigtesting.assertions.web.test;

import static org.bigtesting.WebAssertions.*;
import static org.bigtesting.html.HtmlWriter.*;

import java.util.concurrent.Future;

import org.bigtesting.assertions.web.concurrent.Client;
import org.bigtesting.fixtures.http.Method;
import org.bigtesting.fixtures.http.PathParamSessionHandler;
import org.bigtesting.fixtures.http.ServerFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ning.http.client.AsyncHttpClient;
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
    public void test1() {
        
       server.handle(Method.GET, "/")
             .with(200, "text/html", 
                     html(body(h1("Hello"))));
        
        assertThatRequestFor(localhost + "/")
            .producesPage()
            .withH1Tag(withContent("Hello"));
    }
    
    @Test
    public void test2() {
        
        /*
         * TODO test that assertThatRequestFor()
         * really does associate a new WebClient
         * with the current thread consistently
         */
        
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
    public void test3() {
       
        server.handle(Method.GET, "/user/:name")
              .with(200, "text/html", 
                      html(body(h1("OK"))))
              .withNewSession(new PathParamSessionHandler());
        
        server.handle(Method.GET, "/user")
              .with(200, "text/html", 
                      html(body(h1("Hello {name}"))));
        
        assertThatRequestFor(localhost + "/user/Joe")
            .producesPage()
            .withH1Tag(withContent("OK"));
        
        //TODO newWebClient(); //produces no server response error
        assertThatRequestFor(localhost + "/user")
            .producesPage()
            .withH1Tag(withContent("Hello Joe"));
    }
    
    //@Test
    public void test4() throws Exception {
        
        /*
         * TODO add support to server fixture to 
         * return async response; see: http://www.simpleframework.org/doc/tutorial/tutorial.php
         * near bottom
         */
        
        /*
         * TODO take the body of an async response and convert it
         * into an HtmlUnit Page, so that we can make our typical
         * assertions on it
         */
        
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Future<Response> f = asyncHttpClient.prepareGet("http://www.ning.com/").execute();
        Response r = f.get();
        
        System.out.println(r.getResponseBody());
    }
}
