/**
 * 
 */
package org.ubimix.resources.impl;

import org.ubimix.commons.adapters.AdapterFactoryUtils;
import org.ubimix.commons.adapters.IAdapterRegistry;
import org.ubimix.resources.IWrfResource;
import org.ubimix.resources.adapters.FileAdapter;
import org.ubimix.resources.adapters.cache.CachedResourceAdapter;
import org.ubimix.resources.adapters.encoding.EncodingAdapter;
import org.ubimix.resources.adapters.html.HTMLAdapter;
import org.ubimix.resources.adapters.images.ImageAdapter;
import org.ubimix.resources.adapters.mime.MimeTypeAdapter;
import org.ubimix.resources.adapters.string.StringAdapter;
import org.ubimix.resources.adapters.xml.XmlAdapter;
import org.ubimix.resources.adapters.zip.ZipAdapter;

/**
 * @author kotelnikov
 */
public class WrfRepositoryUtils {

    public static void registerDefaultResourceAdapters(
        IAdapterRegistry adapterRegistry) {
        registerResourceAdapter(adapterRegistry, CachedResourceAdapter.class);
        registerResourceAdapter(adapterRegistry, FileAdapter.class);
        registerResourceAdapter(adapterRegistry, XmlAdapter.class);
        registerResourceAdapter(adapterRegistry, EncodingAdapter.class);
        registerResourceAdapter(adapterRegistry, HTMLAdapter.class);
        registerResourceAdapter(adapterRegistry, MimeTypeAdapter.class);
        registerResourceAdapter(adapterRegistry, StringAdapter.class);
        registerResourceAdapter(adapterRegistry, ImageAdapter.class);
        registerResourceAdapter(adapterRegistry, ZipAdapter.class);
    }

    public static void registerResourceAdapter(
        IAdapterRegistry adapterRegistry,
        Class<?> adapterType) {
        registerResourceAdapter(adapterRegistry, adapterType, adapterType);
    }

    public static void registerResourceAdapter(
        IAdapterRegistry adapterRegistry,
        Class<?> adapterInterface,
        Class<?> adapterImpl) {
        AdapterFactoryUtils.registerAdapter(
            adapterRegistry,
            IWrfResource.class,
            adapterInterface,
            adapterImpl);
    }

    /**
     * 
     */
    public WrfRepositoryUtils() {
    }
}
