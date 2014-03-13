package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DoNotUseFilter implements Filter {

    private Boolean doNotUse;

    /**
     * The parameter must never be null
     *
     * @param doNotUse
     */
    public DoNotUseFilter(final Boolean doNotUse) {
        this.doNotUse = doNotUse;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof DbArtifact){
            return doNotUse.equals( ((DbArtifact)datamodelObj).getDoNotUse());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> filters =  new HashMap<String, Object>();
        filters.put(DbArtifact.DO_NOT_USE, doNotUse);

        return filters;
    }
}
