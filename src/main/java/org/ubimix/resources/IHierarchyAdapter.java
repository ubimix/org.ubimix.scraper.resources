/**
 * 
 */
package org.ubimix.resources;

import java.util.Iterator;

import org.ubimix.commons.uri.Path;

/**
 * @author kotelnikov
 */
public interface IHierarchyAdapter extends IWrfResourceAdapter {

    /**
     * Returns an iterator over child paths. Each child resource has the path
     * starting with the parent path.
     * 
     * @return an iterator over child resource paths
     */
    Iterator<Path> getChildren();

}
