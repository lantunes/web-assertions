package org.bigtesting.fixtures.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class ServerFixture {

    private final int port;
    private final FixtureContainer container;
    
    private Server server;
    private Connection connection;
    
    public ServerFixture(int port) {
        
        this.port = port;
        this.container = new FixtureContainer();
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
