package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.NotFoundException;
import com.yammer.dropwizard.jersey.params.BooleanParam;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.axway.grapes.server.webapp.auth.Role;
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
	public Response postLicense(@Role final List<AvailableRoles> roles, final License license){
        if(!roles.contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

		LOG.info("Got a post license request.");
		
		if(isNotValid(license)){
			LOG.info("The following license is not valid: " + license.toString());
			return Response.serverError().status(HttpStatus.BAD_REQUEST_400).build();
		}
		
		try {
			getRequestHandler().store(license);
		} catch (Exception e) {
			LOG.error("Failed to store the following license: " + license.toString(), e);
			return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
		}

		return Response.ok().status(HttpStatus.CREATED_201).build();
	}

	/**
	 * Check if the provided license is valid and could be stored into the database
	 * 
	 * @param license the license to test
	 * @return Boolean true only if the license is NOT valid
	 */
	private boolean isNotValid(final License license) {
		if(license.getName() == null ||
				license.getName().isEmpty()){
			return true;
		}
		if(license.getLongName() == null ||
				license.getLongName().isEmpty()){
			return true;
		}

		return false;
	}
	
	/**
	 *  Return the list of available license name.
	 * This method is call via GET <dm_url>/license/names
     *
	 * @return Response A list of license name in HTML or JSON
	 */
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	@Path(ServerAPI.GET_NAMES)
	public Response getNames(@Context final UriInfo uriInfo){
		LOG.info("Got a get license names request.");
		ListView names;

		final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
		filters.init(uriInfo.getQueryParameters());
		
		try {
			names = getRequestHandler().getLicensesNames(filters);
			
		} catch (Exception e) {
			LOG.error("Failed retrieve the license names lists.", e);
			return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
		}

		return Response.ok(names).build();
	}

    /**
     * Return a license
     * This method is call via GET <dm_url>/license/<name>
     *
     * @param name
     * @return Response A license in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}")
    public Response get(@PathParam("name") final String name){
        LOG.info("Got a get license request.");
        LicenseView license;

        try {
            license = getRequestHandler().getLicense(name);

        } catch (NotFoundException e) {
            LOG.error("License not found: " + name);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the license: "+ name, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(license).build();
    }

    /**
     * Delete a license
     * This method is call via DELETE <dm_url>/license/<name>
     *
     * @param name
     * @return Response
     */
    @DELETE
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}")
    public Response delete(@Role final List<AvailableRoles> roles, @PathParam("name") final String name){
        if(!roles.contains(AvailableRoles.DATA_DELETER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a delete license request.");

        try {
            getRequestHandler().deleteLicenses(name);

        } catch (NotFoundException e) {
            LOG.error("License not found: " + name);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the license: "+ name, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

    /**
     * Validate a license
     * This method is call via POST <dm_url>/license/<name>?approved=<boolean>
     *
     * @param name
     * @return Response
     */
    @POST
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}")
    public Response approve(@Role final List<AvailableRoles> roles, @PathParam("name") final String name, @QueryParam(ServerAPI.APPROVED_PARAM) final BooleanParam approved){
        if(!roles.contains(AvailableRoles.LICENSE_CHECKER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a get license request.");

        if(approved == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        try {
            getRequestHandler().approveLicenses(name, approved.get());

        } catch (NotFoundException e) {
            LOG.error("License not found: " + name);
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        } catch (Exception e) {
            LOG.error("Failed retrieve the license: "+ name, e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok("done").build();
    }

}
