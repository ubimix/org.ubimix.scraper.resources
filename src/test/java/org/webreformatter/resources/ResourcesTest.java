package org.webreformatter.resources;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.webreformatter.commons.adapters.CompositeAdapterFactory;
import org.webreformatter.commons.io.IOUtil;
import org.webreformatter.commons.uri.Path;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.commons.uri.UriToPath;
import org.webreformatter.resources.impl.WrfRepositoryUtils;
import org.webreformatter.resources.impl.WrfResourceRepository;

/**
 * @author kotelnikov
 */
public class ResourcesTest extends TestCase {

    protected CompositeAdapterFactory fAdapters = new CompositeAdapterFactory();

    protected WrfResourceRepository fResourceRepository;

    public ResourcesTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        File root = new File("./tmp");
        IOUtil.delete(root);
        fResourceRepository = new WrfResourceRepository(fAdapters, root);
        WrfRepositoryUtils.registerDefaultResourceAdapters(fAdapters);
    }

    public void test() throws Exception {
        Path path = new Path("/abc.txt");
        IWrfResourceProvider resourceProvider = fResourceRepository
            .getResourceProvider("test", true);
        IWrfResource resource = resourceProvider.getResource(path, false);
        assertNull(resource);
        resource = resourceProvider.getResource(path, true);
        assertNotNull(resource);
        IContentAdapter content = resource.getAdapter(IContentAdapter.class);
        assertNotNull(content);
        assertFalse(content.exists());
        OutputStream out = content.getContentOutput();
        try {
            IOUtil.writeString(out, "Hello, there");
        } finally {
            out.close();
        }
        assertTrue(content.exists());
    }

    public void testLongResourceProperties() throws IOException {
        String url = "http://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Charles_Th%C3%A9venin_-_La_prise_de_la_Bastille.jpg/300px-Charles_Th%C3%A9venin_-_La_prise_de_la_Bastille.jpg";
        Uri uri = new Uri(url);
        Path path = UriToPath.getPath(uri);
        IWrfResourceProvider resourceProvider = fResourceRepository
            .getResourceProvider("test", true);
        IWrfResource resource = resourceProvider.getResource(path, true);
        assertNotNull(resource);
        IPropertyAdapter properties = resource
            .getAdapter(IPropertyAdapter.class);
        properties.setProperty("test", "ABC");
        String value = properties.getProperty("test");
        assertEquals("ABC", value);
    }
}
