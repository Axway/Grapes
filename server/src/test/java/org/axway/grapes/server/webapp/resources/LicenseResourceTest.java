package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.webapp.auth.GrapesAuthProvider;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.MediaType;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class LicenseResourceTest extends ResourceTest {

    private RepositoryHandler repositoryHandler;

	@Override
	protected void setUpResources() throws Exception {
		repositoryHandler = mock(RepositoryHandler.class);
        final GrapesServerConfig dmConfig = GrapesTestUtils.getConfigMock();
		LicenseResource resource = new LicenseResource(repositoryHandler, dmConfig);

		addProvider(new GrapesAuthProvider(dmConfig));
		addProvider(ViewMessageBodyWriter.class);
		addResource(resource);	
	}
    
    @Test
	public void getDocumentation(){		
		WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE);
		ClientResponse response = resource.type(MediaType.TEXT_HTML).get(ClientResponse.class);
        
		assertNotNull(response);
		assertEquals(HttpStatus.OK_200, response.getStatus());
	}
	
	@Test
	public void postLicense() throws AuthenticationException, UnknownHostException{
		client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
		WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE);
		ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createLicense("shorName", "shortLongName", "BlaBla", "sdfsd", "www.somewhere.org"));
		assertNotNull(response);
		assertEquals(HttpStatus.CREATED_201, response.getStatus());
	}
    
    @Test
	public void postMalformedLicenses() throws AuthenticationException, UnknownHostException{
		client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
		WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE);
		ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createLicense("", "longName", "BlaBla", "sdfsd", "www.somewhere.org"));
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

		response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createLicense(null, "shortLongName", "BlaBla", "sdfsd", "www.somewhere.org"));
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
		
		response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createLicense("shorName", "", "BlaBla", "sdfsd", "www.somewhere.org"));
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

		response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createLicense("shorName", null, "BlaBla", "sdfsd", "www.somewhere.org"));
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, DataModelFactory.createLicense("shorName", "longName", "BlaBla", "[", "www.somewhere.org"));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
	}
	
	@Test
	public void getAllLicenseNames() throws UnknownHostException{		
		final List<String> names = new ArrayList<String>();
        names.add("licenseId");
        when(repositoryHandler.getLicenseNames((FiltersHolder) anyObject())).thenReturn(names);

		WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + ServerAPI.GET_NAMES);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertNotNull(response);
		assertEquals(HttpStatus.OK_200, response.getStatus());

        ArrayList<String> licenses = response.getEntity(ArrayList.class);
		assertNotNull(licenses);
		assertEquals(1, licenses.size());
		assertEquals("licenseId", licenses.get(0));
	}


    @Test
    public void getALicense() throws UnknownHostException{
        DbLicense license = new DbLicense();
        license.setName("license");
        license.setLongName("longName");
        license.setComments("bla");
        license.setRegexp("gdfg");
        license.setUrl("www.somewhere.org");
        license.setApproved(true);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);

        WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/" + license.getName());
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        License dbLicense = response.getEntity(License.class);
        assertNotNull(dbLicense);
        assertEquals(license.getName(), dbLicense.getName());
        assertEquals(license.getLongName(), dbLicense.getLongName());
        assertEquals(license.getComments(), dbLicense.getComments());
        assertEquals(license.getRegexp(), dbLicense.getRegexp());
        assertEquals(license.getUrl(), dbLicense.getUrl());
        assertEquals(license.isApproved(), dbLicense.isApproved());
    }

    @Test
    public void getWrongLicense() throws UnknownHostException{
        WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/license");
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void deleteALicense() throws UnknownHostException, AuthenticationException{
        final String licenseName = "licenseId";
        when(repositoryHandler.getLicense(anyString())).thenReturn(new DbLicense());

        List<DbArtifact> artifacts = new ArrayList<DbArtifact>();
        artifacts.add(new DbArtifact());
        ArgumentCaptor<FiltersHolder> filters = ArgumentCaptor.forClass(FiltersHolder.class);
        when(repositoryHandler.getArtifacts(filters.capture())).thenReturn(artifacts);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/" + licenseName);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(repositoryHandler, times(1)).removeLicenseFromArtifact( (DbArtifact)any() , captor.capture());
        assertEquals(licenseName, captor.getValue());
    }

    @Test
    public void approveALicense() throws AuthenticationException, UnknownHostException{
        final DbLicense license = new DbLicense();
        license.setName("licenseId");

        when(repositoryHandler.getLicense(anyString())).thenReturn(license);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/" + license.getName());
        ClientResponse response = resource.queryParam(ServerAPI.APPROVED_PARAM, "true").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        ArgumentCaptor<DbLicense> captedLicense = ArgumentCaptor.forClass(DbLicense.class);
        ArgumentCaptor<Boolean> captedValidation = ArgumentCaptor.forClass(Boolean.class);
        verify(repositoryHandler, times(1)).approveLicense(captedLicense.capture(), captedValidation.capture());
        assertEquals(license.getName(), captedLicense.getValue().getName());
        assertEquals(true, captedValidation.getValue());

        resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/" + license.getName());
        response = resource.queryParam(ServerAPI.APPROVED_PARAM, "false").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        verify(repositoryHandler, times(2)).approveLicense(captedLicense.capture(), captedValidation.capture());
        assertEquals(license.getName(), captedLicense.getValue().getName());
        assertEquals(false, captedValidation.getValue());

    }

    @Test
    public void checkAuthenticationOnPostAndDeleteMethods(){
        WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());


        resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/licenseId");
        response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());


        resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/licenseId");
        response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void notFound() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/licenseName" );
        ClientResponse response = resource.queryParam(ServerAPI.APPROVED_PARAM, "true").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/licenseName");
        response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.LICENSE_RESOURCE + "/licenseName");
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }
    
}
