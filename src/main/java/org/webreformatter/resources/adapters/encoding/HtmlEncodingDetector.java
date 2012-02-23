package org.webreformatter.resources.adapters.encoding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kotelnikov
 */
public class HtmlEncodingDetector {

    private Pattern FIRST_PATTERN = Pattern
        .compile("<meta(.*)? http-equiv\\s*=\\s*['\"]\\s*content-type\\s*['\"](.*?)>");

    private Pattern SECOND_PATTERN = Pattern
        .compile("content\\s*=\\s*['\"]\\s*text/html;\\s*charset\\s*=\\s*(.*)?['\"]");

    public String detectEncoding(String str) {
        str = str.toLowerCase();
        String result = null;
        Matcher matcher = FIRST_PATTERN.matcher(str);
        if (matcher.find()) {
            result = getEncoding(matcher.group(1));
            if (result == null) {
                result = getEncoding(matcher.group(2));
            }
        }
        return result;
    }

    private String getEncoding(String str) {
        if (str == null) {
            return null;
        }
        String result = null;
        Matcher matcher = SECOND_PATTERN.matcher(str);
        if (matcher.find()) {
            result = matcher.group(1);
            if (result != null) {
                result = result.trim();
                result = result.toUpperCase();
            }
        }
        return result;
    }
}