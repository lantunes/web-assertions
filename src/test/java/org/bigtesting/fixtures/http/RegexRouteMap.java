package org.bigtesting.fixtures.http;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class RegexRouteMap implements RouteMap {

private final Set<RegexRoute> routes = new HashSet<RegexRoute>();
    
    public void add(Route route) {
        routes.add(new RegexRoute(route));
    }
    
    public Route getRoute(String path) {
        
        for (RegexRoute route : routes) {
            Matcher m = route.pattern().matcher(path);
            if (m.find()) {
                return route.getRoute();
            }
        }
        
        return null;
    }
}
