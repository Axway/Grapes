package org.axway.grapes.server.core;


import org.axway.grapes.server.core.interfaces.LicenseMatcher;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Artifact Handler
 *
 * <p>Manages all operation regarding Artifacts. It can, get/update Artifacts of the database.</p>
 *
 * @author jdcoffre
 */
public class ArtifactHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactHandler.class);

    private final RepositoryHandler repositoryHandler;
    private final LicenseMatcher licenseMatcher;

    public ArtifactHandler(final RepositoryHandler repositoryHandler,
                           final LicenseMatcher matcher) {
        this.repositoryHandler = repositoryHandler;
        this.licenseMatcher = matcher;
    }

    /**
     * Update/save an artifact to the database
     *
     * @param dbArtifact DbArtifact
     */
    public void store(final DbArtifact dbArtifact) {
        repositoryHandler.store(dbArtifact);
    }

    /**
     * If the Artifact does not exist, it will add it to the database. Nothing if it already exit.
     *
     * @param fromClient DbArtifact
     */
    public void storeIfNew(final DbArtifact fromClient) {
        final DbArtifact existing = repositoryHandler.getArtifact(fromClient.getGavc());

        if(existing != null){
            existing.setLicenses(fromClient.getLicenses());
            store(existing);
        }

        if(existing == null){
	        store(fromClient);
        }
    }

    /**
     * Adds a license to an artifact if the license exist into the database
     *
     * @param gavc String
     * @param licenseId String
     */
    public void addLicense(final String gavc, final String licenseId) {
        final DbArtifact dbArtifact = getArtifact(gavc);

        // Try to find an existing license that match the new one
        final LicenseHandler licenseHandler = new LicenseHandler(repositoryHandler);
        final DbLicense license = licenseHandler.resolve(licenseId);

        // If there is no existing license that match this one let's use the provided value but
        // only if the artifact has no license  yet. Otherwise it could mean that users has already
        // identify the license manually.
        if(license == null){
            if(dbArtifact.getLicenses().isEmpty()){
                LOG.warn("Add reference to a non existing license called " + licenseId + " in  artifact " + dbArtifact.getGavc());
                repositoryHandler.addLicenseToArtifact(dbArtifact, licenseId);
            }
        }
        // Add only if the license is not already referenced
        else if(!dbArtifact.getLicenses().contains(license.getName())){
            repositoryHandler.addLicenseToArtifact(dbArtifact, license.getName());
        }
    }

    /**
     * Gather the available gavc regarding the filters
     *
     * @param filters FiltersHolder
     * @return List<String>
     */
    public List<String> getArtifactGavcs(final FiltersHolder filters) {
        return repositoryHandler.getGavcs(filters);
    }

    /**
     * Gather the available groupIds regarding the filters
     *
     * @param filters
     * @return List<String>
     */
    public List<String> getArtifactGroupIds(final FiltersHolder filters) {
        return repositoryHandler.getGroupIds(filters);
    }

    /**
     * Returns a the list of available version of an artifact
     *
     * @param gavc String
     * @return List<String>
     */
    public List<String> getArtifactVersions(final String gavc) {
        final DbArtifact artifact = getArtifact(gavc);
        return repositoryHandler.getArtifactVersions(artifact);
    }

    /**
     * Returns the last available version of an artifact
     *
     * @param gavc String
     * @return String
     */
    public String getArtifactLastVersion(final String gavc) {
        final List<String> versions = getArtifactVersions(gavc);

        final VersionsHandler versionHandler = new VersionsHandler(repositoryHandler);
        final String viaCompare = versionHandler.getLastVersion(versions);

        if (viaCompare != null) {
            return viaCompare;
        }

        //
        // These versions cannot be compared
        // Let's use the Collection.max() method by default, so goingo for a fallback
        // mechanism.
        //
        LOG.info("The versions cannot be compared");
        return Collections.max(versions);

    }

    /**
     * Return an artifact regarding its gavc
     *
     * @param gavc String
     * @return DbArtifact
     */
    public DbArtifact getArtifact(final String gavc) {
        final DbArtifact artifact = repositoryHandler.getArtifact(gavc);

        if(artifact == null){
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Artifact " + gavc + " does not exist.").build());
        }

        return artifact;
    }   

    /**
     * Return an artifact regarding its gavc
     *
     * @param sha256 String
     * @return DbArtifact
     */
    public DbArtifact getArtifactUsingSHA256(final String sha256) {
        return repositoryHandler.getArtifactUsingSHA256(sha256);
    }

    /**
     * Returns the Module of artifact or null if there is none
     *
     * @param dbArtifact DbArtifact
     * @return DbModule
     */
    public DbModule getModule(final DbArtifact dbArtifact) {
        return repositoryHandler.getRootModuleOf(dbArtifact.getGavc());
    }

    /**
     * Returns the Organization that produce this artifact or null if there is none
     *
     * @param dbArtifact DbArtifact
     * @return DbOrganization
     */
    public DbOrganization getOrganization(final DbArtifact dbArtifact) {
        final DbModule module = getModule(dbArtifact);

        if(module == null || module.getOrganization() == null){
            return null;
        }

        return repositoryHandler.getOrganization(module.getOrganization());
    }

    /**
     * Update artifact download url of an artifact
     *
     * @param gavc String
     * @param downLoadUrl String
     */
    public void updateDownLoadUrl(final String gavc, final String downLoadUrl) {
        final DbArtifact artifact = getArtifact(gavc);
        repositoryHandler.updateDownloadUrl(artifact, downLoadUrl);
    }

    /**
     * Update artifact provider
     *
     * @param gavc String
     * @param provider String
     */
    public void updateProvider(final String gavc, final String provider) {
        final DbArtifact artifact = getArtifact(gavc);
        repositoryHandler.updateProvider(artifact, provider);
    }

    /**
     * Delete an artifact
     *
     * @param gavc String
     */
    public void deleteArtifact(final String gavc){
        getArtifact(gavc);
        repositoryHandler.deleteArtifact(gavc);
    }

    /**
     * Add "DO_NOT_USE" flag to an artifact
     *
     * @param gavc String
     * @param doNotUse Boolean
     */
    public void updateDoNotUse(final String gavc, final Boolean doNotUse) {
        final DbArtifact artifact = getArtifact(gavc);
        repositoryHandler.updateDoNotUse(artifact, doNotUse);
    }

    /**
     * Return the list of module that uses the targeted artifact
     *
     * @param gavc String
     * @param filters FiltersHolder
     * @return List<DbModule>
     */
    public List<DbModule> getAncestors(final String gavc, final FiltersHolder filters) {
        final DbArtifact dbArtifact = getArtifact(gavc);
        return repositoryHandler.getAncestors(dbArtifact, filters);
    }

    /**
     * Return the list of licenses attached to an artifact
     *
     * @param gavc String
     * @param filters FiltersHolder
     * @return List<DbLicense>
     */
    public List<DbLicense> getArtifactLicenses(final String gavc, final FiltersHolder filters) {
        final DbArtifact artifact = getArtifact(gavc);
        final List<DbLicense> licenses = new ArrayList<>();

        for(final String name: artifact.getLicenses()){
            final Set<DbLicense> matchingLicenses = licenseMatcher.getMatchingLicenses(name);

            // Here is a license to identify
            if(matchingLicenses.isEmpty()){
                final DbLicense notIdentifiedLicense = new DbLicense();
                notIdentifiedLicense.setName(name);
                licenses.add(notIdentifiedLicense);
            } else {
                matchingLicenses.stream()
                        .filter(filters::shouldBeInReport)
                        .forEach(licenses::add);
            }
        }

        return licenses;
    }

    /**
     * Add a license to an artifact
     *
     * @param gavc String
     * @param licenseId String
     */
    public void addLicenseToArtifact(final String gavc, final String licenseId) {
        final DbArtifact dbArtifact = getArtifact(gavc);

        // Don't need to access the DB if the job is already done
        if(dbArtifact.getLicenses().contains(licenseId)){
            return;
        }

        final DbLicense dbLicense = repositoryHandler.getLicense(licenseId);
        if(dbLicense == null){
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("License " + licenseId + " does not exist.").build());
        }

        repositoryHandler.addLicenseToArtifact(dbArtifact, dbLicense.getName());
    }

    /**
     * Remove a license from an artifact
     *
     * @param gavc String The artifact GAVC
     * @param licenseId String The license id to be removed.
     */
    public void removeLicenseFromArtifact(final String gavc, final String licenseId) {
        final DbArtifact dbArtifact = getArtifact(gavc);

        //
        // The artifact may not have the exact string associated with it, but rather one
        // matching license regexp expression.
        //
        repositoryHandler.removeLicenseFromArtifact(dbArtifact, licenseId, licenseMatcher);
    }

    /**
     * Returns a list of artifact regarding the filters
     *
     * @param filters FiltersHolder
     * @return List<DbArtifact>
     */
    public List<DbArtifact> getArtifacts(final FiltersHolder filters) {
        return repositoryHandler.getArtifacts(filters);
    }

    /**k
     * Returns a list of artifact regarding the filters
     *
     * @return List<DbArtifact>
     */
	public String getModuleJenkinsJobInfo(final DbArtifact dbArtifact) {
		final DbModule module = getModule(dbArtifact);
		if(module == null){
			return "";
		}
		
		final String jenkinsJobUrl = module.getBuildInfo().get("jenkins-job-url");
		
		if(jenkinsJobUrl == null){
			return "";			
		}

		return jenkinsJobUrl;
	}
}
