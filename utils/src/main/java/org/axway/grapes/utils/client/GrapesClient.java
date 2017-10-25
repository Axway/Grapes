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

    public static final String FAILED_TO_GET_CORPORATE_FILTERS = "Failed to get Corporate filters";
    public static final String FAILED_TO_GET_MODULE = "Failed to %s module %s, version %s";

    private static final Logger LOG = LoggerFactory.getLogger(GrapesClient.class);

    private static final String HTTP_STATUS_TEMPLATE_MSG = "%s. Http status: %s";

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

        return Client.create(cfg);
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

        if(LOG.isErrorEnabled()) {
            LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, "Failed to reach the targeted Grapes server", response.getStatus()));
        }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Get a module build info
     *
     * @param moduleName String
     * @param moduleVersion String
     * @throws GrapesCommunicationException
     */
    public Map<String, String> getBuildInfo(final String moduleName, final String moduleVersion) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getBuildInfoPath(moduleName, moduleVersion));
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to GET buildInfo";
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(new GenericType<Map<String,String>>(){});
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            final String message = String.format(FAILED_TO_GET_MODULE, "to delete module", name, version);

            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            final String message = String.format(FAILED_TO_GET_MODULE, "get module details", name, version);
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(Module.class);
    }

    /**
     * Get a list of modules regarding filters
     *
     * @param filters Map<String,String>
     * @return List<Module>
     * @throws GrapesCommunicationException
     */
    public List<Module> getModules(final Map<String, String> filters) throws GrapesCommunicationException {
        final Client client = getClient();
        WebResource resource = client.resource(serverURL).path(RequestUtils.getAllModulesPath());
        for(final Map.Entry<String,String> queryParam: filters.entrySet()){
            resource = resource.queryParam(queryParam.getKey(), queryParam.getValue());
        }

        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get filtered modules.";
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(new GenericType<List<Module>>(){});
    }

    /**
     * Send a get module versions request
     *
     * @param name String
     * @return a list of versions
     * @throws GrapesCommunicationException
     */
    public List<String> getModuleVersions(final String name) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getModuleVersionsPath(name));
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get module versions of " + name;
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(new GenericType<List<String>>(){});
    }

    /**
     * Send a get module promotion status request
     *
     * @param name String
     * @param version String
     * @return a boolean
     * @throws GrapesCommunicationException
     */
    public Boolean getModulePromotionStatus(final String name, final String version) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getModulePromotionPath(name, version));
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = String.format(FAILED_TO_GET_MODULE, "get module promotion path", name, version);

            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(Boolean.class);
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
            final String message = String.format(FAILED_TO_GET_MODULE, "promote module", name, version);
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }

    /**
     * Check if a module can be promoted in the Grapes server
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
            final String message = String.format(FAILED_TO_GET_MODULE, "check if the module can be promoted", name, version);

            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(Boolean.class);
    }


    public <T> T getModulePromotionReportRaw(final String name,
                                             final String version,
                                             final boolean excludeSnapshotValidation,
                                             final Class<T> entityClass) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.promoteModuleReportPath(name, version, excludeSnapshotValidation));
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = String.format(FAILED_TO_GET_MODULE, "get module promotion report", name, version);

            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(entityClass);
    }

    /**
     * Check if a module can be promoted in the Grapes server
     *
     * @param name
     * @param version
     * @return a boolean which is true only if the module can be promoted
     * @throws GrapesCommunicationException
     */
    public PromotionEvaluationReport getModulePromotionReport(final String name, final String version) throws GrapesCommunicationException {
        return getModulePromotionReportRaw(name, version, false, PromotionEvaluationReport.class);
    }
    
    /**
     * Post an artifact to the Grapes server
     *
     * @param artifact The artifact to post
     * @param user The user posting the information
     * @param password The user password
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }
    
    
    /**
     * send a get request to check artifact is marled as "DO_NOT_USE" 
     *
     * @param gavc
     * @return if artifact is using any dependencies which are marked with DO_NOT_USE flag.
     * @throws GrapesCommunicationException
     */
    public Boolean isMarkedAsDoNotUse(final String gavc) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getDoNotUseArtifact(gavc));
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to check do not use artifact";
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }
        
        return response.getEntity(Boolean.class);
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
            final String message = FAILED_TO_GET_CORPORATE_FILTERS;
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            final String message = FAILED_TO_GET_CORPORATE_FILTERS;
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            final String message = FAILED_TO_GET_CORPORATE_FILTERS;
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            final String message = String.format(FAILED_TO_GET_MODULE, "get module ancestors", moduleName, moduleVersion);
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
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
            final String message = String.format(FAILED_TO_GET_MODULE, "get module ancestors ", moduleName, moduleVersion);
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(new GenericType<List<Dependency>>(){});
    }


    /**
     * Returns the organization of a given module
     *
     * @return Organization
     */
    public Organization getModuleOrganization(final String moduleName, final String moduleVersion) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getModuleOrganizationPath(moduleName, moduleVersion));
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get module's organization";
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(Organization.class);

    }

    /**
     * Returns the list of module names of a product
     *
     * @return List<String>
     */
    public List<String> getProductModuleNames(final String projectId) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getProjectModuleNames(projectId));
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get project module names";
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(new GenericType<List<String>>(){});

    }

    /**
     * Returns the delivery of Product
     * 
     * @return Delivery
     */
    public List<String> getAllProductNames() throws GrapesCommunicationException{
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getProductNames());
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get product names";
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }

        return response.getEntity(new GenericType<List<String>>(){});
    }
    
    /**
     * Returns the delivery of Product
     *
     * @return Delivery
     */
    public Delivery getProductDelivery(final String productLogicalName, final String commercialName, final String commercialVersion) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getProductDelivery(productLogicalName, commercialName, commercialVersion));
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get product deliveries";
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }
        return response.getEntity(Delivery.class);

    }
    
    /**
     * Returns the list of Product delivery
     *
     * @return List<Delivery>
     */
    public List<Delivery> getProductDeliveries(final String productLogicalName) throws GrapesCommunicationException {
        final Client client = getClient();
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getProductDelivery(productLogicalName));
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        client.destroy();
        if(ClientResponse.Status.OK.getStatusCode() != response.getStatus()){
            final String message = "Failed to get product deliveries";
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }
        return response.getEntity(new GenericType<List<Delivery>>(){});

    }
    

    /**
     * Create an Product delivery
     *
     * @throws AuthenticationException, GrapesCommunicationException, IOException 
     */
    public void createProductDelivery(final String productLogicalName, final Delivery delivery, final String user, final String password) throws GrapesCommunicationException, AuthenticationException {      
    	final Client client = getClient(user, password);
        final WebResource resource = client.resource(serverURL).path(RequestUtils.getProductDelivery(productLogicalName));
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, delivery);

        client.destroy();
        if(ClientResponse.Status.CREATED.getStatusCode() != response.getStatus()){
            final String message = "Failed to create a delivery";
            if(LOG.isErrorEnabled()) {
                LOG.error(String.format(HTTP_STATUS_TEMPLATE_MSG, message, response.getStatus()));
            }
            throw new GrapesCommunicationException(message, response.getStatus());
        }
    }
}
