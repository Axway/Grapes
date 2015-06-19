package org.axway.grapes.core.webapi.resources;

import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.exceptions.DataValidationException;
import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.service.CredentialService;
import org.axway.grapes.core.service.LicenseService;
import org.axway.grapes.core.webapi.utils.DataValidator;
import org.axway.grapes.model.api.ServerAPI;
import org.axway.grapes.model.datamodel.Credential;
import org.axway.grapes.model.datamodel.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Body;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Path;
import org.wisdom.api.annotations.PathParameter;
import org.wisdom.api.annotations.QueryParameter;
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
@Path(ServerAPI.LICENSE_RESOURCE)
public class LicenseController extends DefaultController {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseController.class);
    @View("LicenseResourceDocumentation")
    Template LicenseResourceDocumentation;
    @Requires
    LicenseService licenseService;
    @Requires
    CredentialService credentialService;

    /**
     * The action method returning the welcome page. It handles
     * HTTP GET request on the "/" URL.
     *
     * @return the license Api Documentation.
     */
    @Route(method = HttpMethod.GET, uri = "")
    public Result welcome() {
        return ok(render(LicenseResourceDocumentation, "welcome", "Welcome to The New Grapes Under Construction!"));
    }

    /**
     *
     * Handle license posts when the server got a request POST <dm_url>/license & MIME that contains the license.
     *
     * @param license The license to add to Grapes database
     * @return Result An acknowledgment:<br/>- 400 if the artifact is MIME is malformed<br/>- 500 if internal error<br/>- 201 if ok
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "")
    public Result postLicense(@Body final License license) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
//
        LOG.info("Got a post license request.");
        // Checks if the data is corrupted
        try {
            DataValidator.validate(license);
        } catch (DataValidationException e) {
            return ok(e.getLocalizedMessage()).status(Result.BAD_REQUEST).json();
        }
        // Save the license
        licenseService.store(license);
        return ok().status(Result.CREATED);
    }

    /**
     * todo problem with tobevalidatedfilter
     * Return the list of available license name.
     * This method is call via GET <dm_url>/license/names
     *
     * @param
     * @return Result A list of license name in HTML or JSON
     */
    @Route(method = HttpMethod.GET, uri = ServerAPI.GET_NAMES)
    public Result getNames() {
        LOG.info("Got a get license names request.");
        final FiltersHolder filters = new FiltersHolder();
        filters.init(context().parameters());
        final List<String> names = licenseService.getLicensesNames(filters);
        return ok(names).json();
    }

    /**
     *
     * Get a license based on its unique name.
     * This method is call via GET <dm_url>/license/<name>;
     *
     * @param name String name of the license.
     * @return Result A license in JSON.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}")
    public Result get(@PathParameter("name") final String name) {
        LOG.info("Got a get license request.");
        try {
            final License license = licenseService.getLicense(name);
            return ok(license).json();
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The license does not exist.");

        }
       }

    /**
     *
     * Delete a license.
     * This method is call via DELETE <dm_url>/license/<name>.
     *
     * @param name String of the license.
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.DELETE, uri = "/{name}")
    public Result delete(@PathParameter("name") final String name) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_DELETER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a delete license request.");
        try {
            licenseService.deleteLicense(name);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The license does not exist.");
        }
        return ok("done");
    }

    /**
     *
     * Validate a license.
     * This method is call via POST <dm_url>/license/<name>?approved=<boolean>
     *
     * @param name     String
     * @param approved boolean true is 'true' "true" 1 "on" see wisdom docs
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "/{name}")
    public Result approve(@PathParameter("name") final String name, @QueryParameter(ServerAPI.APPROVED_PARAM) final Boolean approved) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.LICENSE_CHECKER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a get license request.");
        try {
            licenseService.approveLicense(name, approved);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The license does not exist.");
        }
        return ok("done");
    }
}
