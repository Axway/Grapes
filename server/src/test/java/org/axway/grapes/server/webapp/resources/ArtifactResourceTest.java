package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.webapp.auth.GrapesAuthProvider;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ArtifactResourceTest extends ResourceTest {

    private RepositoryHandler repositoryHandler;

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = mock(RepositoryHandler.class);
        final GrapesServerConfig dmConfig = GrapesTestUtils.getConfigMock();
        ArtifactResource resource = new ArtifactResource(repositoryHandler, dmConfig);

        addProvider(new GrapesAuthProvider(dmConfig));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);
    }

    @Test
    public void getDocumentation(){
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        ClientResponse response = resource.type(MediaType.TEXT_HTML).get(ClientResponse.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void postArtifact() throws AuthenticationException, UnknownHostException {
        Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extension");
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

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createArtifact("groupId", "artifactId", null, null, null, null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createArtifact("", "", "", null, null, null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
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
        when(repositoryHandler.getAncestors(anyString(), (FiltersHolder) anyObject())).thenReturn(dependencies);
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

        final List<String> licenses = response.getEntity(new GenericType<List<String>>(){});
        assertNotNull(licenses);
        assertEquals(1, licenses.size());
        assertEquals(license.getName(), licenses.get(0));
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
        verify(repositoryHandler, times(1)).addLicenseToArtifact(artifact, license);
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

        verify(repositoryHandler, times(1)).removeLicenseFromArtifact(artifact, license.getName());
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
        ClientResponse response = resource.queryParam(ServerAPI.DO_NOT_USE, "true").post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        verify(repositoryHandler, times(1)).updateDoNotUse(artifact, Boolean.TRUE);

    }

    @Test
    public void checkAuthenticationOnPostAndDeleteMethods(){
        WebResource resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        ClientResponse response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE);
        response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc");
        response = resource.delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.GET_LICENSES);
        response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.GET_LICENSES);
        response = resource.delete(ClientResponse.class);

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.SET_DO_NOT_USE);
        response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        response = resource.delete(ClientResponse.class);

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.GET_DOWNLOAD_URL);
        response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.ARTIFACT_RESOURCE + "/gavc" + ServerAPI.GET_PROVIDER);
        response = resource.post(ClientResponse.class);
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
        response = resource.queryParam(ServerAPI.DO_NOT_USE, "true").post(ClientResponse.class);
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
