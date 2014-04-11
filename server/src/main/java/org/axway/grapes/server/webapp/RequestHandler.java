/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.axway.grapes.server.webapp;

import com.sun.jersey.api.NotFoundException;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.DependenciesHandler;
import org.axway.grapes.server.core.GraphsHandler;
import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.core.VersionsHandler;
import org.axway.grapes.server.core.graphs.AbstractGraph;
import org.axway.grapes.server.core.graphs.TreeNode;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.options.filters.CorporateFilter;
import org.axway.grapes.server.core.options.filters.Filter;
import org.axway.grapes.server.core.options.filters.HasLicenseFilter;
import org.axway.grapes.server.core.options.filters.LicenseIdFilter;
import org.axway.grapes.server.core.reports.DependencyReport;
import org.axway.grapes.server.core.version.IncomparableException;
import org.axway.grapes.server.core.version.NotHandledVersionException;
import org.axway.grapes.server.core.version.Version;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.webapp.views.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Request Handler
 *
 * <p>This class holds all the request that provides from the resources.<br/>
 * It handles the datamodel transformation between the client/server model and the database model.</p>
 * @author jdcoffre
 */
public class RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final LicenseHandler licenseHandler = new LicenseHandler();
    private final RepositoryHandler repoHandler;
    private final GrapesServerConfig config;

    public RequestHandler(final RepositoryHandler repoHandler, final GrapesServerConfig config) {
        this.repoHandler = repoHandler;
        this.config = config;

        final List<DbLicense> licenses = repoHandler.getAllLicenses();
        licenseHandler.update(licenses);
    }

    /**
     * Add or update a license to the database
     *
     * @param license
     */
    public void store(final License license) {
        final DbLicense dbLicense = DataUtils.getDbLicense(license);
        repoHandler.store(dbLicense);

        final List<DbLicense> licenses = repoHandler.getAllLicenses();
        licenseHandler.update(licenses);
    }

    /**
     * Return a list of license names. This list can either be serialized in HTML or in JSON
     *
     * @param filters
     * @return ListView
     */
    public ListView getLicensesNames(final FiltersHolder filters) {
        final List<String> names = repoHandler.getLicenseNames(filters);
        final ListView view = new ListView("License names view", "license");
        view.addAll(names);

        return view;
    }



    /**
     * Return a html view that contains the targeted license
     *
     * @param name
     * @return LicenseView serializable in JSON or in HTML
     */
    public LicenseView getLicense(final String name) {
        final DbLicense license = repoHandler.getLicense(name);

        if(license == null){
            throw new NotFoundException();
        }

        final LicenseView view = new LicenseView();
        view .setLicense(license);

        return view;
    }

    /**
     * Delete a license from the repository
     *
     * @param name
     */
    public void deleteLicenses(final String name) {
        if(repoHandler.getLicense(name) == null){
            throw new NotFoundException();
        }

        repoHandler.deleteLicense(name);

        final FiltersHolder filters = new FiltersHolder(config.getCorporateGroupIds());
        final LicenseIdFilter licenseIdFilter = new LicenseIdFilter(name);
        filters.addFilter(licenseIdFilter);

        for(DbArtifact artifact: repoHandler.getArtifacts(filters)){
            repoHandler.removeLicenseFromArtifact(artifact, name);
        }

        final List<DbLicense> licenses = repoHandler.getAllLicenses();
        licenseHandler.update(licenses);

    }

    /**
     * Approve or reject a license
     *
     * @param name
     * @param approved
     */
    public void approveLicenses(final String name, final Boolean approved) {
        final DbLicense license = repoHandler.getLicense(name);

        if(license == null){
            throw new NotFoundException();
        }
        repoHandler.approveLicense(license, approved);
    }

    /**
     * Manage the storage of an artifact.
     *
     * @param artifact the artifact to store
     */
    public void store(final Artifact artifact) {
        final DbArtifact dbArtifact = DataUtils.getDbArtifact(artifact);

        repoHandler.store(dbArtifact);

        // Handle licenses
        for(String licenseId: artifact.getLicenses()){
            final DbLicense license = licenseHandler.resolve(licenseId);

            if(license != null){
                addLicenseToArtifact(dbArtifact.getGavc(), license.getName());
            }
        }
    }

    /**
     * Gather the available gavc regarding the filters
     *
     * @param filters
     * @return ListView serializable in JSON or in HTML
     */
    public ListView getArtifactGavcs(final FiltersHolder filters) {
        final List<String> gavcs = repoHandler.getGavcs(filters);
        final ListView view = new ListView("GAVCS view", "gavc");
        view.addAll(gavcs);

        return view;
    }

    /**
     * Gather the available groupIds regarding the filters
     *
     * @param filters
     * @return ListView serializable in JSON or in HTML
     */
    public ListView getArtifactGroupIds(final FiltersHolder filters) {
        final List<String> groupIds = repoHandler.getGroupIds(filters);
        final ListView view = new ListView("GroupIds view", "groupId");
        view.addAll(groupIds);

        return view;
    }

    /**
     * Return an artifact
     *
     * @param gavc
     * @return ArtifactView serializable in JSON or in HTML
     */
    public ArtifactView getArtifact(final String gavc) {
        final DbArtifact dbArtifact = repoHandler.getArtifact(gavc);

        if(dbArtifact == null){
            throw new NotFoundException();
        }

        final Artifact artifact = DataUtils.getArtifact(dbArtifact);

        final ArtifactView view = new ArtifactView();
        view.setArtifact(artifact);
        view.setShouldNotBeUse(dbArtifact.getDoNotUse());

        final CorporateFilter corporateGroupIdsFilter = new CorporateFilter(config.getCorporateGroupIds());
        view.setIsCorporate(corporateGroupIdsFilter.matches(dbArtifact));

        return view;
    }

    /**
     * Delete an artifact
     *
     * @param gavc
     */
    public void deleteArtifact(final String gavc){
        if(repoHandler.getArtifact(gavc) == null){
            throw new NotFoundException();
        }

        repoHandler.deleteArtifact(gavc);
    }

    /**
     * Return an list of ancestors regarding the filters
     *
     * @param gavc
     * @param filters
     * @return DependencyListView that contains the ancestors
     */
    public DependencyListView getAncestors(final String gavc, final FiltersHolder filters) {
        final DbArtifact dbArtifact = repoHandler.getArtifact(gavc);
        if(dbArtifact == null){
            throw new NotFoundException();
        }

        final DependencyListView view = new DependencyListView("Ancestor List Of " + gavc, licenseHandler.getLicenses(), filters.getDecorator());

        for(DbModule dbAncestor : repoHandler.getAncestors(gavc, filters)){
            for(DbDependency dbDependency: DataUtils.getAllDbDependencies(dbAncestor)){
                if(dbDependency.getTarget().equals(gavc)){
                    final Dependency dependency = DataModelFactory.createDependency(DataUtils.getArtifact(dbArtifact), dbDependency.getScope());
                    dependency.setSourceName(dbAncestor.getName());
                    dependency.setSourceVersion(dbAncestor.getVersion());
                    view.addDependency(dependency);
                }
            }
        }

        return view;
    }

    /**
     * Return the list of licenses attached to an artifact
     *
     * @param gavc
     * @param filters
     * @return ListView
     */
    public ListView getArtifactLicenses(final String gavc, final FiltersHolder filters) {
        final DbArtifact artifact = repoHandler.getArtifact(gavc);

        if(artifact == null){
            throw new NotFoundException();
        }

        final ListView view = new ListView("Licenses of " + gavc, "license");

        for(String name: artifact.getLicenses()){
            final DbLicense license = repoHandler.getLicense(name);

            if(license == null){
                // Should never happen
                LOG.error("Reference to a non existing license detected: " + name);
            }
            // The license has to be validated
            else if(filters.shouldBeInReport(license)){
                view.add(name);
            }

        }

        return view;
    }

    /**
     * Returns a the list of available version of an artifact
     *
     * @param gavc String
     * @return List<String>
     */
    public List<String> getArtifactVersions(final String gavc) {
        final DbArtifact artifact = repoHandler.getArtifact(gavc);

        if(artifact == null){
            throw new NotFoundException();
        }

        final List<String> versions = repoHandler.getArtifactVersions(artifact);
        Collections.sort(versions);

        return versions;
    }

    /**
     * Returns a the last available version of an artifact
     *
     * @param gavc String
     * @return String
     */
    public String getArtifactLastVersion(final String gavc) {
        final List<String> versions = getArtifactVersions(gavc);
        String lastVersion = null;

        try{
            Version lastVersionUntilNow = null;

            for(String version: versions){
                if(lastVersionUntilNow == null ){
                    lastVersionUntilNow = new Version(version);
                }
                else{
                    Version newVersion = new Version(version);

                    if(lastVersionUntilNow.compare(newVersion) < 0){
                        lastVersionUntilNow=newVersion;
                    }

                }
            }

            lastVersion = lastVersionUntilNow.toString();

        } catch (NotHandledVersionException e) {
            // nothing to do
        } catch (IncomparableException e) {
            // nothing to do
        }

        if(lastVersion == null){
            return Collections.max(versions);
        }

        return lastVersion;
    }

    /**
     * Add a license to an artifact
     *
     * @param gavc
     * @param licenseId
     */
    public void addLicenseToArtifact(final String gavc, final String licenseId) {
        final DbArtifact artifact = repoHandler.getArtifact(gavc);
        final DbLicense license = repoHandler.getLicense(licenseId);

        if(artifact == null || license == null){
            throw new NotFoundException();
        }

        repoHandler.addLicenseToArtifact(artifact, license);

    }

    /**
     * Remove a license from an artifact
     *
     * @param gavc
     * @param licenseId
     */
    public void removeLicenseFromArtifact(final String gavc, final String licenseId) {
        final DbArtifact artifact = repoHandler.getArtifact(gavc);

        if(artifact == null){
            throw new NotFoundException();
        }

        repoHandler.removeLicenseFromArtifact(artifact, licenseId);

    }

    /**
     * Manage the storage of a module.
     *
     * @param module the module to store
     */
    public void store(final Module module){
        // Add the module
        final DbModule dbModule = DataUtils.getDbModule(module);
        repoHandler.store(dbModule);

        // Add the artifacts
        final Set<Artifact> artifacts = DataUtils.getAllArtifacts(module);
        for(Artifact artifact: artifacts){
            store(artifact);
        }

        // Add dependencies that does not exist
        final Set<Dependency> dependencies = DataUtils.getAllDependencies(module);
        for(Dependency dep: dependencies){
            final String depGavc = DbArtifact.generateGAVC(dep.getTarget());
            final DbArtifact dbDep = repoHandler.getArtifact(depGavc);

            if(dbDep == null){
                store(dep.getTarget());
            }
        }
    }

    /**
     * Gather the available module names regarding the filters
     *
     * @param filters
     * @return ListView serializable in JSON or in HTML
     */
    public ListView getModuleNames(final FiltersHolder filters) {
        final List<String> names = repoHandler.getModuleNames(filters);
        Collections.sort(names);
        final ListView view = new ListView("Module names view", "name");
        view.addAll(names);

        return view;
    }

    /**
     * Gather the available module names regarding the filters
     *
     * @param name
     * @param filters
     */
    public ListView getModuleVersions(final String name, final FiltersHolder filters) {
        final List<String> versions = repoHandler.getModuleVersions(name, filters);

        if(versions.isEmpty()){
            throw new NotFoundException();
        }

        Collections.sort(versions);
        final ListView view = new ListView("Versions of " + name, "version");
        view.addAll(versions);

        return view;
    }
    /**
     * Return a ModuleView which that contains the module
     *
     * @param name
     * @param version
     * @return ModuleView
     */
    public ModuleView getModule(final String name, final String version) {
        final DbModule dbModule = repoHandler.getModule(DbModule.generateID(name, version));

        if(dbModule == null){
            throw new NotFoundException();
        }

        final Module module = getModule(dbModule);

        final ModuleView view = new ModuleView();
        view.setModule(module);

        return view;
    }

    private Module getModule(final DbModule dbModule) {
        final Module module =DataModelFactory.createModule(dbModule.getName(), dbModule.getVersion());
        module.setPromoted(dbModule.isPromoted());
        module.setSubmodule(dbModule.isSubmodule());

        //Artifacts
        for(String gavc: dbModule.getArtifacts()){
            final DbArtifact dbArtifact = repoHandler.getArtifact(gavc);
            final Artifact artifact = DataUtils.getArtifact(dbArtifact);
            module.addArtifact(artifact);
        }

        //Dependencies
        for(DbDependency dbDependency: dbModule.getDependencies()){
            final DbArtifact dbArtifact = repoHandler.getArtifact(dbDependency.getTarget());
            final Artifact artifact = DataUtils.getArtifact(dbArtifact);
            final Dependency dependency = DataModelFactory.createDependency(artifact, dbDependency.getScope());
            module.addDependency(dependency);
        }

        //Submodules
        for(DbModule dbSubmodule: dbModule.getSubmodules()){
            module.addSubmodule(getModule(dbSubmodule));
        }

        return module;
    }

    /**
     * Delete a module
     *
     * WARNING: It does not delete the thirdparty
     *
     * @param name
     * @param version
     */
    public void deleteModule(final String name, final String version) {
        final String moduleId = DbModule.generateID(name, version);
        final DbModule module = repoHandler.getModule(moduleId);

        if(module == null){
            throw new  NotFoundException();
        }

        repoHandler.deleteModule(moduleId);
        for(String gavc: DataUtils.getAllArtifacts(module)){
            repoHandler.deleteArtifact(gavc);
        }
    }

    /**
     * Perform the module promotion
     *
     * @param name
     * @param version
     */
    public void promoteModule(final String name, final String version) {
        final String moduleId = DbModule.generateID(name, version);
        final DbModule module = repoHandler.getModule(moduleId);

        if(module == null){
            throw new  NotFoundException();
        }

        for(String gavc: DataUtils.getAllArtifacts(module)){
            final DbArtifact artifact = repoHandler.getArtifact(gavc);
            artifact.setPromoted(true);
            repoHandler.store(artifact);
        }

        repoHandler.promoteModule(module);

    }

    /**
     * Return an ancestors view of a module regarding the filters
     *
     * @param name
     * @param version
     * @param filters
     * @return DependencyListView that contains the ancestors
     */
    public DependencyListView getModuleAncestors(final String name, final String version, final FiltersHolder filters) {
        final String moduleId = DbModule.generateID(name, version);
        final DbModule module = repoHandler.getModule(moduleId);

        if(module == null){
            throw  new NotFoundException();
        }

        final DependencyListView view = new DependencyListView("Ancestor List Of " + name +" in version " + version , licenseHandler.getLicenses(), filters.getDecorator());

        final List<String> artifacts = DataUtils.getAllArtifacts(module);
        for(String gavc: artifacts){
            final DbArtifact dbArtifact = repoHandler.getArtifact(gavc);
            for(DbModule dbAncestor : repoHandler.getAncestors(gavc, filters)){
                if (!dbAncestor.getId().equals(module.getId())) {
                    for(DbDependency dbDependency: DataUtils.getAllDbDependencies(dbAncestor)){
                        if(dbDependency.getTarget().equals(gavc)){
                            final Dependency dependency = DataModelFactory.createDependency(DataUtils.getArtifact(dbArtifact), dbDependency.getScope());
                            dependency.setSourceName(dbAncestor.getName());
                            dependency.setSourceVersion(dbAncestor.getVersion());
                            view.addDependency(dependency);
                        }
                    }
                }
            }
        }

        return view;
    }

    /**
     * Return a dependency view of a module regarding the filters
     *
     * @param name
     * @param version
     * @param filters
     * @return DependencyListView
     */
    public DependencyListView getModuleDependencies(final String name, final String version, final FiltersHolder filters, final Boolean toUpdate) {
        final String moduleId = DbModule.generateID(name, version);
        final DbModule module = repoHandler.getModule(moduleId);
        if(module == null){
            throw  new NotFoundException();
        }

        final VersionsHandler versionHandler = new VersionsHandler(repoHandler);
        final DependenciesHandler depHandler = new DependenciesHandler(repoHandler, filters);

        final List<DbDependency> dbDependencies = depHandler.getDependencies(moduleId);
        final List<String> artifacts = DataUtils.getAllArtifacts(module);

        final DependencyListView view = new DependencyListView("Dependency List Of " + name + " in version " + version, licenseHandler.getLicenses(), filters.getDecorator());

        for(DbDependency dbDependency: dbDependencies){
            // Avoid to get internal dependencies in module dependencies
            if(!artifacts.contains(dbDependency.getTarget())){
                final DbArtifact artifact = repoHandler.getArtifact(dbDependency.getTarget());
                final Dependency dependency = DataModelFactory.createDependency(DataUtils.getArtifact(artifact), dbDependency.getScope());
                dependency.setSourceName(DataUtils.getModuleName(dbDependency.getSource()));
                dependency.setSourceVersion(DataUtils.getModuleVersion(dbDependency.getSource()));

                // Filter Dependencies if toUpdate parameters has been provided buy the client
                if(toUpdate == null || toUpdate != versionHandler.isUpToDate(artifact)){
                    view.addDependency(dependency);
                }
            }
        }

        return view;
    }

    /**
     * Return a licenses view of the targeted module, regarding the filters
     *
     * @param name
     * @param version
     * @return ListView
     */
    public ListView getModuleLicenses(final String name, final String version) {
        final String moduleId = DbModule.generateID(name, version);
        final DbModule module = repoHandler.getModule(moduleId);

        if(module == null){
            throw new NotFoundException();
        }

        final FiltersHolder filters = new FiltersHolder(config.getCorporateGroupIds());
        filters.getDepthHandler().setFullRecursive(true);
        filters.getDecorator().setShowThirdparty(true);

        final Filter hasLicenseFilter = new HasLicenseFilter(true);
        filters.addFilter(hasLicenseFilter);

        final DependenciesHandler depsHandler = new DependenciesHandler(repoHandler, filters);

        final ListView view = new ListView("Licenses of " + name + " in version " + version, "license");

        for(DbDependency dependency: depsHandler.getDependencies(moduleId)){
            final DbArtifact artifact = repoHandler.getArtifact(dependency.getTarget());
            view.addAll(artifact.getLicenses());
        }

        return view;
    }

    /**
     * Check if a module can be promoted
     *
     * @param name
     * @param version
     * @return Boolean
     */
    public Boolean canModuleBePromoted(final String name, final String version) {
        final PromotionReportView report = getPromotionReport(name, version, new FiltersHolder(config.getCorporateGroupIds()));

        if(report == null){
            throw new NotFoundException();
        }

        return report.canBePromoted();
    }

    /**
     * Provide a report about the promotion feasibility
     *
     * @param name
     * @param version
     * @param filters
     * @return PromotionReportView
     */
    public PromotionReportView getPromotionReport(final String name, final String version, final FiltersHolder filters) {
        final String moduleId = DbModule.generateID(name, version);
        final DbModule module = repoHandler.getModule(moduleId);

        if(module == null){
            throw new NotFoundException();
        }

        final PromotionReportView report = new PromotionReportView();
        report.setRootModule(DataModelFactory.createModule(module.getName(), module.getVersion()));

        final DependenciesHandler depHandler = new DependenciesHandler(repoHandler, filters);

        // Checks if all the dependency modules has been promoted first
        for(DbModule dependency: depHandler.getModuleDependencies(moduleId, true)){
            if(!dependency.isPromoted()){
                report.addUnPromotedDependency(DataModelFactory.createModule(dependency.getName(), dependency.getVersion()));

                if(filters.getDepthHandler().getFullRecursive()){
                    report.addDependencyReport(dependency.getId(), getPromotionReport(dependency.getName(), dependency.getVersion(), filters));
                }
            }
        }

        // Checks if the module has dependencies that shouldn't be used
        final List<String> treatedArtifacts = new ArrayList<String>();
        for(DbDependency dependency: DataUtils.getAllDbDependencies(module)){
            final DbArtifact artifactDep = repoHandler.getArtifact(dependency.getTarget());

            if(artifactDep.getDoNotUse() && !treatedArtifacts.contains(artifactDep.getGavc())){
                report.addDoNotUseArtifact(DataUtils.getArtifact(artifactDep));
                treatedArtifacts.add(artifactDep.getGavc());
            }
        }

        return report;
    }

    /**
     * Generates an module AbstractGraph regarding the filters
     *
     * @param moduleName
     * @param moduleVersion
     * @param filters
     * @return AbstractGraph A graph of Artifact
     */
    public AbstractGraph getModuleGraph(final String moduleName, final String moduleVersion, final FiltersHolder filters) {
        final String moduleId = DbModule.generateID(moduleName, moduleVersion);
        final GraphsHandler builder = new GraphsHandler(repoHandler, filters);

        return builder.getModuleGraph(moduleId);
    }

    /**
     * Generate an module AbstractGraph regarding the filters
     *
     * @param moduleName
     * @param moduleVersion
     * @param filters
     * @return TreeNode
     */
    public TreeNode getModuleTree(final String moduleName, final String moduleVersion, final FiltersHolder filters) {
        final String moduleId = DbModule.generateID(moduleName, moduleVersion);
        final GraphsHandler builder = new GraphsHandler(repoHandler, filters);

        return builder.getModuleTree(moduleId);
    }

    /**
     * Generate a report about the targeted module dependencies
     *
     * @param name
     * @param version
     * @param filters
     * @return DependencyReport
     */
    public DependencyReport getDependencyReport(final String name, final String version, final FiltersHolder filters) throws NotHandledVersionException, IncomparableException {
        final String moduleId = DbModule.generateID(name, version);
        final DependenciesHandler dependencyHandler = new DependenciesHandler(repoHandler, filters);

        return dependencyHandler.getReport(moduleId);
    }

    /**
     * Provide a list of artifact regarding the filters
     *
     * @param filters
     * @return List<Artifact>
     */
    public List<Artifact> getArtifacts(final FiltersHolder filters) {
        final List<Artifact> artifacts = new ArrayList<Artifact>();
        final List<DbArtifact> dbArtifacts = repoHandler.getArtifacts(filters);

        for(DbArtifact dbArtifact: dbArtifacts){
            artifacts.add(DataUtils.getArtifact(dbArtifact));
        }

        return artifacts;
    }

    /**
     * Add "DO_NOT_USE" flag to an artifact
     *
     * @param gavc
     * @param doNotUse
     */
    public void setDoNotUse(final String gavc, final Boolean doNotUse) {
        final DbArtifact artifact = repoHandler.getArtifact(gavc);

        if(artifact == null){
            throw new NotFoundException();
        }

        repoHandler.updateDoNotUse(artifact, doNotUse);
    }

    /**
     * Provide a list of module regarding the filters
     *
     * @param filters
     * @return List<Module>
     */
    public List<Module> getModules(final FiltersHolder filters) {
        final List<Module> modules = new ArrayList<Module>();
        final List<DbModule> dbModules = repoHandler.getModules(filters);

        for(DbModule dbModule: dbModules){
            final Module module = DataModelFactory.createModule(dbModule.getName(), dbModule.getVersion());
            modules.add(module);
        }

        return modules;
    }

    /**
     * Update artifact download url
     * @param gavc
     * @param downLoadUrl
     */
    public void updateDownLoadUrl(final String gavc, final String downLoadUrl) {
        final DbArtifact artifact = repoHandler.getArtifact(gavc);
        if(artifact == null){
            throw new NotFoundException();
        }

        repoHandler.updateDownloadUrl(artifact, downLoadUrl);
    }

    /**
     * Update artifact provider
     * @param gavc
     * @param provider
     */
    public void updateProvider(final String gavc, final String provider) {
        final DbArtifact artifact = repoHandler.getArtifact(gavc);
        if(artifact == null){
            throw new NotFoundException();
        }

        repoHandler.updateProvider(artifact, provider);
    }
}
