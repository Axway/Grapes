package org.axway.grapes.core.service;


import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;

import java.util.List;

/**
 * Created by jennifer on 4/24/15.
 */
public interface OrganizationService {
    void store(Organization organization);

    List<String> getOrganizationNames();

    Organization getOrganization(String organizationId);

    List<Organization> getAllOrganizations();

    void deleteOrganization(String organizationId);

    List<String> getCorporateGroupIds(String organizationId);

    void addCorporateGroupId(String organizationId, String corporateGroupId);

    void removeCorporateGroupId(String organizationId, String corporateGroupId);


    void removeModulesOrganization(final Organization organization);
    void removeModulesOrganization(final String corporateGidPrefix, final Organization organization);
    void addModulesOrganization(final String corporateGidPrefix, final Organization organization);

}
