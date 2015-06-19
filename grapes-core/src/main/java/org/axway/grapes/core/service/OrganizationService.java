package org.axway.grapes.core.service;

import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;

import java.util.List;

/**
 * Created by jennifer on 4/24/15.
 */
public interface OrganizationService {
    public void store(Organization organization);

    public Organization getOrganization(String organizationName);

    public List<String> getOrganizationNames();

    public List<Organization> getAllOrganizations();

    public void deleteOrganization(String organizationName);

    public List<String> getCorporateGroupIds(String organizationName);

    void addCorporateGroupId(String organizationName, String corporateGroupId);

    void removeCorporateGroupId(String organizationName, String corporateGroupId);

    void removeModulesOrganization(final Organization organization);

    void removeModulesOrganization(final String corporateGidPrefix, final Organization organization);

    void addModulesOrganization(final String corporateGidPrefix, final Organization organization);
    public Organization getMatchingOrganization(final Module module);


}
