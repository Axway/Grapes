package org.axway.grapes.server.webapp.resources;

import com.yammer.dropwizard.auth.Auth;
import com.yammer.dropwizard.jersey.caching.CacheControl;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ArtifactHandler;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.options.filters.CorporateFilter;
import org.axway.grapes.server.core.reports.DependencyReport;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.axway.grapes.server.webapp.DataValidator;
import org.axway.grapes.server.webapp.views.*;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Module Resource
 *
 * <p>This server resource handles all the request about modules.<br/>
 * This resource extends DepManViews to holds its own documentation.
 * The documentation is available in ModuleResourceDocumentation.ftl file.</p>
 * @author jdcoffre
 */
@Path(ServerAPI.MODULE_RESOURCE)
public class ModuleResource extends AbstractResource{

    private static final Logger LOG = LoggerFactory.getLogger(ModuleResource.class);


    public ModuleResource(final RepositoryHandler repoHandler, final GrapesServerConfig dmConfig) {
        super(repoHandler, "ModuleResourceDocumentation.ftl", dmConfig);
    }

    /**
     * Handle the update/addition of a module in Grapes database
     *
     * @param credential DbCredential
     * @param module Module
     * @return Response
     */
    @POST
    @Produces(MediaType.TEXT_HTML)
    public Response postModule(@Auth final DbCredential credential, final Module module){
        if(!credential.getRoles().contains(AvailableRoles.DEPENDENCY_NOTIFIER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a post Module request.");

        // Checks if the data is corrupted
        DataValidator.validate(module);

        // Save the module
        final DbModule dbModule = getModelMapper().getDbModule(module);
        getModuleHandler().store(dbModule);

        final ArtifactHandler artifactHandler = getArtifactHandler();

        // Add the artifacts
        final Set<Artifact> artifacts = DataUtils.getAllArtifacts(module);
        for(Artifact artifact: artifacts){
            artifactHandler.store(getModelMapper().getDbArtifact(artifact));
        }

        // Add dependencies that does not already exist
        for(Dependency dep: DataUtils.getAllDependencies(module)){
            final DbArtifact dbDependency = getModelMapper().getDbArtifact(dep.getTarget());
            artifactHandler.storeIfNew(dbDependency);
        }

        return Response.ok().status(HttpStatus.CREATED_201).build();
    }

    /**
     * Return a list of moduleNames, stored in Grapes, regarding the filters passed in the query parameters.
     * This method is call via GET <dm_url>/module/names
     *
     * @param uriInfo UriInfo
     * @return Response A list (in HTML or JSON) of moduleNames
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path(ServerAPI.GET_NAMES)
    public Response getNames(@Context final UriInfo uriInfo){
        LOG.info("Got a get module names request.");

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final ListView view = new ListView("Module names view", "name");
        final List<String> moduleNames = getModuleHandler().getModuleNames(filters);
        Collections.sort(moduleNames);
        view.addAll(moduleNames);

        return Response.ok(view).build();
    }

    /**
     * Return a list of moduleNames, stored in Grapes, regarding the filters passed in the query parameters.
     * This method is call via GET <dm_url>/module/<name>/versions
     *
     * @param name String
     * @param uriInfo UriInfo
     * @return Response A list (in HTML or JSON) of moduleNames
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}" + ServerAPI.GET_VERSIONS)
    public Response getVersions(@PathParam("name") final String name, @Context final UriInfo uriInfo){
        LOG.info("Got a get versions request.");

        if(name == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final ListView view = new ListView("Versions of " + name, "version");
        final List<String> versions = getModuleHandler().getModuleVersions(name, filters);
        Collections.sort(versions);
        view.addAll(versions);

        return Response.ok(view).build();
    }

    /**
     * Return a module.
     * This method is call via GET <dm_url>/module/<name>/<version>
     *
     * @param name String
     * @param version String
     * @return Response A list (in HTML or JSON) of moduleNames
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}")
    public Response get(@PathParam("name") final String name, @PathParam("version") final String version){
        LOG.info("Got a get module request.");
        final ModuleView view = new ModuleView();

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final String moduleId = DbModule.generateID(name, version);
        final DbModule dbModule = getModuleHandler().getModule(moduleId);
        final Module module = getModelMapper().getModule(dbModule);
        view.setModule(module);

        return Response.ok(view).build();
    }

    /**
     * Delete a module.
     * This method is call via DELETE <dm_url>/module/<name>/<version>
     *
     * @param credential DbCredential
     * @param name String
     * @param version String
     * @return Response
     */
    @DELETE
    @Path("/{name}/{version}")
    public Response delete(@Auth final DbCredential credential, @PathParam("name") final String name, @PathParam("version") final String version){
        if(!credential.getRoles().contains(AvailableRoles.DATA_DELETER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }
        LOG.info("Got a delete module request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final String moduleId = DbModule.generateID(name, version);
        getModuleHandler().deleteModule(moduleId);

        return Response.ok("done").build();
    }

    /**
     * Return ancestor list of a module.
     * This method is call via GET <dm_url>/module/<name>/<version>/ancestors
     *
     * @param name String
     * @param version String
     * @param uriInfo UriInfo
     * @return Response A list of module
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.GET_ANCESTORS)
    @CacheControl(maxAge = 5, maxAgeUnit = TimeUnit.MINUTES)
    public Response getAncestors(@PathParam("name") final String name,
                                   @PathParam("version") final String version,
                                     @Context final UriInfo uriInfo){
        LOG.info("Got a get module ancestors request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final String moduleId = DbModule.generateID(name, version);
        final DbModule dbModule = getModuleHandler().getModule(moduleId);
        final DbOrganization dbOrganization = getModuleHandler().getOrganization(dbModule);
        final ArtifactHandler artifactHandler = getArtifactHandler();
        final FiltersHolder filters = new FiltersHolder();
        filters.getDecorator().setShowLicenses(false);
        filters.init(uriInfo.getQueryParameters());
        filters.setCorporateFilter(new CorporateFilter(dbOrganization));

        final AncestorsView view = new AncestorsView("Ancestor List Of " + name +" in version " + version , getLicenseHandler().getLicenses(), filters.getDecorator());

        for(String artifactId: DataUtils.getAllArtifacts(dbModule)){
            final DbArtifact dbArtifact = artifactHandler.getArtifact(artifactId);
            final Artifact artifact = getModelMapper().getArtifact(dbArtifact);

            for(DbModule dbAncestor: artifactHandler.getAncestors(artifactId, filters)){
                if(!dbAncestor.getId().equals(dbModule.getId())){
                    final Module ancestor = getModelMapper().getModule(dbAncestor);
                    view.addAncestor(ancestor, artifact);
                }
            }
        }

        return Response.ok(view).build();
    }

    /**
     *
     * Return a module dependency list.
     * This method is call via GET <dm_url>/module/<name>/<version>/dependencies
     *
     * @param name String
     * @param version String
     * @param uriInfo UriInfo
     * @return Response A list of dependencies in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.GET_DEPENDENCIES)
    @CacheControl(maxAge = 5, maxAgeUnit = TimeUnit.MINUTES)
    public Response getDependencies(@PathParam("name") final String name,
                                    @PathParam("version") final String version,
                                    @Context final UriInfo uriInfo){

        LOG.info("Got a get module dependencies request.");
        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final DependencyListView view = new DependencyListView("Dependency List Of " + name + " in version " + version, getLicenseHandler().getLicenses(), filters.getDecorator());
        final String moduleId = DbModule.generateID(name, version);
        view.addAll(getDependencyHandler().getModuleDependencies(moduleId, filters));

        return Response.ok(view).build();
    }

    /**
     *
     * Return a report about the targeted module dependencies.
     * This method is call via GET <dm_url>/module/<name>/<version>/dependencies/report
     *
     * @param name String
     * @param version String
     * @param uriInfo UriInfo
     * @return Response A list of dependencies in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.GET_DEPENDENCIES + ServerAPI.GET_REPORT)
    @CacheControl(maxAge = 5, maxAgeUnit = TimeUnit.MINUTES)
    public Response getDependencyReport(@PathParam("name") final String name,
                                    @PathParam("version") final String version,
                                    @Context final UriInfo uriInfo){

        LOG.info("Got a get dependency report request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final String moduleId = DbModule.generateID(name, version);
        final DependencyReport report = getDependencyHandler().getDependencyReport(moduleId, filters);

        return Response.ok(report).build();
    }

    /**
     * Return license list of a module.
     * This method is call via GET <dm_url>/module/<name>/<version>/licenses
     *
     * @param name String
     * @param version String
     * @return Response A list of license
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.GET_LICENSES)
    @CacheControl(maxAge = 5, maxAgeUnit = TimeUnit.MINUTES)
    public Response getLicenses(@PathParam("name") final String name, @PathParam("version") final String version){
        LOG.info("Got a get module licenses request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final LicenseListView view = new LicenseListView("Licenses of " + name + " in version " + version);
        final String moduleId = DbModule.generateID(name,version);
        final List<DbLicense> dbLicenses = getModuleHandler().getModuleLicenses(moduleId);

        for(DbLicense dbLicense: dbLicenses){
            final License license = getModelMapper().getLicense(dbLicense);
            view.add(license);
        }

        return Response.ok(view).build();
    }

    /**
     * Promote a module.
     * This method is call via POST <dm_url>/module/<name>/<version>/promote
     *
     * @param credential DbCredential
     * @param name String
     * @param version String
     * @return Response
     */
    @POST
    @Path("/{name}/{version}" + ServerAPI.PROMOTION)
    public Response promote(@Auth final DbCredential credential, @PathParam("name") final String name, @PathParam("version") final String version){
        if(!credential.getRoles().contains(AvailableRoles.DEPENDENCY_NOTIFIER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a get promote module request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final String moduleId = DbModule.generateID(name,version);
        getModuleHandler().promoteModule(moduleId);

        return Response.ok("done").build();
    }

    /**
     * Check if a module can be promoted or not
     * This method is call via GET <dm_url>/module/<name>/<version>/promotion/check
     *
     * @param name String
     * @param version String
     * @return Response true if the module can be promoted, false otherwise.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{name}/{version}" + ServerAPI.PROMOTION + ServerAPI.GET_FEASIBLE)
    public Response canBePromoted(@PathParam("name") final String name, @PathParam("version") final String version){
        LOG.info("Got a is the module promotable request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final String moduleId = DbModule.generateID(name,version);
        final PromotionReportView promotionReportView = getModuleHandler().getPromotionReport(moduleId);

        return Response.ok(promotionReportView.canBePromoted()).build();
    }

    /**
     * Return a promotion report
     *
     * @return Response A promotion report
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.PROMOTION + ServerAPI.GET_REPORT)
    @CacheControl(maxAge = 5, maxAgeUnit = TimeUnit.MINUTES)
    public Response getPromotionStatusReport(@PathParam("name") final String name, @PathParam("version") final String version){
        LOG.info("Got a get promotion report request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final String moduleId = DbModule.generateID(name,version);
        final PromotionReportView promotionReportView = getModuleHandler().getPromotionReport(moduleId);

        return Response.ok(promotionReportView).build();
    }

    /**
     * Check if a module is promoted or not
     * This method is call via GET <dm_url>/module/<name>/<version>/promotion
     *
     * @return Response true if the module is promoted, false otherwise.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{name}/{version}" + ServerAPI.PROMOTION)
    public Response isPromoted(@PathParam("name") final String name, @PathParam("version") final String version){
        LOG.info("Got a get promotion status request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }


        final String moduleId = DbModule.generateID(name,version);
        final DbModule module = getModuleHandler().getModule(moduleId);
        final Boolean promoted = module.isPromoted();

        return Response.ok(promoted).build();
    }

    /**
     * Provide a list of module regarding the query parameter filters
     *
     * @param uriInfo
     * @return Response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(ServerAPI.GET_ALL)
    @CacheControl(maxAge = 5, maxAgeUnit = TimeUnit.MINUTES)
    public Response getAllModules(@Context final UriInfo uriInfo){
        LOG.info("Got a get all modules request.");

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final List<Module> modules = new ArrayList<Module>();
        final List<DbModule> dbModules = getModuleHandler().getModules(filters);

        for(DbModule dbModule: dbModules){
            final Module module = DataModelFactory.createModule(dbModule.getName(), dbModule.getVersion());
            modules.add(module);
        }

        return Response.ok(modules).build();
    }

}
