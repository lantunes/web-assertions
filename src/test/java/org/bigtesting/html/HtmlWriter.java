package org.bigtesting.html;

public class HtmlWriter {

    public static String html(BodyTag body) {
        
        return html(null, body);
    }
    
    public static String html(HeadTag head, BodyTag body) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        if (head != null) {
            sb.append(head.render());
        }
        sb.append(body.render());
        sb.append("</html>");
        
        return sb.toString();
    }
    
    public static BodyTag body(HTMLElement children) {
        return new BodyTag(children);
    }
    
    public static H1Tag h1(String content) {
        return new H1Tag(content);
    }
    
    /*-------------------------------------*/
    
    public static interface HTMLElement {
        String render();
    }
    
    public static class HeadTag implements HTMLElement {

        public String render() {
            // TODO Auto-generated method stub
            return null;
        }
    }
    
    public static class BodyTag implements HTMLElement {

        //TODO this should be a more specific type
        //of element, as per the xhtml schema
        private final HTMLElement[] children;
        
        public BodyTag(HTMLElement ... children) {
            this.children = children;
        }
        
        public String render() {
            
            StringBuilder sb = new StringBuilder();
            sb.append("<body>");
            for (HTMLElement child : children) {
                sb.append(child.render());
            }
            sb.append("</body>");
            return sb.toString();
        }
    }
    
    public static class H1Tag implements HTMLElement {

        private final String content;
        
        public H1Tag(String content) {
            this.content = content;
        }
        
        public String render() {
            return "<h1>" + content + "</h1>";
        }
    }
}
