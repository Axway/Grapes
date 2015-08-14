package org.axway.grapes.core.handler;
//todo so many todos.....
import com.google.common.collect.Lists;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.options.filters.CorporateFilter;
import org.axway.grapes.core.options.filters.PromotedFilter;
import org.axway.grapes.core.reports.PromotionReport;
import org.axway.grapes.core.reports.ReportToJson;
import org.axway.grapes.core.service.DependencyService;
import org.axway.grapes.core.webapi.utils.JongoUtils;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.MiscService;
import org.axway.grapes.core.service.ModuleService;
import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.jongo.datamodel.DbArtifact;
import org.axway.grapes.jongo.datamodel.DbDependency;
import org.axway.grapes.jongo.datamodel.DbModule;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.DataModelFactory;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.annotations.Model;
import org.wisdom.api.annotations.Service;
import org.wisdom.api.model.Crud;
import org.wisdom.jongo.service.MongoFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by jennifer on 4/28/15.
 */
@Service
public class ModuleHandler implements ModuleService {
    private static final Logger LOG = LoggerFactory.getLogger(ModuleHandler.class);
    @Model(value = Module.class)
    private Crud<Module, String> moduleCrud;
    @Model(value = Organization.class)
    private Crud<Organization, String> organizationCrud;
    @Model(value = Artifact.class)
    private Crud<Artifact, String> artifactCrud;
    @Requires(optional = true)
    private ArtifactService artifactService;
    @Requires(optional = true)
    private DependencyService dependencyService;



    @Requires(optional = true)
    private OrganizationService organizationService;
    //  private final OrganizationService organizationService = new OrganizationHandler();

    @Requires
    DataUtils dataUtils;

    @Override
    public void store(Module module) {
        System.out.println("module id: " + module.getId());
        final Module oldModule = moduleCrud.findOne(module.getId());
        // has to be done due to mongo limitation: https://jira.mongodb.org/browse/SERVER-267
        module.updateHasAndUse();
        if (oldModule == null) {
            System.out.println("save new mod " + module.getId());
            moduleCrud.save(module);
            Module m = moduleCrud.findOne(module.getId());
            System.out.printf("after save: " + m.getId());
        } else {
            System.out.println("update exsisting");
            // let's keep the old build info and override with new values if any
            final Map<String, String> consolidatedBuildInfo = oldModule.getBuildInfo();
            consolidatedBuildInfo.putAll(module.getBuildInfo());
            module.setBuildInfo(consolidatedBuildInfo);
            moduleCrud.save(module);
        }
    }

    @Override
    public Module getModule(String moduleId) {
        final Module module = moduleCrud.findOne(moduleId);
        if (module == null) {
            throw new NoSuchElementException("Module with id: "+moduleId);
        }
        return module;
    }

    @Override
    public List<String> getModuleNames(FiltersHolder filters) {
        Set<String> listOfNames = new HashSet<>();
        Iterable<Module> list = moduleCrud.findAll(
                new MongoFilter<Module>(JongoUtils.generateQuery(filters.getModuleFieldsFilters())));

        for (Module module : list) {
            listOfNames.add(module.getName());
        }
        return Lists.newArrayList(listOfNames);
    }

    @Override

    public List<Module> getModules(FiltersHolder filters) {
        Iterable<Module> list = moduleCrud.findAll(
                new MongoFilter<Module>(JongoUtils.generateQuery(filters.getModuleFieldsFilters())));
        return Lists.newArrayList(list);
    }

    @Override
    public List<Artifact> getModuleArtifacts(String moduleId) {
        return getModuleArtifacts(getModule(moduleId));
    }

    @Override
    public void getModuleDependencies() {
    }

    @Override
    //todo this does not get 3rd party artifact licenses maybe it should?
    public List<License> getModuleLicenses(String moduleId) {
        final Module module = getModule(moduleId);
        final Set<License> licenses = new HashSet<>();
        final FiltersHolder filters = new FiltersHolder();
        for (Artifact artifact : getModuleArtifacts(module)) {
            licenses.addAll(artifactService.getArtifactLicenses(artifact.getGavc(), filters));
        }

        return Lists.newArrayList(licenses);
    }

    @Override

    public List<String> getModuleVersions(String name, FiltersHolder filters) {
        final Map<String, Object> params = filters.getModuleFieldsFilters();
        params.put("name",name);
 Iterable<Module> list = moduleCrud.findAll(new MongoFilter<Module>(JongoUtils.generateQuery(params)));
        Set<String> listOfVersions = new HashSet<>();
        if (Lists.newArrayList(list).isEmpty()) {
            throw new NoSuchElementException(name);
        }
        for (Module module : list) {
            listOfVersions.add(module.getVersion());
        }
        return Lists.newArrayList(listOfVersions);
    }

    @Override

    public Organization getOrganization(Module module) {
        if (module.getOrganization() == null ||
                module.getOrganization().isEmpty()) {
            final Organization organization = new Organization();
            organization.setName("No organization registered");
            return organization;
        }
        return organizationService.getOrganization(module.getOrganization());

    }
    @Override
    //todo
    public Module getModuleOf(final String gavc) {
        final Module module = getRootModuleOf(gavc);

//        // It may be a submodule...
//        if(module != null && !module.getArtifacts().contains(gavc)){
//            for(Module submodule: DataUtils.getAllSubmodules(module)){
//                if(submodule.getArtifacts().contains(gavc)){
//                    return submodule;
//                }
//            }
//        }

        return module;
    }
    @Override
    //todo check
    public Module getRootModuleOf(final String gavc) {
        System.out.println(JongoUtils.generateQuery("has", gavc));
        Module module = moduleCrud.findOne(new MongoFilter<Module>(JongoUtils.generateQuery("has", gavc)));
        Module module2 = moduleCrud.findOne(new MongoFilter<Module>(JongoUtils.generateQuery("uses", gavc)));
        LOG.error("INSDIE FIND ROOT for: "+gavc+". Found in has "+module +" found in use "+module2);

            return module;

//        return datastore.getCollection(DbCollections.DB_MODULES)
//                .findOne(JongoUtils.generateQuery(DbModule.HAS_DB_FIELD, gavc))
//                .as(DbModule.class);
    }

    @Override
    public void promoteModule(String moduleId) {
        final Module module = getModule(moduleId);
      promoteArtifacts(true, module);
         module.setPromoted(true);
        moduleCrud.save(module);
    }

    @Override
    public boolean canBePromoted(Module module) {
        //todo currently this returns true if not a snapshot, all artifacts dont have do not use, all dependencies are promoted see github issues.
        //this is defined in the promotion report.
        return false;
    }

    public void promoteArtifacts(Boolean promoted, Module module){

        for (String artifactGavc : dataUtils.getAllArtifactsGavcs(module)) {
            Artifact artifact= artifactService.getArtifact(artifactGavc);
            artifact.setPromoted(promoted);
            artifactService.store(artifact);
        }


    }

      public PromotionReport getPromotionReport(final String moduleId, FiltersHolder filters) {
        //fitlers passed in from context?scopeTest=true&scopeRuntime=true&showThirdparty=true&showCorporate=false&showSources=false&showLicenses=true&fullRecursive=true
        final Module module = getModule(moduleId);
        final Organization organization = getOrganization(module);


        final PromotionReport report = new PromotionReport();
        report.setRootModule(module);

        if(!report.isSnapshot()) {//should have some sort of semantic versioning flag because they can be promoted as snapshots
            // filters initialization
//            final FiltersHolder filters = new FiltersHolder();
//           filters.addFilter(new PromotedFilter(false));
//           filters.addFilter(new CorporateFilter(organization));

            // Checks if each dependency module has been promoted
            for (Dependency dependency : dependencyService.getModuleDependencies(moduleId, filters)) {
                final Module depModule = getRootModuleOf(dependency.getTarget());
                if (depModule != null && !depModule.getId().equals(moduleId)) {
                    if (!depModule.isPromoted()) {
                        report.addUnPromotedDependency(depModule.getId());
                        report.addDependencyPromotionReport(depModule.getId(), getPromotionReport(depModule.getId(),filters));
                    }
                }
            }

            // Checks if the module has dependencies that shouldn't be used
            final List<String> treatedArtifacts = new ArrayList<String>();

            for (Dependency dependency : dataUtils.getAllDbDependencies(module)) {
                final Artifact artifactDep = artifactService.getArtifact(dependency.getTarget());
                LOG.error(" artifact DONOTUSE:::: "+artifactDep.getGavc()+"  "+artifactDep.getDoNotUse());

                if (artifactDep == null) {
                    // handle the case of a corporate artifact which is not available in the repository
                    continue;
                }
                LOG.error("if: "+artifactDep.getDoNotUse()+"  "+!treatedArtifacts.contains(artifactDep.getGavc()));
                if (artifactDep.getDoNotUse() && !treatedArtifacts.contains(artifactDep.getGavc())) {
                    LOG.error("made it into the if");
                    report.addDoNotUseArtifact(artifactDep);
                    treatedArtifacts.add(artifactDep.getGavc());
                }
            }
        }

        report.compute();

        return report;
    }

    @Override

    public void deleteModule(String moduleId) {
        final Module module = getModule(moduleId);

        for(String gavcs: dataUtils.getAllArtifactsGavcs(module)){
            artifactService.deleteArtifact(gavcs);
       }
        moduleCrud.delete(moduleId);
    }

    @Override
    //todo
    public List<Module> getAncestors(final Artifact artifact, final FiltersHolder filters) {
        final Map<String, Object> queryParams = filters.getModuleFieldsFilters();
        queryParams.put("DbModule.USE_DB_FIELD", artifact.getGavc());
//        final Iterable<Module> results = datastore.getCollection(DbCollections.DB_MODULES)
//                .find(JongoUtils.generateQuery(queryParams))
//                .as(DbModule.class);
        final List<Module> ancestors = new ArrayList<Module>();
//        for(Module ancestor: results){
//            ancestors.add(ancestor);
//        }
        return ancestors;
    }

    @Override
    public Organization getMatchingOrganization(Module module) {
        //todo
//        if(module.getOrganization() != null
//                && !module.getOrganization().isEmpty()){
//            return getOrganization(module.getOrganization());
//        }
//
//        for(Organization organization: organizationCrud.findAll()){
//            final CorporateFilter corporateFilter = new CorporateFilter(organization);
//            if(corporateFilter.matches(module)){
//                return organization;
//            }
//        }
        return null;
    }

    //todo this methos is in data utils
    public List<String> getModuleArtifactsGavcs(final Module module) {
        final List<String> gavcs = new ArrayList<>();
        for (String artifact: module.getArtifacts()){
            gavcs.add(artifact);
        }
        for (Module submodule : module.getSubmodules()) {
            gavcs.addAll(getModuleArtifactsGavcs(submodule));
        }
        return gavcs;
    }

    //todo this method is in data utiles
        public List<Artifact> getModuleArtifacts(final Module module) {
        final Set<Artifact> artifacts = new HashSet<Artifact>();
        List<String> gavcsList = getModuleArtifactsGavcs(module);
        for (String gavc : gavcsList) {
            //todo what happens if null
            artifacts.add(artifactCrud.findOne(gavc));
        }
        for (Module submodule : module.getSubmodules()) {
            artifacts.addAll(getModuleArtifacts(submodule));
        }
        return Lists.newArrayList(artifacts);
    }
}
