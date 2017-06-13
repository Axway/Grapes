package org.axway.grapes.server.materials;

import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.materials.cases.DependencyCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Repository Handler
 * 
 * <p>Mock implemented for testing purposes.</p>
 * 
 * @author jdcoffre
 */
public class TestingRepositoryHandler implements RepositoryHandler {
    
    private final List<DbCredential> credentials = new ArrayList<DbCredential>();

    private final List<DbOrganization> organizations = new ArrayList<DbOrganization>();
    private final List<DbModule> modules = new ArrayList<DbModule>();
    private final List<DbArtifact> artifacts = new ArrayList<DbArtifact>();
    private final List<DbLicense> licenses = new ArrayList<DbLicense>();

    @Override
    public void store(final DbCredential credential) {
        final DbCredential dbCredential = getCredential(credential.getUser());
		
		if(dbCredential != null){
			credentials.remove(dbCredential);
		}
		
        credentials.add(credential);
    }

    @Override
    public void addUserRole(String user, DbCredential.AvailableRoles role) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeUserRole(String user, DbCredential.AvailableRoles role) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public DbCredential getCredential(final String user) {
        for(DbCredential dbCredential: credentials){
            if(dbCredential.getUser().equals(user)){
                return dbCredential;
            }
        }
        
        return null;
    }

    @Override
    public void store(final DbLicense license) {
        final DbLicense dbLicense = getLicense(license.getName());
		
		if(dbLicense != null){
			licenses.remove(dbLicense);
		}
		
        licenses.add(license);
    }

    @Override
    public List<DbLicense> getAllLicenses() {
        return licenses;
    }

    @Override
    public DbLicense getLicense(final String name) {
        for(DbLicense dbLicense: licenses){
            if(dbLicense.getName().equals(name)){
                return dbLicense;
            }
        }

        return null;
    }

    @Override
    public void deleteLicense(String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<DbArtifact> getArtifacts(final FiltersHolder filters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addLicenseToArtifact(DbArtifact artifact, String licenseId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeLicenseFromArtifact(DbArtifact artifact, String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void approveLicense(final DbLicense license, final Boolean approved) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void store(final DbArtifact dbArtifact) {
        artifacts.add(dbArtifact);
    }

    @Override
    public List<String> getGavcs(final FiltersHolder filters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getGroupIds(FiltersHolder filters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getArtifactVersions(final DbArtifact artifact) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DbArtifact getArtifact(final String gavc) {
        for(DbArtifact artifact: artifacts){
            if(artifact.getGavc().equals(gavc)){
                return artifact;
            }
        }

        return null;
    }
	
    @Override
    public DbArtifact getArtifactUsingSHA256(final String sha256) {
        for(DbArtifact artifact: artifacts){
            if(artifact.getSha256().equals(sha256)){
                return artifact;
            }
        }

        return null;
    }

    @Override
    public void deleteArtifact(final String gavc) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateDoNotUse(DbArtifact artifact, Boolean doNotUse) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateDownloadUrl(DbArtifact artifact, String downLoadUrl) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateProvider(DbArtifact artifact, String provider) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<DbModule> getAncestors(final DbArtifact artifact, FiltersHolder filters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void store(final DbModule dbModule) {
        dbModule.updateHasAndUse();
        modules.add(dbModule);
    }

    @Override
    public List<String> getModuleNames(final FiltersHolder filters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getModuleVersions(final String name, final FiltersHolder filters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DbModule getModule(final String moduleId) {
        for(DbModule module: modules){
            if(module.getId().equals(moduleId)){
                return module;
            }
        }

        return null;
    }

    @Override
    public List<DbModule> getModules(FiltersHolder filters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteModule(final String moduleId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void promoteModule(final DbModule module) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DbModule getRootModuleOf(final String gavc) {
        for(DbModule module: modules){
            module.updateHasAndUse();
            if(module.getHas().contains(gavc)){
                return module;
            }
        }
        return null;
    }

    @Override
    public DbModule getModuleOf(final String gavc) {
        for(DbModule module: modules){
            if(module.getHas().contains(gavc)){
                return module;
            }

            for(DbModule submodule: DataUtils.getAllSubmodules(module)){
                if(submodule.getHas().contains(gavc)){
                    return submodule;
                }
            }
        }
        return null;
    }

    @Override
    public List<String> getOrganizationNames() {
        List<String> names = new ArrayList<String>();

        for(DbOrganization organization: organizations){
            names.add(organization.getName());
        }

        return names;
    }

    @Override
    public DbOrganization getOrganization(String name) {
        for(DbOrganization organization: organizations){
            if(name.equals(organization.getName())){
              return organization;
            }
        }

        return null;
    }

    @Override
    public void deleteOrganization(String organizationId) {
        organizations.remove(getOrganization(organizationId));
    }

    @Override
    public void store(DbOrganization organization) {
        organizations.add(organization);
    }

    @Override
    public void addModulesOrganization(String corporateGroupId, DbOrganization dbOrganization) {

    }

    @Override
    public void removeModulesOrganization(String corporateGroupId, DbOrganization dbOrganization) {

    }

    @Override
    public void removeModulesOrganization(DbOrganization dbOrganization) {

    }

    @Override
    public List<DbOrganization> getAllOrganizations() {
        return organizations;
    }

    @Override
    public void store(DbProduct dbProduct) {

    }

    @Override
    public DbProduct getProduct(String name) {
        return null;
    }

    @Override
    public List<String> getProductNames() {
        return null;
    }

    @Override
    public void deleteProduct(String name) {

    }

    @Override
    public <T> Optional<T> getOneByQuery(String collectionName, String query, Class<T> c) {
        return Optional.empty();
    }

    @Override
    public <T> List<T> getListByQuery(String collectionName, String query, Class<T> c) {
        return Collections.emptyList();
    }

    @Override
    public long getResultCount(String collectionName, String query) {
        return 0;
    }

    @Override
    public List<String> getLicenseNames(final FiltersHolder filters) {
        final List<String> names = new ArrayList<String>();

        for(DbLicense license: licenses){
            if(filters.shouldBeInReport(license)){
                names.add(license.getName());
            }
        }

        return names;
    }

    public List<DbModule> getTargetedModules(final List<DbDependency> dependencies) {
        final List<String> treatedModuleId = new ArrayList<String>();
        final List<DbModule> axModuleDeps = new ArrayList<DbModule>();

        for(DbDependency dependency: dependencies){
            final DbModule module = getModule(dependency.getTarget());

            if(!treatedModuleId.contains(module.getId())){
                axModuleDeps.add(module);
                treatedModuleId.add(module.getId());
            }
        }

        return axModuleDeps;
    }


    public void loadTestCase(final DependencyCase testCase) {
        final DbOrganization organization = new DbOrganization();
        organization.setName(GrapesTestUtils.ORGANIZATION_NAME_4TEST);
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        store(organization);

        for(DbModule module: testCase.dbModulesToLoad()){
            store(module);
        }
        for(DbArtifact artifact: testCase.dbArtifactsToLoad()){
            store(artifact);
        }
        for(DbLicense license: testCase.dbLicensesToLoad()){
            store(license);
        }
    }

}
