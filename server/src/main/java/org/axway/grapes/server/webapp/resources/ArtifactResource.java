package org.axway.grapes.server.webapp.resources;

import com.yammer.dropwizard.auth.Auth;
import com.yammer.dropwizard.jersey.params.BooleanParam;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.datamodel.Organization;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ArtifactHandler;
import org.axway.grapes.server.core.options.FiltersHolder;
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
     * Handle artifact posts when the server got a request POST <grapes_url>/artifact & MIME that contains the artifact.
     *
     * @param credential DbCredential
     * @param artifact The artifact to add to Grapes database
     * @return Response An acknowledgment:<br/>- 400 if the artifact is MIME is malformed<br/>- 500 if internal error<br/>- 201 if ok
     */
    @POST
    public Response postArtifact(@Auth final DbCredential credential, final Artifact artifact){
        if(!credential.getRoles().contains(AvailableRoles.DEPENDENCY_NOTIFIER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a post Artifact request.");

        // Checks if the data is corrupted
        DataValidator.validate(artifact);

        // Store the Artifact
        final ArtifactHandler artifactHandler = getArtifactHandler();
        final DbArtifact dbArtifact = getModelMapper().getDbArtifact(artifact);
        artifactHandler.store(dbArtifact);

        // Add the licenses
        for(String license: artifact.getLicenses()){
            artifactHandler.addLicense(dbArtifact.getGavc(), license);
        }

        return Response.ok().status(HttpStatus.CREATED_201).build();
    }

    /**
     * Return a list of gavc, stored in Grapes, regarding the filters passed in the query parameters.
     * This method is call via GET <grapes_url>/artifact/gavcs
     *
     * @return Response A list (in HTML or JSON) of gavc
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path(ServerAPI.GET_GAVCS)
    public Response getGavcs(@Context final UriInfo uriInfo){
        LOG.info("Got a get gavc request.");
        final ListView view = new ListView("GAVCS view", "gavc");
        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final List<String> gavcs = getArtifactHandler().getArtifactGavcs(filters);
        Collections.sort(gavcs);
        view.addAll(gavcs);

        return Response.ok(view).build();
    }

    /**
     * Return a list of groupIds, stored in Grapes.
     * This method is call via GET <grapes_url>/artifact/groupids
     *
     * @return Response A list (in HTML or JSON) of gavc
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path(ServerAPI.GET_GROUPIDS)
    public Response getGroupIds(@Context final UriInfo uriInfo){
        LOG.info("Got a get groupIds request.");
        final ListView view = new ListView("GroupIds view", "groupId");
        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final List<String> groupIds = getArtifactHandler().getArtifactGroupIds(filters);
        Collections.sort(groupIds);
        view.addAll(groupIds);

        return Response.ok(view).build();
    }

    /**
     * Returns the list of available versions of an artifact
     * This method is call via GET <grapes_url>/artifact/<gavc>/versions
     *
     * @param gavc String
     * @return Response a list of versions in JSON or in HTML
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{gavc}" + ServerAPI.GET_VERSIONS)
    public Response getVersions(@PathParam("gavc") final String gavc){
        LOG.info("Got a get artifact versions request.");
        final ListView view = new ListView("Versions View", "version");

        final List<String> versions = getArtifactHandler().getArtifactVersions(gavc);
        Collections.sort(versions);
        view.addAll(versions);

        return Response.ok(view).build();
    }

    /**
     * Returns the list of available versions of an artifact
     * This method is call via GET <grapes_url>/artifact/<gavc>/versions
     *
     * @param gavc String
     * @return Response String version in JSON
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{gavc}" + ServerAPI.GET_LAST_VERSION)
    public Response getLastVersion(@PathParam("gavc") final String gavc){
        LOG.info("Got a get artifact last version request.");

        final String lastVersion = getArtifactHandler().getArtifactLastVersion(gavc);

        return Response.ok(lastVersion).build();
    }

    /**
     * Return an Artifact regarding its gavc.
     * This method is call via GET <grapes_url>/artifact/<gavc>
     *
     *
     * @param gavc String
     * @return Response An artifact in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{gavc}")
    public Response get(@PathParam("gavc") final String gavc){
        LOG.info("Got a get artifact request.");
        final ArtifactView view = new ArtifactView();

        final DbArtifact dbArtifact = getArtifactHandler().getArtifact(gavc);
        view.setShouldNotBeUse(dbArtifact.getDoNotUse());

        final Artifact artifact = getModelMapper().getArtifact(dbArtifact);
        view.setArtifact(artifact);

        final DbOrganization dbOrganization = getArtifactHandler().getOrganization(dbArtifact);
        if(dbOrganization != null){
            final Organization organization = getModelMapper().getOrganization(dbOrganization);
            view.setOrganization(organization);
        }

        return Response.ok(view).build();
    }

    /**
     *  Update an artifact download url.
     * This method is call via GET <grapes_url>/artifact/<gavc>/downloadurl?url=<targetUrl>
     *
     * @param credential DbCredential
     * @param gavc String
     * @param downLoadUrl String
     * @return Response
     */
    @POST
    @Path("/{gavc}" + ServerAPI.GET_DOWNLOAD_URL)
    public Response updateDownloadUrl(@Auth final DbCredential credential, @PathParam("gavc") final String gavc, @QueryParam(ServerAPI.URL_PARAM) final String downLoadUrl){
        if(!credential.getRoles().contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got an update downloadUrl request.");

        if(gavc == null || downLoadUrl == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        getArtifactHandler().updateDownLoadUrl(gavc, downLoadUrl);

        return Response.ok("done").build();
    }

    /**
     * Update an artifact download url.
     * This method is call via GET <grapes_url>/artifact/<gavc>/downloadurl?url=<targetUrl>
     *
     */
    @POST
    @Path("/{gavc}" + ServerAPI.GET_PROVIDER)
    public Response updateProvider(@Auth final DbCredential credential, @PathParam("gavc") final String gavc, @QueryParam(ServerAPI.PROVIDER_PARAM) final String provider){
        if(!credential.getRoles().contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got an update downloadUrl request.");

        if(gavc == null || provider == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        getArtifactHandler().updateProvider(gavc, provider);

        return Response.ok("done").build();
    }

    /**
     * Delete an Artifact regarding its gavc.
     * This method is call via DELETE <grapes_url>/artifact/<gavc>
     *
     * @param credential DbCredential
     * @param gavc String
     * @return Response
     */
    @DELETE
    @Path("/{gavc}")
    public Response delete(@Auth final DbCredential credential, @PathParam("gavc") final String gavc){
        if(!credential.getRoles().contains(AvailableRoles.DATA_DELETER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a delete artifact request.");
        getArtifactHandler().deleteArtifact(gavc);

        return Response.ok().build();
    }

    /**
     * Add "DO_NOT_USE" flag to an artifact
     *
     * @param credential DbCredential
     * @param gavc String
     * @param doNotUse boolean
     * @return Response
     */
    @POST
    @Path("/{gavc}" + ServerAPI.SET_DO_NOT_USE)
    public Response postDoNotUse(@Auth final DbCredential credential, @PathParam("gavc") final String gavc,@QueryParam(ServerAPI.DO_NOT_USE) final BooleanParam doNotUse){
        if(!credential.getRoles().contains(AvailableRoles.ARTIFACT_CHECKER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a add \"DO_NOT_USE\" request.");
        getArtifactHandler().updateDoNotUse(gavc, doNotUse.get());

        return Response.ok("done").build();
    }

    /**
     * Return true if the targeted artifact is flagged with "DO_NOT_USE".
     * This method is call via GET <grapes_url>/artifact/<gavc>/donotuse
     *
     * @param gavc String
     * @return Response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{gavc}" + ServerAPI.SET_DO_NOT_USE)
    public Response getDoNotUse(@PathParam("gavc") final String gavc){
        LOG.info("Got a get doNotUse artifact request.");
        final DbArtifact artifact = getArtifactHandler().getArtifact(gavc);

        return Response.ok(artifact.getDoNotUse()).build();
    }

    /**
     * Return the list of ancestor of an artifact.
     * This method is call via GET <grapes_url>/artifact/<gavc>/ancestors
     *
     * @param gavc String
     * @param uriInfo UriInfo
     * @return Response A list of ancestor in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{gavc}" + ServerAPI.GET_ANCESTORS)
    public Response getAncestors(@PathParam("gavc") final String gavc, @Context final UriInfo uriInfo){
        LOG.info("Got a get artifact request.");

        final FiltersHolder filters = new FiltersHolder();
        filters.getDecorator().setShowLicenses(false);
        filters.init(uriInfo.getQueryParameters());

        final AncestorsView view = new AncestorsView("Ancestor List Of " + gavc, getLicenseHandler().getLicenses(), filters.getDecorator());

        final List<DbModule> dbAncestors = getArtifactHandler().getAncestors(gavc, filters);
        final Artifact artifact = DataUtils.createArtifact(gavc);

        for(DbModule dbAncestor : dbAncestors){
            final Module ancestor = getModelMapper().getModule(dbAncestor);
            view.addAncestor(ancestor, artifact);
        }

        return Response.ok(view).build();
    }

    /**
     * Returns the list of licenses used by an artifact.
     * This method is call via GET <grapes_url>/artifact/{gavc}/licenses
     *
     * @param gavc
     * @return Response A list of dependencies in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{gavc}" + ServerAPI.GET_LICENSES)
    public Response getLicenses(@PathParam("gavc") final String gavc, @Context final UriInfo uriInfo){
        LOG.info("Got a get artifact licenses request.");
        final LicenseListView view = new LicenseListView("Licenses of " + gavc);

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final List<DbLicense> dbLicenses = getArtifactHandler().getArtifactLicenses(gavc,filters);
        for(DbLicense license: dbLicenses){
            view.add(getModelMapper().getLicense(license));
        }

        return Response.ok(view).build();
    }


    /**
     * Add a license to an artifact
     *
     * @param credential DbCredential
     * @param gavc String
     * @param licenseId String
     * @return Response
     */
    @POST
    @Path("/{gavc}" + ServerAPI.GET_LICENSES)
    public Response addLicense(@Auth final DbCredential credential, @PathParam("gavc") final String gavc,@QueryParam(ServerAPI.LICENSE_ID_PARAM) final String licenseId){
        if(!credential.getRoles().contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a add license request.");

        if(licenseId == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        getArtifactHandler().addLicenseToArtifact(gavc, licenseId);

        return Response.ok("done").build();
    }

    /**
     * Removes a license from an artifact
     *
     * @param credential DbCredential
     * @param gavc String
     * @param licenseId String
     * @return Response
     */
    @DELETE
    @Path("/{gavc}" + ServerAPI.GET_LICENSES)
    public Response deleteLicense(@Auth final DbCredential credential, @PathParam("gavc") final String gavc,@QueryParam(ServerAPI.LICENSE_ID_PARAM) final String licenseId){
        if(!credential.getRoles().contains(AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a delete license request.");

        if(licenseId == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        getArtifactHandler().removeLicenseFromArtifact(gavc, licenseId);

        return Response.ok("done").build();
    }

    /**
     * Returns the Module of an artifact.
     * This method is call via GET <grapes_url>/artifact/{gavc}/module
     *
     * @param gavc String
     * @return Response a module in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{gavc}" + ServerAPI.GET_MODULE)
    public Response getModule(@PathParam("gavc") final String gavc, @Context final UriInfo uriInfo){
        LOG.info("Got a get artifact's module request.");
        final ArtifactHandler artifactHandler = getArtifactHandler();
        final DbArtifact artifact = artifactHandler.getArtifact(gavc);
        final DbModule module = artifactHandler.getModule(artifact);

        if(module == null){
            return Response.noContent().build();
        }

        final ModuleView view = new ModuleView();
        view.setModule(getModelMapper().getModule(module));
        view.setOrganization(module.getOrganization());

        return Response.ok(view).build();
    }

    /**
     * Returns the Organization of an artifact.
     * This method is call via GET <grapes_url>/artifact/{gavc}/module
     *
     * @param gavc String
     * @return Response a module in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{gavc}" + ServerAPI.GET_ORGANIZATION)
    public Response getOrganization(@PathParam("gavc") final String gavc, @Context final UriInfo uriInfo){
        LOG.info("Got a get artifact's organization request.");
        final ArtifactHandler artifactHandler = getArtifactHandler();
        final DbArtifact artifact = artifactHandler.getArtifact(gavc);
        final DbModule module = artifactHandler.getModule(artifact);
        if(module == null || module.getOrganization().isEmpty()){
            return Response.noContent().build();
        }

        final DbOrganization organization = getOrganizationHandler().getOrganization(module.getOrganization());
        final OrganizationView view = new OrganizationView(getModelMapper().getOrganization(organization));
        return Response.ok(view).build();
    }

    /**
     * Return all the artifacts that matches the filters.
     * This method is call via GET <grapes_url>/artifact/<gavc>
     * Following filters can be used: artifactId, classifier, groupId, hasLicense, licenseId, type, uriInfo, version
     *
     * @param uriInfo UriInfo
     * @return Response An artifact in HTML or JSON
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(ServerAPI.GET_ALL)
    public Response getAll(@Context final UriInfo uriInfo){
        LOG.info("Got a get all artifact request.");

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final List<Artifact> artifacts = new ArrayList<Artifact>();

        final List<DbArtifact> dbArtifacts = getArtifactHandler().getArtifacts(filters);
        for(DbArtifact dbArtifact: dbArtifacts){
            artifacts.add(getModelMapper().getArtifact(dbArtifact));
        }

        return Response.ok(artifacts).build();
    }
}