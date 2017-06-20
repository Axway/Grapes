package org.axway.grapes.server.webapp.resources;


import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Organization;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.MediaType;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrganizationResourceTest extends ResourceTest {


    private RepositoryHandler repositoryHandler;

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = mock(RepositoryHandler.class);

        final RepositoryHandler repoHandler = GrapesTestUtils.getRepoHandlerMock();
        final OrganizationResource resource = new OrganizationResource(repositoryHandler, mock(GrapesServerConfig.class));
        addProvider(new BasicAuthProvider<DbCredential>(new GrapesAuthenticator(repoHandler), "test auth"));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);

    }
    @Test
    public void getDocumentation(){
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE);
        ClientResponse response = resource.type(MediaType.TEXT_HTML).get(ClientResponse.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void postOrganization(){
        Organization organization = DataModelFactory.createOrganization("name");
        organization.getCorporateGroupIdPrefixes().add("org.test");

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, organization);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());
    }

    @Test
    public void postMalFormedArtifact() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createOrganization(null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void getAllOrganizationNames(){
        final List<String> names = new ArrayList<String>();
        names.add("organization1");
        when(repositoryHandler.getOrganizationNames()).thenReturn(names);

        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + ServerAPI.GET_NAMES);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        List<?> organization = (List<?>)response.getEntity(List.class);
        assertNotNull(organization);
        assertEquals(1, organization.size());
        assertEquals("organization1", organization.get(0));
    }

    @Test
    public void getAnOrganization(){
        final DbOrganization dbOrganization= new DbOrganization();
        dbOrganization.setName("organization1");
        dbOrganization.getCorporateGroupIdPrefixes().add("org.test");
        when(repositoryHandler.getOrganization(dbOrganization.getName())).thenReturn(dbOrganization);

        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/" + dbOrganization.getName());
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final Organization organization = response.getEntity(Organization.class);
        assertNotNull(organization);
        assertEquals(dbOrganization.getName(), organization.getName());
        assertEquals(1, organization.getCorporateGroupIdPrefixes().size());
        assertEquals(dbOrganization.getCorporateGroupIdPrefixes().get(0), organization.getCorporateGroupIdPrefixes().get(0));
    }

    @Test
    public void deleteAnOrganization(){
        final DbOrganization dbOrganization= new DbOrganization();
        dbOrganization.setName("organization1");
        dbOrganization.getCorporateGroupIdPrefixes().add("org.test");
        when(repositoryHandler.getOrganization(dbOrganization.getName())).thenReturn(dbOrganization);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/" + dbOrganization.getName());
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(repositoryHandler, times(1)).deleteOrganization(captor.capture());
        assertEquals(dbOrganization.getName(), captor.getValue());
    }

    @Test
    public void getCorporateGroupIds(){
        final DbOrganization dbOrganization= new DbOrganization();
        dbOrganization.setName("organization1");
        dbOrganization.getCorporateGroupIdPrefixes().add("org.test");
        when(repositoryHandler.getOrganization(dbOrganization.getName())).thenReturn(dbOrganization);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/" + dbOrganization.getName() + ServerAPI.GET_CORPORATE_GROUPIDS);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        List<?> corporateGroupIds = (List<?>)response.getEntity(List.class);
        assertNotNull(corporateGroupIds);
        assertEquals(1, corporateGroupIds.size());
        assertEquals("org.test", corporateGroupIds.get(0));

    }

    @Test
    public void addCorporateGroupIds(){
        final DbOrganization dbOrganization= new DbOrganization();
        dbOrganization.setName("organization1");
        dbOrganization.getCorporateGroupIdPrefixes().add("org.test");
        ArgumentCaptor<DbOrganization> captor = ArgumentCaptor.forClass(DbOrganization.class);
        when(repositoryHandler.getOrganization(dbOrganization.getName())).thenReturn(dbOrganization);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/" + dbOrganization.getName() + ServerAPI.GET_CORPORATE_GROUPIDS);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, "com.test");

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());
        verify(repositoryHandler).store(captor.capture());
        assertTrue(captor.getValue().getCorporateGroupIdPrefixes().contains("com.test"));
    }

    @Test
    public void addCorporateGroupIdsButCorporateGroupIdIsMissing(){
        final DbOrganization dbOrganization= new DbOrganization();
        dbOrganization.setName("organization1");
        dbOrganization.getCorporateGroupIdPrefixes().add("org.test");
        when(repositoryHandler.getOrganization(dbOrganization.getName())).thenReturn(dbOrganization);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/" + dbOrganization.getName() + ServerAPI.GET_CORPORATE_GROUPIDS);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void removeCorporateGroupId(){
        final DbOrganization dbOrganization= new DbOrganization();
        dbOrganization.setName("organization1");
        dbOrganization.getCorporateGroupIdPrefixes().add("org.test");
        ArgumentCaptor<DbOrganization> captor = ArgumentCaptor.forClass(DbOrganization.class);
        when(repositoryHandler.getOrganization(dbOrganization.getName())).thenReturn(dbOrganization);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/" + dbOrganization.getName() + ServerAPI.GET_CORPORATE_GROUPIDS);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class, "org.test");

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        verify(repositoryHandler).store((DbOrganization)captor.capture());
        assertFalse(captor.getValue().getCorporateGroupIdPrefixes().contains("org.test"));
    }

    @Test
    public void removeCorporateGroupIdsButCorporateGroupIdIsMissing(){
        final DbOrganization dbOrganization= new DbOrganization();
        dbOrganization.setName("organization1");
        dbOrganization.getCorporateGroupIdPrefixes().add("org.test");
        when(repositoryHandler.getOrganization(dbOrganization.getName())).thenReturn(dbOrganization);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/" + dbOrganization.getName() + ServerAPI.GET_CORPORATE_GROUPIDS);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void checkAuthenticationOnPostAndDeleteMethods(){
        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());


        resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/organization1");
        response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());


        resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/organization1" + ServerAPI.GET_CORPORATE_GROUPIDS);
        response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());


        resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/organization1" + ServerAPI.GET_CORPORATE_GROUPIDS);
        response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void notFound() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));

        WebResource resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/organization1" );
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());


        resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/notExisting" );
        response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/notExisting" + ServerAPI.GET_CORPORATE_GROUPIDS);
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/notExisting" + ServerAPI.GET_CORPORATE_GROUPIDS);
        response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, "test");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.ORGANIZATION_RESOURCE + "/notExisting" + ServerAPI.GET_CORPORATE_GROUPIDS);
        response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class, "test");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }


}
