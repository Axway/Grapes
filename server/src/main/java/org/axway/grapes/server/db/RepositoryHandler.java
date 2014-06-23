package org.axway.grapes.server.db;

import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;

import java.util.List;

/**
 * Repository Handler Interface
 * 
 * <p>This interface has to be implemented by all the classes that handles the interaction between the application and the database.</p>
 * 
 * @author jdcoffre
 */
public interface RepositoryHandler {

    /**
     * Store a new credential or update an existing one
     *
     * @param credential DbCredential
     */
    public void store(final DbCredential credential);

    /**
     * Returns the credentials of a user
     *
     * @param userId String
     * @return DbCredential
     */
    public DbCredential getCredential(final String userId);

    /**
     * Add a role to the targeted user
     *
     * @param user String
     * @param role AvailableRoles
     */
    public void addUserRole(final String user, final AvailableRoles role);

    /**
     * Remove a role to the targeted user
     *
     * @param user String
     * @param role AvailableRoles
     */
    public void removeUserRole(final String user, final AvailableRoles role);

    /**
     * Store a new license or update an existing one
     *
     * @param license DbLicense
     */
    public void store(final DbLicense license);

    /**
     * Return a list of all the available licenses regarding the provided filters
     *
     * @param filters FiltersHolder
     * @return List<String>
     */
    public List<String> getLicenseNames(final FiltersHolder filters);

    /**
     * Retrieve the targeted license from the database
     *
     * @param name String
     * @return DbLicense
     */
    public DbLicense getLicense(final String name);

    /**
     * Retrieve all licenses from the database
     *
     * @return List<DbLicense>
     */
    public List<DbLicense> getAllLicenses();

    /**
     * Delete the targeted license from the database
     *
     * @param name String
     */
    public void deleteLicense(final String name);

    /**
     * Return a list of all the artifacts that match the filters
     *
     * @param filters FiltersHolder
     * @return List<DbArtifact>
     */
    public List<DbArtifact> getArtifacts(final FiltersHolder filters);

    /**
     * Add a license to an existing artifact
     *
     * @param artifact DbArtifact
     * @param licenseId String
     */
    public void addLicenseToArtifact(final DbArtifact artifact, final String licenseId);

    /**
     * Remove a license from an existing artifact
     *
     * @param artifact DbArtifact
     * @param name String
     */
    public void removeLicenseFromArtifact(final DbArtifact artifact, final String name);

    /**
     * Approve or reject a license
     *
     * @param license DbLicense
     * @param approved Boolean
     */
    public void approveLicense(final DbLicense license, final Boolean approved);

    /**
     * Create a new artifact or update an existing one in the database
     *
     * @param dbArtifact DbArtifact
     */
    public void store(final DbArtifact dbArtifact);

    /**
     * Return the list of all the artifacts gavcs that match the provided filters
     *
     * @param filters FiltersHolder
     * @return List<String>
     */
    public List<String> getGavcs(final FiltersHolder filters);

    /**
     * Return the list of all the artifacts groupIds that match the provided filters
     *
     * @param filters FiltersHolder
     * @return List<String>
     */
    public List<String> getGroupIds(final FiltersHolder filters);

    /**
     * Return the list of all the available versions of the targeted artifacts
     *
     * @param artifact DbArtifact
     * @return List<String>
     */
    public List<String> getArtifactVersions(final DbArtifact artifact);

    /**
     * Return the targeted artifact
     *
     * @param gavc String
     * @return DbArtifact
     */
    public DbArtifact getArtifact(final String gavc);

    /**
     * Delete the targeted artifact
     *
     * @param gavc String
     */
    public void deleteArtifact(final String gavc);

    /**
     * Update "DO_NOT_USE" field of an artifact
     *
     * @param artifact DbArtifact
     * @param doNotUse Boolean
     */
    public void updateDoNotUse(final DbArtifact artifact, final Boolean doNotUse);

    /**
     * Update the download url field of an artifact
     *
     * @param artifact DbArtifact
     * @param downLoadUrl String
     */
    public void updateDownloadUrl(final DbArtifact artifact, final String downLoadUrl);

    /**
     * Update provider field of an artifact
     *
     * @param artifact DbArtifact
     * @param provider String
     */
    public void updateProvider(final DbArtifact artifact, final String provider);

    /**
     * Retrieve the list of the modules the use the targeted artifact
     *
     * @param artifact String
     * @param filters FiltersHolder
     * @return List<DbModule>
     */
    public List<DbModule> getAncestors(final DbArtifact artifact, final FiltersHolder filters);

    /**
     * Create a new module or update an existing one into the database
     *
     * @param dbModule DbModule
     */
    public void store(final DbModule dbModule);

    /**
     * Return the list of all the module names that match the provided filters
     *
     * @param filters FiltersHolder
     * @return List<String>
     */
    public List<String> getModuleNames(final FiltersHolder filters);

    /**
     * Retrieve the list of all the available versions of a module
     *
     * @param name String
     * @param filters FiltersHolder
     * @return List<String>
     */
    public List<String> getModuleVersions(final String name, final FiltersHolder filters);

    /**
     * Return the targeted module
     *
     * @param moduleId String
     * @return DbModule
     */
    public DbModule getModule(final String moduleId);


    /**
     * Return  a list od module regarding the filters
     *
     * @param filters FiltersHolder
     * @return List<DbModule>
     */
    public List<DbModule> getModules(final FiltersHolder filters);

    /**
     * Delete the targeted module
     *
     * @param moduleId String
     */
    public void deleteModule(final String moduleId);

    /**
     * Promote the targeted module
     *
     * @param module String
     */
    public void promoteModule(final DbModule module);

    /**
     * Return the module that contains the gavc.
     * It returns null if no module matches.
     *
     * @param gavc String
     * @return DbModule
     */
    public DbModule getRootModuleOf(final String gavc);

    /**
     * Return the module or the submodule that contains the gavc.
     * It returns null if no module matches.
     *
     * @param gavc String
     * @return DbModule
     */
    public DbModule getModuleOf(final String gavc);

    /**
     * Return a list of groupId considered as corporate production
     *
     * @return List<String>
     */
    public List<String> getCorporateGroupIds();

    /**
     * Add a new groupId in the corporate groupIds
     *
     * @param corporateGroupId String
     */
    public void addNewCorporateGroupId(final String corporateGroupId);

    /**
     * Remove a groupId from the corporate groupIds
     *
     * @param corporateGroupId String
     */
    public void removeCorporateGroupId(final String corporateGroupId);

    /**
     * Returns all the organization names
     *
     * @return List<String>
     */
    public List<String> getOrganizationNames();

    /**
     * Returns an organization
     *
     * @param name String
     * @return DbOrganization
     */
    public DbOrganization getOrganization(final String name);

    /**
     * Remove an organization
     *
     * @param organizationId String
     */
    public void deleteOrganization(final String organizationId);

    /**
     * Stores an organization into the database
     *
     * @param organization DbOrganization
     */
    public void store(final DbOrganization organization);

    /**
     * Updates all module that matches the corporate GID prefix setting the Organization name
     *
     * @param corporateGroupId String
     * @param dbOrganization DbOrganization
     */
    public void addModulesOrganization(final String corporateGroupId, final DbOrganization dbOrganization);

    /**
     * Updates all module that matches the corporate GID prefix AND that reference to the organization removing the Organization field
     *
     * @param corporateGroupId String
     * @param dbOrganization DbOrganization
     */
    public void removeModulesOrganization(final String corporateGroupId, final DbOrganization dbOrganization);

    /**
     * Updates all module reference to the organization removing the Organization field
     *
     * @param dbOrganization DbOrganization
     */
    public void removeModulesOrganization(final DbOrganization dbOrganization);
}
