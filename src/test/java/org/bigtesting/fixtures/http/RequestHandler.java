package org.bigtesting.fixtures.http;

import java.util.List;

import org.bigtesting.fixtures.http.Route.PathParameterElement;

public class RequestHandler {

    private int statusCode;
    private String contentType;
    private String body;
    
    public void withResponse(int statusCode, String contentType, String body) {
        
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    int statusCode() {
        return statusCode;
    }

    String contentType() {
        return contentType;
    }

    String body(String path, List<PathParameterElement> pathParams) {
        
        String responseBody = body;
        String[] pathTokens = RouteHelper.getPathElements(path);
        for (PathParameterElement param : pathParams) {
            responseBody = responseBody.replaceAll(":" + param.name(), pathTokens[param.index()]);
        }
        return responseBody;
    }
}
