package org.webreformatter.resources;

import java.io.File;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.webreformatter.commons.adapters.CompositeAdapterFactory;
import org.webreformatter.commons.io.IOUtil;
import org.webreformatter.commons.uri.Path;
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
}
