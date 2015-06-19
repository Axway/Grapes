package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Module;

import java.util.Collections;
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
        if(datamodelObj instanceof Module){
            return promoted.equals( ((Module)datamodelObj).isPromoted());
        }
        if(datamodelObj instanceof Artifact){
            return promoted.equals( ((Artifact)datamodelObj).isPromoted());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("promoted", promoted);
        return fields;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        return Collections.emptyMap();
    }
}
