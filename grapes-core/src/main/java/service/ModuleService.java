package service;


import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;

import java.util.List;

/**
 * Created by jennifer on 4/24/15.
 */
public interface ModuleService {
    void store(Module dbModule);

//    List<String> getModuleNames(FiltersHolder filters);

//    List<String> getModuleVersions(String name, FiltersHolder filters);

    Module getModule(String moduleId);

    void deleteModule(String moduleId);

    List<License> getModuleLicenses(String moduleId);

    void promoteModule(String moduleId);

//    PromotionReportView getPromotionReport(String moduleId);
Organization getOrganization(Module module);

//    List<DbModule> getModules(FiltersHolder filters);
}
