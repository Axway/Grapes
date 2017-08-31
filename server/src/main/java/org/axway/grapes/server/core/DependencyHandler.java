package org.axway.grapes.server.core;

import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.server.core.interfaces.LicenseMatcher;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.options.filters.CorporateFilter;
import org.axway.grapes.server.core.reports.DependencyReport;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.ModelMapper;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dependency Handler
 *
 * <p>Manages all reports regarding Dependencies.</p>
 *
 * @author jdcoffre
 */
public class DependencyHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DependencyHandler.class);


    private final RepositoryHandler repositoryHandler;
    private final ModelMapper modelMapper;
    private final ModuleHandler moduleHandler;

    public DependencyHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
        this.modelMapper = new ModelMapper(repositoryHandler);
        this.moduleHandler = new ModuleHandler(repositoryHandler);
    }

    /**
     * Returns the list of module dependencies regarding the provided filters
     *
     * @param moduleId String
     * @param filters FiltersHolder
     * @return List<Dependency>
     */
    public List<Dependency> getModuleDependencies(final String moduleId, final FiltersHolder filters){
        final DbModule module = moduleHandler.getModule(moduleId);
        final DbOrganization organization = moduleHandler.getOrganization(module);
        filters.setCorporateFilter(new CorporateFilter(organization));

        return getModuleDependencies(module, filters, 1, new ArrayList<String>());
    }

    private List<Dependency> getModuleDependencies(final DbModule module, final FiltersHolder filters, final int depth, final List<String> doneModuleIds){
        // Checks if the module has already been done
        if(module == null || doneModuleIds.contains(module.getId())){
            return Collections.<Dependency>emptyList();
        }
        else {
            doneModuleIds.add(module.getId());
        }

        final List<Dependency> dependencies = new ArrayList<Dependency>();
        for(final DbDependency dbDependency: DataUtils.getAllDbDependencies(module)){
            if(filters.shouldBeInReport(dbDependency)){
                final Dependency dependency = modelMapper.getDependency(dbDependency, module.getName(), module.getVersion());
                dependencies.add(dependency);

                if(filters.getDepthHandler().shouldGoDeeper(depth)){
                    final DbModule dependencyModule = repositoryHandler.getRootModuleOf(dbDependency.getTarget());
                    dependencies.addAll(getModuleDependencies(dependencyModule, filters, depth + 1, doneModuleIds));
                }
            }
        }

        return dependencies;
    }

    /**
     * Generate a report about the targeted module dependencies
     *
     * @param moduleId String
     * @param filters FiltersHolder
     * @return DependencyReport
     */
    public DependencyReport getDependencyReport(final String moduleId, final FiltersHolder filters) {
        final DbModule module = moduleHandler.getModule(moduleId);
        final DbOrganization organization = moduleHandler.getOrganization(module);
        filters.setCorporateFilter(new CorporateFilter(organization));

        final DependencyReport report = new DependencyReport(moduleId);
        final List<String> done = new ArrayList<String>();
        for(final DbModule submodule: DataUtils.getAllSubmodules(module)){
            done.add(submodule.getId());
        }

        addModuleToReport(report, module, filters, done, 1);

        return report;
    }

    private void addModuleToReport(final DependencyReport report, final DbModule module, final FiltersHolder filters, final List<String> done, final int depth) {
        if(module == null || done.contains(module.getId())){
            return;
        }
        done.add(module.getId());
        for(final DbDependency dependency: DataUtils.getAllDbDependencies(module)){
            addDependenciesToReport(report, dependency, filters, done, depth);
        }
    }

    private void  addDependenciesToReport(final DependencyReport report, final DbDependency dbDependency, final FiltersHolder filters, final List<String> done, final int depth) {
        final DbArtifact artifact = repositoryHandler.getArtifact(dbDependency.getTarget());

        if(artifact == null){
            return;
        }

        if(filters.shouldBeInReport(dbDependency)){

            if(artifact.getDoNotUse()){
                report.addShouldNotUse(artifact.getGavc());
            }

            final VersionsHandler versionHandler = new VersionsHandler(repositoryHandler);
            String lastRelease = null;

            try{
                lastRelease = versionHandler.getLastRelease(repositoryHandler.getArtifactVersions(artifact));
            }catch (Exception e){
                LOG.info("Failed to find the latest artifact release version: " + artifact.getVersion(), e);
            }

            final Dependency dependency = DataModelFactory.createDependency(modelMapper.getArtifact(artifact), dbDependency.getScope());
            dependency.setSourceName(DataUtils.getModuleName(dbDependency.getSource()));
            dependency.setSourceVersion(DataUtils.getModuleVersion(dbDependency.getSource()));
            report.addDependency(dependency, lastRelease);
        }

        if(filters.getDepthHandler().shouldGoDeeper(depth)){
            final DbModule module = repositoryHandler.getRootModuleOf(dbDependency.getTarget());
            addModuleToReport(report, module, filters, done, depth + 1);
        }
    }
}
