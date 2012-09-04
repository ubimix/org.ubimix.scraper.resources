/**
 * 
 */
package org.ubimix.resources.adapters.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.w3c.dom.Document;
import org.ubimix.commons.xml.XmlException;
import org.ubimix.commons.xml.XmlWrapper;
import org.ubimix.resources.IContentAdapter;
import org.ubimix.resources.IWrfResource;
import org.ubimix.resources.adapters.encoding.EncodingAdapter;
import org.ubimix.resources.adapters.xml.XmlAdapter;

/**
 * @author kotelnikov
 */
public class HTMLAdapter extends XmlAdapter {

    public HTMLAdapter(IWrfResource instance) {
        super(instance);
    }

    @Override
    protected Document readDocument() throws IOException, XmlException {
        EncodingAdapter mimeTypeAdapter = fResource
            .getAdapter(EncodingAdapter.class);
        String encoding = mimeTypeAdapter.getEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        IContentAdapter content = fResource.getAdapter(IContentAdapter.class);
        InputStream input = content.getContentInput();
        try {
            Reader reader = new InputStreamReader(input, encoding);
            try {
                XmlWrapper wrapper = HTMLUtils.getXmlWrapper(reader);
                return wrapper.getDocument();
            } catch (Exception e) {
                throw XmlWrapper.handleError(
                    "Can not clean up the underlying HTML document.",
                    e);
            }
        } finally {
            input.close();
        }
    }

}
