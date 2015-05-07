package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Artifact;

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
        if(datamodelObj instanceof Artifact){
            return type.equals( ((Artifact)datamodelObj).getType());
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
        queryParams.put("type", type);
        return queryParams;
    }
}
