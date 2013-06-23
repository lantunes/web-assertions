package org.bigtesting.fixtures.http;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class RouteHelper {

    public static final String PATH_ELEMENT_SEPARATOR = "/";
    public static final String PARAM_PREFIX = ":";
    public static final char CUSTOM_REGEX_START = '<';
    public static final char CUSTOM_REGEX_END = '>';
    /*
     * From the Java API documentation for the Pattern class:
     * 
     * Instances of this (Pattern) class are immutable and are safe for use by 
     * multiple concurrent threads. Instances of the Matcher class are not safe 
     * for such use.
     */
    public static final Pattern CUSTOM_REGEX_PATTERN = Pattern.compile("<[^>]+>");
    /*
     * set of regex special chars to escape
     */
    private static final Set<Character> specialChars = new HashSet<Character>(Arrays.asList(
            '[',']','(',')','{','}','+','*','^','?','$','.','\\'));
    
    public static String[] getPathElements(String path) {
        if (path == null) throw new IllegalArgumentException("path cannot be null");
        path = path.trim();
        if (path.length() == 0) throw new IllegalArgumentException("path cannot be empty");
        return path.substring(1).split(PATH_ELEMENT_SEPARATOR);
    }
    
    public static String escapeNonCustomRegex(String path) {
        /*
         * TODO replace with a regular expression
         */
        StringBuilder sb = new StringBuilder();
        boolean inCustomRegion = false;
        CharacterIterator it = new StringCharacterIterator(path);
        for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
            
            if (ch == CUSTOM_REGEX_START) {
                inCustomRegion = true;
            } else if (ch == CUSTOM_REGEX_END) {
                inCustomRegion = false;
            }
            
            if (specialChars.contains(ch) && !inCustomRegion) {
                sb.append('\\');
            }
            
            sb.append(ch);
        }
        
        return sb.toString();
    }
}
