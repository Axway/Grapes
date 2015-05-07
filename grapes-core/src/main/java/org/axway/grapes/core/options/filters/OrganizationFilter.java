package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Module;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrganizationFilter implements Filter {

    private String organization;

    /**
     * The parameter must never be null
     *
     * @param organization
     */
    public OrganizationFilter(final String organization) {
        this.organization = organization;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof Module){
            return organization.equals(((Module)datamodelObj).getOrganization());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("orginization", organization);
        return fields;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        return Collections.emptyMap();
    }
}
