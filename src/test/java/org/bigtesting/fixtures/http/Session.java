package org.bigtesting.fixtures.http;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<String, Object> values = new HashMap<String, Object>();
    
    public Object get(String key) {
        
        return values.get(key);
    }
    
    public void set(String key, Object value) {
        
        values.put(key, value);
    }
}
