/**
 * 
 */
package org.ubimix.resources.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.ubimix.commons.adapters.AdaptableObject;
import org.ubimix.commons.adapters.AdapterFactoryUtils;
import org.ubimix.commons.adapters.CompositeAdapterFactory;
import org.ubimix.commons.adapters.IAdapterFactory;
import org.ubimix.commons.adapters.IAdapterRegistry;
import org.ubimix.commons.io.IOUtil;
import org.ubimix.resources.IContentAdapter;
import org.ubimix.resources.IHierarchyAdapter;
import org.ubimix.resources.IPropertyAdapter;
import org.ubimix.resources.IWrfRepository;
import org.ubimix.resources.IWrfResource;
import org.ubimix.resources.IWrfResourceProvider;

/**
 * @author kotelnikov
 */
public class WrfResourceRepository extends AdaptableObject
    implements
    IWrfRepository {

    public static IWrfRepository newRepository(File root, boolean reset) {
        CompositeAdapterFactory adaptersFactory = new CompositeAdapterFactory();
        if (reset) {
            IOUtil.delete(root);
        }
        WrfRepositoryUtils.registerDefaultResourceAdapters(adaptersFactory);
        IWrfRepository repo = new WrfResourceRepository(adaptersFactory, root);
        return repo;
    }

    private IAdapterRegistry fAdapterRegistry;

    private Map<String, IWrfResourceProvider> fCache = new HashMap<String, IWrfResourceProvider>();

    private File fRoot;

    public WrfResourceRepository(
        IAdapterRegistry adapterRegistry,
        IAdapterFactory adapterFactory,
        File root) {
        super(adapterFactory);
        fAdapterRegistry = adapterRegistry;
        fRoot = root;
        initDefaultAdapters();
    }

    public <T extends IAdapterRegistry & IAdapterFactory> WrfResourceRepository(
        T adapters,
        File root) {
        this(adapters, adapters, root);
    }

    @Override
    public IWrfResourceProvider getResourceProvider(String key, boolean create) {
        synchronized (fCache) {
            IWrfResourceProvider provider = fCache.get(key);
            if (provider == null) {
                File dir = getResourceProviderDir(key);
                if (dir.exists() || create) {
                    provider = new WrfResourceProvider(this, dir);
                    fCache.put(key, provider);
                }
            }
            return provider;
        }
    }

    private File getResourceProviderDir(String key) {
        return new File(fRoot, key);
    }

    protected void initDefaultAdapters() {
        registerResourceAdapter(IHierarchyAdapter.class, HierarchyAdapter.class);
        registerResourceAdapter(IContentAdapter.class, ContentAdapter.class);
        registerResourceAdapter(IPropertyAdapter.class, PropertyAdapter.class);
    }

    public void registerResourceAdapter(Class<?> adapterType) {
        registerResourceAdapter(adapterType, adapterType);
    }

    public void registerResourceAdapter(
        Class<?> adapterInterface,
        Class<?> adapterImpl) {
        AdapterFactoryUtils.registerAdapter(
            fAdapterRegistry,
            IWrfResource.class,
            adapterInterface,
            adapterImpl);
    }

    public void registerResourceAdapter(
        Class<?> adapterType,
        IAdapterFactory adapterFactory) {
        fAdapterRegistry.registerAdapterFactory(
            adapterFactory,
            IWrfResource.class);
    }

}
