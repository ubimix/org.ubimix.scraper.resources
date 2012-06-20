/**
 * 
 */
package org.webreformatter.resources.adapters.html;

import java.io.IOException;
import java.io.Reader;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.webreformatter.commons.xml.XmlException;
import org.webreformatter.commons.xml.XmlWrapper;
import org.webreformatter.commons.xml.XmlWrapper.XmlContext;

/**
 * @author kotelnikov
 */
public class HTMLUtils {

    /**
     * @param reader
     * @return
     * @throws Exception
     */
    public static Document cleanupHTML(Reader reader) throws Exception {
        try {
            CleanerProperties properties = getHtmlCleanerProperties();
            HtmlCleaner cleaner = new HtmlCleaner(properties);
            TagNode tag = cleaner.clean(reader);
            DomSerializer serializer = new DomSerializer(properties);
            Document document = serializer.createDOM(tag);
            return document;
        } finally {
            reader.close();
        }
    }

    protected static CleanerProperties getHtmlCleanerProperties() {
        CleanerProperties properties = new CleanerProperties();
        properties.setTranslateSpecialEntities(true);
        properties.setRecognizeUnicodeChars(true);
        properties.setNamespacesAware(false);
        properties.setUseEmptyElementTags(false);
        properties.setAdvancedXmlEscape(true);
        properties.setUseEmptyElementTags(false);
        properties.setOmitUnknownTags(true);
        properties.setTreatUnknownTagsAsContent(false);
        return properties;
    }

    public static XmlWrapper getXmlWrapper(Reader reader)
        throws IOException,
        XmlException {
        XmlContext context = XmlContext.build("", XmlTagSerializer._NS_XHTML);
        return getXmlWrapper(context, reader);
    }

    protected static XmlWrapper getXmlWrapper(
        XmlContext xmlContext,
        Reader reader) throws IOException, XmlException {
        CleanerProperties properties = getHtmlCleanerProperties();
        HtmlCleaner cleaner = new HtmlCleaner(properties);
        TagNode tag = cleaner.clean(reader);
        XmlTagSerializer serializer = new XmlTagSerializer(
            xmlContext,
            properties);
        XmlWrapper wrapper = serializer.createDOM(tag);
        return wrapper;

    }

}
