package org.bigtesting.web.assertions;

import static org.bigtesting.web.assertions.WebAssertions.*;

import java.util.concurrent.Future;

import org.bigtesting.web.assertions.concurrent.Client;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class WebAssertionsTest {
    
    @BeforeClass
    public static void beforeClass() {
//        System.out.println("before class " + " : " + Thread.currentThread().getName());
    }
    
    @Before
    public void beforeEachTest() {
        
        newWebClient();
        
//        System.out.println("before " + this + " : " + Thread.currentThread().getName());
    }
    
//    @Test
    public void test1() {
        
//        assertThatRequestFor("http://en.wikipedia.org/wiki/Hacker_News")
//            .producesPage()
//            .withH1Tag(withContent("Hacker News"));
        
        assertThatRequestFor("http://localhost")
            .producesPage();
        
//        System.out.println("test1 " + this + " : " + Thread.currentThread().getName());
    }
    
//    @Test
    public void test2() {
        
        assertThatClients(
                new Client() {
                    public void onRequest() {
                        assertThatRequestFor("http://localhost")
                            .producesPage();
                    }
                }, 
                new Client() {
                    public void onRequest() {
                        assertThatRequestFor("http://localhost")
                            .producesPage();
                    }
                })
                .canMakeConcurrentRequests();
                
//        System.out.println("test2 " + this + " : " + Thread.currentThread().getName());
    }
    
//    @Test
    public void test3() {
        
        assertThatRequestFor("http://localhost")
            .producesPage();
        
//        System.out.println("test3 " + this + " : " + Thread.currentThread().getName());
    }
    
//    @Test
    public void test4() throws Exception {
        
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Future<Response> f = asyncHttpClient.prepareGet("http://www.ning.com/").execute();
        Response r = f.get();
        
        System.out.println(r.getResponseBody());
    }
    
    @After
    public void afterEachTest() {
//        System.out.println("after " + this + " : " + Thread.currentThread().getName());
    }
    
    @AfterClass
    public static void afterClass() {
//        System.out.println("after class " + " : " + Thread.currentThread().getName());
    }
}
