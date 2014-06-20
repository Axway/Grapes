package org.axway.grapes.utils.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.api.client.ClientResponse.Status;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.commons.utils.JsonUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.naming.AuthenticationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class GrapesClientTest {

    public static final String PROPERTY_PORT = "server.mock.http.port";
    private static final String DEFAULT_PORT = "8074";

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(Integer.valueOf(System.getProperty(PROPERTY_PORT, DEFAULT_PORT)));

    private static GrapesClient client;
    private static String serverPort;

    @BeforeClass
    public static void startMock() throws IOException, InterruptedException{
        serverPort = System.getProperty(PROPERTY_PORT, DEFAULT_PORT);
        client = new GrapesClient("127.0.0.1", serverPort);
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
    public void getCorporateFilters() throws IOException {
        final List<String> filters = new ArrayList<String>();
        filters.add("com.my.company");

        stubFor(get(urlMatching(ServerAPI.GET_CORPORATE_FILTERS + "?.*"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody(JsonUtils.serialize(filters))
                        .withStatus(Status.OK.getStatusCode())));

        Exception exception = null;
        List<String> receivedFilters = null;

        try{
            receivedFilters = client.getCorporateFilters();
        }catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        assertNotNull(receivedFilters);
        assertEquals(1, receivedFilters.size());
        assertEquals("com.my.company", receivedFilters.get(0));
    }
}
