package org.axway.grapes.server.core;


import org.apache.commons.jcs.access.CacheAccess;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.core.cache.CacheName;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.options.filters.CorporateFilter;
import org.axway.grapes.server.core.options.filters.PromotedFilter;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.ModelMapper;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.webapp.views.PromotionReportView;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module Handler
 * <p>
 * <p>Manages all operation regarding Modules. It can, get/update Modules of the database.</p>
 *
 * @author jdcoffre
 */
public class ModuleHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleHandler.class);

    private final RepositoryHandler repositoryHandler;

    private CacheAccess<String, PromotionReportView> cache = null;


    public ModuleHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
        cache = CacheUtils.initCache(CacheName.PROMOTION_REPORTS, PromotionReportView.class);
    }

    /**
     * Add/update module in the database
     *
     * @param dbModule DbModule
     */
    public void store(final DbModule dbModule) {
        repositoryHandler.store(dbModule);
    }

    /**
     * Returns the available module names regarding the filters
     *
     * @param filters FiltersHolder
     * @return List<String>
     */
    public List<String> getModuleNames(final FiltersHolder filters) {
        return repositoryHandler.getModuleNames(filters);
    }

    /**
     * Returns the available module names regarding the filters
     *
     * @param name    String
     * @param filters FiltersHolder
     * @return List<String>
     */
    public List<String> getModuleVersions(final String name, final FiltersHolder filters) {
        final List<String> versions = repositoryHandler.getModuleVersions(name, filters);

        if (versions.isEmpty()) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Module " + name + " does not exist.").build());
        }

        return versions;
    }

    /**
     * Returns a module
     *
     * @param moduleId String
     * @return DbModule
     */
    public DbModule getModule(final String moduleId) {
        final DbModule dbModule = repositoryHandler.getModule(moduleId);

        if (dbModule == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Module " + moduleId + " does not exist.").build());
        }

        return dbModule;
    }

    /**
     * Delete a module
     *
     * @param moduleId String
     */
    public void deleteModule(final String moduleId) {
        final DbModule module = getModule(moduleId);
        repositoryHandler.deleteModule(module.getId());

        for (final String gavc : DataUtils.getAllArtifacts(module)) {
            repositoryHandler.deleteArtifact(gavc);
        }
    }

    /**
     * Return a licenses view of the targeted module
     *
     * @param moduleId String
     * @return List<DbLicense>
     */
    public List<DbLicense> getModuleLicenses(final String moduleId) {
        final DbModule module = getModule(moduleId);

        final List<DbLicense> licenses = new ArrayList<>();
        final FiltersHolder filters = new FiltersHolder();
        final ArtifactHandler artifactHandler = new ArtifactHandler(repositoryHandler);

        for (final String gavc : DataUtils.getAllArtifacts(module)) {
            licenses.addAll(artifactHandler.getArtifactLicenses(gavc, filters));
        }

        return licenses;
    }

    /**
     * Perform the module promotion
     *
     * @param moduleId String
     */
    public void promoteModule(final String moduleId) {
        final DbModule module = getModule(moduleId);

        for (final String gavc : DataUtils.getAllArtifacts(module)) {
            final DbArtifact artifact = repositoryHandler.getArtifact(gavc);
            artifact.setPromoted(true);
            repositoryHandler.store(artifact);
        }

        repositoryHandler.promoteModule(module);
    }

    /**
     * Provide a report about the promotion feasibility
     *
     * @param moduleId String
     * @return PromotionReportView
     */
    public PromotionReportView getPromotionReport(final String moduleId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format(":: Starting promo report [%s]", moduleId));
        }

        final PromotionReportView fromCache = cache.get(moduleId);
        if (null != fromCache) {
            return fromCache;
        }

        final DependencyHandler depHandler = new DependencyHandler(repositoryHandler);
        final ModelMapper modelMapper = new ModelMapper(repositoryHandler);
        final DbModule module = getModule(moduleId);
        final DbOrganization organization = getOrganization(module);


        final PromotionReportView report = new PromotionReportView();
        report.setRootModule(DataModelFactory.createModule(module.getName(), module.getVersion()));

        if (!report.isSnapshot()) {
            // filters initialization
            final FiltersHolder filters = new FiltersHolder();
            filters.addFilter(new PromotedFilter(false));
            filters.addFilter(new CorporateFilter(organization));

            // Checks if each dependency module has been promoted
            final List<Dependency> deps = depHandler.getModuleDependencies(moduleId, filters);
            removeDuplicates(deps);

            for (final Dependency dependency : deps) {
                final DbModule depModule = repositoryHandler.getRootModuleOf(dependency.getTarget().getGavc());
                if (depModule != null && !depModule.getId().equals(moduleId) && !depModule.isPromoted()) {
                    report.addUnPromotedDependency(depModule.getId());
                    report.addDependencyPromotionReport(depModule.getId(), getPromotionReport(depModule.getId()));
                }
            }

            // Checks if the module has dependencies that shouldn't be used
            final List<String> treatedArtifacts = new ArrayList<>();
            for (final DbDependency dependency : DataUtils.getAllDbDependencies(module)) {
                if(dependency.getScope().equals(Scope.TEST)) {
                    continue;
                }

                final DbArtifact artifactDep = repositoryHandler.getArtifact(dependency.getTarget());

                if (artifactDep == null) {
                    // handle the case of a corporate artifact which is not available in the repository
                    continue;
                }

                if (artifactDep.getDoNotUse() && !treatedArtifacts.contains(artifactDep.getGavc())) {
                    DbComment dbComment = repositoryHandler.getLatestComment(artifactDep.getGavc(), artifactDep.getClass().getSimpleName());
                    report.addDoNotUseArtifact(modelMapper.getArtifact(artifactDep), dbComment == null ? null : modelMapper.getComment(dbComment));
                    treatedArtifacts.add(artifactDep.getGavc());
                }

                // Checks if the module has third party dependency license missing
                // filter the corporate dependencies and check the third party
                List<String> artifactLicenses = artifactDep.getLicenses();
                if (!filters.getCorporateFilter().filter(dependency)) {
                    if (artifactLicenses.isEmpty()) {
                        // report.addMissingThirdPartyDependencyLicenses(modelMapper.getArtifact(artifactDep));
                        LOG.warn(String.format("Missing license on artifact [%s]", artifactDep.getGavc()));
                    } else {
                        // Check if the existing license name exists in the database
                        for (String licenseName : artifactLicenses) {
                            if (null == licenseName) {
                                continue;
                            }
                            DbLicense currentLicense = repositoryHandler.getLicense(licenseName);
                            if (currentLicense == null) {
                                // report.addMissingThirdPartyDependencyLicenses(modelMapper.getArtifact(artifactDep));
                                LOG.warn(String.format("Artifact license [%s] not known", licenseName));
                            } else if (currentLicense.isApproved() != null && !currentLicense.isApproved()) { // Check if the third party license is approved. If approved == null it is still valid license
                                // add to a not approved list
                                Pair<String, String> pair = Pair.create(modelMapper.getArtifact(artifactDep).getGavc(), modelMapper.getLicense(currentLicense).getName());
                                report.setDependenciesWithNotAcceptedLicenses(pair);
                            }
                        }
                    }
                }
            }
        }

        report.compute();
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format(":: Done promo report %s", moduleId));
        }

        cache.put(moduleId, report);
        return report;
    }

    private void removeDuplicates(List<Dependency> deps) {
        Map<String, Dependency> left = new HashMap<>();

        for (Dependency d : deps) {
            String key = d.getTarget().getGavc();
            if (!left.containsKey(key)) {
                left.put(key, d);
            }
        }

        deps.clear();
        deps.addAll(left.values());
    }

    public DbOrganization getOrganization(final DbModule module) {
        if (module.getOrganization() == null ||
                module.getOrganization().isEmpty()) {
            final DbOrganization organization = new DbOrganization();
            organization.setName("No organization registered");
            return organization;
        }

        final OrganizationHandler handler = new OrganizationHandler(repositoryHandler);
        return handler.getOrganization(module.getOrganization());
    }

    /**
     * Provides a list of module regarding the filters
     *
     * @param filters FiltersHolder
     * @return List<DbModule>
     */
    public List<DbModule> getModules(final FiltersHolder filters) {
        return repositoryHandler.getModules(filters);
    }

}
