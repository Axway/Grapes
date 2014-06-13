package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.NotFoundException;
import com.yammer.dropwizard.auth.Auth;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Organization;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.webapp.views.ListView;
import org.axway.grapes.server.webapp.views.OrganizationView;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Organization Resource
 *
 * <p>This server resource handles all the request about organization.<br/>
 * This resource extends DepManViews to holds its own documentation.
 * The documentation is available in OrganizationResourceDocumentation.ftl file.</p>
 * @author jdcoffre
 */
@Path(ServerAPI.ORGANIZATION_RESOURCE)
public class OrganizationResource extends AbstractResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationResource.class);

    public OrganizationResource(final RepositoryHandler repositoryHandler, final GrapesServerConfig configuration) {
        super(repositoryHandler, "OrganizationResourceDocumentation.ftl", configuration);
    }


    /**
     * Handle organization posts when the server got a request POST <dm_url>/organization & MIME that contains an organization.
     *
     * @param organization The organization to add to Grapes database
     * @return Response An acknowledgment:<br/>- 400 if the artifact is MIME is malformed<br/>- 500 if internal error<br/>- 201 if ok
     */
    @POST
    public Response postOrganization(@Auth final DbCredential credential, final Organization organization){
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a post organization request.");

        if(isNotValid(organization)){
            LOG.info("The following organization is not valid: " + organization.toString());
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        try {
            getRequestHandler().store(organization);
        } catch (Exception e) {
            LOG.error("Failed to store the following organization: " + organization.toString(), e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok().status(HttpStatus.CREATED_201).build();
    }

    private boolean isNotValid(final Organization organization) {
        if(organization.getName() == null){
            return true;
        }
        return false;
    }

    /**
     * Return the list of available organization name.
     * This method is call via GET <dm_url>/organization/names
     *
     * @return Response A list of organization name in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path(ServerAPI.GET_NAMES)
    public Response getNames(){
        LOG.info("Got a get organization names request.");
        ListView names;

        try {
            names = getRequestHandler().getOrganizationNames();

        } catch (Exception e) {
            LOG.error("Failed organization the license names lists.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(names).build();
    }

    /**
     * Return an organization
     * This method is call via GET <dm_url>/organization/<name>
     *
     * @param name String
     * @return Response An Organization in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}")
    public Response get(@PathParam("name") final String name){
        LOG.info("Got a get organization request.");
        OrganizationView organization;

        try {
            organization = getRequestHandler().getOrganization(name);

        } catch (NotFoundException e) {
            LOG.error("Organization not found: " + name);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the organization: "+ name, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(organization).build();
    }

    /**
     * Delete an organization
     * This method is call via DELETE <dm_url>/organization/<name>
     *
     * @param name String Organization name
     * @return Response
     */
    @DELETE
    @Path("/{name}")
    public Response delete(@Auth final DbCredential credential, @PathParam("name") final String name){
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DATA_DELETER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a delete organization request.");

        try {
            getRequestHandler().deleteOrganization(name);

        } catch (NotFoundException e) {
            LOG.error("Organization not found: " + name);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed delete the organization: "+ name, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

    /**
     * Return the list of corporate GroupId prefix configured for an organization.
     *
     * @return Response A list of corporate groupId prefix in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}" + ServerAPI.GET_CORPORATE_GROUPIDS)
    public Response getCorporateGroupIdPrefix(@PathParam("name") final String organizationId){
        LOG.info("Got a get corporate groupId prefix request for organization " + organizationId +".");
        ListView corporateGroupIds;

        try {
            corporateGroupIds = getRequestHandler().getCorporateGroupIds(organizationId);

        } catch (NotFoundException e) {
            LOG.error("Organization not found: " + organizationId);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed to get corporate groupId prefix list.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(corporateGroupIds).build();
    }

    /**
     * Add a new Corporate GroupId to an organization.
     *
     * @return Response
     */
    @POST
    @Path("/{name}" + ServerAPI.GET_CORPORATE_GROUPIDS)
    public Response addCorporateGroupIdPrefix(@Auth final DbCredential credential, @PathParam("name") final String organizationId, final String corporateGroupId){
        LOG.info("Got an add a corporate groupId prefix request for organization " + organizationId +".");
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        if(corporateGroupId == null || corporateGroupId.isEmpty()){
            LOG.error("No corporate GroupId to add!");
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        try {
            getRequestHandler().addCorporateGroupId(organizationId, corporateGroupId);

        } catch (NotFoundException e) {
            LOG.error("Organization not found: " + organizationId);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed to add corporate groupId prefix to the list.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok().status(HttpStatus.CREATED_201).build();
    }

    /**
     * Remove an existing Corporate GroupId from an organization.
     *
     * @return Response
     */
    @DELETE
    @Path("/{name}" + ServerAPI.GET_CORPORATE_GROUPIDS)
    public Response removeCorporateGroupIdPrefix(@Auth final DbCredential credential, @PathParam("name") final String organizationId, final String corporateGroupId){
        LOG.info("Got an remove a corporate groupId prefix request for organization " + organizationId +".");
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        if(corporateGroupId == null || corporateGroupId.isEmpty()){
            LOG.error("No corporate GroupId to remove!");
            return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
        }

        try {
            getRequestHandler().removeCorporateGroupId(organizationId, corporateGroupId);

        } catch (NotFoundException e) {
            LOG.error("Organization not found: " + organizationId);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed to remove corporate groupId prefix to the list.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }


}
