/**
 * 
 */
package org.ubimix.resources.adapters.string;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ubimix.commons.io.IOUtil;
import org.ubimix.resources.IContentAdapter;
import org.ubimix.resources.IWrfResource;
import org.ubimix.resources.WrfResourceAdapter;

/**
 * This adapter is used to represent a resource as a string.
 * 
 * @author kotelnikov
 */
public class StringAdapter extends WrfResourceAdapter {

    public StringAdapter(IWrfResource instance) {
        super(instance);
    }

    public String getContentAsString() throws IOException {
        IContentAdapter contentAdapter = fResource
            .getAdapter(IContentAdapter.class);
        if (!contentAdapter.exists()) {
            return null;
        }
        InputStream input = contentAdapter.getContentInput();
        try {
            return IOUtil.readString(input);
        } finally {
            input.close();
        }
    }

    public void setContentAsString(String content) throws IOException {
        IContentAdapter contentAdapter = fResource
            .getAdapter(IContentAdapter.class);
        OutputStream output = contentAdapter.getContentOutput();
        IOUtil.writeString(output, content);

    }

}
