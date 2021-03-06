/**
 * 
 */
package org.ubimix.resources.adapters.zip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.ubimix.commons.uri.Path;
import org.ubimix.resources.IContentAdapter;
import org.ubimix.resources.IWrfResource;
import org.ubimix.resources.IWrfResourceProvider;
import org.ubimix.resources.WrfResourceAdapter;

/**
 * @author kotelnikov
 */
public class ZipAdapter extends WrfResourceAdapter {

    private static void addToZip(
        ZipBuilder builder,
        Iterable<Map.Entry<Path, IWrfResource>> entries,
        boolean pack) throws IOException {
        if (entries == null) {
            return;
        }
        for (Map.Entry<Path, IWrfResource> entry : entries) {
            Path path = entry.getKey();
            IWrfResource resource = entry.getValue();
            if (resource != null) {
                IContentAdapter resourceContentAdapter = resource
                    .getAdapter(IContentAdapter.class);
                InputStream input = resourceContentAdapter.exists()
                    ? resourceContentAdapter.getContentInput()
                    : new ByteArrayInputStream(new byte[0]);
                if (pack) {
                    builder.addEntry(path.toString(), input);
                } else {
                    builder.addStoredEntry(path.toString(), input);
                }
            }
        }
    }

    public static void zip(
        Iterable<Map.Entry<Path, IWrfResource>> storedResources,
        Iterable<Map.Entry<Path, IWrfResource>> packedResources,
        OutputStream output) throws IOException {
        try {
            ZipBuilder builder = new ZipBuilder(output);
            try {
                addToZip(builder, storedResources, false);
                addToZip(builder, packedResources, true);
            } finally {
                builder.close();
            }
        } finally {
            output.close();
        }
    }

    public static void zip(
        Iterable<Map.Entry<Path, IWrfResource>> resources,
        OutputStream output) throws IOException {
        zip(null, resources, output);
    }

    public ZipAdapter(IWrfResource instance) {
        super(instance);
    }

    /**
     * This method zip all resources referenced by the specified in this
     * resource. Keys of the given map contains logical paths of entries in the
     * resulting zip, and the corresponding values represents paths of resources
     * to compress.
     * 
     * @param resourceMapping
     * @throws IOException
     */
    public void zip(Map<Path, Path> resourceMapping) throws IOException {
        IWrfResourceProvider provider = fResource.getProvider();
        Map<Path, IWrfResource> resources = new HashMap<Path, IWrfResource>();
        for (Map.Entry<Path, Path> entry : resourceMapping.entrySet()) {
            Path key = entry.getKey();
            Path resourcePath = entry.getValue();
            IWrfResource resource = provider.getResource(resourcePath, false);
            if (resource != null) {
                resources.put(key, resource);
            }
        }
        zipResources(resources.entrySet());
    }

    /**
     * This method compress all resources given in the specified map in this
     * resource. Keys of the given map contains logical paths of entries to
     * compress, and the corresponding values represents compressed resources.
     * 
     * @param resourceMapping
     * @throws IOException
     */
    public void zipResources(Iterable<Map.Entry<Path, IWrfResource>> resources)
        throws IOException {
        IContentAdapter content = fResource.getAdapter(IContentAdapter.class);
        OutputStream output = content.getContentOutput();
        zip(resources, output);
    }

}
