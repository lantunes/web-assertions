package org.bigtesting.fixtures.http;

import static org.bigtesting.fixtures.http.RouteHelper.*;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private final String resourcePath;
    private final List<PathParameterElement> pathParamElements;
    
    public Route(String paramPath) {
        this.resourcePath = paramPath;
        this.pathParamElements = extractPathParamElements();
    }
    
    private List<PathParameterElement> extractPathParamElements() {
        List<PathParameterElement> elements = new ArrayList<PathParameterElement>();
        String path = CUSTOM_REGEX_PATTERN.matcher(resourcePath).replaceAll("");
        String[] pathElements = getPathElements(path);
        for (int i = 0; i < pathElements.length; i++) {
            String currentElement = pathElements[i];
            if (currentElement.startsWith(PARAM_PREFIX)) {
                currentElement = currentElement.substring(1);
                elements.add(new PathParameterElement(currentElement, i));
            }
        }
        return elements;
    }
    
    public String getResourcePath() {
        return resourcePath;
    }
    
    public String toString() {
        return resourcePath;
    }
    
    public List<PathParameterElement> pathParameterElements() {
        return pathParamElements;            
    }
    
    public int hashCode() {
        int hash = 1;
        hash = hash * 13 + (resourcePath == null ? 0 : resourcePath.hashCode());
        return hash;
    }
    
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Route)) return false;
        Route that = (Route)o;
        return 
                (this.resourcePath == null ? that.resourcePath == null : 
                    this.resourcePath.equals(that.resourcePath));
    }
    
    public static class PathParameterElement {
        private final String name;
        private final int index;
        
        public PathParameterElement(String name, int index) {
            this.name = name;
            this.index = index;
        }
        
        public String name() {
            return name;
        }
        
        public int index() {
            return index;
        }
    }
}
