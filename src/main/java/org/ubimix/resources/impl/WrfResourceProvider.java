/**
 * 
 */
package org.ubimix.resources.impl;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.ubimix.commons.adapters.AdaptableObject;
import org.ubimix.commons.digests.Sha1Digest;
import org.ubimix.commons.uri.Path;
import org.ubimix.commons.uri.UriUtil;
import org.ubimix.resources.IWrfRepository;
import org.ubimix.resources.IWrfResource;
import org.ubimix.resources.IWrfResourceProvider;

/**
 * @author kotelnikov
 */
public class WrfResourceProvider extends AdaptableObject
    implements
    IWrfResourceProvider {

    private Map<Path, WeakReference<IWrfResource>> fCache = new HashMap<Path, WeakReference<IWrfResource>>();

    private WrfResourceRepository fRepository;

    private File fRoot;

    /**
     * @param root
     * @param conf
     */
    public WrfResourceProvider(WrfResourceRepository repository, File root) {
        super(repository.getAdapterFactory());
        fRepository = repository;
        fRoot = root;
        fRoot.mkdirs();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WrfResourceProvider)) {
            return false;
        }
        WrfResourceProvider o = (WrfResourceProvider) obj;
        return fRoot.equals(o.fRoot);
    }

    /**
     * @return the maximal length of the path.
     */
    protected int getMaxPathLength() {
        return 100;
    }

    @Override
    public IWrfRepository getRepository() {
        return fRepository;
    }

    @Override
    public synchronized IWrfResource getResource(Path link, boolean create) {
        IWrfResource result = null;
        WeakReference<IWrfResource> ref = fCache.get(link);
        if (ref != null) {
            result = ref.get();
            if (result == null) {
                fCache.remove(link);
            }
        }
        if (result == null) {
            if (!create) {
                File dir = getResourceDirectory(link);
                create = dir.exists();
            }
            if (create) {
                result = new WrfResource(WrfResourceProvider.this, link);
                ref = new WeakReference<IWrfResource>(result);
                fCache.put(link, ref);
            }
        }
        return result;
    }

    public File getResourceDirectory(Path path) {
        String str = path.getPath(true, false);
        int maxLen = getMaxPathLength();
        if (str.length() > maxLen) {
            str = str.substring(0, maxLen)
                + "/---/"
                + Sha1Digest.builder().update(str).build();
        }
        String escapedLink = UriUtil.toPath(str);
        File rootDir = getRoot();
        File file = new File(rootDir, escapedLink);
        return file;
    }

    public File getRoot() {
        return fRoot;
    }

    @Override
    public int hashCode() {
        return fRoot.hashCode();
    }

    void removeFromCache(Path path) {
        fCache.remove(path);
    }

    @Override
    public String toString() {
        return fRoot.toString();
    }

}
