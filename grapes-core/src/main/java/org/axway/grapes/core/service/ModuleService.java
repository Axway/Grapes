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
    void store(Module dbModule);

  List<String> getModuleNames(FiltersHolder filters);

   List<String> getModuleVersions(String name, FiltersHolder filters);

    Module getModule(String moduleId);

    void deleteModule(String moduleId);

    List<License> getModuleLicenses(String moduleId);

    void promoteModule(String moduleId);

   //TODO PromotionReportView getPromotionReport(String moduleId);
Organization getOrganization(Module module);

   List<Module> getModules(FiltersHolder filters);
    void removeModulesOrganization(final Organization organization);
    void removeModulesOrganization(final String corporateGidPrefix, final Organization organization);
    void addModulesOrganization(final String corporateGidPrefix, final Organization organization);

    Module getRootModuleOf(String gavc);

    List<Module> getAncestors(Artifact artifact, FiltersHolder filters);
}
