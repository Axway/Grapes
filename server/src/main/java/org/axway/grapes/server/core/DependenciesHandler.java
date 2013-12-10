package org.axway.grapes.server.core;

import com.sun.jersey.api.NotFoundException;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.reports.DependencyReport;
import org.axway.grapes.server.core.version.IncomparableException;
import org.axway.grapes.server.core.version.NotHandledVersionException;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Dependencies Handler
 *
 * <p>This class handles the dependencies retrieval, sort & filtering</p>
 *
 * @author jdcoffre
 */
public class DependenciesHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DependenciesHandler.class);

    private final RepositoryHandler repoHandler;
    private final FiltersHolder filters;

    public DependenciesHandler(final RepositoryHandler repoHandler, final FiltersHolder filters) {
        this.repoHandler = repoHandler;
        this.filters = filters;
    }

    /**
     * Return the dependencies of a targeted module regarding the configured filters
     *
     * @param moduleId
     * @return List<DbDependency>
     */
    public List<DbDependency> getDependencies(final String moduleId) {
        final DbModule module = repoHandler.getModule(moduleId);
        if(module == null){
            throw  new NotFoundException();
        }

        final List<DbDependency> dependencies = new ArrayList<DbDependency>();
        addDependencies(module, dependencies, 1, new ArrayList<String>());

        return dependencies;
    }

    private void addDependencies(final DbModule module, final List<DbDependency> dependencies, final int depth, final List<String> done) {
        if(module == null || done.contains(module.getUid())){
            return ;
        }
        done.add(module.getUid());

        for(DbDependency dependency: DataUtils.getAllDbDependencies(module)){
            if(filters.shouldBeInReport(dependency) && !dependencies.contains(dependency)){
                dependencies.add(dependency);
            }

            if(filters.getDepthHandler().shouldGoDeeper(depth)){
                final DbModule dependencyModule = repoHandler.getModuleOf(dependency.getTarget());
                addDependencies(dependencyModule, dependencies, depth + 1, done);
            }
        }
    }

    /**
     * Generate a report about the targeted module dependencies
     *
     * @param moduleId
     * @return DependencyReport
     */
    public DependencyReport getReport(final String moduleId) throws NotHandledVersionException, IncomparableException {
        final DbModule module = repoHandler.getModule(moduleId);

        if(module == null){
            throw new NotFoundException();
        }

        final DependencyReport report = new DependencyReport(moduleId);
        final List<String> done = new ArrayList<String>();
        for(DbModule submodule: DataUtils.getAllSubmodules(module)){
            done.add(submodule.getUid());
        }

        addModuleToReport(report, module, done, 1);

        return report;
    }

    private void addModuleToReport(final DependencyReport report, final DbModule module, final List<String> done, final int depth) {
        if(module == null || done.contains(module.getUid())){
            return;
        }
        done.add(module.getUid());
        for(DbDependency dependency: DataUtils.getAllDbDependencies(module)){
            addDependenciesToReport(report, dependency, done, depth);
        }
    }

    private void  addDependenciesToReport(final DependencyReport report, final DbDependency dbDependency, final List<String> done, final int depth) {
        final DbArtifact artifact = repoHandler.getArtifact(dbDependency.getTarget());

        if(artifact == null){
            return;
        }

        if(filters.shouldBeInReport(dbDependency)){

            if(artifact.getDoNotUse()){
                report.addShouldNotUse(artifact.getGavc());
            }

            final VersionsHandler versionHandler = new VersionsHandler(repoHandler);
            String lastRelease = null;

            try{
                lastRelease = versionHandler.getLastRelease(repoHandler.getArtifactVersions(artifact));
            }catch (Exception e){
                LOG.info("Failed to find the latest artifact release version: " + artifact.getVersion());
            }

            final Dependency dependency = DataModelFactory.createDependency(DataUtils.getArtifact(artifact), dbDependency.getScope());
            dependency.setSourceName(DataUtils.getModuleName(dbDependency.getSource()));
            dependency.setSourceVersion(DataUtils.getModuleVersion(dbDependency.getSource()));
            report.addDependency(dependency, lastRelease);
        }

        if(filters.getDepthHandler().shouldGoDeeper(depth)){
            final DbModule module = repoHandler.getModuleOf(dbDependency.getTarget());
            addModuleToReport(report, module, done, depth + 1);
        }
    }

    /**
     * Return the list of modules dependencies
     * @param moduleId
     * @param rootOnly if true, only root module will be in the result list
     * @return List<DbModule>
     */
    public List<DbModule> getModuleDependencies(final String moduleId, final boolean rootOnly){
        final DbModule module = repoHandler.getModule(moduleId);

        if(module == null){
            throw new NotFoundException();
        }

        final List<DbModule> modules = new ArrayList<DbModule>();
        final List<String> treatedModules = new ArrayList<String>();
        treatedModules.add(moduleId);

        for(DbDependency dependency: getAllAxwayDependencies(module)){
            DbModule depModule;
            if(rootOnly){
                depModule = repoHandler.getRootModuleOf(dependency.getTarget());
            }
            else{
                depModule = repoHandler.getModuleOf(dependency.getTarget());
            }

            if(depModule == null || treatedModules.contains(depModule.getUid())){
               continue;
            }

            modules.add(depModule);
            treatedModules.add(depModule.getUid());
        }

        return modules;
    }


    /**
     * Return all the Axway dependencies of a module
     * @param module
     * @return List<DbDependency>
     */
    public List<DbDependency> getAllAxwayDependencies(final DbModule module) {
        final List<DbDependency> axwayDependencies = new ArrayList<DbDependency>();

        for(DbDependency dependency: DataUtils.getAllDbDependencies(module)){
            if(filters.getCorporateFilter().matches(dependency)){
                axwayDependencies.add(dependency);
            }
        }

        return axwayDependencies;
    }

}
