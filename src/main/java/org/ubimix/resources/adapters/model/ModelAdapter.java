/**
 * 
 */
package org.ubimix.resources.adapters.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.ubimix.commons.adapters.IAdapterFactory;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.UnboundedCharStream;
import org.ubimix.commons.parser.html.HtmlParser;
import org.ubimix.commons.parser.json.IJsonParser;
import org.ubimix.commons.parser.json.JsonParser;
import org.ubimix.commons.parser.json.utils.JavaObjectBuilder;
import org.ubimix.commons.parser.stream.StreamCharLoader;
import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XmlParser;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.ModelObject;
import org.ubimix.model.xml.XmlBuilder;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.resources.IContentAdapter;
import org.ubimix.resources.IWrfResource;
import org.ubimix.resources.WrfResourceAdapter;
import org.ubimix.resources.adapters.encoding.EncodingAdapter;

/**
 * @author kotelnikov
 */
public class ModelAdapter extends WrfResourceAdapter {

    /**
     * Returns a new adapter factory.
     * 
     * @return a new adapter factory
     */
    public static IAdapterFactory getAdapterFactory() {
        return new IAdapterFactory() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T getAdapter(Object instance, Class<T> type) {
                if (type != ModelAdapter.class) {
                    return null;
                }
                return (T) new ModelAdapter((IWrfResource) instance);
            }
        };
    }

    /**
     * The default constructor initializing the internal fields.
     * 
     * @param instance the resource instance to set
     */
    public ModelAdapter(IWrfResource instance) {
        super(instance);
    }

    protected ICharStream getCharStream(Reader reader) {
        StreamCharLoader loader = new StreamCharLoader(reader);
        ICharStream stream = new UnboundedCharStream(loader);
        return stream;
    }

    protected IXmlParser getHtmlParser() {
        return new HtmlParser();
    }

    private Reader getReader() throws IOException {
        boolean ok = false;
        InputStream input = null;
        try {
            EncodingAdapter encodingAdapter = fResource
                .getAdapter(EncodingAdapter.class);
            String encoding = encodingAdapter.getEncoding();
            if (encoding == null) {
                encoding = "ISO-8859-1";
            }
            IContentAdapter contentAdapter = fResource
                .getAdapter(IContentAdapter.class);
            input = contentAdapter.getContentInput();
            InputStreamReader reader = new InputStreamReader(input, encoding);
            ok = true;
            return reader;
        } finally {
            if (!ok && input != null) {
                input.close();
            }
        }
    }

    protected IXmlParser getXmlParser() {
        return new XmlParser();
    }

    /**
     * Interprets the stored resource as an HTML document and returns the
     * {@link XmlElement} corresponding to this document.
     * 
     * @return a root element of the XML document
     * @throws IOException
     */
    public XmlElement readHtml() throws IOException {
        IXmlParser parser = getHtmlParser();
        return readXml(parser);
    }

    public ModelObject readJson() throws IOException {
        return readJson(ModelObject.FACTORY);
    }

    public <T extends ModelObject> T readJson(IValueFactory<T> factory)
        throws IOException {
        Reader reader = getReader();
        try {
            ICharStream stream = getCharStream(reader);
            JavaObjectBuilder builder = new JavaObjectBuilder();
            IJsonParser parser = new JsonParser();
            parser.parse(stream, builder);
            Object top = builder.getTop();
            T result = factory.newValue(top);
            return result;
        } finally {
            reader.close();
        }
    }

    /**
     * Interprets the stored resource as an XML document and returns the
     * {@link XmlElement} corresponding to this document.
     * 
     * @return a root element of the XML document
     * @throws IOException
     */
    public XmlElement readXml() throws IOException {
        IXmlParser parser = getXmlParser();
        return readXml(parser);
    }

    private XmlElement readXml(IXmlParser parser)
        throws IOException,
        UnsupportedEncodingException {
        Reader reader = getReader();
        try {
            XmlBuilder builder = new XmlBuilder();
            ICharStream stream = getCharStream(reader);
            parser.parse(stream, builder);
            XmlElement result = builder.getResult();
            return result;
        } finally {
            reader.close();
        }
    }
}
