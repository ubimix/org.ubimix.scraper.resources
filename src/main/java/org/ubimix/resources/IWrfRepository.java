/**
 * 
 */
package org.ubimix.resources;

/**
 * @author kotelnikov
 */
public interface IWrfRepository {

    IWrfResourceProvider getResourceProvider(String key, boolean create);

}
