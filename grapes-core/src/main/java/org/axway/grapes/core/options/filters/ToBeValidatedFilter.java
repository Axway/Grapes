package org.axway.grapes.core.options.filters;
//todo This method never diplays values to be validated because even if it is empty in the database the get method fills in the value with false if it is missing.
//todo However apprently it doesnt work correctly in the other version either.

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
        if (datamodelObj instanceof License) {
            if (toBeValidated) {
                return ((License) datamodelObj).isApproved() == null;
            } else {
                return ((License) datamodelObj).isApproved() != null;
            }
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
