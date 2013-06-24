package org.bigtesting.fixtures.http;

import java.util.List;

import org.bigtesting.fixtures.http.Route.PathParameterElement;
import org.simpleframework.http.Request;

public class PathParamSessionHandler implements SessionHandler {

    public void onCreate(Request request, Route route, Session session) {
        
        String path = request.getPath().getPath();
        List<PathParameterElement> pathParams = route.pathParameterElements();
        String[] pathTokens = RouteHelper.getPathElements(path);
        
        for (PathParameterElement pathParam : pathParams) {
            session.set(pathParam.name(), pathTokens[pathParam.index()]);
        }
    }
}
