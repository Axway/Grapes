package org.axway.grapes.utils.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.utils.data.model.ArtifactList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Abstract Client
 *
 * <p>Implemented Grapes client.</p>
 *
 * @author jdcoffre
 */
public class GrapesClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrapesClient.class);

    private final String serverURL;

    private Integer timeout = 60000;

    public GrapesClient(final String host, final String port){
        // Generate Grapes Url
        final StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(host);
        if(port != null && !port.isEmpty()){
            sb.append(":");
            sb.append(port);
        }
        sb.append("/");

        this.serverURL = sb.toString();
    }

    public void setTimeout(final Integer timeout) {
        this.timeout = timeout;
    }


    public String getServerURL(){
        return serverURL;
    }

    /**
     * Provide Jersey client for the targeted Grapes server
     *
     * @return webResource
     */
    private Client getClient(){
        final ClientConfig cfg = new DefaultClientConfig();
        cfg.getClasses().add(com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider.class);
        cfg.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, timeout);

        final Client jerseyClient = Client.create(cfg);

        return jerseyClient;
    }

    /**
     *
     * Provide Jersey client for the targeted Grapes server with authentication
     *
     * @param user
     * @param password
     * @return
     * @throws javax.naming.AuthenticationException
     */
    private Client getClient(final String user, final String password) throws AuthenticationException {
        if(user == null || password == null){
            LOG.error("You are currently using a method that requires credentials. Please use '-user' '-password'.");
            throw new AuthenticationException();
        }

        final Client client = getClient();
        client.addFilter(new HTTPBasicAuthFilter(user, password));

        return client;
    }

    /**
     * Checks if the dependency server is available
     *
     * @return true if the server is reachable, false otherwise
     */
    public boolean isServerAvailable(){
        final Client client = getClient();
        final ClientResponse response = client.resource(serverURL).get(ClientResponse.class);

        if(ClientResponse.Status.OK.getStatusCode() == response.getStatus()){
            return true;
        }

        LOG.error("Failed to reach the targeted Grapes server. Http status: " + response.getStatus());
        client.destroy();

        return false;
    }

    /**
     * Post a build info to the server
     *
     * @param moduleName String
     * @param moduleVersion String
     * @param buildInfo Map<String,String>
     * @param user String
     * @param password String
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void postBuildInfo(final String moduleName, final String moduleVersion, final Map<String, String> buildInfo, final String user, final String password) throws GrapesCommunicationException, AuthenticationException {
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getBuildInfoPath(moduleName, moduleVersion));
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, buildInfo);

        client.destroy();
        if(ClientResponse.Status.CREATED.getStatusCode() != response.getStatus()){
            final String message = "Failed to POST buildInfo";
            LOG.error("%s . Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Post a module to the server
     *
     * @param module
     * @param user
     * @param password
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void postModule(final Module module, final String user, final String password) throws GrapesCommunicationException, AuthenticationException {
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.moduleResourcePath());
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, module);

        client.destroy();
        if(ClientResponse.Status.CREATED.getStatusCode() != response.getStatus()){
            final String message = "Failed to POST module";
            LOG.error("%s . Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Delete a module from Grapes server
     *
     * @param name
     * @param version
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void deleteModule(final String name, final String version, final String user, final String password) throws GrapesCommunicationException, AuthenticationException{
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getModulePath(name, version));
        final ClientResponse response = resource.delete(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to DELETE module " + name + " in version " + version;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Send a get module request
     *
     * @param name
     * @param version
     * @return the targeted module
     * @throws GrapesCommunicationException
     */
    public Module getModule(final String name, final String version) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getModulePath(name, version));
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get module " + name + " in version " + version;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(Module.class);
    }

    /**
     * Promote a module in the Grapes server
     *
     * @param name
     * @param version
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void promoteModule(final String name, final String version, final String user, final String password) throws GrapesCommunicationException, AuthenticationException{
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.promoteModulePath(name, version));
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to promote module " + name + " in version " + version;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Promote a module in the Grapes server
     *
     * @param name
     * @param version
     * @return a boolean which is true only if the module can be promoted
     * @throws GrapesCommunicationException
     */
    public Boolean moduleCanBePromoted(final String name, final String version) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.canBePromotedModulePath(name, version));
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get the promotion status of module " + name + " in version " + version;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(Boolean.class);
    }

    /**
     * Post an artifact to the Grapes server
     *
     * @param artifact
     * @param user
     * @param password
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void postArtifact(final Artifact artifact, final String user, final String password) throws GrapesCommunicationException, AuthenticationException {
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.artifactResourcePath());
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, artifact);

        client.destroy();
        if(ClientResponse.Status.CREATED.getStatusCode() != response.getStatus()){
            final String message = "Failed to POST artifact";
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Delete an artifact in the Grapes server
     *
     * @param gavc
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void deleteArtifact(final String gavc, final String user, final String password) throws GrapesCommunicationException, AuthenticationException{
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getArtifactPath(gavc));
        final ClientResponse response = resource.delete(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to DELETE artifact " + gavc;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Send a get artifact request
     *
     * @param gavc
     * @return the targeted artifact
     * @throws GrapesCommunicationException
     */
    public Artifact getArtifact(final String gavc) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getArtifactPath(gavc));
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get artifact " + gavc;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(Artifact.class);
    }

    /**
     * Send a get artifacts request
     *
     * @param hasLicense
     * @return list of artifact
     * @throws GrapesCommunicationException
     */
    public List<Artifact> getArtifacts(final Boolean hasLicense) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getArtifactsPath());
        final ClientResponse response = resource.queryParam(ServerAPI.HAS_LICENSE_PARAM, hasLicense.toString())
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get artifacts";
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(ArtifactList.class);
    }

    /**
     * Post boolean flag "DO_NOT_USE" to an artifact
     *
     * @param gavc
     * @param doNotUse
     * @param user
     * @param password
     * @throws GrapesCommunicationException
     */
    public void postDoNotUseArtifact(final String gavc, final Boolean doNotUse, final String user, final String password) throws GrapesCommunicationException, AuthenticationException {
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getDoNotUseArtifact(gavc));
        final ClientResponse response = resource.queryParam(ServerAPI.DO_NOT_USE, doNotUse.toString())
                .accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to post do not use artifact";
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }


    /**
     * Returns the artifact available versions
     *
     * @param gavc String
     * @return List<String>
     */
    public List<String> getArtifactVersions(final String gavc) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getArtifactVersions(gavc));
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get Corporate filters";
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(new GenericType<List<String>>(){});

    }


    /**
     * Returns the artifact last version
     *
     * @param gavc String
     * @return String
     */
    public String getArtifactLastVersion(final String gavc) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getArtifactLastVersion(gavc));
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get Corporate filters";
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(String.class);

    }


    /**
     * Returns the module of an artifact or null if there is none
     *
     * @param gavc String
     * @return Module
     */
    public Module getArtifactModule(final String gavc) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getArtifactModule(gavc));
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.NO_CONTENT.getStatusCode() == response.getStatus()){
            return null;
        }

        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get Corporate filters";
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(Module.class);

    }

    /**
     * Add a license to an artifact
     *
     * @param gavc
     * @param licenseId
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void addLicense(final String gavc, final String licenseId, final String user, final String password) throws GrapesCommunicationException, AuthenticationException{
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getArtifactLicensesPath(gavc));
        final ClientResponse response = resource.queryParam(ServerAPI.LICENSE_ID_PARAM, licenseId).post(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to add license " + licenseId + " to artifact " + gavc;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Post a license to the server
     *
     * @param license
     * @param user
     * @param password
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void postLicense(final License license, final String user, final String password) throws GrapesCommunicationException, AuthenticationException {
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.licenseResourcePath());
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, license);

        client.destroy();
        if(ClientResponse.Status.CREATED.getStatusCode() != response.getStatus()){
            final String message = "Failed to POST license";
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Delete a license in the server
     *
     * @param licenseId
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void deleteLicense(final String licenseId, final String user, final String password) throws GrapesCommunicationException, AuthenticationException{
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getLicensePath(licenseId));
        final ClientResponse response = resource.delete(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to DELETE license " + licenseId;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Send a get license request
     *
     * @param licenseId
     * @return the targeted license
     * @throws GrapesCommunicationException
     */
    public License getLicense(final String licenseId) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getLicensePath(licenseId));
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get license " + licenseId;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(License.class);
    }

    /**
     * Approve or reject a license
     *
     * @param licenseId
     * @param approve
     * @throws GrapesCommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public void approveLicense(final String licenseId, final Boolean approve, final String user, final String password) throws GrapesCommunicationException, AuthenticationException{
        final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getLicensePath(licenseId));
        final ClientResponse response = resource.queryParam(ServerAPI.APPROVED_PARAM, approve.toString()).post(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to approve license " + licenseId;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Return the list of module ancestors
     *
     * @param moduleName
     * @param moduleVersion
     * @return List<Dependency>
     * @throws GrapesCommunicationException
     */
    public List<Dependency> getModuleAncestors(final String moduleName, final String moduleVersion) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getArtifactAncestors(moduleName, moduleVersion));
        final ClientResponse response = resource.queryParam(ServerAPI.SCOPE_COMPILE_PARAM, "true")
                .queryParam(ServerAPI.SCOPE_PROVIDED_PARAM, "true")
                .queryParam(ServerAPI.SCOPE_RUNTIME_PARAM, "true")
                .queryParam(ServerAPI.SCOPE_TEST_PARAM, "true")
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get module ancestors " + moduleName + " in version " + moduleVersion;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(new GenericType<List<Dependency>>(){});
    }

    /**
     * Return the list of module dependencies
     *
     * @param moduleName
     * @param moduleVersion
     * @param fullRecursive
     * @param corporate
     * @param thirdParty
     * @return List<Dependency>
     * @throws GrapesCommunicationException
     */
    public List<Dependency> getModuleDependencies(final String moduleName, final String moduleVersion, final Boolean fullRecursive, final Boolean corporate, final Boolean thirdParty) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getArtifactDependencies(moduleName, moduleVersion));
        final ClientResponse response = resource.queryParam(ServerAPI.SCOPE_COMPILE_PARAM, "true")
                .queryParam(ServerAPI.SCOPE_PROVIDED_PARAM, "true")
                .queryParam(ServerAPI.SCOPE_RUNTIME_PARAM, "true")
                .queryParam(ServerAPI.SCOPE_TEST_PARAM, "true")
                .queryParam(ServerAPI.RECURSIVE_PARAM, fullRecursive.toString())
                .queryParam(ServerAPI.SHOW_CORPORATE_PARAM, corporate.toString())
                .queryParam(ServerAPI.SHOW_THIRPARTY_PARAM, thirdParty.toString())
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get module ancestors " + moduleName + " in version " + moduleVersion;
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(new GenericType<List<Dependency>>(){});
    }


    /**
     * Returns the organization of a given module
     *
     * @return Organization
     */
    public Organization getModuleOrganization(final String moduleName, final String moduleVersion) throws GrapesCommunicationException, IOException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getModuleOrganizationPath(moduleName,moduleVersion));
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get module's organization";
            LOG.error("%s. Http status: %s", message, response.getStatus());
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(Organization.class);

    }
}
