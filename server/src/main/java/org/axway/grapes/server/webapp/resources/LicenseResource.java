package org.axway.grapes.server.webapp.resources;

import com.yammer.dropwizard.auth.Auth;
import com.yammer.dropwizard.jersey.params.BooleanParam;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ServiceHandler;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.webapp.DataValidator;
import org.axway.grapes.server.webapp.views.LicenseView;
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
 * License Resource
 * 
 * <p>This server resource handles all the request about licenses.<br/>
 * This resource extends DepManViews to holds its own documentation.
 * The documentation is available in LicenseResourceDocumentation.ftl file.</p>
 * 
 * @author jdcoffre
 */
@Path(ServerAPI.LICENSE_RESOURCE)
public class LicenseResource extends AbstractResource{
    
    private static final Logger LOG = LoggerFactory.getLogger(LicenseResource.class);
    
    public LicenseResource(final RepositoryHandler repoHandler, final GrapesServerConfig dmConfig){
        super(repoHandler, "LicenseResourceDocumentation.ftl", dmConfig);
    }
    
    /**
	 * Handle license posts when the server got a request POST <dm_url>/license & MIME that contains the license.
	 * 
	 * @param license The license to add to Grapes database
	 * @return Response An acknowledgment:<br/>- 400 if the artifact is MIME is malformed<br/>- 500 if internal error<br/>- 201 if ok
	 */
	@POST
	public Response postLicense(@Auth final DbCredential credential, final License license){
        if(!credential.getRoles().contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

		LOG.info("Got a post license request.");

        // Checks if the data is corrupted
        DataValidator.validate(license);

        // Save the license
        final DbLicense dbLicense = getModelMapper().getDbLicense(license);
        getLicenseHandler().store(dbLicense);

		return Response.ok().status(HttpStatus.CREATED_201).build();
	}
	
	/**
	 * Return the list of available license name.
	 * This method is call via GET <dm_url>/license/names
     *
     * @param uriInfo UriInfo
	 * @return Response A list of license name in HTML or JSON
	 */
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	@Path(ServerAPI.GET_NAMES)
	public Response getNames(@Context final UriInfo uriInfo){
		LOG.info("Got a get license names request.");
        final ListView view = new ListView("License names view", "license");

		final FiltersHolder filters = new FiltersHolder();
		filters.init(uriInfo.getQueryParameters());

        final List<String> names = getLicenseHandler().getLicensesNames(filters);
        view.addAll(names);

		return Response.ok(view).build();
	}

    /**
     * Return a license
     * This method is call via GET <dm_url>/license/<name>
     *
     * @param name String
     * @return Response A license in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}")
    public Response get(@PathParam("name") final String name){
        LOG.info("Got a get license request.");
        final LicenseView view = new LicenseView();

        final DbLicense dbLicense = getLicenseHandler().getLicense(name);
        final License license = getModelMapper().getLicense(dbLicense);
        view.setLicense(license);

        return Response.ok(view).build();
    }

    /**
     * Delete a license
     * This method is call via DELETE <dm_url>/license/<name>
     *
     * @param credential DbCredential
     * @param name String
     * @return Response
     */
    @DELETE
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}")
    public Response delete(@Auth final DbCredential credential, @PathParam("name") final String name){
        if(!credential.getRoles().contains(AvailableRoles.DATA_DELETER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a delete license request.");
        getLicenseHandler().deleteLicense(name);

        return Response.ok("done").build();
    }

    /**
     * Validate a license
     * This method is call via POST <dm_url>/license/<name>?approved=<boolean>
     *
     * @param credential DbCredential
     * @param name String
     * @param approved BooleanParam
     * @return Response
     */
    @POST
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}")
    public Response approve(@Auth final DbCredential credential, @PathParam("name") final String name, @QueryParam(ServerAPI.APPROVED_PARAM) final BooleanParam approved){
        if(!credential.getRoles().contains(AvailableRoles.LICENSE_CHECKER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a get license request.");

        if(approved == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        getLicenseHandler().approveLicense(name, approved.get());

        return Response.ok("done").build();
    }

}
