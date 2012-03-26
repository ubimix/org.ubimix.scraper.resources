/**
 * 
 */
package org.webreformatter.resources.impl;

import org.webreformatter.commons.adapters.AdapterFactoryUtils;
import org.webreformatter.commons.adapters.IAdapterRegistry;
import org.webreformatter.resources.IWrfResource;
import org.webreformatter.resources.adapters.FileAdapter;
import org.webreformatter.resources.adapters.cache.CachedResourceAdapter;
import org.webreformatter.resources.adapters.encoding.EncodingAdapter;
import org.webreformatter.resources.adapters.html.HTMLAdapter;
import org.webreformatter.resources.adapters.images.ImageAdapter;
import org.webreformatter.resources.adapters.mime.MimeTypeAdapter;
import org.webreformatter.resources.adapters.string.StringAdapter;
import org.webreformatter.resources.adapters.xml.XmlAdapter;
import org.webreformatter.resources.adapters.zip.ZipAdapter;

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
