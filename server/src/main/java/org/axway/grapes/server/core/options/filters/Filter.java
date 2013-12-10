package org.axway.grapes.server.core.options.filters;

import java.util.Map;

/**
 * Filter Interface
 *
 * <p> This interface defines the dependency filters API. A filter is used to select the objects of the data model that should appears in reports.</p>
 *
 *@author jdcoffre
 */
public interface Filter {

    public boolean filter(final Object datamodelObj);

    public Map<String, Object> moduleFilterFields();

    public Map<String, Object> artifactFilterFields();

}
