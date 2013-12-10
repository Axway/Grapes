package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.NotFoundException;
import com.yammer.dropwizard.jersey.params.BooleanParam;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.options.filters.PromotedFilter;
import org.axway.grapes.server.core.reports.DependencyReport;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.axway.grapes.server.webapp.auth.Role;
import org.axway.grapes.server.webapp.views.DependencyListView;
import org.axway.grapes.server.webapp.views.ListView;
import org.axway.grapes.server.webapp.views.ModuleView;
import org.axway.grapes.server.webapp.views.PromotionReportView;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

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
     * @param roles
     * @param module
     * @return Response
     */
    @POST
    @Produces(MediaType.TEXT_HTML)
    public Response postModule(@Role final List<AvailableRoles> roles, final Module module){
        if(!roles.contains(AvailableRoles.DEPENDENCY_NOTIFIER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a post Module request.");

        if(isNotValid(module)){
            LOG.error("The module is not valid.");
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        try {
            getRequestHandler().store(module);
        } catch (Exception e) {
            LOG.error("Failed to store the following module: " + module.getName(), e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok().status(HttpStatus.CREATED_201).build();
    }

    /**
     * Check if the provided module is valid and could be stored into the database
     *
     * @param module the module to test
     * @return Boolean true only if the artifact is NOT valid
     */
    private boolean isNotValid(final Module module) {
        if(module == null){
            return true;
        }
        if(module.getName() == null ||
                module.getName().isEmpty()){
            return true;
        }
        if(module.getVersion()== null ||
                module.getVersion().isEmpty()){
            return true;
        }

        return false;
    }

    /**
     * Return a list of moduleNames, stored in Grapes, regarding the filters passed in the query parameters.
     * This method is call via GET <dm_url>/module/names
		if(obj instanceof GraphElement){
			return value.equals(((GraphElement) obj).value);
		}
     *
     * @return Response A list (in HTML or JSON) of moduleNames
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path(ServerAPI.GET_NAMES)
    public Response getNames(@Context final UriInfo uriInfo){
        LOG.info("Got a get module names request.");
        ListView names;
        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        try {
            names = getRequestHandler().getModuleNames(filters);

        } catch (Exception e) {
            LOG.error("Failed retrieve the module names.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(names).build();
    }

    /**
     * Return a list of moduleNames, stored in Grapes, regarding the filters passed in the query parameters.
     * This method is call via GET <dm_url>/module/<name>/versions
     *
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

        ListView versions;
        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        try {
            versions = getRequestHandler().getModuleVersions(name, filters);

        } catch (NotFoundException e){
            LOG.error("Targeted module name does not exist: " + name);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the following module: " + name, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(versions).build();
    }

    /**
     * Return a module.
     * This method is call via GET <dm_url>/module/<name>/<version>
     *
     * @return Response A list (in HTML or JSON) of moduleNames
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}")
    public Response get(@PathParam("name") final String name, @PathParam("version") final String version){
        LOG.info("Got a get module request.");
        ModuleView module;

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        try {
            module = getRequestHandler().getModule(name, version);

        } catch (NotFoundException e){
            LOG.error("Targeted module does not exist: " + name + " " + version);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }catch (Exception e) {
            LOG.error("Failed retrieve the targeted module: " + name + " " + version, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(module).build();
    }

    /**
     *
     * Delete a module.
     * This method is call via DELETE <dm_url>/module/<name>/<version>
     *
     * @param roles
     * @param name
     * @param version
     * @return Response
     */
    @DELETE
    @Path("/{name}/{version}")
    public Response delete(@Role final List<AvailableRoles> roles, @PathParam("name") final String name, @PathParam("version") final String version){
        if(!roles.contains(AvailableRoles.DATA_DELETER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }
        LOG.info("Got a delete module request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        try {

            getRequestHandler().deleteModule(name, version);

        }catch (NotFoundException e){
            LOG.error("Targeted module does not exist: " + name + " " + version);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }catch (Exception e) {
            LOG.error("Failed retrieve the targeted module does not exist: " + name + " " + version, e);

            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

    /**
     * Return ancestor list of a module.
     * This method is call via GET <dm_url>/module/<name>/<version>/ancestors
     *
     * @param name
     * @param version
     * @return Response A list of module
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.GET_ANCESTORS)
    public Response getAncestors(@PathParam("name") final String name,
                                 @PathParam("version") final String version,
                                 @Context final UriInfo uriInfo){

        LOG.info("Got a get module ancestors request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());
        DependencyListView view;

        try {
            view = getRequestHandler().getModuleAncestors(name, version, filters);

        }catch (NotFoundException e){
            LOG.error("Targeted module does not exist: " + name + " " + version);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }catch (Exception e) {
            LOG.error("Failed retrieve the targeted module does not exist: " + name + " " + version, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(view).build();
    }

    /**
     * Return a module dependency list.
     * This method is call via GET <dm_url>/module/<name>/<version>/dependencies
     *
     * @return Response A list of dependencies in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.GET_DEPENDENCIES)
    public Response getDependencies(@PathParam("name") final String name,
                                    @PathParam("version") final String version,
                                    @QueryParam(ServerAPI.TO_UPDATE_PARAM) final BooleanParam toUpdateParam,
                                    @Context final UriInfo uriInfo){

        LOG.info("Got a get module dependencies request.");
        DependencyListView dependencies;

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());


        Boolean toUpdate = null;
        if(toUpdateParam != null){
            toUpdate = toUpdateParam.get();
        }

        try {
            dependencies = getRequestHandler().getModuleDependencies(name, version, filters, toUpdate);

        } catch (NotFoundException e) {
            LOG.error("Targeted module does not exist: " + name + " " + version);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the module dependency: " + name + version, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(dependencies).build();
    }

    /**
     * Return a report about the targeted module dependencies.
     * This method is call via GET <dm_url>/module/<name>/<version>/dependencies/report
     *
     * @return Response A list of dependencies in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.GET_DEPENDENCIES + ServerAPI.GET_REPORT)
    public Response getDependencyReport(@PathParam("name") final String name,
                                    @PathParam("version") final String version,
                                    @Context final UriInfo uriInfo){

        LOG.info("Got a get dependency report request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        DependencyReport report;

        try {
            report = getRequestHandler().getDependencyReport(name, version, filters);

        } catch (NotFoundException e) {
            LOG.error("Targeted module does not exist: " + name + " " + version);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the module dependency: " + name + version, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(report).build();
    }

    /**
     * Return license list of a module.
     * This method is call via GET <dm_url>/module/<name>/<version>/licenses
     *
     * @return Response A list of license
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.GET_LICENSES)
    public Response getLicenses(@PathParam("name") final String name,
                                @PathParam("version") final String version){

        LOG.info("Got a get module licenses request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        ListView view;

        try {
            view = getRequestHandler().getModuleLicenses(name, version);

        }catch (NotFoundException e){
            LOG.error("Targeted module does not exist: " + name + " " + version);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }catch (Exception e) {
            LOG.error("Failed retrieve the targeted module does not exist: " + name + " " + version, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(view).build();
    }

    /**
     * Promote a module.
     * This method is call via POST <dm_url>/module/<name>/<version>/promote
     *
     * @return Response
     */
    @POST
    @Path("/{name}/{version}" + ServerAPI.PROMOTION)
    public Response promote(@Role final List<AvailableRoles> roles, @PathParam("name") final String name, @PathParam("version") final String version){
        if(!roles.contains(AvailableRoles.DEPENDENCY_NOTIFIER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a get promote module request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        try {

            getRequestHandler().promoteModule(name, version);

        }catch (NotFoundException e){
            LOG.error("Targeted module does not exist: " + name + " " + version);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }catch (Exception e) {
            LOG.error("Failed retrieve the targeted module does not exist: " + name + " " + version, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

    /**
     * Check if a module can be promoted or not
     * This method is call via GET <dm_url>/module/<name>/<version>/promotion/check
     *
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

        Boolean promulgable;

        try {
            promulgable = getRequestHandler().canModuleBePromoted(name, version);

        }catch (NotFoundException e){
            LOG.error("Targeted module does not exist: " + name + " " + version);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }catch (Exception e) {
            LOG.error("Failed retrieve the targeted module does not exist: " + name + " " + version, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(promulgable).build();
    }

    /**
     * Return a promotion report
     *
     * @return Response A promotion report
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}/{version}" + ServerAPI.PROMOTION + ServerAPI.GET_REPORT)
    public Response getPromotionStatusReport(@PathParam("name") final String name, @PathParam("version") final String version, @Context final UriInfo uriInfo){
        LOG.info("Got a get promotion report request.");

        if(name == null || version == null){
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        final PromotedFilter filter = new PromotedFilter(false);
        filters.addFilter(filter);

        PromotionReportView report;

        try {
            report = getRequestHandler().getPromotionReport(name, version, filters);
        }
        catch (NotFoundException e){
            LOG.error("Targeted module does not exist: " + name + " " + version, e);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }
        catch (Exception e) {
            LOG.error("Failed retrieve the targeted module: " + name + " " + version, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(report).build();
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

        Boolean promoted;

        try {
            final ModuleView module = getRequestHandler().getModule(name, version);
            promoted = module.getModule().isPromoted();

        }catch (NotFoundException e){
            LOG.error("Targeted module does not exist: " + name + " " + version);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }catch (Exception e) {
            LOG.error("Failed retrieve the targeted module does not exist: " + name + " " + version, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

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
    public Response getAllModules(@Context final UriInfo uriInfo){
        LOG.info("Got a get all modules request.");

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        List<Module> modules = null;

        try {
            modules = getRequestHandler().getModules(filters);

        } catch (Exception e) {
            LOG.error("Failed.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(modules).build();
    }

}
