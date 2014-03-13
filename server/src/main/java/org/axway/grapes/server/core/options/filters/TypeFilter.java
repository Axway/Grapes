package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TypeFilter implements Filter {

    private String type;

    /**
     * The parameter must never be null
     *
     * @param type
     */
    public TypeFilter(final String type) {
        this.type = type;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof DbArtifact){
            return type.equals( ((DbArtifact)datamodelObj).getType());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(DbArtifact.TYPE_DB_FIELD, type);
        return queryParams;
    }
}
