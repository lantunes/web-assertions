package org.bigtesting.fixtures.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

/*
 * fixtd, http-fixtures:
 * 
 * Allows one to quickly set up an HTTP server for an HTTP client test:
 * - little overhead; a server instance can be created for each test
 * - fully functional, high-performance HTTP server, via the Simple framework
 * - simple, fluent API for configuring how the server should behave
 * - support for creating complex routing rules, based on HTTP method, 
 *   content type, and URI patterns
 * - support for using sessions for tests that require state between requests
 * - support for asynchronous HTTP responses
 * - support for asynchronous HTTP subscribe-broadcast scenarios 
 * - add a delay to a response for tests that require a delayed response
 */
public class ServerFixture {

    private final int port;
    private final FixtureContainer container;
    
    private Server server;
    private Connection connection;
    
    public ServerFixture(int port) {
        this(port, 10);
    }
    
    public ServerFixture(int port, int aysncThreadPoolSize) {
        
        this.port = port;
        this.container = new FixtureContainer(aysncThreadPoolSize);
    }
    
    public void start() throws IOException {
        
        server = new ContainerServer(container);
        connection = new SocketConnection(server, new LoggingAgent());
        SocketAddress address = new InetSocketAddress(port);
        connection.connect(address);
    }
    
    public void stop() throws IOException {
        
        connection.close();
        server.stop();
    }
    
    public RequestHandler handle(Method method, String resource) {
        
        RequestHandler handler = new RequestHandler();
        container.addHandler(handler, method, resource);
        return handler;
    }
    
    public RequestHandler handle(Method method, String resource, String contentType) {
        
        RequestHandler handler = new RequestHandler();
        container.addHandler(handler, method, resource, contentType);
        return handler;
    }
}
