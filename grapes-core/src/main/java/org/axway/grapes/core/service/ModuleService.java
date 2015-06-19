package org.axway.grapes.core.service;

import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;

import java.util.List;

/**
 * Created by jennifer on 4/24/15.
 */
public interface ModuleService {

    public void store(Module dbModule);

    public Module getModule(String moduleId);

    public List<String> getModuleNames(FiltersHolder filters);

    public List<Module> getModules(FiltersHolder filters);

    public List<Artifact> getModuleArtifacts(String moduleId);

    public void getModuleDependencies();

    public List<License> getModuleLicenses(String moduleId);

    public List<String> getModuleVersions(String name, FiltersHolder filters);

    public Organization getOrganization(Module module);

    Module getModuleOf(String gavc);

    public Module getRootModuleOf(String gavc);

    public void promoteModule(String moduleId);

    public boolean canBePromoted(Module module);

    public void deleteModule(String moduleId);

    List<Module> getAncestors(Artifact artifact, FiltersHolder filters);

    Organization getMatchingOrganization(Module module);
    //TODO PromotionReportView getPromotionReport(String moduleId);
}
