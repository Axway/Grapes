package org.axway.grapes.utils.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse.Status;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.commons.utils.JsonUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;



import javax.naming.AuthenticationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;


public class GrapesClientTest {

    public static final String PROPERTY_PORT = "server.mock.http.port";
    private static final String DEFAULT_PORT = "8074";

    @Rule
    public ExpectedException exc = ExpectedException.none();

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(Integer.valueOf(System.getProperty(PROPERTY_PORT, DEFAULT_PORT)));

    private static GrapesClient client;
    private static String serverPort;

    @BeforeClass
    public static void startMock() throws IOException, InterruptedException{
        serverPort = System.getProperty(PROPERTY_PORT, DEFAULT_PORT);
        client = new GrapesClient("127.0.0.1", serverPort);
        client.setTimeout(1000);
    }

    @Test
    public void checkServerUrl() {
        GrapesClient grapesClient = new GrapesClient("host", null);
        assertEquals("http://host/", grapesClient.getServerURL());

        grapesClient = new GrapesClient("host", "");
        assertEquals("http://host/", grapesClient.getServerURL());

        grapesClient = new GrapesClient("host", "12345");
        assertEquals("http://host:12345/", grapesClient.getServerURL());
    }

    @Test
    public void serverIsAvailable() {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(Status.OK.getStatusCode())));

        assertTrue(client.isServerAvailable());
    }

    @Test
    public void serverIsNotAvailable() {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        assertFalse(client.isServerAvailable());
    }

    @Test
    public void postModule() throws IOException, AuthenticationException{
        Module module1 = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");
        Artifact artifact1 = DataModelFactory.createArtifact("com.axway.test", "artifact1", "1.0.0-SNAPSHOT", "win32",  "jar", "");
        Artifact artifact2 = DataModelFactory.createArtifact("com.axway.test", "artifact2", "1.0.0-SNAPSHOT", "win32", "jar", "");
        Artifact dependency = DataModelFactory.createArtifact("com.axway.test", "dependency", "1.0.0-SNAPSHOT", "win32", "jar", "");

        module1.addDependency(DataModelFactory.createDependency(artifact2,Scope.COMPILE));
        module1.addDependency(DataModelFactory.createDependency(dependency, Scope.TEST));
        module1.addArtifact(artifact1);
        module1.addArtifact(artifact2);

        stubFor(post(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE))
                .withRequestBody(equalTo(JsonUtils.serialize(module1)))
                .willReturn(aResponse()
                        .withStatus(Status.CREATED.getStatusCode())));

        Exception exception = null;

        try{
            client.postModule(module1, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void postModuleAuthFailes() throws IOException, AuthenticationException{
        Module module1 = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");
        Exception exception = null;

        try{
            client.postModule(module1, null, null);

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void postModuleFailed() throws AuthenticationException{
        Module module1 = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");
        Artifact artifact1 = DataModelFactory.createArtifact("com.axway.test", "artifact1", "1.0.0-SNAPSHOT", "win32",  "jar", "");
        Artifact artifact2 = DataModelFactory.createArtifact("com.axway.test", "artifact2", "1.0.0-SNAPSHOT", "win32", "jar", "");
        Artifact dependency = DataModelFactory.createArtifact("com.axway.test", "dependency", "1.0.0-SNAPSHOT", "win32", "jar", "");

        module1.addDependency(DataModelFactory.createDependency(artifact2,Scope.COMPILE));
        module1.addDependency(DataModelFactory.createDependency(dependency, Scope.TEST));
        module1.addArtifact(artifact1);
        module1.addArtifact(artifact2);

        stubFor(post(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_ACCEPTABLE.getStatusCode())));

        GrapesCommunicationException exception = null;

        try{
            client.postModule(module1, "user", "password");

        }catch (GrapesCommunicationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), exception.getHttpStatus());
    }

    @Test
    public void deleteModule(){
        String moduleName = "module";
        String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(delete(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion))
                .willReturn(aResponse()
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;

        try{
            client.deleteModule(moduleName, moduleVersion, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void deleteModuleNotFound(){
        String moduleName = "module";
        String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(delete(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.deleteModule(moduleName, moduleVersion, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getModule() throws IOException{
        String moduleName = "module";
        String moduleVersion = "1.0.0-SNAPSHOT";

        Module module1 = DataModelFactory.createModule(moduleName, moduleVersion);
        Artifact artifact1 = DataModelFactory.createArtifact("com.axway.test", "artifact1", "1.0.0-SNAPSHOT", "win32", "jar", "");
        Artifact artifact2 = DataModelFactory.createArtifact("com.axway.test", "artifact2", "1.0.0-SNAPSHOT", "win32", "jar", "");
        Artifact dependency = DataModelFactory.createArtifact("com.axway.test", "dependency", "1.0.0-SNAPSHOT", "win32", "jar", "");

        module1.addDependency(DataModelFactory.createDependency(artifact2,Scope.COMPILE));
        module1.addDependency(DataModelFactory.createDependency(dependency, Scope.TEST));
        module1.addArtifact(artifact1);
        module1.addArtifact(artifact2);

        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(module1))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        Module module2= null;

        try{
            module2 = client.getModule(moduleName, moduleVersion);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(module2);
        assertEquals(module1, module2);
    }

    @Test
    public void getModuleNotFound(){
        String moduleName = "module";
        String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getModule(moduleName, moduleVersion);

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getModules() throws IOException{
        String moduleName = "module";
        String moduleVersion = "1.0.0-SNAPSHOT";
        Module module1 = DataModelFactory.createModule(moduleName, moduleVersion);

        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + ServerAPI.GET_ALL + "?test=test.test"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(Collections.singletonList(module1)))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        List<Module> modules = null;

        try{
            modules = client.getModules(Collections.singletonMap("test", "test.test"));

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(modules);
        assertEquals(1, modules.size());
        assertEquals(module1, modules.get(0));
    }

    @Test
    public void getModulesNotFound() throws IOException{
        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + ServerAPI.GET_ALL + "?test=test.test"))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getModules(Collections.singletonMap("test", "test.test"));

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getModuleVersions() throws IOException{
        final String moduleName = "testModule";
        final List<String> versions = Lists.newArrayList("1", "2", "3");

        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + ServerAPI.GET_VERSIONS))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(versions))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        List<String> gotVersions = null;

        try{
            gotVersions = client.getModuleVersions(moduleName);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(gotVersions);
        assertEquals(versions.size(), gotVersions.size());
    }

    @Test
    public void getModuleVersionsNotFound() throws IOException{

        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/doesNotExit" + ServerAPI.GET_VERSIONS))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getModuleVersions("doesNotExit");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getModulePromotionStatus() throws IOException{
        final String moduleName = "testModule";
        final String moduleVersion = "1.2.0-3";

        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.PROMOTION))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(Boolean.TRUE))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        Boolean promotionStatus = null;

        try{
            promotionStatus = client.getModulePromotionStatus(moduleName, moduleVersion);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(promotionStatus);
        assertTrue(promotionStatus);
    }

    @Test
    public void getModulePromotionStatusNotFound() throws IOException{

        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/doesNotExit/1.0.0" + ServerAPI.PROMOTION))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(Boolean.TRUE))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;

        try{
            client.getModulePromotionStatus("doesNotExist", "1.0.0");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }


    @Test
    public void promoteModule(){
        String moduleName = "module";
        String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(post(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.PROMOTION))
                .willReturn(aResponse()
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;

        try{
            client.promoteModule(moduleName, moduleVersion, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void promoteModuleNotFound(){
        String moduleName = "module";
        String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(post(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.PROMOTION))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.promoteModule(moduleName, moduleVersion, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void isPromulgableModule(){
        String moduleName = "module";
        String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.PROMOTION + ServerAPI.GET_FEASIBLE))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody("true")
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        Boolean isPromulgable = null;

        try{
            isPromulgable = client.moduleCanBePromoted(moduleName, moduleVersion);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(isPromulgable);
        assertTrue(isPromulgable);
    }

    @Test
    public void isPromulgableModuleNotFound(){
        String moduleName = "module";
        String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(get(urlEqualTo("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.PROMOTION + ServerAPI.GET_FEASIBLE))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.moduleCanBePromoted(moduleName, moduleVersion);

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void postArtifact() throws IOException{
        Artifact artifact1 = DataModelFactory.createArtifact("com.axway.test", "artifact1", "1.0.0-SNAPSHOT", "win32",  "jar", "");

        stubFor(post(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE))
                .withRequestBody(equalTo(JsonUtils.serialize(artifact1)))
                .willReturn(aResponse()
                        .withStatus(Status.CREATED.getStatusCode())));

        Exception exception = null;

        try{
            client.postArtifact(artifact1, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void postWrongArtifact() throws IOException{
        Artifact artifact1 = DataModelFactory.createArtifact("com.axway.test", "artifact1", "1.0.0-SNAPSHOT", "win32",  "jar", "");

        stubFor(post(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE))
                .withRequestBody(equalTo(JsonUtils.serialize(artifact1)))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_ACCEPTABLE.getStatusCode())));

        Exception exception = null;

        try{
            client.postArtifact(artifact1, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void deleteArtifact(){
        String gavc = "test:test:test:test:test:test";

        stubFor(delete(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc))
                .willReturn(aResponse()
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;

        try{
            client.deleteArtifact(gavc, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void deleteArtifactNotFound(){
        String gavc = "test:test:test:test:test:test";

        stubFor(delete(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.deleteArtifact(gavc, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getArtifact() throws IOException{
        String gavc = "com.axway.test:artifact1:1.0.0-SNAPSHOT:win32:jar";
        Artifact artifact1 = DataModelFactory.createArtifact("com.axway.test", "artifact1", "1.0.0-SNAPSHOT", "win32",  "jar", "");

        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(artifact1))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        Artifact artifact2 = null;

        try{
            artifact2 = client.getArtifact(gavc);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(artifact2);
        assertEquals(artifact1, artifact2);
    }

    @Test
    public void getArtifactNotFound(){
        String gavc = "com.axway.test:artifact1:1.0.0-SNAPSHOT:win32:jar";

        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getArtifact(gavc);

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getArtifacts() throws IOException{
        Artifact artifact1 = DataModelFactory.createArtifact("org.axway.test", "artifact1", "1.0.0-SNAPSHOT", "win32",  "jar", "");

        List<Artifact> thirdpartyList = new ArrayList<Artifact>();
        thirdpartyList.add(artifact1);

        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + ServerAPI.GET_ALL + "?hasLicense=false"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(thirdpartyList))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        List<Artifact> artifacts = null;

        try{
            artifacts = client.getArtifacts(false);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(artifacts);
        assertEquals(1, artifacts.size());
    }

    @Test
    public void getArtifactsNotFound() throws IOException{
        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + ServerAPI.GET_ALL + "?hasLicense=false"))
                .willReturn(aResponse().withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getArtifacts(false);

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void licenseAddToArtifact(){
        String gavc = "com.axway.test:artifact1:1.0.0-SNAPSHOT:win32:jar";
        String licenseId = "licenseId";

        stubFor(post(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.GET_LICENSES + "?" + ServerAPI.LICENSE_ID_PARAM + "=" + licenseId))
                .willReturn(aResponse()
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;

        try{
            client.addLicense(gavc, licenseId, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void licenseAddToArtifactNotFound(){
        String gavc = "com.axway.test:artifact1:1.0.0-SNAPSHOT:win32:jar";
        String licenseId = "licenseId";

        stubFor(post(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.GET_LICENSES + "?" + ServerAPI.LICENSE_ID_PARAM + "=" + licenseId))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.addLicense(gavc, licenseId, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void postDoNotUseArtifact() throws IOException{
        Artifact artifact1 = DataModelFactory.createArtifact("com.axway.test", "artifact1", "1.0.0-SNAPSHOT", "win32",  "jar", "");

        stubFor(post(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact1.getGavc() + ServerAPI.SET_DO_NOT_USE + "?" + ServerAPI.DO_NOT_USE + "=true"))
                .willReturn(aResponse()
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;

        try{
            client.postDoNotUseArtifact(artifact1.getGavc(), true, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void postDoNotUseArtifactNotFound() throws IOException{
        Artifact artifact1 = DataModelFactory.createArtifact("com.axway.test", "artifact1", "1.0.0-SNAPSHOT", "win32",  "jar", "");

        stubFor(post(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + artifact1.getGavc() + ServerAPI.SET_DO_NOT_USE + "?" + ServerAPI.DO_NOT_USE + "=true"))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.postDoNotUseArtifact(artifact1.getGavc(), true, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }
    @Test
    public void getArtifactVersions() throws IOException{
        final String gavc = "com.my.company:test:1233:jar";
        final List<String> versions = new ArrayList<String>();
        versions.add("1.0.0");

        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.GET_VERSIONS))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(versions))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        List<String> receivedVersions = null;

        try{
            receivedVersions = client.getArtifactVersions(gavc);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(receivedVersions);
        assertEquals(1, receivedVersions.size());
        assertEquals("1.0.0", receivedVersions.get(0));
    }

    @Test
    public void getArtifactVersionsNotFound() throws IOException{
        final String gavc = "com.my.company:test:1233:jar";
        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.GET_VERSIONS))
                .willReturn(aResponse().withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getArtifactVersions(gavc);

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }
    @Test
    public void getArtifactLastVersion() throws IOException{
        final String gavc = "com.my.company:test:1233:jar";

        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.GET_LAST_VERSION))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody("1.0.0")
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        String lastVersion = null;

        try{
            lastVersion = client.getArtifactLastVersion(gavc);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(lastVersion);
        assertEquals("1.0.0", lastVersion);
    }

    @Test
    public void getArtifactLastVersionNotFound() throws IOException{
        final String gavc = "com.my.company:test:1233:jar";
        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.GET_LAST_VERSION))
                .willReturn(aResponse().withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getArtifactLastVersion(gavc);

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getArtifactModuleNotFound() throws IOException{
        final String gavc = "com.my.company:test:1233:jar";
        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.GET_MODULE))
                .willReturn(aResponse().withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getArtifactModule(gavc);

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getArtifactModuleButWithNoModule() throws IOException{
        final String gavc = "com.my.company:test:1233:jar";
        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.GET_MODULE))
                .willReturn(aResponse().withStatus(Status.NO_CONTENT.getStatusCode())));

        Exception exception = null;

        try{
            final Module module = client.getArtifactModule(gavc);
            assertNull(module);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void getArtifactModule() throws IOException{
        final Module module = DataModelFactory.createModule("name","1.2.3");
        final String gavc = "com.my.company:test:1233:jar";
        stubFor(get(urlEqualTo("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.GET_MODULE))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(module))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;

        try{
            final Module gotModule = client.getArtifactModule(gavc);
            assertNotNull(gotModule);
            assertEquals(module, gotModule);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }


    @Test
    public void postLicense() throws IOException{
        License license = DataModelFactory.createLicense("test", "longName", "comments", "regexp", "url");

        stubFor(post(urlEqualTo("/" + ServerAPI.LICENSE_RESOURCE))
                .withRequestBody(equalTo(JsonUtils.serialize(license)))
                .willReturn(aResponse()
                        .withStatus(Status.CREATED.getStatusCode())));

        Exception exception = null;

        try{
            client.postLicense(license, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void postWrongLicense() throws IOException{
        License license = DataModelFactory.createLicense("test", "longName", "comments", "regexp", "url");

        stubFor(post(urlEqualTo("/" + ServerAPI.LICENSE_RESOURCE))
                .withRequestBody(equalTo(JsonUtils.serialize(license)))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_ACCEPTABLE.getStatusCode())));

        Exception exception = null;

        try{
            client.postLicense(license, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void deleteLicense(){
        License license = DataModelFactory.createLicense("test", "longName", "comments", "regexp", "url");

        stubFor(delete(urlEqualTo("/" + ServerAPI.LICENSE_RESOURCE + "/" + license.getName()))
                .willReturn(aResponse()
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;

        try{
            client.deleteLicense(license.getName(), "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void deleteLicenseNotFound(){
        License license = DataModelFactory.createLicense("test", "longName", "comments", "regexp", "url");

        stubFor(delete(urlEqualTo("/" + ServerAPI.LICENSE_RESOURCE + "/" + license.getName()))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.deleteLicense(license.getName(), "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getLicense() throws IOException {
        License license1 = DataModelFactory.createLicense("test", "longName", "comments", "regexp", "url");

        stubFor(get(urlEqualTo("/" + ServerAPI.LICENSE_RESOURCE + "/" + license1.getName()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(license1))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        License license2 = null;

        try{
            license2 = client.getLicense(license1.getName());

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(license2);
        assertEquals(license1, license2);
    }

    @Test
    public void getLicenseNotFound() throws IOException {
        License license1 = DataModelFactory.createLicense("test", "longName", "comments", "regexp", "url");

        stubFor(get(urlEqualTo("/" + ServerAPI.LICENSE_RESOURCE + "/" + license1.getName()))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getLicense(license1.getName());

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void approveLicense() throws IOException {
        License license1 = DataModelFactory.createLicense("test", "longName", "comments", "regexp", "url");

        stubFor(post(urlEqualTo("/" + ServerAPI.LICENSE_RESOURCE + "/" + license1.getName() + "?" + ServerAPI.APPROVED_PARAM + "=true"))
                .willReturn(aResponse()
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;

        try{
            client.approveLicense(license1.getName(), true, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void approveLicenseNotFound() throws IOException {
        License license1 = DataModelFactory.createLicense("test", "longName", "comments", "regexp", "url");

        stubFor(post(urlEqualTo("/" + ServerAPI.LICENSE_RESOURCE + "/" + license1.getName() + "?" + ServerAPI.APPROVED_PARAM + "=true"))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.approveLicense(license1.getName(), true, "user", "password");

        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getModuleAncestors() throws IOException {
        final List<Dependency> list = new ArrayList<Dependency>();
        final Dependency dependency = DataModelFactory.createDependency(DataModelFactory.createArtifact("", "ancestor", "", "", "", ""), Scope.COMPILE);
        list.add(dependency);

        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(get(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_ANCESTORS + "?.*"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(list))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        List<Dependency> list2 = null;

        try{
            list2 = client.getModuleAncestors(moduleName, moduleVersion);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(list2);
    }

    @Test
    public void getModuleAncestorsNotFound() throws IOException {
        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(get(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_ANCESTORS + "?.*"))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getModuleAncestors(moduleName, moduleVersion);
        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getModuleDependencies() throws IOException {
        final List<Dependency> list = new ArrayList<Dependency>();
        final Dependency dependency = DataModelFactory.createDependency(DataModelFactory.createArtifact("", "target", "", "", "", ""), Scope.COMPILE);
        list.add(dependency);

        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(get(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_DEPENDENCIES + "?.*"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(list))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        List<Dependency> list2 = null;

        try{
            list2 = client.getModuleDependencies(moduleName, moduleVersion, false, true, true);

        }catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
        assertNotNull(list2);
    }

    @Test
    public void getModuleDependenciesNotFound() throws IOException {
        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(get(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_DEPENDENCIES + "?.*"))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getModuleDependencies(moduleName, moduleVersion, false, true, true);
        }catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void getModuleOrganization() throws IOException {
        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";
        final Organization organization = DataModelFactory.createOrganization("organizationTest");

        stubFor(get(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_ORGANIZATION))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(organization))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        Organization receivedOrganization = null;

        try{
            receivedOrganization = client.getModuleOrganization(moduleName,moduleVersion);
        }catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        assertNotNull(receivedOrganization);
        assertEquals(organization, receivedOrganization);
    }

    @Test
    public void getModuleOrganizationNotFound() throws IOException {
        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(get(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_ORGANIZATION))
                .willReturn(aResponse()
                        .withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try{
            client.getModuleOrganization(moduleName, moduleVersion);
        }catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void postBuildInfo() throws IOException {
        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";
        final Map<String, String> buildInfo = new HashMap<String, String>();
        buildInfo.put("test", "test.test");

        stubFor(post(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_BUILD_INFO))
                .willReturn(aResponse().withStatus(Status.CREATED.getStatusCode())));

        Exception exception = null;

        try {
            client.postBuildInfo(moduleName, moduleVersion, buildInfo, "user", "pwd");
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void postBuildInfoNotFound() throws IOException {
        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";
        final Map<String, String> buildInfo = new HashMap<String, String>();
        buildInfo.put("test", "test.test");

        stubFor(post(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_BUILD_INFO))
                .willReturn(aResponse().withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try {
            client.postBuildInfo(moduleName, moduleVersion, buildInfo, null, null);
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void getBuildInfo() throws IOException {
        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";
        final Map<String, String> buildInfo = new HashMap<String, String>();
        buildInfo.put("test", "test.test");

        stubFor(get(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_BUILD_INFO))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(buildInfo))
                        .withStatus(Status.OK.getStatusCode())));

        Map<String, String> gotBuildInfo= null;
        Exception exception = null;

        try {
            gotBuildInfo = client.getBuildInfo(moduleName, moduleVersion);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        assertNotNull(gotBuildInfo);
    }

    @Test
    public void getBuildInfoNotFound() throws IOException {
        final String moduleName = "module";
        final String moduleVersion = "1.0.0-SNAPSHOT";

        stubFor(get(urlMatching("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + "/" + moduleVersion + ServerAPI.GET_BUILD_INFO))
                .willReturn(aResponse().withStatus(Status.NOT_FOUND.getStatusCode())));

        Exception exception = null;

        try {
            client.getBuildInfo(moduleName, moduleVersion);
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void getProductModuleNames() throws IOException {
        final String product = "product";
        final List<String> names = Lists.newArrayList("module1", "module2", "module3", "module4");

        stubFor(get(urlMatching("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product + ServerAPI.GET_MODULES))
                .willReturn(aResponse().withStatus(Status.OK.getStatusCode())
                        .withBody(JsonUtils.serialize(names))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        List<String> gotNames = null;
        Exception exception = null;

        try {
            gotNames = client.getProductModuleNames(product);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        assertNotNull(gotNames);
        assertEquals(names.size(), gotNames.size());
    }

    @Test
    public void getProductModuleNamesNotFound() throws IOException {

        stubFor(get(urlMatching("/" + ServerAPI.PRODUCT_RESOURCE + "/doesNotExist" + ServerAPI.GET_MODULES))
                .willReturn(aResponse().withStatus(Status.NOT_FOUND.getStatusCode())));

        List<String> gotNames = null;
        Exception exception = null;

        try {
            gotNames = client.getProductModuleNames("doesNotExist");
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void testGetArtifactWith_DO_NOT_USE_Artifacts() throws IOException {
        String gavc = "dummy";
        List<String> names = new ArrayList<String>();
        Boolean mockedReply = Boolean.TRUE;

        stubFor(get(urlMatching("/" + ServerAPI.ARTIFACT_RESOURCE + "/" + gavc + ServerAPI.SET_DO_NOT_USE))
                .willReturn(aResponse().withStatus(Status.OK.getStatusCode())
                        .withBody(JsonUtils.serialize(mockedReply))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        Exception exception = null;

        try {
            Boolean reply = client.isMarkedAsDoNotUse(gavc);
            assertEquals(reply.booleanValue(), mockedReply);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void testGetArtifactThrowsException() throws GrapesCommunicationException, AuthenticationException {
        exc.expect(GrapesCommunicationException.class);
        exc.expectMessage("Failed to check do not use artifact");
        client.isMarkedAsDoNotUse("toto");
    }
    
    @Test
    public void isProductExistTest() throws IOException {
        final String product = "product";

        stubFor(get(urlMatching("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product))
                .willReturn(aResponse().withStatus(Status.NOT_FOUND.getStatusCode())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        boolean isProductExist = true;
        Exception exception = null;

        try {
        	isProductExist = client.isProductExist(product);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        assertFalse(isProductExist);
    }
    
    @Test
    public void getProductDeliveriesTest() throws IOException {
        final String product = "product";
        
        List<Delivery> deliverySet = new ArrayList<Delivery>();
        
        Delivery delivery1 = new Delivery();
        delivery1.setCommercialName("commercialName1");
        delivery1.setCommercialVersion("1.0.0");
        
        Delivery delivery2 = new Delivery();
        delivery2.setCommercialName("commercialName1");
        delivery2.setCommercialVersion("1.0.0");

        deliverySet.add(delivery1);
        deliverySet.add(delivery2);

        stubFor(get(urlMatching("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product + ServerAPI.GET_DELIVERIES))
                .willReturn(aResponse().withStatus(Status.OK.getStatusCode())
                        .withBody(JsonUtils.serialize(deliverySet))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        List<Delivery> deliveries = null;
        Exception exception = null;

        try {
        	deliveries = client.getProductDeliveries(product);
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        assertNotNull(deliveries.get(0));
        assertNotNull(deliveries.get(1));
        assertEquals(deliverySet.get(0).getCommercialName(), deliveries.get(0).getCommercialName());
        assertEquals(deliverySet.get(1).getCommercialName(), deliveries.get(1).getCommercialName());
        assertEquals(deliverySet.get(0).getCommercialVersion(), deliveries.get(0).getCommercialVersion());
        assertEquals(deliverySet.get(1).getCommercialVersion(), deliveries.get(1).getCommercialVersion());
    }

}
