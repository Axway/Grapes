package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbModule;

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
        if(datamodelObj instanceof DbModule){
            return organization.equals(((DbModule)datamodelObj).getOrganization());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(DbModule.ORGANIZATION_DB_FIELD, organization);
        return fields;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        return Collections.emptyMap();
    }
}
