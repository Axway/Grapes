package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.License;

import java.util.Collections;
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
        if(datamodelObj instanceof License){
            return licenseId.equals( ((License)datamodelObj).getName());
        }

        if(datamodelObj instanceof Artifact){
            return ((Artifact)datamodelObj).getLicenses().contains(licenseId);
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
        queryParams.put("licenses", licenseId);

        return queryParams;
    }
}
