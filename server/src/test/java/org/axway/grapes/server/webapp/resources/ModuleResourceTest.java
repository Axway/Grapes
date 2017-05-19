package org.axway.grapes.server.webapp.resources;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.apache.log4j.Logger;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.MediaType;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ModuleResourceTest extends ResourceTest {

    private RepositoryHandler repositoryHandler;

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final GrapesServerConfig config = mock(GrapesServerConfig.class);

        final ModuleResource resource = new ModuleResource(repositoryHandler, config);
        addProvider(new BasicAuthProvider<DbCredential>(new GrapesAuthenticator(repositoryHandler), "test auth"));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);
    }

    @Test
    public void getDocumentation() {
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE);
        final ClientResponse response = resource.type(MediaType.TEXT_HTML).get(ClientResponse.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void postModule() throws UnknownHostException, AuthenticationException {
        final Module module = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");
        final Artifact artifact = DataModelFactory.createArtifact(GrapesTestUtils.CORPORATE_GROUPID_4TEST, "artifactId", "version", "classifier", "type", "extension");
        module.addArtifact(artifact);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE);
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, module);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());

        final ArgumentCaptor<DbModule> captor = ArgumentCaptor.forClass(DbModule.class);
        verify(repositoryHandler, times(1)).store(captor.capture());
        verify(repositoryHandler, times(1)).store((DbArtifact) anyObject());

        assertEquals(GrapesTestUtils.ORGANIZATION_NAME_4TEST, captor.getValue().getOrganization());
    }

    @Test
    public void postMalFormedModule() throws UnknownHostException, AuthenticationException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createModule(null, null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void postModuleWithWrongCredentials() throws UnknownHostException, AuthenticationException {
        final Module module = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extension");
        module.addArtifact(artifact);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.WRONG_USER_4TEST, GrapesTestUtils.WRONG_PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE);
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, module);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void getAllModuleNames() throws UnknownHostException {
        when(repositoryHandler.getModuleNames((FiltersHolder) anyObject())).thenReturn(Lists.newArrayList("module1"));

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + ServerAPI.GET_NAMES);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<String> results = response.getEntity(new GenericType<List<String>>() {
        });
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("module1", results.get(0));
    }

    @Test
    public void getModuleVersions() throws UnknownHostException {
        final String moduleName = "moduleTest";
        final String moduleVersion = "1.2.3-4";
        when(repositoryHandler.getModuleVersions(eq(moduleName), (FiltersHolder) anyObject())).thenReturn(Lists.newArrayList(moduleVersion));

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + moduleName + ServerAPI.GET_VERSIONS);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<String> results = response.getEntity(new GenericType<List<String>>() {
        });
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(moduleVersion, results.get(0));
    }

    @Test
    public void getModule() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion());
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final Module results = response.getEntity(Module.class);
        assertNotNull(results);
        assertEquals(dbModule.getName(), results.getName());
        assertEquals(dbModule.getVersion(), results.getVersion());
    }

    @Test
    public void getAllModules() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        when(repositoryHandler.getModules((FiltersHolder) anyObject())).thenReturn(Collections.singletonList(dbModule));

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + ServerAPI.GET_ALL);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<Module> results = response.getEntity(new GenericType<List<Module>>() {
        });
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    public void deleteModule() throws AuthenticationException, UnknownHostException {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion());
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        verify(repositoryHandler, times(1)).deleteModule(dbModule.getId());
    }

    @Test
    public void deleteModuleWithWrongCredentials() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.WRONG_USER_4TEST, GrapesTestUtils.WRONG_PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/what/ever");
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void promoteModule() throws AuthenticationException, UnknownHostException {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.PROMOTION);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        verify(repositoryHandler, times(1)).promoteModule((DbModule) any());
    }

    @Test
    public void promoteModuleWithWrongCredentials() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.WRONG_USER_4TEST, GrapesTestUtils.WRONG_PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/what/ever" + ServerAPI.PROMOTION);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void getModuleAncestors() throws UnknownHostException {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        dbModule.setOrganization(GrapesTestUtils.ORGANIZATION_NAME_4TEST);
        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        dbArtifact.setArtifactId("artifact1");
        dbArtifact.setVersion("1.2.3");
        dbModule.addArtifact(dbArtifact);
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);
        when(repositoryHandler.getArtifact(dbArtifact.getGavc())).thenReturn(dbArtifact);

        final DbModule ancestor = new DbModule();
        ancestor.setName("ancestor");
        ancestor.setVersion("1");
        ancestor.addDependency(dbArtifact.getGavc(), Scope.PROVIDED);
        when(repositoryHandler.getAncestors(eq(dbArtifact), (FiltersHolder) anyObject())).thenReturn(Collections.singletonList(ancestor));

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.GET_ANCESTORS);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<Dependency> results = response.getEntity(new GenericType<List<Dependency>>() {
        });
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dbArtifact.getGavc(), results.get(0).getTarget().getGavc());
        assertEquals(Scope.PROVIDED, results.get(0).getScope());
        assertEquals(ancestor.getName(), results.get(0).getSourceName());
        assertEquals(ancestor.getVersion(), results.get(0).getSourceVersion());
    }

    @Test
    public void getModuleDependencies() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        dbArtifact.setArtifactId("artifact1");
        dbArtifact.setVersion("1.2.3");
        dbModule.addDependency(dbArtifact.getGavc(), Scope.COMPILE);
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);
        when(repositoryHandler.getArtifact(dbArtifact.getGavc())).thenReturn(dbArtifact);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.GET_DEPENDENCIES);
        final ClientResponse response = resource.queryParam(ServerAPI.SCOPE_COMPILE_PARAM, "true")
                .queryParam(ServerAPI.SHOW_THIRPARTY_PARAM, "true")
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<Dependency> results = response.getEntity(new GenericType<List<Dependency>>() {
        });
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dbArtifact.getGavc(), results.get(0).getTarget().getGavc());
        assertEquals(Scope.COMPILE, results.get(0).getScope());
        assertEquals(dbModule.getName(), results.get(0).getSourceName());
        assertEquals(dbModule.getVersion(), results.get(0).getSourceVersion());
    }

    @Test
    public void getModuleDependencyReport() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.GET_DEPENDENCIES + ServerAPI.GET_REPORT);
        final ClientResponse response = resource
                .queryParam(ServerAPI.SCOPE_COMPILE_PARAM, "true")
                .queryParam(ServerAPI.SHOW_THIRPARTY_PARAM, "true")
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void getLicenses() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        dbArtifact.setArtifactId("artifact1");
        dbArtifact.setVersion("1.2.3");
        final DbLicense dbLicense = new DbLicense();
        dbLicense.setName("license1");
        dbArtifact.addLicense(dbLicense);
        dbModule.addArtifact(dbArtifact);
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);
        when(repositoryHandler.getArtifact(dbArtifact.getGavc())).thenReturn(dbArtifact);
        when(repositoryHandler.getLicense(dbLicense.getName())).thenReturn(dbLicense);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.GET_LICENSES);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<License> results = response.getEntity(new GenericType<List<License>>() {
        });
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dbLicense.getName(), results.get(0).getName());
    }

    @Test
    public void isPromoted() throws UnknownHostException {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.PROMOTION);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        Boolean results = response.getEntity(Boolean.class);
        assertNotNull(results);
        assertFalse(results);

        dbModule.setPromoted(true);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        results = response.getEntity(Boolean.class);
        assertNotNull(results);
        assertTrue(results);
    }

    @Test
    public void canBePromoted() throws UnknownHostException {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.PROMOTION + ServerAPI.GET_FEASIBLE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        Boolean results = response.getEntity(Boolean.class);
        assertNotNull(results);
        assertTrue(results);
    }

    @Test
    public void getPromotionStatusReport() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.PROMOTION + ServerAPI.GET_REPORT);
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        PromotionDetails results = response.getEntity(PromotionDetails.class);
        assertNotNull(results);
        assertTrue(results.canBePromoted);

    }

    @Test
    public void getPromotionStatusReportForSnapshotVersion() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0-SNAPSHOT");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.PROMOTION + ServerAPI.GET_REPORT);
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        PromotionDetails results = response.getEntity(PromotionDetails.class);
        assertNotNull(results);

        assertFalse(results.canBePromoted);
        assertTrue(results.isSnapshot);

    }


    @Test
    public void getPromotionStatusReport2() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0-SNAPSHOT");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.PROMOTION + ServerAPI.GET_REPORT2);
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        Map<String, Object> results = response.getEntity(HashMap.class);
        assertNotNull(results);

        assertFalse((Boolean) results.get("canBePromoted"));
        assertFalse(((List) results.get("errors")).isEmpty());

    }

    @Test
    public void getPromotionStatusReport2ForSnapshot() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0-SNAPSHOT");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.PROMOTION + ServerAPI.GET_REPORT2);
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        Map<String, Object> results = response.getEntity(HashMap.class);
        assertNotNull(results);

        List<String> errors = (List) results.get("errors");

        assertFalse((Boolean) results.get("canBePromoted"));
        assertFalse(errors.isEmpty());

        assertEquals(errors.get(0), "Version is SNAPSHOT");

    }

    @Test
    public void getPromotionStatusReport2ThirdPartyLicenseError() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.1.0");

        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId(GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST);
        dbArtifact.setArtifactId(GrapesTestUtils.MISSING_LICENSE_ARTIFACTID_4TEST);
        dbArtifact.setVersion(GrapesTestUtils.ARTIFACT_VERSION_4TEST);
        dbArtifact.setClassifier(GrapesTestUtils.ARTIFACT_CLASSIFIER_4TEST);
        dbArtifact.setExtension(GrapesTestUtils.ARTIFACT_EXTENSION_4TEST);
        // Setting empty license list to simulate missing license
        dbArtifact.setLicenses(Collections.<String>emptyList());

        dbModule.addArtifact(dbArtifact);
        dbModule.addDependency(dbArtifact.getGavc(), Scope.COMPILE);

        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);
        // get the module dependency
        when(repositoryHandler.getArtifact(dbModule.getDependencies().get(0).getTarget())).thenReturn(dbArtifact);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.PROMOTION + ServerAPI.GET_REPORT2);
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        Map<String, Object> results = response.getEntity(HashMap.class);
        assertNotNull(results);

        List<String> errors = (List) results.get("errors");

        assertFalse((Boolean) results.get("canBePromoted"));
        assertFalse(errors.isEmpty());
        assertEquals(errors.get(0), GrapesTestUtils.MISSING_LICENSE_MESSAGE_4TEST + GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST + GrapesTestUtils.COLON
                + GrapesTestUtils.MISSING_LICENSE_ARTIFACTID_4TEST + GrapesTestUtils.COLON + GrapesTestUtils.ARTIFACT_VERSION_4TEST + GrapesTestUtils.COLON
                + GrapesTestUtils.ARTIFACT_CLASSIFIER_4TEST + GrapesTestUtils.COLON + GrapesTestUtils.ARTIFACT_EXTENSION_4TEST);
    }

    @Test
    public void getModuleOrganization() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        dbModule.setOrganization(GrapesTestUtils.ORGANIZATION_NAME_4TEST);
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + "/" + ServerAPI.ORGANIZATION_RESOURCE);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final Organization gotOrganization = response.getEntity(Organization.class);
        assertNotNull(gotOrganization);
        assertEquals(GrapesTestUtils.ORGANIZATION_NAME_4TEST, gotOrganization.getName());
    }

    @Test
    public void checkRedirectionOnGetModuleWithoutVersion() {
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/moduleName");
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.SEE_OTHER_303, response.getStatus());
    }

    @Test
    public void getBuildInfo() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        dbModule.getBuildInfo().put("test", "what a test!");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.GET_BUILD_INFO);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final Map<String, String> getBuildInfo = response.getEntity(new GenericType<Map<String, String>>() {
        });
        assertNotNull(getBuildInfo);
        assertEquals(1, getBuildInfo.size());
        assertEquals("what a test!", getBuildInfo.get("test"));
    }

    @Test
    public void getBuildInfoOnModuleThatDoesNotExist() {
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/doesNotExist/doesNotExist" + ServerAPI.GET_BUILD_INFO);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void postBuildInfo() {
        final DbModule dbModule = new DbModule();
        dbModule.setName("moduleTest");
        dbModule.setVersion("1.0.0");
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

        final Map<String, String> buildInfo = new HashMap<String, String>();
        buildInfo.put("test", "what a test!");

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.GET_BUILD_INFO);
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, buildInfo);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());

        final ArgumentCaptor<DbModule> captor = ArgumentCaptor.forClass(DbModule.class);
        verify(repositoryHandler).store(captor.capture());
        final DbModule gotModule = captor.getValue();

        assertEquals(1, gotModule.getBuildInfo().size());
        assertEquals("what a test!", gotModule.getBuildInfo().get("test"));
    }

    @Test
    public void postBuildInfoOnModuleThatDoesNotExist() {
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/doesNotExist/doesNotExist" + ServerAPI.GET_BUILD_INFO);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

}
