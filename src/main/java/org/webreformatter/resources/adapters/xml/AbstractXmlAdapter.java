/**
 * 
 */
package org.webreformatter.resources.adapters.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import org.webreformatter.commons.xml.XmlException;
import org.webreformatter.commons.xml.XmlWrapper;
import org.webreformatter.resources.IContentAdapter;
import org.webreformatter.resources.IWrfResource;
import org.webreformatter.resources.WrfResourceAdapter;

/**
 * @author kotelnikov
 */
public abstract class AbstractXmlAdapter extends WrfResourceAdapter {

    private WeakReference<XmlWrapper> fWrapper;

    public AbstractXmlAdapter(IWrfResource resource) {
        super(resource);
    }

    public XmlWrapper applyXSLT(IWrfResource resource)
        throws IOException,
        XmlException {
        XmlWrapper result = applyXSLT(resource, XmlWrapper.class);
        return result;
    }

    public <T extends XmlWrapper> T applyXSLT(
        IWrfResource xslResource,
        Class<T> type) throws IOException, XmlException {
        XmlWrapper wrapper = getWrapper();
        XmlAdapter adapter = xslResource.getAdapter(XmlAdapter.class);
        XmlWrapper xsl = adapter.getWrapper();
        T result = wrapper.applyXSL(xsl, type);
        return result;
    }

    public void applyXSLT(IWrfResource xslResource, IWrfResource targetResource)
        throws Exception {
        IContentAdapter content = targetResource
            .getAdapter(IContentAdapter.class);
        OutputStream out = content.getContentOutput();
        try {
            XmlWrapper wrapper = getWrapper();
            XmlAdapter adapter = xslResource.getAdapter(XmlAdapter.class);
            XmlWrapper xsl = adapter.getWrapper();
            wrapper.applyXSL(xsl, out);
        } finally {
            out.close();
        }
    }

    protected void clearWrapper() {
        fWrapper = null;
    }

    public XmlWrapper getWrapper() throws IOException, XmlException {
        XmlWrapper result = fWrapper != null ? fWrapper.get() : null;
        if (result == null) {
            result = readWrapper();
            fWrapper = new WeakReference<XmlWrapper>(result);
        }
        return result;
    }

    public <T extends XmlWrapper> T getWrapper(Class<T> type)
        throws IOException,
        XmlException {
        XmlWrapper wrapper = getWrapper();
        return wrapper != null ? wrapper.to(type) : null;
    }

    protected abstract XmlWrapper readWrapper()
        throws IOException,
        XmlException;

    public void setDocument(XmlWrapper doc) throws IOException, XmlException {
        IContentAdapter content = fResource.getAdapter(IContentAdapter.class);
        OutputStream output = content.getContentOutput();
        try {
            doc.serializeXML(output, true);
        } finally {
            output.close();
        }
        clearWrapper();
    }

}
