package org.axway.grapes.core.handler;

import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.options.filters.CorporateFilter;
import org.axway.grapes.core.reports.DependencyReport;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.DependencyService;
import org.axway.grapes.core.service.ModuleService;
import org.axway.grapes.core.service.VersionsService;
import org.axway.grapes.core.webapi.resources.DependencyComplete;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.annotations.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jennifer on 4/28/15.
 */
@Service
public class DependencyHandler implements DependencyService {

    private static final Logger LOG = LoggerFactory.getLogger(DependencyHandler.class);
    @Requires
    ModuleService moduleService;
    @Requires
    ArtifactService artifactService;
    @Requires
    VersionsService versionsService;
    @Requires
    DataUtils dataUtils;

    @Override
    public List<Dependency> getModuleDependencies(String moduleId, FiltersHolder filters) {
        LOG.error("inside of the level one get dependencies");
        final Module module = moduleService.getModule(moduleId);
        final Organization organization = moduleService.getOrganization(module);
        filters.setCorporateFilter(new CorporateFilter(organization));
        return getModuleDependencies(module, filters, 1, new ArrayList<String>());
    }

    private List<Dependency> getModuleDependencies(final Module module, final FiltersHolder filters, final int depth, final List<String> doneModuleIds) {
        // Checks if the module has already been done
        LOG.error("inside level two of get depe");
        if (module == null || doneModuleIds.contains(module.getId())) {
            LOG.error("returnings empty list");
            return Collections.<Dependency>emptyList();
        } else {
            doneModuleIds.add(module.getId());
        }
        final List<Dependency> dependencies = new ArrayList<Dependency>();
        for (Dependency dependency : dataUtils.getAllDbDependencies(module)) {
            LOG.error(" should be in report ? "+dependency.getTarget()+" "+filters.shouldBeInReport(dependency));
            if (filters.shouldBeInReport(dependency)) {
                // final Dependency dependency = modelMapper.getDependency(dbDependency, module.getName(), module.getVersion());
                dependencies.add(dependency);
                if (filters.getDepthHandler().shouldGoDeeper(depth)) {
                    LOG.error("checking on dependencie: "+dependency.generateSourceID()+" for target "+dependency.getTarget());
                    final Module dependencyModule = moduleService.getRootModuleOf(dependency.getTarget());
                    //todo if rootmodual is the same as original module no need to recall this
                    LOG.error(" the root modal for this dependencie is "+dependencyModule);
                    dependencies.addAll(getModuleDependencies(dependencyModule, filters, depth + 1, doneModuleIds));
                }
            }
        }
        return dependencies;
    }

    @Override
    public DependencyReport getDependencyReport(String moduleId, FiltersHolder filters) {
        final Module module = moduleService.getModule(moduleId);
        final Organization organization = moduleService.getOrganization(module);
        filters.setCorporateFilter(new CorporateFilter(organization));
        final DependencyReport report = new DependencyReport(moduleId);
        final List<String> done = new ArrayList<String>();
        for (Module submodule : dataUtils.getAllSubmodules(module)) {
            done.add(submodule.getId());
        }
        addModuleToReport(report, module, filters, done, 1);
        return report;
    }

    private void addModuleToReport(final DependencyReport report, final Module module, final FiltersHolder filters, final List<String> done, final int depth) {
        if (module == null || done.contains(module.getId())) {
            return;
        }
        done.add(module.getId());
        for (Dependency dependency : dataUtils.getAllDbDependencies(module)) {
            addDependenciesToReport(report, dependency, filters, done, depth);
        }
    }

    //todo need to create a report
    private void addDependenciesToReport(final DependencyReport report, final Dependency dbDependency, final FiltersHolder filters, final List<String> done, final int depth) {
        final Artifact artifact = artifactService.getArtifact(dbDependency.getTarget());
        if (artifact == null) {
            return;
        }
        if (filters.shouldBeInReport(dbDependency)) {
            if (artifact.getDoNotUse()) {
                report.addShouldNotUse(artifact.getGavc());
            }
            String lastRelease = null;
            try {
                lastRelease = versionsService.getLastRelease(artifactService.getArtifactVersions(artifact));
            } catch (Exception e) {
                LOG.info("Failed to find the latest artifact release version: " + artifact.getVersion());
            }
            final DependencyComplete dependency = new DependencyComplete();
            dependency.setTarget(artifact);
            dependency.setScope(dbDependency.getScope());
            dependency.setSourceName(dataUtils.getModuleName(dbDependency.getSource()));
            dependency.setSourceVersion(dataUtils.getModuleVersion(dbDependency.getSource()));
            report.addDependency(dependency, lastRelease);
        }
        if (filters.getDepthHandler().shouldGoDeeper(depth)) {
            final Module module = moduleService.getRootModuleOf(dbDependency.getTarget());
            addModuleToReport(report, module, filters, done, depth + 1);
        }
    }
}
