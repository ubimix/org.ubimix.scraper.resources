package org.ubimix.resources;

import org.ubimix.commons.adapters.IAdaptableObject;
import org.ubimix.commons.uri.Path;

/**
 * @author kotelnikov
 */
public interface IWrfResourceProvider extends IAdaptableObject {

    IWrfRepository getRepository();

    IWrfResource getResource(Path path, boolean create);

}