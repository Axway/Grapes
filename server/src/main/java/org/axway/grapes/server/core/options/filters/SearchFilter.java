package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbSearch;

import java.util.HashMap;
import java.util.Map;

/**
 * Filter class for filtering modules and/or artifacts in the search result
 */

public class SearchFilter implements Filter {

    private Boolean includeModules;
    private Boolean includeArtifacts;

    public SearchFilter(final Boolean modules, final Boolean artifacts) {
        this.includeArtifacts = artifacts;
        this.includeModules = modules;
    }

    @Override
    public boolean filter(Object datamodelObj) {
        if(datamodelObj instanceof DbSearch){
            return includeModules.equals(((DbSearch)datamodelObj).getModules() == null || ((DbSearch)datamodelObj).getModules().isEmpty())
                    && includeArtifacts.equals(((DbSearch)datamodelObj).getArtifacts() == null || ((DbSearch) datamodelObj).getArtifacts().isEmpty());
        }
        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(DbSearch.MODULES_DB_FIELD, includeModules);
        return queryParams;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(DbSearch.ARTIFACTS_DB_FIELD, includeArtifacts);
        return queryParams;
    }
}
