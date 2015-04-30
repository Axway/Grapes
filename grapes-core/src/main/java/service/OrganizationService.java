package service;


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

    void deleteOrganization(String organizationId);

    List<String> getCorporateGroupIds(String organizationId);

    void addCorporateGroupId(String organizationId, String corporateGroupId);

    void removeCorporateGroupId(String organizationId, String corporateGroupId);

    Organization getMatchingOrganization(Module module);
}
