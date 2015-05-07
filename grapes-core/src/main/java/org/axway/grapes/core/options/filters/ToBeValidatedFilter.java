package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.License;

import java.util.Collections;
import java.util.Map;

public class ToBeValidatedFilter implements Filter {

    private Boolean toBeValidated;

    /**
     * The parameter must never be null
     *
     * @param toBeValidated
     */
    public ToBeValidatedFilter(final Boolean toBeValidated) {
        this.toBeValidated = toBeValidated;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof License){
//todo
//            if(toBeValidated){
//                return ((License)datamodelObj).isApproved() == null;
//            }
//            else{
//                return ((License)datamodelObj).isApproved() != null;
//            }
        return true;
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        return Collections.emptyMap();
    }
}
