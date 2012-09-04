package org.ubimix.resources.adapters.cache;

import java.io.IOException;
import java.util.Map;

import org.ubimix.resources.IContentAdapter;
import org.ubimix.resources.IPropertyAdapter;
import org.ubimix.resources.IWrfResource;
import org.ubimix.resources.WrfResourceAdapter;

/**
 * @author kotelnikov
 */
public class CachedResourceAdapter extends WrfResourceAdapter {

    private static long fExpirationTimeout = (long) DateUtil.DAY * 1;

    private static long fRefreshTimeout = (long) DateUtil.MIN * 5;

    private static final String PROPERTY_LAST_LOADED = "Last-Loaded";

    private static final String PROPERTY_LAST_MODIFIED = "Last-Modified";

    private static final String PROPERTY_STATUS_CODE = "StatusCode";

    /**
     * Sets a new expiration timeout for the "Last-Modified" field.
     * 
     * @param timeout
     */
    public static void setExpirationTimeout(long timeout) {
        fExpirationTimeout = timeout;
    }

    /**
     * Sets the refresh timeout. This is the minimal timeout between two access
     * to the remote resource.
     * 
     * @param MinRefreshTimeout
     */
    public static void setRefreshTimeout(long MinRefreshTimeout) {
        fRefreshTimeout = MinRefreshTimeout;
    }

    public CachedResourceAdapter(IWrfResource resource) {
        super(resource);
    }

    public void checkLastModified() throws IOException {
        long lastModified = getLastModified();
        if (lastModified < 0) {
            long now = now();
            setLastModified(now);
        }
    }

    public void copyPropertiesFrom(CachedResourceAdapter from)
        throws IOException {
        copyPropertiesFrom(from.getResource());
    }

    public void copyPropertiesFrom(IPropertyAdapter from) throws IOException {
        IPropertyAdapter to = fResource.getAdapter(IPropertyAdapter.class);
        Map<String, String> properties = from.getProperties();
        to.setProperties(properties);
    }

    public void copyPropertiesFrom(IWrfResource resource) throws IOException {
        IPropertyAdapter from = resource.getAdapter(IPropertyAdapter.class);
        copyPropertiesFrom(from);
    }

    protected long getExpirationTimeout() {
        return fExpirationTimeout;
    }

    public long getLastLoaded() throws IOException {
        return getTime(PROPERTY_LAST_LOADED);
    }

    public long getLastModified() throws IOException {
        return getTime(PROPERTY_LAST_MODIFIED);
    }

    /**
     * Returns the minimal timeout between two resource downloads. This timeout
     * is used to avoid too frequent resource downloads.
     * 
     * @return minimal timeout between two resource downloads
     */
    protected long getRefreshTimeout() {
        return fRefreshTimeout;
    }

    public int getStatusCode() throws IOException {
        IPropertyAdapter properties = fResource
            .getAdapter(IPropertyAdapter.class);
        String statusCode = properties.getProperty(PROPERTY_STATUS_CODE);
        int code = 500;
        if (statusCode != null) {
            try {
                code = Integer.parseInt(statusCode.trim());
            } catch (Exception e) {
            }
        }
        return code;
    }

    private long getTime(IPropertyAdapter resource, String string)
        throws IOException {
        String str = resource.getProperty(string);
        return str != null ? DateUtil.parseDate(str) : -1;
    }

    private long getTime(String propertyName) throws IOException {
        IPropertyAdapter properties = fResource
            .getAdapter(IPropertyAdapter.class);
        long lastModified = getTime(properties, propertyName);
        return lastModified;
    }

    public synchronized boolean isExpired() throws IOException {
        IContentAdapter content = fResource.getAdapter(IContentAdapter.class);
        boolean expired = !content.exists();
        if (expired) {
            return true;
        }
        expired = true;
        long lastModified = getLastModified();
        if (lastModified > 0) {
            long now = now();
            long delta = now - lastModified;
            long expirationTimeout = getExpirationTimeout();
            expired = (delta >= expirationTimeout);
            if (expired) {
                long lastLoaded = getLastLoaded();
                delta = now - lastLoaded;
                long refreshTimeout = getRefreshTimeout();
                expired = (delta > refreshTimeout);
            }
        }
        return expired;
    }

    protected long now() {
        return System.currentTimeMillis();
    }

    public void setLastLoaded(long now) throws IOException {
        IPropertyAdapter propertyAdapter = fResource
            .getAdapter(IPropertyAdapter.class);
        propertyAdapter.setProperty(
            PROPERTY_LAST_LOADED,
            DateUtil.formatDate(now));
    }

    public void setLastModified(long now) throws IOException {
        IPropertyAdapter propertyAdapter = fResource
            .getAdapter(IPropertyAdapter.class);
        propertyAdapter.setProperty(
            PROPERTY_LAST_MODIFIED,
            DateUtil.formatDate(now));
    }

    public void setStatusCode(int statusCode) throws IOException {
        IPropertyAdapter properties = fResource
            .getAdapter(IPropertyAdapter.class);
        properties.setProperty(PROPERTY_STATUS_CODE, statusCode + "");
    }

    public void updateLastLoaded() throws IOException {
        long now = now();
        setLastLoaded(now);
    }

    public void updateMetadataFrom(CachedResourceAdapter adapter)
        throws IOException {
        long lastModified = adapter.getLastModified();
        setLastModified(lastModified);
    }

    public void updateMetadataFrom(IWrfResource resource) throws IOException {
        updateMetadataFrom(resource.getAdapter(CachedResourceAdapter.class));
    }
}
