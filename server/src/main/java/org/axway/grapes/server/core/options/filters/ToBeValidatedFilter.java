package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbLicense;

import java.util.HashMap;
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
        if(datamodelObj instanceof DbLicense){

            if(toBeValidated){
                return ((DbLicense)datamodelObj).isApproved() == null;
            }
            else{
                return ((DbLicense)datamodelObj).isApproved() != null;
            }
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        return new HashMap<String, Object>();
    }
}
