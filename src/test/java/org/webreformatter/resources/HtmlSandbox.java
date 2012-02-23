/**
 * 
 */
package org.webreformatter.resources;

import java.io.File;
import java.io.StringReader;

import org.w3c.dom.Document;
import org.webreformatter.commons.io.IOUtil;
import org.webreformatter.commons.xml.XmlWrapper;
import org.webreformatter.resources.adapters.html.HTMLUtils;

/**
 * @author kotelnikov
 */
public class HtmlSandbox {

    public static void main(String[] args) throws Exception {
        String html = IOUtil.readString(new File("./tmp/test.html"));
        Document doc = HTMLUtils.cleanupHTML(new StringReader(html));
        XmlWrapper wrapper = new XmlWrapper(doc);
        IOUtil.writeString(new File("./tmp/test.xml"), wrapper.toString());
    }

    /**
     * 
     */
    public HtmlSandbox() {
        // TODO Auto-generated constructor stub
    }

}
