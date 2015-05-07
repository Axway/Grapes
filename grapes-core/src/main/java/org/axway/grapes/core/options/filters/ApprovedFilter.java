package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.License;

import java.util.Collections;
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
        if(datamodelObj instanceof License){
            return approved.equals( ((License)datamodelObj).isApproved());
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
