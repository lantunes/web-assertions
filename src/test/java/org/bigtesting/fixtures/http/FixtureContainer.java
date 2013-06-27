package org.bigtesting.fixtures.http;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixtureContainer implements Container {
    
    private static final Logger logger = LoggerFactory.getLogger(FixtureContainer.class);
    
    private static final String SESSION_COOKIE_NAME = "Simple-Session";

    private final Map<HandlerKey, RequestHandler> handlerMap = 
            new HashMap<HandlerKey, RequestHandler>();
    
    private final RouteMap routeMap = new RegexRouteMap();
    
    private final Map<String, Session> sessions = new HashMap<String, Session>();
    
    private final Executor asyncExecutor;
    
    private Set<HandlerKey> uponHandlers = new HashSet<HandlerKey>();
    
    public FixtureContainer(int aysncThreadPoolSize) {
        asyncExecutor = Executors.newFixedThreadPool(aysncThreadPoolSize);
    }
    
    public HandlerKey addHandler(RequestHandler handler, 
            Method method, String resource) {
        
        return addHandler(handler, method, resource, null);
    }
    
    public HandlerKey addHandler(RequestHandler handler, 
            Method method, String resource, String contentType) {
        
        Route route = new Route(resource);
        HandlerKey key = new HandlerKey(method.name(), route, contentType);
        handlerMap.put(key, handler);
        routeMap.add(route);
        return key;
    }
    
    public void handle(Request request, Response response) {

        String responseContentType = "text/plain";
        String responseBody = "";
        
        String method = request.getMethod();
        String path = request.getPath().getPath();
        ContentType requestContentType = request.getContentType();
        
        /* get the route and the handler for this request */
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
        
        /////////Upon handling
        
        if (uponHandlers.contains(key)) {
            //TODO notify suspended response with path
            //continue through handling this request
        }
        
        Upon upon = handler.upon();
        if (upon != null) {
            RequestHandler uponHandler = 
                    new RequestHandler().with(200, "text/plain", "");
            HandlerKey uponKey = addHandler(uponHandler, upon.getMethod(), 
                    upon.getResource(), upon.getContentType());
            uponHandlers.add(uponKey);            
        }
        
        ////////////
        
        /* set the content type */
        String handlerContentType = handler.contentType();
        if (handlerContentType != null && 
                handlerContentType.trim().length() != 0) {
            
            responseContentType = handlerContentType;
        }
        
        /* set the response body */
        Session session = getSessionIfExists(request);
        String handlerBody = handler.body(path, route.pathParameterElements(), session);
        if (handlerBody != null && handlerBody.trim().length() != 0) {
            responseBody = handlerBody;
        }
        
        /* create a new session if required */
        SessionHandler sessionHandler = handler.sessionHandler();
        if (sessionHandler != null) {
            createNewSession(request, response, route, sessionHandler);
        }
        
        /* set the response status code */
        int handlerStatusCode = handler.statusCode();
        if (handlerStatusCode == -1) {
            throw new RuntimeException("a response status code must be specified");
        }
        response.setCode(handler.statusCode());
        
        /* handle the response */
        if (handler.isAsync()) {
            doAsync(response, handler, responseContentType, responseBody);
        } else {
            sendAndCommitResponse(response, responseContentType, responseBody);
        }
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
    
    private void doAsync(Response response, RequestHandler handler, 
            String responseContentType, String responseBody) {
        
        AsyncTask task = new AsyncTask(response, handler, 
                responseContentType, responseBody);
        asyncExecutor.execute(task);
    }
    
    private void sendAndCommitResponse(Response response, 
            String responseContentType, String responseBody) {
        
        try {
            
            PrintStream body = 
                    sendResponse(response, responseContentType, responseBody);
            body.close();
            
         } catch(Exception e) {
            throw new RuntimeException(e);
         }
    }

    private PrintStream sendResponse(Response response, 
            String responseContentType, String responseBody)
            throws IOException {
        
        PrintStream body = response.getPrintStream();
        addStandardHeaders(response, responseContentType);
        body.println(responseBody);
        body.flush();
        return body;
    }

    private void addStandardHeaders(Response response, String responseContentType) {
        
        long time = System.currentTimeMillis();
        response.setValue("Content-Type", responseContentType);
        response.setValue("Server", "HelloWorld/1.0 (Simple 5.1.4)");
        response.setDate("Date", time);
        response.setDate("Last-Modified", time);
    }
    
    private final class AsyncTask implements Runnable {

        private Response response; 
        private RequestHandler handler;
        private String responseContentType; 
        private String responseBody;
        
        public AsyncTask(Response response, RequestHandler handler, 
                String responseContentType, String responseBody) {
            
            this.response = response;
            this.handler = handler;
            this.responseContentType = responseContentType;
            this.responseBody = responseBody;
        }

        public void run() {
            
            delayIfRequired(handler);
            
            if (handler.isSuspend()) {
                
                //TODO suspend this response and send content upon notification
                
            } else {
            
                long period = handler.period();
                if (period > -1) {
                    respondPeriodically(period);
                } else {
                    sendAndCommitResponse(response, responseContentType, responseBody);
                }
            }
        }
        
        private void delayIfRequired(RequestHandler handler) {
            
            long delay = handler.delay();
            if (delay > -1) {
                
                try {
                    
                    TimeUnit delayUnit = handler.delayUnit();
                    long delayInMillis = delayUnit.toMillis(delay);
                    Thread.sleep(delayInMillis);
                    
                } catch (Exception e) {
                    throw new RuntimeException("error delaying response", e);
                }
            }
        }

        private void respondPeriodically(long period) {
            
            TimeUnit periodUnit = handler.periodUnit();
            long periodInMillis = periodUnit.toMillis(period);
            final int times = handler.periodTimes();
            final Timer timer = new Timer("ServerFixtureTimer", true);
            timer.scheduleAtFixedRate(new TimerTask() {
                
                private int count = 0;
                
                @Override
                public void run() {
                    try {
                        
                        if (times > -1 && count >= times) {
                            timer.cancel();
                            timer.purge();
                            response.getPrintStream().close();
                            return;
                        }
                        
                        sendResponse(response, responseContentType, responseBody);
                        
                        count++;
                        
                    } catch (Exception e) {
                        logger.error("error sending async response at fixed rate", e);
                    }
                }
            }, 0, periodInMillis);
        }
    }
}
