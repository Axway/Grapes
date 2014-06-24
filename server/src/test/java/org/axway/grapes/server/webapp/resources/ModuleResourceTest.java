package org.axway.grapes.server.webapp.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.axway.grapes.server.webapp.views.PromotionReportView;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;

public class ModuleResourceTest extends ResourceTest {

    private DbOrganization dbOrganization;
    private DbModule dbModule, dbSubmodule;
    private DbArtifact artifact, artifact2, artifact3, artifact4;
    private final ArgumentCaptor<FiltersHolder> filters = ArgumentCaptor.forClass(FiltersHolder.class);

    private RepositoryHandler repositoryHandler;

    @Override
    protected void setUpResources() throws Exception {
        dbOrganization = new DbOrganization();
        dbOrganization.setName("corp");
        dbOrganization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);

        dbModule = new DbModule();
        dbModule.setName("root");
        dbModule.setVersion("1.0.0-SNAPSHOT");
        dbModule.setOrganization(dbOrganization.getName());
        
        artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1.0.0-SNAPSHOT");
        dbModule.addArtifact(artifact);

        artifact2 = new DbArtifact();
        artifact2.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        artifact2.setArtifactId("artifactId2");
        artifact2.setVersion("1.0.0-11");
        dbModule.addDependency(artifact2.getGavc(), Scope.COMPILE);

        dbSubmodule = new DbModule();
        dbSubmodule.setName("sub");
        dbSubmodule.setVersion("1.0.0-SNAPSHOT");
        artifact3 = new DbArtifact();
        artifact3.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        artifact3.setArtifactId("artifactId3");
        artifact3.setVersion("1.0.0-SNAPSHOT");
        dbSubmodule.addArtifact(artifact3);

        artifact4 = new DbArtifact();
        artifact4.setGroupId("groupId4");
        artifact4.setArtifactId("artifactId4");
        artifact4.setVersion("1.0.0-11");
        dbSubmodule.addDependency(artifact4.getGavc(), Scope.COMPILE);
        dbModule.addSubmodule(dbSubmodule);

        repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getOrganization(dbOrganization.getName())).thenReturn(dbOrganization);
        when(repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getArtifact(artifact2.getGavc())).thenReturn(artifact2);
        when(repositoryHandler.getArtifact(artifact3.getGavc())).thenReturn(artifact3);
        when(repositoryHandler.getArtifact(artifact4.getGavc())).thenReturn(artifact4);
        when(repositoryHandler.getModule(dbSubmodule.getId())).thenReturn(dbSubmodule);

        final List<DbModule> modules = new ArrayList<DbModule>();
        modules.add(dbModule);
        when(repositoryHandler.getModules(filters.capture())).thenReturn(modules);

        final RepositoryHandler repoHandler = GrapesTestUtils.getRepoHandlerMock();

        final GrapesServerConfig config =mock(GrapesServerConfig.class);
        when(config.getCorporateGroupIds()).thenReturn(Collections.singletonList(GrapesTestUtils.CORPORATE_GROUPID_4TEST));
        final ModuleResource resource = new ModuleResource(repositoryHandler, config);
        addProvider(new BasicAuthProvider<DbCredential>(new GrapesAuthenticator(repoHandler), "test auth"));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);
    }

    @Test
    public void getDocumentation(){
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE);
        final ClientResponse response = resource.type(MediaType.TEXT_HTML).get(ClientResponse.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void postModule() throws UnknownHostException, AuthenticationException {
        final Module module = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extension");
        module.addArtifact(artifact);

        final Module submodule = DataModelFactory.createModule("module2", "1.0.0-SNAPSHOT");
        final Artifact artifact2 = DataModelFactory.createArtifact("groupId2", "artifactId2", "version", "classifier", "type", "extension");
        submodule.addArtifact(artifact2);
        final Artifact artifact3 = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extension");
        final Dependency dependency = DataModelFactory.createDependency(artifact3, Scope.IMPORT);
        submodule.addDependency(dependency);
        module.addSubmodule(submodule);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE);
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, module);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());
        verify(repositoryHandler, times(1)).store((DbModule) anyObject());
        verify(repositoryHandler, times(3)).store((DbArtifact) anyObject());
    }

    @Test
    public void postMalFormedModule() throws UnknownHostException, AuthenticationException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createModule(null, null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createModule("module", null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createModule(null, "1.0.0"));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createModule("", "1.0.0"));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createModule("module", ""));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        final Module moduleWitWrongArtifact = DataModelFactory.createModule("module", "1.0.0");
        moduleWitWrongArtifact.addArtifact(DataModelFactory.createArtifact(null, null, null, null, null, null));
        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, moduleWitWrongArtifact);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        final Module moduleWitWrongDependency = DataModelFactory.createModule("module", "1.0.0");
        final Artifact artifact = DataModelFactory.createArtifact(null, null, null, null, null, null);
        moduleWitWrongDependency.addDependency(DataModelFactory.createDependency(artifact, Scope.COMPILE));
        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, moduleWitWrongArtifact);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void getAllModuleNames() throws UnknownHostException{
        final List<String> names = new ArrayList<String>();
        names.add("module1");
        when(repositoryHandler.getModuleNames((FiltersHolder) anyObject())).thenReturn(names);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + ServerAPI.GET_NAMES);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<String> results = response.getEntity(new GenericType<List<String>>(){});
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("module1", results.get(0));
    }

    @Test
    public void getModuleVersions() throws UnknownHostException{
        final List<String> versions = new ArrayList<String>();
        versions.add("1.0.0-6");
        when(repositoryHandler.getModuleVersions(anyString(), (FiltersHolder) anyObject())).thenReturn(versions);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/module1" + ServerAPI.GET_VERSIONS);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<String> results = response.getEntity(new GenericType<List<String>>(){});
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("1.0.0-6", results.get(0));
    }

    @Test
    public void getModule(){
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion());
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final Module results = response.getEntity(Module.class);
        assertNotNull(results);
        assertEquals(dbModule.getName(), results.getName());
        assertEquals(dbModule.getVersion(), results.getVersion());
        assertEquals(1, results.getArtifacts().size());
        assertEquals(artifact.getGavc(), results.getArtifacts().iterator().next().getGavc());
        assertEquals(1, results.getDependencies().size());
        Dependency dependency = results.getDependencies().iterator().next();
        assertEquals(artifact2.getGavc(), dependency.getTarget().getGavc());
        assertEquals(dependency.getScope(), dependency.getScope());
        assertEquals(1, results.getSubmodules().size());
        final Module submodule = results.getSubmodules().iterator().next();
        assertEquals(dbSubmodule.getName(), submodule.getName());
        assertEquals(dbSubmodule.getVersion(), submodule.getVersion());
        assertEquals(1, submodule.getArtifacts().size());
        assertEquals(artifact3.getGavc(), submodule.getArtifacts().iterator().next().getGavc());
        assertEquals(1, submodule.getDependencies().size());
        dependency = submodule.getDependencies().iterator().next();
        assertEquals(artifact4.getGavc(), dependency.getTarget().getGavc());
        assertEquals(Scope.COMPILE, dependency.getScope());
        assertEquals(0, submodule.getSubmodules().size());
    }

    @Test
    public void getAllModule(){
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + ServerAPI.GET_ALL);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<Module> results = response.getEntity(new GenericType<List<Module>>(){});
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    public void getAllPromotedModules(){
        dbModule.setPromoted(true);
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + ServerAPI.GET_ALL);
        final ClientResponse response = resource.queryParam(ServerAPI.PROMOTED_PARAM, "true")
                    .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<Module> results = response.getEntity(new GenericType<List<Module>>(){});
        assertNotNull(results);
        assertEquals(1, results.size());
        
        // check that modules are promoted
        for (final Module module : results)
        {
            assertEquals(true, module.isPromoted());
        }
    }
    
    @Test
    public void deleteModule() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion());
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        verify(repositoryHandler, times(1)).deleteModule(dbModule.getId());
        verify(repositoryHandler, times(2)).deleteArtifact(anyString());
    }

    @Test
    public void promoteModule() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion()+ ServerAPI.PROMOTION);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        verify(repositoryHandler, times(1)).promoteModule((DbModule)any());
        verify(repositoryHandler, times(2)).store((DbArtifact) anyObject());
    }

    @Test
    public void getModuleAncestors() throws UnknownHostException {
        final DbModule module1 = new DbModule();
        module1.setName("module1");
        module1.setVersion("1");

        module1.addDependency(artifact3.getGavc(), Scope.IMPORT);

        final List<DbModule> list1 = new ArrayList<DbModule>();
        list1.add(dbModule);
        list1.add(module1);

        when(repositoryHandler.getAncestors(eq(artifact3), (FiltersHolder) anyObject())).thenReturn(list1);
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion()+ ServerAPI.GET_ANCESTORS);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<Dependency> results = response.getEntity(new GenericType<List<Dependency>>(){});
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(artifact3.getGavc(), results.get(0).getTarget().getGavc());
        assertEquals(module1.getName(), results.get(0).getSourceName());
        assertEquals(module1.getVersion(), results.get(0).getSourceVersion());
    }

    @Test
    public void getFilteredAncestors() throws UnknownHostException {
        final DbModule module1 = new DbModule();
        module1.setName("module1");
        module1.setVersion("1");
        module1.addDependency(artifact3.getGavc(), Scope.IMPORT);

        final List<DbModule> list1 = new ArrayList<DbModule>();
        list1.add(module1);

        final ArgumentCaptor<FiltersHolder> filters = ArgumentCaptor.forClass(FiltersHolder.class);
        when(repositoryHandler.getAncestors(eq(artifact3), filters.capture())).thenReturn(list1);
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion() + ServerAPI.GET_ANCESTORS);
        final ClientResponse response = resource.queryParam(ServerAPI.PROMOTED_PARAM , "true")
                .queryParam(ServerAPI.SCOPE_COMPILE_PARAM, "false")
                .queryParam(ServerAPI.SCOPE_PROVIDED_PARAM, "false")
                .queryParam(ServerAPI.SCOPE_RUNTIME_PARAM, "true")
                .queryParam(ServerAPI.SCOPE_TEST_PARAM, "true")
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<Dependency> results = response.getEntity(new GenericType<List<Dependency>>(){});
        assertNotNull(results);
    }

    @Test
    public void getModuleDependencies(){
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion()+ ServerAPI.GET_DEPENDENCIES);
        final ClientResponse response = resource.queryParam(ServerAPI.SCOPE_COMPILE_PARAM, "true")
                .queryParam(ServerAPI.SHOW_THIRPARTY_PARAM, "true")
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<Dependency> results = response.getEntity(new GenericType<List<Dependency>>(){});
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    public void getModuleDependencyReport(){
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion()+ ServerAPI.GET_DEPENDENCIES + ServerAPI.GET_REPORT);
        final ClientResponse response = resource
                .queryParam(ServerAPI.SCOPE_COMPILE_PARAM, "true")
                .queryParam(ServerAPI.SHOW_THIRPARTY_PARAM, "true")
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void getLicenses(){
        final DbLicense license1 = new DbLicense();
        license1.setName("license1");
        final DbLicense license2 = new DbLicense();
        license2.setName("license2");
        artifact.addLicense(license1);
        artifact.addLicense(license2);
        final DbLicense license3 = new DbLicense();
        license3.setName("license3");
        artifact3.addLicense(license3);

        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion()+ ServerAPI.GET_LICENSES);
        final ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<License> results = response.getEntity(new GenericType<List<License>>(){});
        assertNotNull(results);
        assertEquals(3, results.size());
    }

    @Test
    public void isPromoted() throws UnknownHostException {
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion()+ ServerAPI.PROMOTION);
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
        final DbModule module1 = new DbModule();
        module1.setPromoted(false);

        when(repositoryHandler.getRootModuleOf(artifact2.getGavc())).thenReturn(module1);
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion()+ ServerAPI.PROMOTION + ServerAPI.GET_FEASIBLE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        Boolean results = response.getEntity(Boolean.class);
        assertNotNull(results);
        assertFalse(results);

        module1.setPromoted(true);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        results = response.getEntity(Boolean.class);
        assertNotNull(results);
        assertTrue(results);
    }

    @Test
    public void getPromotionStatusReportWhileDependenciesToPromote() throws UnknownHostException {
        final DbModule module1 = new DbModule();
        module1.setName("module1");
        module1.setPromoted(false);

        when(repositoryHandler.getRootModuleOf(artifact2.getGavc())).thenReturn(module1);
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion()+ ServerAPI.PROMOTION + ServerAPI.GET_REPORT);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        PromotionReportView results = response.getEntity(PromotionReportView.class);
        assertNotNull(results);
        assertEquals(1, results.getUnPromotedDependencies().size());
        assertEquals(module1.getName(), results.getUnPromotedDependencies().get(0).getName());

        module1.setPromoted(true);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        results = response.getEntity(PromotionReportView.class);
        assertNotNull(results);
        assertEquals(0, results.getUnPromotedDependencies().size());
        assertTrue(results.canBePromoted());
    }

    @Test
    public void getPromotionStatusReportWhileDependenciesToNotUse() throws UnknownHostException {
        artifact4.setDoNotUse(true);
        final WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/" + dbModule.getName() + "/" + dbModule.getVersion()+ ServerAPI.PROMOTION + ServerAPI.GET_REPORT);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        PromotionReportView results = response.getEntity(PromotionReportView.class);
        assertNotNull(results);
        assertFalse(results.canBePromoted());
        assertEquals(1, results.getDoNotUseArtifacts().size());
        assertEquals(artifact4.getGavc(), results.getDoNotUseArtifacts().get(0).getGavc());

        artifact4.setDoNotUse(false);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        results = response.getEntity(PromotionReportView.class);
        assertNotNull(results);
        assertEquals(0, results.getUnPromotedDependencies().size());
        assertTrue(results.canBePromoted());
    }

    @Test
    public void checkAuthenticationOnPostAndDeleteMethods(){
        WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE);
        ClientResponse response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/version");
        response = resource.delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/version" + ServerAPI.PROMOTION);
        response = resource.post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void notFound(){
        WebResource resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0");
        ClientResponse response = resource.get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/versions");
        response = resource.get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0");
        response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0" + ServerAPI.PROMOTION);
        response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0" + ServerAPI.PROMOTION);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0" + ServerAPI.PROMOTION + ServerAPI.GET_FEASIBLE);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0" + ServerAPI.PROMOTION + ServerAPI.GET_REPORT);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0" + ServerAPI.GET_ANCESTORS);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0" + ServerAPI.GET_DEPENDENCIES);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0" + ServerAPI.GET_DEPENDENCIES + ServerAPI.GET_REPORT);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.MODULE_RESOURCE + "/name/1.0.0" + ServerAPI.GET_LICENSES);
        response = resource.queryParam(ServerAPI.PROMOTED_PARAM, "true").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }



}
