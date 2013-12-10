package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;

import java.util.HashMap;
import java.util.Map;

public class LicenseIdFilter implements Filter {

    private String licenseId;

    /**
     * The parameter must never be null
     *
     * @param licenseId
     */
    public LicenseIdFilter(final String licenseId) {
        this.licenseId = licenseId;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof DbLicense){
            return licenseId.equals( ((DbLicense)datamodelObj).getName());
        }

        if(datamodelObj instanceof DbArtifact){
            return ((DbArtifact)datamodelObj).getLicenses().contains(licenseId);
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(DbArtifact.LICENCES_DB_FIELD, licenseId);

        return queryParams;
    }
}
