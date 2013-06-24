package org.bigtesting.fixtures.http;

import org.simpleframework.http.Request;

public interface SessionHandler {

    void onCreate(Request request, Route route, Session session);
}
