package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.NotFoundException;
import com.yammer.dropwizard.jersey.params.BooleanParam;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.axway.grapes.server.webapp.auth.Role;
import org.axway.grapes.server.webapp.views.ArtifactView;
import org.axway.grapes.server.webapp.views.DependencyListView;
import org.axway.grapes.server.webapp.views.ListView;
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
 * Artifact Resource
 *
 * <p>This server resource handles all the request about artifacts.<br/>
 * This resource extends DepManViews to holds its own documentation.
 * The documentation is available in ArtifactResourceDocumentation.ftl file.</p>
 * @author jdcoffre
 */
@Path(ServerAPI.ARTIFACT_RESOURCE)
public class ArtifactResource extends AbstractResource {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactResource.class);

    public ArtifactResource(final RepositoryHandler repoHandler, final GrapesServerConfig dmConfig) {
        super(repoHandler, "ArtifactResourceDocumentation.ftl", dmConfig);
    }

    /**
     * Handle artifact posts when the server got a request POST <dm_url>/artifact & MIME that contains the artifact.
     *
     * @param roles
     * @param artifact The artifact to add to Grapes database
     * @return Response An acknowledgment:<br/>- 400 if the artifact is MIME is malformed<br/>- 500 if internal error<br/>- 201 if ok
     */
    @POST
    public Response postArtifact(@Role final List<AvailableRoles> roles, final Artifact artifact){
        if(!roles.contains(AvailableRoles.DEPENDENCY_NOTIFIER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a post Artifact request.");

        if(isNotValid(artifact)){
            LOG.info("The following artifact is not valid: " + artifact.toString());
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        try {
            getRequestHandler().store(artifact);
        } catch (Exception e) {
            LOG.error("Failed to store the following artifact: " + artifact.toString(), e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok().status(HttpStatus.CREATED_201).build();
    }

    /**
     * Check if the provided artifact is valid and could be stored into the database
     *
     * @param artifact the artifact to test
     * @return Boolean true only if the artifact is NOT valid
     */
    public static boolean isNotValid(final Artifact artifact) {
        if(artifact.getGroupId() == null ||
                artifact.getGroupId().isEmpty()){
            return true;
        }
        if(artifact.getArtifactId() == null ||
                artifact.getArtifactId().isEmpty()){
            return true;
        }
        if(artifact.getVersion() == null ||
                artifact.getVersion().isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * Return a list of gavc, stored in Grapes, regarding the filters passed in the query parameters.
     * This method is call via GET <dm_url>/artifact/gavcs
     *
     * @return Response A list (in HTML or JSON) of gavc
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path(ServerAPI.GET_GAVCS)
    public Response getGavcs(@Context final UriInfo uriInfo){
        LOG.info("Got a get gavc request.");
        ListView gavcs;
        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        try {
            gavcs = getRequestHandler().getArtifactGavcs(filters);

        } catch (Exception e) {
            LOG.error("Failed retrieve the gavc.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(gavcs).build();
    }

    /**
     * Return a list of groupIds, stored in Grapes.
     * This method is call via GET <dm_url>/artifact/groupids
     *
     * @return Response A list (in HTML or JSON) of gavc
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path(ServerAPI.GET_GROUPIDS)
    public Response getGroupIds(@Context final UriInfo uriInfo){
        LOG.info("Got a get groupIds request.");
        ListView groupIds;
        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        try {
            groupIds = getRequestHandler().getArtifactGroupIds(filters);

        } catch (Exception e) {
            LOG.error("Failed retrieve the gavc.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(groupIds).build();
    }

    /**
     * Return an Artifact regarding its gavc.
     * This method is call via GET <dm_url>/artifact/<gavc>
     *
     * @return Response An artifact in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{gavc}")
    public Response get(@PathParam("gavc") final String gavc){
        LOG.info("Got a get artifact request.");
        ArtifactView artifact;

        if(gavc == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        try {
            artifact = getRequestHandler().getArtifact(gavc);

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the artifact: " + gavc, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(artifact).build();
    }

    /**
     * Update an artifact download url.
     * This method is call via GET <dm_url>/artifact/<gavc>/downloadurl?url=<targetUrl>
     *
     */
    @POST
    @Path("/{gavc}" + ServerAPI.GET_DOWNLOAD_URL)
    public Response updateDownloadUrl(@Role final List<AvailableRoles> roles, @PathParam("gavc") final String gavc, @QueryParam(ServerAPI.URL_PARAM) final String downLoadUrl){
        if(!roles.contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got an update downloadUrl request.");

        if(gavc == null || downLoadUrl == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        try {
            getRequestHandler().updateDownLoadUrl(gavc, downLoadUrl);

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed to perform the artifact update: " + gavc, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

    /**
     * Update an artifact download url.
     * This method is call via GET <dm_url>/artifact/<gavc>/downloadurl?url=<targetUrl>
     *
     */
    @POST
    @Path("/{gavc}" + ServerAPI.GET_PROVIDER)
    public Response updateProvider(@Role final List<AvailableRoles> roles, @PathParam("gavc") final String gavc, @QueryParam(ServerAPI.PROVIDER_PARAM) final String provider){
        if(!roles.contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got an update downloadUrl request.");

        if(gavc == null || provider == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        try {
            getRequestHandler().updateProvider(gavc, provider);

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed to perform the artifact update: " + gavc, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

    /**
     * Delete an Artifact regarding its gavc.
     * This method is call via DELETE <dm_url>/artifact/<gavc>
     *
     * @param roles
     * @param gavc
     * @return Response
     */
    @DELETE
    @Path("/{gavc}")
    public Response delete(@Role final List<AvailableRoles> roles, @PathParam("gavc") final String gavc){
        if(!roles.contains(AvailableRoles.DATA_DELETER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a delete artifact request.");
        if(gavc == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        try {
            getRequestHandler().deleteArtifact(gavc);

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed remove the artifact: " + gavc, e);

            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok().build();
    }

    /**
     * Add "DO_NOT_USE" flag to an artifact
     *
     * @param roles
     * @param gavc
     * @param doNotUse
     * @return Response
     */
    @POST
    @Path("/{gavc}" + ServerAPI.SET_DO_NOT_USE)
    public Response postDoNotUse(@Role final List<AvailableRoles> roles, @PathParam("gavc") final String gavc,@QueryParam(ServerAPI.DO_NOT_USE) final BooleanParam doNotUse){
        if(!roles.contains(AvailableRoles.ARTIFACT_CHECKER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a add \"DO_NOT_USE\" request.");

        if(gavc == null || doNotUse == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        try {
            getRequestHandler().setDoNotUse(gavc, doNotUse.get());

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed add the license to the artifact: " + gavc, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

    /**
     * Return true if the targeted artifact is flagged with "DO_NOT_USE".
     * This method is call via GET <dm_url>/artifact/<gavc>/donotuse
     *
     * @return Response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{gavc}" + ServerAPI.SET_DO_NOT_USE)
    public Response getDoNotUse(@PathParam("gavc") final String gavc){
        LOG.info("Got a get doNotUse artifact request.");
        ArtifactView artifact;

        if(gavc == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        try {
            artifact = getRequestHandler().getArtifact(gavc);

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the artifact: " + gavc, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(artifact.shouldNotBeUsed()).build();
    }

    /**
     * Return the list of ancestor of an artifact.
     * This method is call via GET <dm_url>/artifact/<gavc>/ancestors
     *
     * @return Response A list of ancestor in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{gavc}" + ServerAPI.GET_ANCESTORS)
    public Response getAncestors(@PathParam("gavc") final String gavc, @Context final UriInfo uriInfo){
        LOG.info("Got a get artifact request.");
        DependencyListView ancestors;

        if(gavc == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        try {
			ancestors = getRequestHandler().getAncestors(gavc,filters);

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed to get the artifact ancestors: " + gavc, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(ancestors).build();
    }

    /**
     *
     * Return the list of licenses used by an artifact.
     * This method is call via GET <dm_url>/artifact/{gavc}/licenses
     *
     * @param gavc
     * @return Response A list of dependencies in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{gavc}" + ServerAPI.GET_LICENSES)
    public Response getLicenses(@PathParam("gavc") final String gavc, @Context final UriInfo uriInfo){
        LOG.info("Got a get artifact licenses request.");
        ListView licenses;

        if(gavc == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        try {
            licenses = getRequestHandler().getArtifactLicenses(gavc,filters);

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the artifact: " + gavc, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(licenses).build();
    }


    /**
     * Add a license to an artifact
     * @param gavc
     * @param licenseId
     * @return Response
     */
    @POST
    @Path("/{gavc}" + ServerAPI.GET_LICENSES)
    public Response addLicense(@Role final List<AvailableRoles> roles, @PathParam("gavc") final String gavc,@QueryParam(ServerAPI.LICENSE_ID_PARAM) final String licenseId){
        if(!roles.contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a add license request.");

        if(gavc == null || licenseId == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        try {
            getRequestHandler().addLicenseToArtifact(gavc, licenseId);

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed add the license to the artifact: " + gavc, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

    /**
     * Remove a license from an artifact
     * @param gavc
     * @param licenseId
     * @return Response
     */
    @DELETE
    @Path("/{gavc}" + ServerAPI.GET_LICENSES)
    public Response deleteLicense(@Role final List<AvailableRoles> roles, @PathParam("gavc") final String gavc,@QueryParam(ServerAPI.LICENSE_ID_PARAM) final String licenseId){
        if(!roles.contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a delete license request.");

        if(gavc == null || licenseId == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        try {
            getRequestHandler().removeLicenseFromArtifact(gavc, licenseId);

        } catch (NotFoundException e) {
            LOG.error("Gavc not found: " + gavc);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed remove the license to the artifact: " + gavc, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

    /**
     * Return all the artifacts that matches the filters.
     * This method is call via GET <dm_url>/artifact/<gavc>
     * Following filters can be used: artifactId, classifier, groupId, hasLicense, licenseId, type, uriInfo, version
     *
     * @return Response An artifact in HTML or JSON
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(ServerAPI.GET_ALL)
    public Response getAll(@Context final UriInfo uriInfo){
        LOG.info("Got a get all artifact request.");

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        List<Artifact> artifacts = null;

        try {
            artifacts = getRequestHandler().getArtifacts(filters);

        } catch (Exception e) {
            LOG.error("Failed.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(artifacts).build();
    }

}
