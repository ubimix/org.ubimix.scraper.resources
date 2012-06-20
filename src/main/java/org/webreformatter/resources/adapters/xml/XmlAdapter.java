/**
 * 
 */
package org.webreformatter.resources.adapters.xml;

import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.webreformatter.commons.xml.XmlException;
import org.webreformatter.commons.xml.XmlWrapper;
import org.webreformatter.commons.xml.XmlWrapper.CompositeNamespaceContext;
import org.webreformatter.commons.xml.XmlWrapper.ElementBasedNamespaceContext;
import org.webreformatter.commons.xml.XmlWrapper.XmlContext;
import org.webreformatter.resources.IContentAdapter;
import org.webreformatter.resources.IContentAdapter.ContentChangeEvent;
import org.webreformatter.resources.IWrfResource;

/**
 * @author kotelnikov
 */
public class XmlAdapter extends AbstractXmlAdapter {

    private CompositeNamespaceContext fNamespaceContext = new CompositeNamespaceContext();

    private ElementBasedNamespaceContext fNamespaceElementContext;

    private XmlContext fXmlContext = XmlContext
        .builder(fNamespaceContext)
        .build();

    public XmlAdapter(IWrfResource instance) {
        super(instance);
    }

    public XmlWrapper getWrapperCopy() throws IOException, XmlException {
        return getWrapperCopy(XmlWrapper.class);
    }

    public <T extends XmlWrapper> T getWrapperCopy(Class<T> type)
        throws IOException,
        XmlException {
        XmlWrapper wrapper = getWrapper();
        return wrapper.newCopy(type);
    }

    /**
     * @see org.webreformatter.resources.WrfResourceAdapter#handleEvent(java.lang.Object)
     */
    @Override
    public void handleEvent(Object event) {
        synchronized (this) {
            if (event instanceof ContentChangeEvent) {
                updateDocumentNamespaceContext(null);
                clearWrapper();
            }
        }
    }

    protected Document readDocument() throws IOException, XmlException {
        IContentAdapter content = fResource.getAdapter(IContentAdapter.class);
        if (!content.exists()) {
            return null;
        }
        InputStream input = content.getContentInput();
        try {
            return XmlWrapper.readXML(input);
        } finally {
            input.close();
        }
    }

    @Override
    protected XmlWrapper readWrapper() throws IOException, XmlException {
        XmlWrapper result = null;
        Document doc = readDocument();
        if (doc != null) {
            updateDocumentNamespaceContext(doc);
            result = new XmlWrapper(doc, fXmlContext);
        }
        return result;
    }

    public void updateDocumentNamespaceContext(Document doc) {
        if (fNamespaceElementContext != null) {
            fNamespaceContext.removeContext(fNamespaceElementContext);
            fNamespaceElementContext = null;
        }
        if (doc != null) {
            fNamespaceElementContext = new ElementBasedNamespaceContext(doc);
            fNamespaceContext.addContext(fNamespaceElementContext);
        }
    }

}
