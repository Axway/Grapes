package org.axway.grapes.server.webapp.resources;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbSearch;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchResourceTest extends ResourceTest {

    private RepositoryHandler repositoryHandler;

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final GrapesServerConfig config = mock(GrapesServerConfig.class);

        final SearchResource resource = new SearchResource(repositoryHandler, config);
        addProvider(new BasicAuthProvider<DbCredential>(new GrapesAuthenticator(repositoryHandler), "test auth"));
        addResource(resource);
    }

    @Test
    public void getSearchResult() throws Exception {

        List<String> moduleIds  = new ArrayList<>();
        moduleIds.add("testSearch_id_1");
        moduleIds.add("testSearch_id_2");
        List<String> artifactIds  = new ArrayList<>();
        artifactIds.add("testSearch_artifact_id_1");
        artifactIds.add("testSearch_artifact_id_2");

        DbSearch search = new DbSearch();
        search.setModules(moduleIds);
        search.setArtifacts(artifactIds);

        when(repositoryHandler.getSearchResult(eq("testSearch"), (FiltersHolder) anyObject())).thenReturn(search);

        final WebResource resource = client().resource("/" + ServerAPI.SEARCH_RESOURCE + "/testSearch");
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final String results = response.getEntity(new GenericType<String>() {
        });
        assertEquals("{\"modules\":[\"testSearch_id_1\",\"testSearch_id_2\"],\"artifacts\":[\"testSearch_artifact_id_1\",\"testSearch_artifact_id_2\"]}", results);
    }

    @Test
    public void getNullSearchResult() {
        DbSearch search = new DbSearch();
        when(repositoryHandler.getSearchResult(eq("testSearch"), (FiltersHolder) anyObject())).thenReturn(search);

        final WebResource resource = client().resource("/" + ServerAPI.SEARCH_RESOURCE + "/testSearch");
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        final String results = response.getEntity(new GenericType<String>() {
        });

        assertEquals("{\"modules\":null,\"artifacts\":null}", results);
    }

    @Test
    public void getModulesSearchResult() {
        DbSearch search = new DbSearch();

        List<String> moduleIds  = new ArrayList<>();
        moduleIds.add("testSearch_id_1");
        moduleIds.add("testSearch_id_2");

        search.setModules(moduleIds);

        when(repositoryHandler.getSearchResult(eq("testSearch"), (FiltersHolder) anyObject())).thenReturn(search);

        final WebResource resource = client().resource("/" + ServerAPI.SEARCH_RESOURCE + "/testSearch");
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        final String results = response.getEntity(new GenericType<String>() {
        });

        assertEquals("{\"modules\":[\"testSearch_id_1\",\"testSearch_id_2\"],\"artifacts\":null}", results);
    }

    @Test
    public void getArtifactsSearchResult() {
        DbSearch search = new DbSearch();

        List<String> artifactIds  = new ArrayList<>();
        artifactIds.add("testSearch_artifact_id_1");
        artifactIds.add("testSearch_artifact_id_2");

        search.setArtifacts(artifactIds);

        when(repositoryHandler.getSearchResult(eq("testSearch"), (FiltersHolder) anyObject())).thenReturn(search);

        final WebResource resource = client().resource("/" + ServerAPI.SEARCH_RESOURCE + "/testSearch");
        final ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        final String results = response.getEntity(new GenericType<String>() {
        });

        assertEquals("{\"modules\":null,\"artifacts\":[\"testSearch_artifact_id_1\",\"testSearch_artifact_id_2\"]}", results);
    }
}