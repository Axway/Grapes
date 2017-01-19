package org.axway.grapes.server.webapp.resources;

import com.yammer.dropwizard.auth.Auth;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Organization;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ServiceHandler;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.axway.grapes.server.webapp.DataValidator;
import org.axway.grapes.server.webapp.views.ListView;
import org.axway.grapes.server.webapp.views.OrganizationView;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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

        // Checks if the data is corrupted
        DataValidator.validate(organization);

        final DbOrganization dbOrganization = getModelMapper().getDbOrganization(organization);
        getOrganizationHandler().store(dbOrganization);

        return Response.ok().status(HttpStatus.CREATED_201).build();
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

        final ListView view = new ListView("Organization Ids list", "Organizations");
        final List<String> names = getOrganizationHandler().getOrganizationNames();
        view.addAll(names);

        return Response.ok(view).build();
    }

    /**
     * Returns an organization
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

        final DbOrganization dbOrganization = getOrganizationHandler().getOrganization(name);
        final Organization organization = getModelMapper().getOrganization(dbOrganization);
        final OrganizationView view = new OrganizationView(organization);

        return Response.ok(view).build();
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
        getOrganizationHandler().deleteOrganization(name);

        return Response.ok("done").build();
    }

    /**
     * Return the list of corporate GroupId prefix configured for an organization.
     *
     * @param organizationId String Organization name
     * @return Response A list of corporate groupId prefix in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}" + ServerAPI.GET_CORPORATE_GROUPIDS)
    public Response getCorporateGroupIdPrefix(@PathParam("name") final String organizationId){
        LOG.info("Got a get corporate groupId prefix request for organization " + organizationId +".");

        final ListView view = new ListView("Organization " + organizationId, "Corporate GroupId Prefix");
        final List<String> corporateGroupIds = getOrganizationHandler().getCorporateGroupIds(organizationId);
        view.addAll(corporateGroupIds);

        return Response.ok(view).build();
    }

    /**
     * Add a new Corporate GroupId to an organization.
     *
     * @param credential DbCredential
     * @param organizationId String Organization name
     * @param corporateGroupId String
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
            throw new WebApplicationException(Response.serverError().status(HttpStatus.BAD_REQUEST_400)
                    .entity("CorporateGroupId to add should be in the query content.").build());
        }

        getOrganizationHandler().addCorporateGroupId(organizationId, corporateGroupId);
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

        getOrganizationHandler().removeCorporateGroupId(organizationId, corporateGroupId);

        return Response.ok("done").build();
    }


}
