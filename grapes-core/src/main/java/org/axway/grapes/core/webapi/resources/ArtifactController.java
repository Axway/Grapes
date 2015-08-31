package org.axway.grapes.core.webapi.resources;

import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.exceptions.DataValidationException;
import org.axway.grapes.core.handler.DataUtils;
import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.LicenseService;
import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.core.webapi.utils.DataValidator;
import org.axway.grapes.model.api.ServerAPI;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Credential;
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
import org.wisdom.api.annotations.QueryParameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.security.Authenticated;
import org.wisdom.api.templates.Template;
import scala.annotation.meta.param;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
//todo basically done

/**
 * This class provides the REST API endpoints to manage Artifacts.
 */
@Path("/" + ServerAPI.ARTIFACT_RESOURCE)
@Controller
public class ArtifactController extends DefaultController {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactController.class);
    @Requires
    ArtifactService artifactService;
    @Requires
    OrganizationService organizationService;
    @Requires
    LicenseService licenseService;
    @Requires
    Json json;
    @Requires
    DataUtils dataUtils;

    @View("ArtifactResourceDocumentation")
    Template ArtifactResourceDocumentation;
    /**
     * Documentation page maybe we can use thymeleaf template.
     *
     * @return the artifact API documentation page.
     */
    @Route(method = HttpMethod.GET, uri = "")
    public Result welcome() {
        return ok(render(ArtifactResourceDocumentation, "welcome", "Welcome to The New Grapes Under Construction!"));
    }

    @Route(method = HttpMethod.GET, uri = "/")
    public Result welcome2() {
        return welcome();
    }
    /**
     * todo see todo below otherwise done
     * Post an artifact to the database.
     *
     * @param artifact in JSON format in the body of the request.
     * @return Unauthorized, if user not authorized to post. Bad Request if the JSON artifact is not valid format,
     *  Created if the request was successful.
     */
    @Route(method = HttpMethod.POST, uri = "")
    @Authenticated("grapes-authenticator")
    public Result postArtifact(@Body final Artifact artifact) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DEPENDENCY_NOTIFIER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a post Artifact request.");
        // Checks if the data is corrupted
        try {
            DataValidator.validate(artifact);
        } catch (DataValidationException e) {
            return ok(e.getLocalizedMessage()).status(Result.BAD_REQUEST).json();
        }
        // Store the Artifact
        artifactService.store(artifact);
        // Add the licenses
        //todo this does nothing as all the license are already in the artifact
        //maybe instead we can add them to the database with the unknown flag.
        for (String license : artifact.getLicenses()) {
            License exists = licenseService.resolve(license);
            if (exists == null) {
                licenseService.storeUnknown(license);
            }
            // artifactService.addLicense(artifact.getGavc(), license);
        }
        return ok().status(Result.CREATED);
    }


    /**
     * Gets a list of gavcs for all artifacts in the database.
     * It can use filters to limit results.
     *
     * @return a list of gavcs (the unique identifier of an artifact) in JSON form.
     */
    @Route(method = HttpMethod.GET, uri = ServerAPI.GET_GAVCS)
    public Result getGavcs() {
        LOG.info("Got a get gavc request.");
        final FiltersHolder filters = new FiltersHolder();
        filters.init(context().parameters());
        Long start = System.nanoTime();
        final List<String> gavcs = artifactService.getArtifactGavcs(filters);
        Long stop = System.nanoTime();
        Long total = stop -start;
        double seconds = (double)total / 1000000000.0;

        Collections.sort(gavcs);
        return ok(gavcs).json();
        //return ok();
    }

    /**
     * Gets all of the existing groupIds for all artifacts.
     * The results can be limited to specific criteria filters.
     *
     * @return list of group ids in json.
     */
    @Route(method = HttpMethod.GET, uri = ServerAPI.GET_GROUPIDS)
    public Result getGroupIds() {
        LOG.info("Got a get groupIds request.");
        final FiltersHolder filters = new FiltersHolder();
        filters.init(context().parameters());
        final List<String> groupIds = artifactService.getArtifactGroupIds(filters);
        Collections.sort(groupIds);
        return ok(groupIds).json();
    }


    /**
     * todo does the filter work?
     * Get all of the artifacts in the database.
     * The results can be limited by using filters in the query parameters.
     *
     * @return
     */
    @Route(method = HttpMethod.GET, uri = ServerAPI.GET_ALL)
    public Result getAll() {
        LOG.info("Got a get all artifact request.");
        LOG.error("params"+ context().parameters());
        final FiltersHolder filters = new FiltersHolder();
        filters.init(context().parameters());
        final List<Artifact> artifacts = artifactService.getArtifacts(filters);
        return ok(artifacts);
    }

    //todo is there a server error on the real grapes?
    //todo the other one doest show organization info so why is it here?
    @Route(method = HttpMethod.GET, uri = "/{gavc}")
    public Result get(@PathParameter("gavc") String gavc) {
        if("groupids".equalsIgnoreCase(gavc)){
           return getGroupIds();
        }
        else if("gavcs".equalsIgnoreCase(gavc)){
           return getGavcs();
        }
        else if ("all".equalsIgnoreCase(gavc)){
            return  getAll();
        }

        LOG.info("Got a get artifact request.");
        try {
            final Artifact artifact = artifactService.getArtifact(gavc);
            //todo
            final Organization organization = artifactService.getOrganization(artifact);
//       ObjectNode result = json.newObject();
//       result.
//       result.put("artifact:", String.valueOf(artifact));
//       result.put("organization", String.valueOf(organization));
            return ok(artifact);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
    }

   /**
            * todo the old version just deletes the artifact but doesnt remove it from any modules should it?
            *
            * @param
    gavc unique artifact id.
            * @return
            */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.DELETE, uri = "/{gavc}")
    public Result delete(@PathParameter("gavc") String gavc) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_DELETER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a delete artifact request.");
        try {
            artifactService.deleteArtifact(gavc);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
        return ok("done");
    }

    /**
     * Gets all of the versions of an artifact.
     *
     * @param gavc the artifacts unique id.
     * @return returns a json list of all the version, or empty list if none are found. Or not found if the
     * artifact does not exist.
     */
    @Route(method = HttpMethod.GET, uri = "/{gavc}" + ServerAPI.GET_VERSIONS)
    public Result getVersions(@PathParameter("gavc") String gavc) {
        LOG.info("Got a get artifact versions request.");
        try {
            final List<String> versions = artifactService.getArtifactVersions(gavc);
            Collections.sort(versions);
            return ok(versions).json();
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
    }

    /**
     * Updates the download url of an artifact.
     *
     * @param gavc        unique artifact id.
     * @param downLoadUrl the new url to update the artifact with.
     * @return not found, if the atrifact doesnt exist, not acceptable if the url or gavc are null,
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "/{gavc}" + ServerAPI.GET_DOWNLOAD_URL)
    public Result updateDownloadUrl(@PathParameter("gavc") String gavc, @QueryParameter(ServerAPI.URL_PARAM) String downLoadUrl) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got an update downloadUrl request.");
        if (gavc == null || downLoadUrl == null) {
            return ok().status(Result.NOT_ACCEPTABLE);
        }
        try {
            artifactService.updateDownLoadUrl(gavc, downLoadUrl);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
        return ok("done");
    }

    /**
     * Gets the last version of an artifact.
     *
     * @param gavc the artifacts gavc id.
     * @return the version number in json; or a not found response.
     */
    @Route(method = HttpMethod.GET, uri = "/{gavc}" + ServerAPI.GET_LAST_VERSION)
    public Result getLastVersion(@PathParameter("gavc") String gavc) {
        LOG.info("Got a get artifact last version request.");
        try {
            final String lastVersion = artifactService.getArtifactLastVersion(gavc);
            return ok(lastVersion).json();
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
    }


    /**
     * Update the provider of an artifact.
     *
     * @param gavc     unique artifact id.
     * @param provider the provider to update the artifact with.
     * @return not found, if the atrifact doesnt exist, not acceptable if the url or gavc are null.
     */
    @Route(method = HttpMethod.POST, uri = "/{gavc}" + ServerAPI.GET_PROVIDER)
    @Authenticated("grapes-authenticator")
    public Result updateProvider(@PathParameter("gavc") String gavc, @QueryParameter(ServerAPI.PROVIDER_PARAM) String provider) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got an update Provider request.");
        if (gavc == null || provider == null) {
            return ok().status(Result.NOT_ACCEPTABLE);
        }
        try {
            artifactService.updateProvider(gavc, provider);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
        return ok("done");
    }



    /**
     * Update the doNotUse flag of an atrifact.
     *
     * @param gavc     unique artifact id.
     * @param doNotUse boolean.
     * @return not found if the artifact cannot be found, ok if it was updated.
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "/{gavc}" + ServerAPI.SET_DO_NOT_USE)
    public Result postDoNotUse(@PathParameter("gavc") String gavc,
                               @QueryParameter(ServerAPI.DO_NOT_USE) boolean doNotUse) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.ARTIFACT_CHECKER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a add \"DO_NOT_USE\" request. " + doNotUse);
        try {
            artifactService.updateDoNotUse(gavc, doNotUse);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
        return ok("done");
    }

    /**
     * Gets the value of the doNotUse flag.
     *
     * @param gavc unique artifact id.
     * @return the value if the flag, or not found if the artifact doesnt exist.
     */
    @Route(method = HttpMethod.GET, uri = "/{gavc}" + ServerAPI.SET_DO_NOT_USE)
    public Result getDoNotUse(@PathParameter("gavc") String gavc) {
        LOG.info("Got a get doNotUse artifact request.");
        try {
            final Artifact artifact = artifactService.getArtifact(gavc);
            return ok(artifact.getDoNotUse());
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
    }

    /**
     * todo half way done basic search works but there is a problem when we store modules it earses the uses.
     * todo also need to test the scopes
     *
     * @param gavc unique artifact id.
     * @return
     */
    @Route(method = HttpMethod.GET, uri = "/{gavc}" + ServerAPI.GET_ANCESTORS)
    public Result getAncestors(@PathParameter("gavc") String gavc) {
        LOG.info("Got a get artifact ancestors request.");
        final FiltersHolder filters = new FiltersHolder();
        filters.getDecorator().setShowLicenses(false);
        filters.init(context().parameters());
        final List<Module> dbAncestors = artifactService.getAncestors(gavc, filters);
        final Artifact artifact = dataUtils.createArtifact(gavc);
        return ok(dbAncestors);
    }

    /**
     * todo done but see below this is for the old grapes as well
     * todo for the moment this gets all licesne of an articat if you use a filter it gets all license that arnt really in the database plus the filtered one
     *
     * @param gavc unique artifact id.
     * @return
     */
    @Route(method = HttpMethod.GET, uri = "/{gavc}" + ServerAPI.GET_LICENSES)
    public Result getLicenses(@PathParameter("gavc") String gavc) {
        LOG.info("Got a get artifact licenses request.");
        final FiltersHolder filters = new FiltersHolder();
        filters.init(context().parameters());
        try {
            final List<License> dbLicenses = artifactService.getArtifactLicenses(gavc, filters);
            return ok(dbLicenses);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
    }

    /**
     * todo done but need to think about the logical process, if the license doesnt exisit in the database do we still add it? do we create it in the database?
     *
     * @param gavc      unique artifact id.
     * @param licenseId
     * @return
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "/{gavc}" + ServerAPI.GET_LICENSES)
    public Result addLicense(@PathParameter("gavc") String gavc,
                             @QueryParameter(ServerAPI.LICENSE_ID_PARAM) String licenseId) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a add license request.");
        if (licenseId == null) {
            return ok().status(Result.NOT_ACCEPTABLE);
        }
        try {
            artifactService.addLicenseToArtifact(gavc, licenseId);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
        return ok("done");
    }

    /**
     * Deletes a license from a specific artifact.
     *
     * @param gavc      unique artifact id.
     * @param licenseId the license name to delete.
     * @return ok if succesfull, not found if the artifact doesnt exsist, not acceptable if the license id is null.
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.DELETE, uri = "/{gavc}" + ServerAPI.GET_LICENSES)
    public Result deleteLicense(@PathParameter("gavc") String gavc,
                                @QueryParameter(ServerAPI.LICENSE_ID_PARAM) String licenseId) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a delete license request.");
        if (licenseId == null) {
            return ok().status(Result.NOT_ACCEPTABLE);
        }
        try {
            artifactService.removeLicenseFromArtifact(gavc, licenseId);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
        return ok("done");
    }

    /**
     * todo!!!!!! get module
     *
     * @param gavc unique artifact id.
     * @return
     */
    @Route(method = HttpMethod.GET, uri = "/{gavc}" + ServerAPI.GET_MODULE)
    public Result getModule(@PathParameter("gavc") String gavc) {
        LOG.info("Got a get artifact's module request.");
        final Artifact artifact = artifactService.getArtifact(gavc);
        final Module module = artifactService.getModule(artifact);
        if (module == null) {
            return ok().status(Result.NO_CONTENT);
        }
        //  final ModuleView view = new ModuleView();
        // view.setModule(getModelMapper().getModule(module));
        //view.setOrganization(module.getOrganization());
        return ok(module);
    }
    //todo done

    /**
     * Gets the organization of an artifact.
     *
     * @param gavc unique artifact id.
     * @return return the organization in json format.
     */
    @Route(method = HttpMethod.GET, uri = "/{gavc}" + ServerAPI.GET_ORGANIZATION)
    public Result getOrganization(@PathParameter("gavc") String gavc) {
        LOG.info("Got a get artifact's organization request.");
        try {
            final Artifact artifact = artifactService.getArtifact(gavc);
            final Module module = artifactService.getModule(artifact);
            if (module == null || module.getOrganization().isEmpty()) {
                return ok().status(Result.NO_CONTENT);
            }
            final Organization organization = organizationService.getOrganization(module.getOrganization());
            return ok(organization).json();
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The gavc " + gavc + " does not exist.");
        }
    }


}
