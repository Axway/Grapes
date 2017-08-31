package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesEmailConfig;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.config.Messages;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.services.email.MessageKey;
import org.axway.grapes.server.db.ModelMapper;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class ArtifactResourceTest extends ResourceTest {

    private RepositoryHandler repositoryHandler;
    private final String templatePath = GrapesTestUtils.class.getResource("all-messages-pretty-print.txt").getPath();

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = GrapesTestUtils.getRepoHandlerMock();

        ArtifactResource resource = new ArtifactResource(repositoryHandler,
                GrapesTestUtils.getGrapesConfig());
        addProvider(new BasicAuthProvider<DbCredential>(new GrapesAuthenticator(repositoryHandler), "test auth"));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);
    }
    
//    private GrapesServerConfig getGrapesConfig(){
//        GrapesServerConfig config = mock(GrapesServerConfig.class);
//        List<String> validatedTypes = new ArrayList<String>();
//        validatedTypes.add("filetype1");
//        validatedTypes.add("filetype2");
//        when(config.getExternalValidatedTypes()).thenReturn(validatedTypes);
//        when(config.getArtifactNotificationRecipients()).thenReturn(new String[] {"toto@axway.com"});
//
//        GrapesEmailConfig emailCfgMock = mock(GrapesEmailConfig.class);
//        when(config.getGrapesEmailConfig()).thenReturn(emailCfgMock);
//
//        Properties p = new Properties();
//        p.setProperty("mail.smtp.host", "1");
//        p.setProperty("mail.smtp.user", "2");
//        p.setProperty("mail.smtp.password", "3");
//        p.setProperty("mail.smtp.ssl.trust", "4");
//        p.setProperty("mail.smtp.from", "5");
//
//        when(emailCfgMock.getProperties()).thenReturn(p);
//
//        return config;
//    }

    @Test
    public void getDocumentation(){
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        ClientResponse response = resource.type(MediaType.TEXT_HTML).get(ClientResponse.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }
    
    @Test
    public void postArtifactTest(){
        final String sha = "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c";
        Artifact artifact = DataModelFactory.createArtifact(GrapesTestUtils.CORPORATE_GROUPID_4TEST, "artifactId", "version", "classifier", "type", "extension");
        artifact.setSha256(sha);
        
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, artifact);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());

        final ArgumentCaptor<DbArtifact> captor = ArgumentCaptor.forClass(DbArtifact.class);
        verify(repositoryHandler, times(1)).store(captor.capture());

        assertEquals(sha, captor.getValue().getSha256());
    }    
    
    @Test
    public void postArtifactNullSHATest(){
        Artifact artifact = DataModelFactory.createArtifact(GrapesTestUtils.CORPORATE_GROUPID_4TEST, "artifactId", "version", "classifier", "type", "extension");
        
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, artifact);

        final String errorMessage = response.getEntity(String.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Artifact SHA256 checksum should not be null or empty", errorMessage);        
        
        final ArgumentCaptor<DbArtifact> captor = ArgumentCaptor.forClass(DbArtifact.class);
        verify(repositoryHandler, times(0)).store(captor.capture());
    } 
    
    @Test
    public void postArtifactWrongSHATest(){
        Artifact artifact = DataModelFactory.createArtifact(GrapesTestUtils.CORPORATE_GROUPID_4TEST, "artifactId", "version", "classifier", "type", "extension");
        artifact.setSha256("smallLengthSHACode");
        
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, artifact);

        final String errorMessage = response.getEntity(String.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Artifact SHA256 checksum length should be 64", errorMessage);        

        final ArgumentCaptor<DbArtifact> captor = ArgumentCaptor.forClass(DbArtifact.class);
        verify(repositoryHandler, times(0)).store(captor.capture());
    }
    
    @Test
    public void postArtifactDuplicateSHATest(){
        String sha256 = "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c";
        DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setArtifactId("artifact");
        dbArtifact.setGroupId("groupId");
        dbArtifact.setVersion("version");
        dbArtifact.setClassifier("classifier");
        dbArtifact.setExtension("extension");
        dbArtifact.setSha256(sha256);
        
        when(repositoryHandler.getArtifactUsingSHA256(sha256)).thenReturn(dbArtifact);
        
    	Artifact artifact = DataModelFactory.createArtifact(GrapesTestUtils.CORPORATE_GROUPID_4TEST, "artifactId", "version", "classifier", "type", "extension");
        artifact.setSha256(sha256);
        
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, artifact);

        final String errorMessage = response.getEntity(String.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT_409, response.getStatus());
        assertEquals("Artifact with same checksum already exists.", errorMessage);        

        final ArgumentCaptor<DbArtifact> captor = ArgumentCaptor.forClass(DbArtifact.class);
        verify(repositoryHandler, times(0)).store(captor.capture());
    }    
    
    @Test
    public void postArtifactDuplicateGAVCTest(){
        String sha256 = "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c";
        DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setArtifactId("artifactId");
        dbArtifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        dbArtifact.setVersion("version");
        dbArtifact.setClassifier("classifier");
        dbArtifact.setExtension("extension");
        dbArtifact.setSha256(sha256);
        
        when(repositoryHandler.getArtifact(dbArtifact.getGavc())).thenReturn(dbArtifact);
        
    	Artifact artifact = DataModelFactory.createArtifact(GrapesTestUtils.CORPORATE_GROUPID_4TEST, "artifactId", "version", "classifier", "type", "extension");
        artifact.setSha256(sha256);
        
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, artifact);

        final String errorMessage = response.getEntity(String.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT_409, response.getStatus());
        assertEquals("Artifact with same GAVC already exists.", errorMessage);        

        final ArgumentCaptor<DbArtifact> captor = ArgumentCaptor.forClass(DbArtifact.class);
        verify(repositoryHandler, times(0)).store(captor.capture());
    }
    
    @Test
    public void isPromoted(){
    	final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        artifact.setClassifier("classifier");
        artifact.setPromoted(true);
        when(repositoryHandler.getArtifactUsingSHA256("6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c")).thenReturn(artifact);
        
    	ArtifactQuery artifactQuery = new ArtifactQuery("User", 1, "File1", "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c", "filetype1", "");

        MultivaluedMap<String, String> params = makeParams(artifactQuery);

        Messages.init(templatePath);

    	WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/isPromoted");
        ClientResponse response = resource.queryParams(params).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        final ArtifactPromotionStatus promotionStatus = response.getEntity(ArtifactPromotionStatus.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertTrue(promotionStatus.isPromoted());
        assertEquals("Artifact is promoted", promotionStatus.getMessage());
    }

    @Test
    public void isPromotedNotValidTypeStageUpload(){
        when(repositoryHandler.getArtifactUsingSHA256("6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c")).thenReturn(null);
        
    	ArtifactQuery artifactQuery = new ArtifactQuery("User", 1, "File1", "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c", "notValidType", "");

        MultivaluedMap<String, String> params = makeParams(artifactQuery);

    	WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/isPromoted");
        ClientResponse response = resource.queryParams(params).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        final String returnMessage = response.getEntity(String.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, response.getStatus());
        assertEquals("Validation is not supported for this type of file", returnMessage);
    }
    
    @Test
    public void isPromotedNotValidTypeStagePublish(){
        final String sha = "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c";
        when(repositoryHandler.getArtifactUsingSHA256(sha)).thenReturn(null);
        
    	ArtifactQuery artifactQuery = new ArtifactQuery("User", 0, "File1", sha, "notValidType", "");

        MultivaluedMap<String, String> params = makeParams(artifactQuery);

    	WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/isPromoted");
        ClientResponse response = resource.queryParams(params).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        final String returnMessage = response.getEntity(String.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, response.getStatus());
        assertEquals("Validation is not supported for this type of file", returnMessage);
    }

    @Test
    public void nonPromotedPublishTime(){
    	final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        artifact.setClassifier("classifier");
        artifact.setPromoted(false);
        when(repositoryHandler.getArtifactUsingSHA256("6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c")).thenReturn(artifact);
        
        ArtifactQuery artifactQuery = new ArtifactQuery("User", 1, "File1", "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c", "filetype1", "");

        MultivaluedMap<String, String> params = makeParams(artifactQuery);
        Messages.init(templatePath);

    	WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/isPromoted");
        ClientResponse response = resource.queryParams(params).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        final ArtifactPromotionStatus promotionStatus = response.getEntity(ArtifactPromotionStatus.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertFalse(promotionStatus.isPromoted());

        assertEquals(Messages.get(MessageKey.ARTIFACT_VALIDATION_NOT_PROMOTED_YET), promotionStatus.getMessage());
    }
    
    @Test
    public void artifactNotKnownUploadTest(){
    	final String sha256 = "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c";
        when(repositoryHandler.getArtifactUsingSHA256(sha256)).thenReturn(null);
        
        ArtifactQuery artifactQuery = new ArtifactQuery("User", 0, "File1", sha256, "filetype1", "");

        MultivaluedMap<String, String> params = makeParams(artifactQuery);

        Messages.init(templatePath);

    	WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/isPromoted");
        ClientResponse response = resource.queryParams(params).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        final String returnMessage = response.getEntity(String.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals(Messages.get(MessageKey.ARTIFACT_VALIDATION_NOT_KNOWN), returnMessage);
    }

    @Test
    public void artifactKnownButNotPromotedTest() {
    	// Returning the message for unpublished artifact at promote time
        DbArtifact a = new DbArtifact();
        a.setPromoted(false);
        a.setGroupId("com.axway.toto");
        a.setSha256("6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c");
        when(repositoryHandler.getArtifactUsingSHA256(a.getSha256())).thenReturn(a);
        
        ArtifactQuery artifactQuery = new ArtifactQuery("User", 1, "File1", "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c", "filetype1", "");

        MultivaluedMap<String, String> params = makeParams(artifactQuery);
        Messages.init(templatePath);
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/isPromoted");
        ClientResponse response = resource.queryParams(params).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());

        String entity = response.getEntity(String.class);
        assertTrue(entity.contains(Messages.get(MessageKey.ARTIFACT_VALIDATION_NOT_PROMOTED_YET)));

    }

    private MultivaluedMap<String, String> makeParams(ArtifactQuery artifactQuery) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("user", artifactQuery.getUser());
        params.add("stage", artifactQuery.getStage() + "");
        params.add("name", artifactQuery.getName());
        params.add("sha256", artifactQuery.getSha256());
        params.add("type", artifactQuery.getType());
        return params;
    }


    @Test
    public void getValidationType(){

        final List<String> results = GrapesTestUtils.getGrapesConfig().getExternalValidatedTypes();;
        
        assertFalse(results.isEmpty());
        assertFalse(!results.contains("filetype1"));
        assertFalse(!results.contains("filetype2"));
    }
    
    @Test
    public void checkDefaultValidationTypes(){
    	GrapesServerConfig config = new GrapesServerConfig();
    	
    	List<String> allValidationTypes = config.getExternalValidatedTypes();
    	
    	assertNotNull(allValidationTypes);
    	assertEquals(8, allValidationTypes.size());
    	assertTrue(allValidationTypes.contains("program"));
    	assertTrue(allValidationTypes.contains("installer"));
    	assertTrue(allValidationTypes.contains("patch"));
    	assertTrue(allValidationTypes.contains("servicepack"));
    	assertTrue(allValidationTypes.contains("upgradepack"));
    	assertTrue(allValidationTypes.contains("install"));
    	assertTrue(allValidationTypes.contains("axwayjre"));
    	assertTrue(allValidationTypes.contains("JREUpdateTool"));
    }

    @Test
    public void postArtifact() throws AuthenticationException, UnknownHostException {
        Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extension");
        artifact.setSha256("6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c");
        artifact.setDownloadUrl("downloadUrl");
        artifact.setSize("size");

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, artifact);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());
    }

    @Test
    public void postMalFormedArtifact() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createArtifact(null, null, null, null, null, null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createArtifact("groupId", null, null, null, null, null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createArtifact("", "", "", null, null, null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }
    
    @Test
    public void checkAuthorizationOnPostArtifact() throws AuthenticationException {
        Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extension");
        artifact.setSha256("6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c");
        artifact.setDownloadUrl("downloadUrl");
        artifact.setSize("size");
        
        //user does not have DATA_UPDATER privileges
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.UNAUTHORIZED_USER_FOR_POSTING, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, artifact);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void updateDownloadURL() throws AuthenticationException, UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        artifact.setClassifier("classifier");
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_DOWNLOAD_URL);
        ClientResponse response = resource.queryParam(ServerAPI.URL_PARAM, "testUrl").post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        verify(repositoryHandler, times(1)).updateDownloadUrl(artifact, "testUrl");
    }

    @Test
    public void updateDownloadURLMalFormed() throws AuthenticationException, UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        artifact.setClassifier("classifier");
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_DOWNLOAD_URL);
        ClientResponse response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_ACCEPTABLE_406, response.getStatus());
    }

    @Test
    public void updateProvider() throws AuthenticationException, UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        artifact.setClassifier("classifier");
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_PROVIDER);
        ClientResponse response = resource.queryParam(ServerAPI.PROVIDER_PARAM, "providerTest").post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        verify(repositoryHandler, times(1)).updateProvider(artifact, "providerTest");
    }

    @Test
    public void updateProviderMalFormed() throws AuthenticationException, UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        artifact.setClassifier("classifier");
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_PROVIDER);
        ClientResponse response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_ACCEPTABLE_406, response.getStatus());
    }

    @Test
    public void getAllGavcs() throws UnknownHostException{
        final List<String> gavcs = new ArrayList<String>();
        gavcs.add("gavc1");
        when(repositoryHandler.getGavcs((FiltersHolder) anyObject())).thenReturn(gavcs);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + ServerAPI.GET_GAVCS);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<String> gavcsResults = response.getEntity(new GenericType<List<String>>(){});
        assertNotNull(gavcsResults);
        assertEquals(1, gavcsResults.size());
        assertEquals("gavc1", gavcsResults.get(0));
    }

    @Test
    public void getAllGroupIds() throws UnknownHostException{
        final List<String> groupIds = new ArrayList<String>();
        groupIds.add("groupId1");
        when(repositoryHandler.getGroupIds((FiltersHolder) anyObject())).thenReturn(groupIds);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + ServerAPI.GET_GROUPIDS);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<String> results = response.getEntity(new GenericType<List<String>>(){});
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("groupId1", results.get(0));
    }

    @Test
    public void getAnArtifact() throws UnknownHostException{
        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId("groupId");
        dbArtifact.setArtifactId("artifactId");
        dbArtifact.setVersion("1.0.0-SNAPSHOT");
        dbArtifact.setClassifier("win");
        dbArtifact.setType("component");
        dbArtifact.setExtension("jar");
        dbArtifact.setDownloadUrl("nowhere");
        dbArtifact.setSize("10Mo");

        final DbLicense license = new DbLicense();
        license.setName("licenseId");
        dbArtifact.addLicense(license);

        when(repositoryHandler.getArtifact(dbArtifact.getGavc())).thenReturn(dbArtifact);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + dbArtifact.getGavc());
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        Artifact artifact = response.getEntity(Artifact.class);
        assertNotNull(artifact);

        assertEquals(dbArtifact.getGroupId(), artifact.getGroupId());
        assertEquals(dbArtifact.getArtifactId(), artifact.getArtifactId());
        assertEquals(dbArtifact.getVersion(), artifact.getVersion());
        assertEquals(dbArtifact.getClassifier(), artifact.getClassifier());
        assertEquals(dbArtifact.getType(), artifact.getType());
        assertEquals(dbArtifact.getExtension(), artifact.getExtension());
        assertEquals(dbArtifact.getSize(), artifact.getSize());
        assertEquals(dbArtifact.getDownloadUrl(), artifact.getDownloadUrl());
        assertEquals(1, artifact.getLicenses().size());
        assertEquals("licenseId", artifact.getLicenses().get(0));
    }


    @Test
    public void deleteAnArtifact() throws UnknownHostException, AuthenticationException{
        when(repositoryHandler.getArtifact(anyString())).thenReturn(new DbArtifact());
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc");
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void getAncestors() throws UnknownHostException {
        final List<DbModule> dependencies = new ArrayList<DbModule>();
        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("version");
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        module.addDependency(artifact.getGavc(), Scope.TEST);
        dependencies.add(module);
        when(repositoryHandler.getAncestors((DbArtifact) anyObject(), (FiltersHolder) anyObject())).thenReturn(dependencies);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_ANCESTORS);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<Dependency> dependencyList = response.getEntity(new GenericType<List<Dependency>>(){});
        assertNotNull(dependencyList);
        assertEquals(1, dependencyList.size());
        assertEquals(module.getName(), dependencyList.get(0).getSourceName());
        assertEquals(module.getVersion(), dependencyList.get(0).getSourceVersion());
        assertEquals(artifact.getGavc(), dependencyList.get(0).getTarget().getGavc());
        assertEquals(Scope.TEST, dependencyList.get(0).getScope());
    }

    @Test
    public void getLicenses() throws UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        final DbLicense license = new DbLicense();
        license.setName("licenseId");
        artifact.addLicense(license);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_LICENSES);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<License> licenses = response.getEntity(new GenericType<List<License>>(){});
        assertNotNull(licenses);
        assertEquals(1, licenses.size());

        final ModelMapper modelMapper = new ModelMapper(repositoryHandler);
        assertEquals(modelMapper.getLicense(license), licenses.get(0));
    }


    @Test
    public void getVersions() throws UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        final List<String> versions = new ArrayList<String>();
        versions.add("1");

        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getArtifactVersions(artifact)).thenReturn(versions);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_VERSIONS);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<String> receivedVersions = response.getEntity(new GenericType<List<String>>(){});
        assertNotNull(receivedVersions);
        assertEquals(1, receivedVersions.size());
        assertEquals("1", receivedVersions.get(0));
    }


    @Test
    public void getLastVersion() throws UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        final List<String> versions = new ArrayList<String>();
        versions.add("1");
        versions.add("2");

        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getArtifactVersions(artifact)).thenReturn(versions);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_LAST_VERSION);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        String lastVersion = response.getEntity(String.class);
        assertNotNull(lastVersion);
        assertEquals("2", lastVersion);
    }

    @Test
    public void getAddLicenseToArtifact() throws AuthenticationException, UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        final DbLicense license = new DbLicense();
        license.setName("licenseId");
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_LICENSES);
        ClientResponse response = resource.queryParam(ServerAPI.LICENSE_ID_PARAM, license.getName()).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        verify(repositoryHandler, times(1)).addLicenseToArtifact(artifact, license.getName());
    }

    @Test
    public void getRemoveLicenseToArtifact() throws AuthenticationException, UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        final DbLicense license = new DbLicense();
        license.setName("licenseId");
        artifact.addLicense(license);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_LICENSES);
        ClientResponse response = resource.queryParam(ServerAPI.LICENSE_ID_PARAM, license.getName()).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        verify(repositoryHandler, times(1)).removeLicenseFromArtifact(eq(artifact), eq(license.getName()), any());
    }

    @Test
    public void addDoNotUseFlag() throws AuthenticationException, UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("version");
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.SET_DO_NOT_USE);
        ClientResponse response = resource.queryParam(ServerAPI.DO_NOT_USE, "true").post(ClientResponse.class, "commentText");
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        verify(repositoryHandler, times(1)).updateDoNotUse(artifact, Boolean.TRUE);
    }


    @Test
    public void getDoNotUse() throws UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1.0.0");

        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.SET_DO_NOT_USE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final Boolean test = response.getEntity(Boolean.class);
        assertNotNull(test);
        assertFalse(test);
    }


    @Test
    public void getModule() throws UnknownHostException {
        final DbModule dbModule = new DbModule();
        dbModule.setName("module1");
        dbModule.setVersion("1.0.0");
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1.0.0");

        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getRootModuleOf(artifact.getGavc())).thenReturn(dbModule);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_MODULE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final Module gotModule = response.getEntity(Module.class);
        assertNotNull(gotModule);
        assertEquals(dbModule.getName(), gotModule.getName());
        assertEquals(dbModule.getVersion(), gotModule.getVersion());
    }


    @Test
    public void getModuleThatDoesNotExist() throws UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("2.3.0");

        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getRootModuleOf(artifact.getGavc())).thenReturn(null);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_MODULE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT_204, response.getStatus());
    }


    @Test
    public void getOrganization() throws UnknownHostException {
        final DbOrganization dbOrganization = new DbOrganization();
        dbOrganization.setName("organization1");
        final DbModule dbModule = new DbModule();
        dbModule.setName("module1");
        dbModule.setVersion("1.0.0");
        dbModule.setOrganization(dbOrganization.getName());
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1.0.0");

        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getRootModuleOf(artifact.getGavc())).thenReturn(dbModule);
        when(repositoryHandler.getOrganization(dbOrganization.getName())).thenReturn(dbOrganization);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_ORGANIZATION);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final Organization gotOrganization = response.getEntity(Organization.class);
        assertNotNull(gotOrganization);
        assertEquals(dbOrganization.getName(), gotOrganization.getName());
    }


    @Test
    public void getOrganizationButTheModuleDoesNotHoldAny() throws UnknownHostException {
        final DbModule dbModule = new DbModule();
        dbModule.setName("module1");
        dbModule.setVersion("1.0.0");
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1.0.0");

        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getRootModuleOf(artifact.getGavc())).thenReturn(dbModule);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_ORGANIZATION);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT_204, response.getStatus());
    }


    @Test
    public void getOrganizationButThereIsNoModule() throws UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1.0.0");

        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact.getGavc() + ServerAPI.GET_ORGANIZATION);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT_204, response.getStatus());
    }

    @Test
    public void checkAuthenticationOnPostAndDeleteMethods(){
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.WRONG_USER_4TEST, GrapesTestUtils.WRONG_PASSWORD_4TEST));

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc");
        response = resource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.GET_LICENSES);
        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.GET_LICENSES);
        response = resource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.SET_DO_NOT_USE);
        response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        response = resource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.GET_DOWNLOAD_URL);
        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.GET_PROVIDER);
        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void notFound() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));

        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrong" + ServerAPI.GET_LICENSES);
        ClientResponse response = resource.queryParam(ServerAPI.LICENSE_ID_PARAM, "test").post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrong" + ServerAPI.GET_LICENSES);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrong" + ServerAPI.GET_ANCESTORS);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrongGavc");
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrongGavc");
        response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrongGavc" + ServerAPI.GET_LICENSES);
        response = resource.queryParam(ServerAPI.LICENSE_ID_PARAM, "test").delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrongGavc"  + ServerAPI.GET_LICENSES);
        response = resource.queryParam(ServerAPI.LICENSE_ID_PARAM, "test").delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrongGavc"  + ServerAPI.SET_DO_NOT_USE);
        response = resource.queryParam(ServerAPI.DO_NOT_USE, "true").post(ClientResponse.class, "commentText");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrongGavc"  + ServerAPI.GET_DOWNLOAD_URL);
        response = resource.queryParam(ServerAPI.URL_PARAM, "test").post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrongGavc"  + ServerAPI.GET_PROVIDER);
        response = resource.queryParam(ServerAPI.PROVIDER_PARAM, "test").post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrongGavc"  + ServerAPI.GET_VERSIONS);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/wrongGavc"  + ServerAPI.GET_LAST_VERSION);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

}
