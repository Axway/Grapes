//todo basically done the other todos
package org.axway.grapes.core.webapi.resources;

import com.google.common.collect.Lists;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.exceptions.DataValidationException;
import org.axway.grapes.core.handler.DataUtils;
import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.options.filters.CorporateFilter;
import org.axway.grapes.core.reports.DependencyReport;
import org.axway.grapes.core.reports.PromotionReport;
import org.axway.grapes.core.reports.ReportToJson;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.DependencyService;
import org.axway.grapes.core.service.ModuleService;
import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.core.webapi.utils.DataValidator;
import org.axway.grapes.model.api.ServerAPI;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Credential;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Body;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Path;
import org.wisdom.api.annotations.PathParameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.security.Authenticated;
import org.wisdom.api.templates.Template;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.net.URLEncoder;

/**
 * Created by jennifer on 4/28/15.
 */
@Controller
@Path(ServerAPI.MODULE_RESOURCE)
public class ModuleController extends DefaultController {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleController.class);
    @Requires
    ModuleService moduleService;
    @Requires
    DependencyService dependencyService;
    @Requires
    OrganizationService organizationService;
    @Requires
    ArtifactService artifactService;
    @Requires
    DataUtils dataUtils;
    @View("ModuleResourceDocumentation")
    Template ModuleResourceDocumentation;
    @Requires
    ReportToJson reportToJson;

    /**
     * @return the welcome page
     */
    @Route(method = HttpMethod.GET, uri = "")
    public Result welcome() {
        LOG.info("Got a post Module doc request.");

        return ok(render(ModuleResourceDocumentation, "welcome", "Welcome to The New Grapes Under Construction!"));
    }

    /**
     * todo donish
     * Handle the update/addition of a module in Grapes database.
     *
     * @param moduleComplete
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "")
    public Result postModule(@Body final ModuleComplete moduleComplete) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DEPENDENCY_NOTIFIER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
//todo need to check that everything is actually getting stored properly. if the module has an organization it is erased why?
        LOG.info("Got a post Module request.");
        // Checks if the data is corrupted
        try {
            DataValidator.validate(moduleComplete);
        } catch (DataValidationException e) {
            return ok(e.getLocalizedMessage()).status(Result.BAD_REQUEST).json();
        }
        final Module module = dataUtils.translateIntoModule(moduleComplete);
        // Add the artifacts
        final Set<Artifact> artifacts = dataUtils.getAllArtifacts(moduleComplete);
        for (final Artifact artifact : artifacts) {
            //todo should check if the module is promtoted if it is then all the artifacts in the model should be set to promoted also and saved to the database?
            //this stores the gavcs
            artifactService.store(artifact);
        }
//        Add dependencies that does not already exist
        for (final DependencyComplete dep : dataUtils.getAllDependencies(moduleComplete)) {
            artifactService.storeIfNew(dep.getTarget());
        }
        // Save the module
        moduleService.store(module);
        //todo maybe it should do something differnt like create the organization in the database? or throw not found?
        try {
            final Organization organization = organizationService.getMatchingOrganization(module);
            if (organization != null) {
                module.setOrganization(organization.getName());
            }
            return ok().status(Result.CREATED);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The organization " + module.getOrganization() + " does not exist in the DB  but the model was created anyways muahahahah");
        }
    }
    //todo is null when encoded encoding problem? : encoded as %3a

    /**
     * A method that redirects to prompt for a module version.
     * A limitation wisdom can't differntiate the path for /all or /names so they are included inside this method.
     *
     * @param name of the module to get.
     * @return returns all modules if the path was /all, all names if the path was /names, and redirects in all other cases.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}")
    public Result getVersionsRedirection(@PathParameter("name") final String name) {
        //this is the (method = HttpMethod.GET, uri = "/all") method
        if ("all".equalsIgnoreCase(name)) {
            LOG.info("Got a get all modules request.");
            final FiltersHolder filters = new FiltersHolder();
            filters.init(context().parameters());
            final List<Module> modules = moduleService.getModules(filters);
            return ok(modules).json();
        } else if ("names".equalsIgnoreCase(name)) {
            LOG.info("Got a get all module names request.");
            final FiltersHolder filters = new FiltersHolder();
            filters.init(context().parameters());
            final List<String> moduleNames = moduleService.getModuleNames(filters);
            Collections.sort(moduleNames);
            return ok(moduleNames).json();
        }
        LOG.info("Got a get module name request. redirecting to versions :" + name);

        try {


            return redirect("/module/" + URLEncoder.encode(name,"UTF-8") + "/versions");
        } catch (UnsupportedEncodingException e) {
            return ok(e.getMessage()).json();
        }


    }

    /**
     * REDIRECTS TO HERE
     *
     * Return a list versions for a module, stored in Grapes, depending on the filters passed in the query parameters.
     * This method is call via GET <dm_url>/module/<name>/versions.
     *
     * @param name String name of the module
     * @return Result A list (in JSON) of versions.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}" + ServerAPI.GET_VERSIONS)
    public Result getVersions(@PathParameter("name") final String name) {
        LOG.info("Got a get versions request. " + name);
        final FiltersHolder filters = new FiltersHolder();
        filters.init(context().parameters());
        try {
            final List<String> versions = moduleService.getModuleVersions(name, filters);
            Collections.sort(versions);
            return ok(versions);
        } catch (NoSuchElementException e) {
            return ok("Sorry but no versions for the module " + name + " were found. Please check query parameters and that module exists").status(Result.NOT_FOUND);
        }
    }

    /**
     * Get a module.
     * This method is call via GET <dm_url>/module/<name>/<version>
     *
     * @param name    String of the module.
     * @param version String version of the module to find.
     * @return Result returns the module if it exists.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}")
    public Result get(@PathParameter("name") final String name, @PathParameter("version") final String version) {
        if("versions".equalsIgnoreCase(version)){
            LOG.info("Redirecting to list version for module.");
            return getVersions(name);
        }
        LOG.info("Got a get module request. ");
        final String moduleId = Module.generateID(name, version);
        try {
            final Module module = moduleService.getModule(moduleId);
            return ok(module);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
    }

    /**
     * todo done but what happens if half way through it finds an artifact that doesnt exsist? then it doesnt delete the rest of them
     * Delete a module.
     * This method is call via DELETE <dm_url>/module/<name>/<version>
     *
     * @param name    String name of the module.
     * @param version String version of the module.
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.DELETE, uri = "/{name}/{version}")
    public Result delete(@PathParameter("name") final String name, @PathParameter("version") final String version) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_DELETER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a delete module request. " + name + version);
        try {
            moduleService.deleteModule(Module.generateID(name, version));
            return ok("done");
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
    }

    /**
     * Get an Organization of a module.
     * This method is call via GET <dm_url>/module/<name>/<version>/organization.
     *
     * @param name    String name of the module.
     * @param version String version of the module.
     * @return Result the organization of the module as json.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}" + ServerAPI.GET_ORGANIZATION)
    public Result getOrganization(@PathParameter("name") final String name, @PathParameter("version") final String version) {
        LOG.info("Got a get module's organization request.");
        final String moduleId = Module.generateID(name, version);
        try {
            final Module module = moduleService.getModule(moduleId);
            final Organization organization = moduleService.getOrganization(module);
            return ok(organization);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
    }

    /**
     * todo i have no idea what this should do as sub modules are not stored so a module wont have ancestors?
     * but it gives the same results as the real grapes :p moche
     * <p>
     * Return ancestor list of a module.
     * This method is call via GET <dm_url>/module/<name>/<version>/ancestors
     *
     * @param name    String
     * @param version String
     * @param
     * @return Result A list of module
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}" + ServerAPI.GET_ANCESTORS)
    public Result getAncestors(@PathParameter("name") final String name,
                               @PathParameter("version") final String version) {
        LOG.info("Got a get module ancestors request.");
        final String moduleId = Module.generateID(name, version);
        try {
            final Module module = moduleService.getModule(moduleId);
            final Organization organization = moduleService.getOrganization(module);
            final FiltersHolder filters = new FiltersHolder();
            filters.getDecorator().setShowLicenses(false);
            filters.init(context().parameters());
            filters.setCorporateFilter(new CorporateFilter(organization));
//        final AncestorsView view = new AncestorsView("Ancestor List Of " + name +" in version " + version , licenseService.getLicenses(), filters.getDecorator());
//todo
            List<Artifact> artifacts = new ArrayList<>();
            Map<Module, List<Artifact>> ancestors = new HashMap<>();
            System.out.println("artifact for this modules are: " + Lists.newArrayList(dataUtils.getAllArtifactsGavcs(module)));
            System.out.println("has field is " + Lists.newArrayList(module.getHas()));
            //same as the has field
            for (final String artifactId : dataUtils.getAllArtifactsGavcs(module)) {
                System.out.println("one artifact in the list : " + artifactId);
                System.out.println("there are this many ancesotrs for this id: " + artifactService.getAncestors(artifactId, filters).size());
                for (final Module dbAncestor : artifactService.getAncestors(artifactId, filters)) {
                    System.out.println("one  ancestor for : " + artifactId + " is " + dbAncestor);
//                if(!dbAncestor.getId().equals(module.getId())){
//                    final Module ancestor = getModelMapper().getModule(dbAncestor);
//                    view.addAncestor(ancestor, artifact);
                    Artifact arty = artifactService.getArtifact(artifactId);
                    // ancestors.put(dbAncestor,(arty));
                }
            }
//        }
            return ok("todo");
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
    }

    /**
     * todo done didnt check flags
     * Return a module dependency list.
     * This method is call via GET <dm_url>/module/<name>/<version>/dependencies
     *
     * @param name    String
     * @param version String
     * @param
     * @return Result A list of dependencies in HTML or JSON
     * <p>
     * <p>
     * fullRecursive	false	Returns the dependencies of the entire corporate tree
     * depth	null	Returns the dependencies of the corporate tree until provided depth
     * scopeComp	true	Includes dependencies with the scope COMPILE
     * scopePro	true	Includes dependencies with the scope PROVIDED
     * scopeRun	false	Includes dependencies with the scope RUNTIME
     * scopeTest	true	Includes dependencies with the scope TEST
     * showCorporate	true	Includes the corporate dependencies in the report
     * showThirdparty	false	Includes the third party libraries in the report
     * doNotUse	null	Filters the dependencies regarding the artifact field DO_NOT_USE
     * showScopes	true	Add or remove the Scope column in HTML results
     * showLicenses	false	Add or remove the License column in HTML results
     * showSources	true	Add or remove the Source column in HTML results
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}" + ServerAPI.GET_DEPENDENCIES)
    public Result getDependencies(@PathParameter("name") final String name,
                                  @PathParameter("version") final String version) {
        LOG.info("Got a get module dependencies request for ."+name +version);
        final FiltersHolder filters = new FiltersHolder();
        filters.init(context().parameters());
        final String moduleId = Module.generateID(name, version);
        Module module = moduleService.getModule(moduleId);
        LOG.error("module"+moduleId);
        List<Dependency> list = dependencyService.getModuleDependencies(moduleId, filters);
        LOG.error("dep list legnth "+list.size());
        return ok(list).json();
    }

    /**
     * todo need to do a report efficiently.
     * Return a report about the targeted module dependencies.
     * This method is call via GET <dm_url>/module/<name>/<version>/dependencies/report
     *
     * @param name    String
     * @param version String
     * @return Result A list of dependencies in HTML or JSON
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}" + ServerAPI.GET_DEPENDENCIES + ServerAPI.GET_REPORT)
    public Result getDependencyReport(@PathParameter("name") final String name,
                                      @PathParameter("version") final String version) {
        LOG.info("Got a get dependency report request.");
        final FiltersHolder filters = new FiltersHolder();
        filters.init(context().parameters());
        final String moduleId = Module.generateID(name, version);
        //todo the below method
        final DependencyReport report = dependencyService.getDependencyReport(moduleId, filters);
         return ok(report);
        //return ok("todo");
    }

    /**
     * Return license list of a module.
     * This method is call via GET <dm_url>/module/<name>/<version>/licenses
     *
     * @param name    String
     * @param version String
     * @return Result A list of license
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}" + ServerAPI.GET_LICENSES)
    public Result getLicenses(@PathParameter("name") final String name, @PathParameter("version") final String version) {
        LOG.info("Got a get module licenses request.");
        if (name == null || version == null) {
            return status(Result.BAD_REQUEST);
        }
        final String moduleId = Module.generateID(name, version);
        try {
            final List<License> licenses = moduleService.getModuleLicenses(moduleId);
            return ok(licenses);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
    }

    /**
     * Check if a module is promoted or not
     * This method is call via GET <dm_url>/module/<name>/<version>/promotion
     *
     * @return Result true if the module is promoted, false otherwise.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}" + ServerAPI.PROMOTION)
    public Result isPromoted(@PathParameter("name") final String name, @PathParameter("version") final String version) {
        LOG.info("Got a get promotion status request.");
        final String moduleId = Module.generateID(name, version);
        try {
            final Module module = moduleService.getModule(moduleId);
            final Boolean promoted = module.isPromoted();
            return ok(promoted);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
    }

    /**
     * Promote a module.
     * This method is call via POST <dm_url>/module/<name>/<version>/promote
     *
     * @param
     * @param name    String
     * @param version String
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "/{name}/{version}" + ServerAPI.PROMOTION)
    public Result promote(@PathParameter("name") final String name, @PathParameter("version") final String version) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DEPENDENCY_NOTIFIER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a get promote module request.");
        final String moduleId = Module.generateID(name, version);
        try {
            moduleService.promoteModule(moduleId);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
        return ok("done");
    }

    /**
     * todo done but the method that computes it is not and returns false always
     * Check if a module can be promoted or not
     * This method is call via GET <dm_url>/module/<name>/<version>/promotion/check
     *
     * @param name    String
     * @param version String
     * @return Result true if the module can be promoted, false otherwise.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}" + ServerAPI.PROMOTION + ServerAPI.GET_FEASIBLE)
    public Result canBePromoted(@PathParameter("name") final String name, @PathParameter("version") final String version) {
        LOG.info("Got a is the module promotable request.");
        final String moduleId = Module.generateID(name, version);
        try {
            Module module = moduleService.getModule(moduleId);
            return ok(moduleService.canBePromoted(module));
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
    }

    /**
     * todo need to create a promotion report. Return the data in json format.
     * Return a promotion report
     *
     * @return Result A promotion report
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}" + ServerAPI.PROMOTION + ServerAPI.GET_REPORT)
    public Result getPromotionStatusReport(@PathParameter("name") final String name, @PathParameter("version") final String version) {
        LOG.info("Got a get promotion report request.");

        final String moduleId = Module.generateID(name, version);
        final FiltersHolder filters = new FiltersHolder();
        filters.init(context().parameters());
        final PromotionReport promotionReport =moduleService.getPromotionReport(moduleId, filters);
        reportToJson.promotionReportToJson(promotionReport);

      return ok(reportToJson.promotionReportToJson(promotionReport));
        //return ok("todo");
    }

    /**
     * Return a build info
     *
     * @param name
     * @param version
     * @return Result that contains a Json Map<String,String>
     */
    @Route(method = HttpMethod.GET, uri = "/{name}/{version}" + ServerAPI.GET_BUILD_INFO)
    public Result getBuildInfo(@PathParameter("name") final String name, @PathParameter("version") final String version) {
        LOG.info("Got a get buildInfo request.");
        final String moduleId = Module.generateID(name, version);
        try {
            final Module module = moduleService.getModule(moduleId);
            return ok(module.getBuildInfo());
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
    }

    /**
     * todo needs credentials?
     * Update a build info
     *
     * @param name
     * @param version
     * @param buildInfo
     * @return Result that contains a Json Map<String,String>
     */
    @Route(method = HttpMethod.POST, uri = "/{name}/{version}" + ServerAPI.GET_BUILD_INFO)
    public Result updateBuildInfo(@PathParameter("name") final String name, @PathParameter("version") final String version,
                                  @Body final Map<String, String> buildInfo) {
        LOG.info("Got a post buildInfo report request.");
        final String moduleId = Module.generateID(name, version);
        try {
            final Module module = moduleService.getModule(moduleId);
            module.getBuildInfo().putAll(buildInfo);
            moduleService.store(module);
            return ok().status(Result.CREATED);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The version " + version + " and module "
                    + name + " combination does not exist.");
        }
    }
}
