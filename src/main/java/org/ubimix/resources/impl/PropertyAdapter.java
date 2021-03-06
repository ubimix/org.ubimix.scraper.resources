/**
 * 
 */
package org.ubimix.resources.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ubimix.commons.io.IOUtil;
import org.ubimix.resources.IPropertyAdapter;
import org.ubimix.resources.WrfResourceAdapter;

/**
 * @author kotelnikov
 */
public class PropertyAdapter extends WrfResourceAdapter
    implements
    IPropertyAdapter {

    private Properties fProperties;

    public PropertyAdapter(WrfResource instance) {
        super(instance);
    }

    private synchronized void checkProperties() throws IOException {
        if (fProperties != null) {
            return;
        }
        fProperties = new Properties();
        File file = getPropertiesFile();
        if (file.exists() && file.isFile()) {
            FileInputStream input = new FileInputStream(file);
            try {
                fProperties.load(input);
            } finally {
                input.close();
            }
        }
    }

    @Override
    public synchronized Map<String, String> getProperties() throws IOException {
        checkProperties();
        Map<String, String> result = new HashMap<String, String>();
        for (Object key : fProperties.keySet()) {
            if (key instanceof String) {
                String k = (String) key;
                String value = fProperties.getProperty(k);
                result.put(k, value);
            }
        }
        return result;
    }

    private File getPropertiesFile() {
        File file = getResource().getResourceFile("data.properties");
        return file;
    }

    /**
     * @see org.ubimix.resources.IWrfResource#getProperty(java.lang.String)
     */
    @Override
    public synchronized String getProperty(final String key) throws IOException {
        checkProperties();
        return (String) fProperties.get(key);
    }

    @Override
    public synchronized Set<String> getPropertyKeys() throws IOException {
        checkProperties();
        Set<String> result = new HashSet<String>();
        for (Object key : fProperties.keySet()) {
            if (key instanceof String) {
                String k = (String) key;
                result.add(k);
            }
        }
        return result;
    }

    @Override
    public WrfResource getResource() {
        return (WrfResource) super.getResource();
    }

    public synchronized void remove() {
        File file = getPropertiesFile();
        IOUtil.delete(file);
    }

    private synchronized void saveProperties()
        throws FileNotFoundException,
        IOException {
        File file = getPropertiesFile();
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        try {
            fProperties.store(out, "");
            fResource.notifyAdapters(new PropertiesChangeEvent());
        } finally {
            out.close();
        }
    }

    @Override
    public synchronized void setProperties(Map<String, String> properties)
        throws IOException {
        checkProperties();
        fProperties.clear();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            fProperties.put(key, value);
        }
        saveProperties();
    }

    /**
     * @see org.ubimix.resources.IWrfResource#setProperty(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public synchronized void setProperty(final String key, final String value)
        throws IOException {
        checkProperties();
        fProperties.put(key, value);
        saveProperties();
    }

}
