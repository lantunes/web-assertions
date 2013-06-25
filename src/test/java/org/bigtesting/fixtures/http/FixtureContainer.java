package org.bigtesting.fixtures.http;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

public class FixtureContainer implements Container {
    
    private static final String SESSION_COOKIE_NAME = "Simple-Session";

    private final Map<HandlerKey, RequestHandler> handlerMap = 
            new HashMap<HandlerKey, RequestHandler>();
    
    private final RouteMap routeMap = new RegexRouteMap();
    
    private final Map<String, Session> sessions = new HashMap<String, Session>();
    
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
        if (route == null) {
            throw new RuntimeException("could not find a route for " + path);
        }
        String contentType = requestContentType != null ? 
                requestContentType.toString() : null;
        HandlerKey key = new HandlerKey(method, route, contentType);
        RequestHandler handler = handlerMap.get(key);
        if (handler == null) {
            throw new RuntimeException("could not find a handler for " + 
                    method + " - " + path);
        }
        
        String handlerContentType = handler.contentType();
        if (handlerContentType != null && 
                handlerContentType.trim().length() != 0) {
            
            responseContentType = handlerContentType;
        }
        
        Session session = getSessionIfExists(request);
        String handlerBody = handler.body(path, route.pathParameterElements(), session);
        if (handlerBody != null && handlerBody.trim().length() != 0) {
            responseBody = handlerBody;
        }
        
        SessionHandler sessionHandler = handler.sessionHandler();
        if (sessionHandler != null) {
            createNewSession(request, response, route, sessionHandler);
        }
        
        int handlerStatusCode = handler.statusCode();
        if (handlerStatusCode == -1) {
            throw new RuntimeException("a response status code must be specified");
        }
        response.setCode(handler.statusCode());
        
        sendResponse(response, responseContentType, responseBody);
    }
    
    private Session getSessionIfExists(Request request) {
        
        Cookie cookie = request.getCookie(SESSION_COOKIE_NAME);
        if (cookie != null) {
            String sessionId = cookie.getValue();
            return sessions.get(sessionId);
        }
        return null;
    }
    
    private void createNewSession(Request request, Response response, 
            Route route, SessionHandler sessionHandler) {
        
        Session session = new Session();
        sessionHandler.onCreate(request, route, session);
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, session);
        
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.setCookie(cookie);
    }
    
    private void sendResponse(Response response, String responseContentType, String responseBody) {
        
        try {
            PrintStream body = response.getPrintStream();
            long time = System.currentTimeMillis();
      
            response.setValue("Content-Type", responseContentType);
            response.setValue("Server", "HelloWorld/1.0 (Simple 5.1.4)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
      
            body.println(responseBody);
            body.close();
         } catch(Exception e) {
            throw new RuntimeException(e);
         }
    }
}
