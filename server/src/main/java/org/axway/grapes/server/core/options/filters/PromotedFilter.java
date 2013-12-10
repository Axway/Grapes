package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.HashMap;
import java.util.Map;

public class PromotedFilter implements Filter {

    private Boolean promoted;

    /**
     * The parameter must never be null
     *
     * @param promoted
     */
    public PromotedFilter(final Boolean promoted) {
        this.promoted = promoted;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof DbModule){
            return promoted.equals( ((DbModule)datamodelObj).isPromoted());
        }
        if(datamodelObj instanceof DbArtifact){
            return promoted.equals( ((DbArtifact)datamodelObj).isPromoted());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(DbArtifact.PROMOTION_DB_FIELD, promoted);
        return fields;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        return new HashMap<String, Object>();
    }
}
