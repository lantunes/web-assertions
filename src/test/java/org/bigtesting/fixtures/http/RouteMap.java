package org.bigtesting.fixtures.http;

public interface RouteMap {

    void add(Route route);
    
    Route getRoute(String path);
}
