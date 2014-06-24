package org.axway.grapes.server.core;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbOrganization;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Artifact Handler
 *
 * <p>Manages all operation regarding Organizations. It can, get/update Organizations of the database.</p>
 *
 * @author jdcoffre
 */
public class OrganizationHandler {

    private final RepositoryHandler repositoryHandler;

    public OrganizationHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }

    /**
     * Stores an Organization in Grapes database
     *
     * @param dbOrganization DbOrganization
     */
    public void store(final DbOrganization dbOrganization) {
        repositoryHandler.store(dbOrganization);
    }

    /**
     * Returns all the organization names
     *
     * @return List<String>
     */
    public List<String> getOrganizationNames() {
        return repositoryHandler.getOrganizationNames();
    }

    /**
     * Returns an Organization
     *
     * @param organizationId String
     * @return DbOrganization
     */
    public DbOrganization getOrganization(final String organizationId) {
        final DbOrganization dbOrganization = repositoryHandler.getOrganization(organizationId);

        if(dbOrganization == null){
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Organization " + organizationId + " does not exist.").build());
        }

        return dbOrganization;
    }

    /**
     * Deletes an organization
     *
     * @param organizationId String
     */
    public void deleteOrganization(final String organizationId) {
        final DbOrganization dbOrganization = getOrganization(organizationId);
        repositoryHandler.deleteOrganization(dbOrganization.getName());
        repositoryHandler.removeModulesOrganization(dbOrganization);
    }

    /**
     * Returns the list view of corporate groupIds of an organization
     *
     * @param organizationId String
     * @return ListView
     */
    public List<String> getCorporateGroupIds(final String organizationId) {
        final DbOrganization dbOrganization = getOrganization(organizationId);
        return dbOrganization.getCorporateGroupIdPrefixes();
    }

    /**
     * Adds a corporate groupId to an organization
     *
     * @param organizationId String
     * @param corporateGroupId String
     */
    public void addCorporateGroupId(final String organizationId, final String corporateGroupId) {
        final DbOrganization dbOrganization = getOrganization(organizationId);

        if(!dbOrganization.getCorporateGroupIdPrefixes().contains(corporateGroupId)){
            dbOrganization.getCorporateGroupIdPrefixes().add(corporateGroupId);
            repositoryHandler.store(dbOrganization);
        }

        repositoryHandler.addModulesOrganization(corporateGroupId, dbOrganization);
    }

    /**
     * Removes a corporate groupId from an Organisation
     *
     * @param organizationId String
     * @param corporateGroupId String
     */
    public void removeCorporateGroupId(final String organizationId, final String corporateGroupId) {
        final DbOrganization dbOrganization = getOrganization(organizationId);

        if(dbOrganization.getCorporateGroupIdPrefixes().contains(corporateGroupId)){
            dbOrganization.getCorporateGroupIdPrefixes().remove(corporateGroupId);
            repositoryHandler.store(dbOrganization);
        }

        repositoryHandler.removeModulesOrganization(corporateGroupId, dbOrganization);
    }


}
