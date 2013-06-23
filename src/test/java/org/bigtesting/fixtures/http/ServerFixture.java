package org.bigtesting.fixtures.http;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
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
        connection = new SocketConnection(server);
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

    private class FixtureContainer implements Container {
        
        private final Map<HandlerKey, RequestHandler> handlerMap = 
                new HashMap<HandlerKey, RequestHandler>();
        
        private final RouteMap routeMap = new RegexRouteMap();
        
        public void addHandler(RequestHandler handler, 
                Method method, String resource) {
            
            addHandler(handler, method, resource, null);
        }
        
        public void addHandler(RequestHandler handler, 
                Method method, String resource, String contentType) {
            
            Route route = new Route(resource);
            HandlerKey key = new HandlerKey(method.name(), route, contentType);
            handlerMap.put(key, handler);
            routeMap.add(route);
        }
        
        public void handle(Request request, Response response) {

            String responseContentType = "text/plain";
            String responseBody = "";
            
            String method = request.getMethod();
            String path = request.getPath().getPath();
            ContentType requestContentType = request.getContentType();
            
            Route route = routeMap.getRoute(path);
            String contentType = requestContentType != null ? 
                    requestContentType.toString() : null;
            HandlerKey key = new HandlerKey(method, route, contentType);
            RequestHandler handler = handlerMap.get(key);
            if (handler == null) {
                throw new RuntimeException("could not find a handler for " + 
                        method + " - " + path);
            }
            
            responseContentType = handler.contentType();
            responseBody = handler.body(path, route.pathParameterElements());
            response.setCode(handler.statusCode());
            
            try {
                PrintStream body = response.getPrintStream();
                long time = System.currentTimeMillis();
          
                response.set("Content-Type", responseContentType);
                response.set("Server", "HelloWorld/1.0 (Simple 4.0)");
                response.setDate("Date", time);
                response.setDate("Last-Modified", time);
          
                body.println(responseBody);
                body.close();
             } catch(Exception e) {
                e.printStackTrace();
             }
        }
    }
}
