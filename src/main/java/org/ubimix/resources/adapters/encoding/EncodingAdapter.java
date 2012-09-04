/**
 * 
 */
package org.ubimix.resources.adapters.encoding;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ubimix.commons.adapters.IAdapterFactory;
import org.ubimix.resources.IContentAdapter;
import org.ubimix.resources.IContentAdapter.ContentChangeEvent;
import org.ubimix.resources.IPropertyAdapter;
import org.ubimix.resources.IPropertyAdapter.PropertiesChangeEvent;
import org.ubimix.resources.IWrfResource;
import org.ubimix.resources.WrfResourceAdapter;
import org.ubimix.server.encoding.EncodingDetector;
import org.ubimix.server.encoding.IEncodingDetector;

/**
 * @author kotelnikov
 */
public class EncodingAdapter extends WrfResourceAdapter {
    private static Pattern CHARSET_REGEX = Pattern.compile(
        "^.*charset\\s*=\\s*([\\w\\d-]+).*$",
        Pattern.CASE_INSENSITIVE);

    private static HtmlEncodingDetector fEncodingDetector = new HtmlEncodingDetector();

    public static IAdapterFactory getAdapterFactory() {
        final IEncodingDetector detector = new EncodingDetector();
        return new IAdapterFactory() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T getAdapter(Object instance, Class<T> type) {
                if (!(instance instanceof IWrfResource)
                    || type != EncodingAdapter.class) {
                    return null;
                }
                return (T) new EncodingAdapter(
                    detector,
                    (IWrfResource) instance);
            }
        };
    }

    private IEncodingDetector fDetector;

    private String fEncoding;

    protected EncodingAdapter(IEncodingDetector detector, IWrfResource instance) {
        super(instance);
        fDetector = detector;
    }

    public synchronized String getEncoding() throws IOException {
        if (fEncoding == null) {
            IPropertyAdapter propertiesAdapter = fResource
                .getAdapter(IPropertyAdapter.class);
            String contentType = propertiesAdapter.getProperty("Content-Type");
            if (contentType != null) {
                Matcher matcher = CHARSET_REGEX.matcher(contentType);
                if (matcher.matches()) {
                    fEncoding = matcher.group(1);
                }
            }
            if (fEncoding == null) {
                IContentAdapter content = fResource
                    .getAdapter(IContentAdapter.class);
                if (contentType.startsWith("text/html")) {
                    // Get the encoding from the content
                    byte[] buf = new byte[1024 * 100];
                    InputStream input = content.getContentInput();
                    try {
                        int len = input.read(buf);
                        String str = new String(buf, 0, len);
                        fEncoding = fEncodingDetector.detectEncoding(str);
                    } finally {
                        input.close();
                    }
                }
                if (fEncoding == null) {
                    InputStream input = content.getContentInput();
                    try {
                        if (!input.markSupported()) {
                            input = new BufferedInputStream(input);
                        }
                        fEncoding = fDetector.getEncoding(input);
                    } finally {
                        input.close();
                    }
                }
            }
        }
        return fEncoding;
    }

    @Override
    public void handleEvent(Object event) {
        if (event instanceof PropertiesChangeEvent
            || event instanceof ContentChangeEvent) {
            fEncoding = null;
        }
    }

}
