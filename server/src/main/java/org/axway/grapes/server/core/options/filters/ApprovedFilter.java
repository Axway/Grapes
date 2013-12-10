package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbLicense;

import java.util.HashMap;
import java.util.Map;

public class ApprovedFilter implements Filter {

    private Boolean approved;

    /**
     * The parameter must never be null
     *
     * @param approved
     */
    public ApprovedFilter(final Boolean approved) {
        this.approved = approved;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof DbLicense){
            return approved.equals( ((DbLicense)datamodelObj).isApproved());
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
