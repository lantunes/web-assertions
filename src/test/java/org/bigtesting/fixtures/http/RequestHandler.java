package org.bigtesting.fixtures.http;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bigtesting.fixtures.http.Route.PathParameterElement;

public class RequestHandler {

    private static final Pattern SESSION_VALUE_PATTERN = Pattern.compile("\\{([^}]*)\\}");
    
    private int statusCode = -1;
    private String contentType;
    private String body;
    private SessionHandler sessionHandler;
    private long delay = -1;
    private TimeUnit delayUnit;
    
    public RequestHandler with(int statusCode, String contentType, String body) {
        
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
        return this;
    }

    public RequestHandler withNewSession(SessionHandler sessionHandler) {
        
        this.sessionHandler = sessionHandler;
        return this;
    }
    
    public RequestHandler after(long delay, TimeUnit delayUnit) {
        
        this.delay = delay;
        this.delayUnit = delayUnit;
        return this;
    }
    
    /*-----------------------------------------*/
    
    int statusCode() {
        return statusCode;
    }

    String contentType() {
        return contentType;
    }

    String body(String path, List<PathParameterElement> pathParams, Session session) {
        
        /* handle any values that start with ':' */
        String responseBody = body;
        String[] pathTokens = RouteHelper.getPathElements(path);
        for (PathParameterElement param : pathParams) {
            responseBody = responseBody.replaceAll(":" + param.name(), pathTokens[param.index()]);
        }
        
        if (session != null) {
            //TODO this should be moved into its own class with tests
            /* 
             * handle any values that are enclosed in '{}'
             * - replacement values can consist of "{}"
             */
            Matcher m = SESSION_VALUE_PATTERN.matcher(responseBody);
            StringBuilder result = new StringBuilder();
            int start = 0;
            while (m.find()) {
                String key = m.group(1);
                Object val = session.get(key);
                if (val != null) {
                    String stringVal = val.toString();
                    result.append(responseBody.substring(start, m.start()));
                    result.append(stringVal);
                    start = m.end();
                }
            }
            result.append(responseBody.substring(start));
            responseBody = result.toString();
        }
        
        return responseBody;
    }
    
    SessionHandler sessionHandler() {
        return sessionHandler;
    }
    
    long delay() {
        return delay;
    }
    
    TimeUnit delayUnit() {
        return delayUnit;
    }
}
