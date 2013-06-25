package org.bigtesting.fixtures.http;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bigtesting.fixtures.http.Route.PathParameterElement;

public class RequestHandler {

    private static final Pattern SESSION_VALUE_PATTERN = Pattern.compile("\\{([^}]*)\\}");
    
    private int statusCode = -1;
    private String contentType;
    private String body;
    
    private SessionHandler sessionHandler;
    
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
            /* handle any values that are enclosed in '{}' */
            Matcher m = SESSION_VALUE_PATTERN.matcher(responseBody);
            while (m.find()) {
                
                String key = m.group(1);
                Object val = session.get(key);
                if (val != null) {
                    String stringVal = val.toString();
                    responseBody = responseBody.replaceFirst(SESSION_VALUE_PATTERN.pattern(), stringVal);
                }
            }
        }
        
        return responseBody;
    }
    
    SessionHandler sessionHandler() {
        return sessionHandler;
    }
}
