package org.axway.grapes.core.webapi.resources;

import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.exceptions.DataValidationException;
import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.core.webapi.utils.DataValidator;
import org.axway.grapes.model.api.ServerAPI;
import org.axway.grapes.model.datamodel.Credential;
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

import java.util.List;
import java.util.NoSuchElementException;
//todo basically done

/**
 * Created by jennifer on 4/28/15.
 */
@Controller
@Path(ServerAPI.ORGANIZATION_RESOURCE)
public class OrganizationController extends DefaultController {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationController.class);

    @Requires
    OrganizationService organizationService;
    @View("OrganizationResourceDocumentation")
    Template OrganizationResourceDocumentation;

    /**
     *
     *
     * @return the welcome page
     */
    @Route(method = HttpMethod.GET, uri = "")
    public Result welcome() {
        return ok(render(OrganizationResourceDocumentation, "welcome", "Welcome to The New Grapes Under Construction!"));
    }

    /**
     *
     * Handle organization posts when the server got a request POST <dm_url>/organization & MIME that contains an organization.
     *
     * @param organization The organization to add to Grapes database.
     * @return Result An acknowledgment:<br/>- 400 if the artifact is MIME is malformed<br/>- 500 if internal error<br/>- 201 if ok.
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "")
    public Result postOrganization(@Body final Organization organization) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a post organization request.");
        // Checks if the data is corrupted
        try {
            DataValidator.validate(organization);
        } catch (DataValidationException e) {
            return ok(e.getLocalizedMessage()).status(Result.BAD_REQUEST).json();
        }
        // final Organization dbOrganization = getModelMapper().getOrganization(organization);
        organizationService.store(organization);
        return ok().status(Result.CREATED);
    }

    /**
     *
     * Gets the list of available organization namen.
     * This method is call via GET <dm_url>/organization/names
     *
     * @return Result A list of organization name in HTML or JSON.
     */
    @Route(method = HttpMethod.GET, uri = ServerAPI.GET_NAMES)
    public Result getNames() {
        LOG.info("Got a get organization names request.");
        final List<String> names = organizationService.getOrganizationNames();
        return ok(names).json();
    }

    /**
     *
     * Gets the specified organization.
     * This method is call via GET <dm_url>/organization/<name>
     *
     * @param name String the organization to look for.
     * @return Result An Organization in HTML or JSON.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}")
    public Result get(@PathParameter("name") final String name) {
        LOG.info("Got a get organization request.");
        try {
            final Organization dbOrganization = organizationService.getOrganization(name);
            return ok(dbOrganization).json();
        } catch (NoSuchElementException e) {
            return ok("Unable to find an organization by the name " + name).status(Result.NOT_FOUND);
        }
    }

    /**
     *
     * Delete an organization.
     * This method is call via DELETE <dm_url>/organization/<name>
     *
     * @param name String Organization name to delete.
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.DELETE, uri = "/{name}")
    public Result delete(@PathParameter("name") final String name) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_DELETER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a delete organization request.");
        try {
            organizationService.deleteOrganization(name);
        } catch (NoSuchElementException e) {
            return ok("Unable to find an organization by the name " + name).status(Result.NOT_FOUND);
        }
        return ok("done");
    }

    /**
     *
     * Get the list of corporate GroupId prefix configured for an organization.
     *
     * @param organizationId String Organization name.
     * @return Result A list of corporate groupId prefix in HTML or JSON.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}" + ServerAPI.GET_CORPORATE_GROUPIDS)
    public Result getCorporateGroupIdPrefix(@PathParameter("name") final String organizationId) {
        LOG.info("Got a get corporate groupId prefix request for organization " + organizationId + ".");
        try {
            final List<String> corporateGroupIds = organizationService.getCorporateGroupIds(organizationId);
            return ok(corporateGroupIds).json();
        } catch (NoSuchElementException e) {
            return ok("Unable to find an organization by the name " + organizationId).status(Result.NOT_FOUND);
        }
    }

    /**
     * todo as far as i can tell it is a bug??? that it doesnt accept text/plain in the body param so use json
     * Add a new Corporate GroupId to an organization.
     *
     * @param
     * @param organizationId   String Organization name
     * @param corporateGroupId String
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "/{name}" + ServerAPI.GET_CORPORATE_GROUPIDS, accepts = {"text/plain", "application/json"})
    public Result addCorporateGroupIdPrefix(@PathParameter("name") final String organizationId, @Body String corporateGroupId) {
        LOG.info("Got an add a corporate groupId prefix request for organization " + organizationId + ".");
        System.out.println("group is to add is " + corporateGroupId);
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        if (corporateGroupId == null || corporateGroupId.isEmpty()) {
            LOG.error("No corporate GroupId to add!");
            return ok("CorporateGroupId to add should be in the query content").status(Result.NOT_ACCEPTABLE);
        }
        try {
            organizationService.addCorporateGroupId(organizationId, corporateGroupId);
        } catch (NoSuchElementException e) {
            return ok("Unable to find an organization by the name " + organizationId).status(Result.NOT_FOUND);
        }
        return ok().status(Result.CREATED);
    }

    /**
     * todo done but should use regex
     * Remove an existing Corporate GroupId from an organization.
     *
     * @param organizationId  String organization name.
     * @param corporateGroupId json string to remove from organization.
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.DELETE, uri = "/{name}" + ServerAPI.GET_CORPORATE_GROUPIDS)
    public Result removeCorporateGroupIdPrefix(@PathParameter("name") final String organizationId, @Body final String corporateGroupId) {
        LOG.info("Got an remove a corporate groupId prefix request for organization " + organizationId + ".");
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        if (corporateGroupId == null || corporateGroupId.isEmpty()) {
            LOG.error("No corporate GroupId to remove!");
            return ok(status(Result.BAD_REQUEST));
        }
        try {
            organizationService.removeCorporateGroupId(organizationId, corporateGroupId);
        } catch (NoSuchElementException e) {
            return ok("Unable to find an organization by the name " + organizationId).status(Result.NOT_FOUND);
        }
        return ok("done");
    }
}
