package org.axway.grapes.server.webapp.resources;


import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.graphs.AbstractGraph;
import org.axway.grapes.server.core.graphs.ModuleGraph;
import org.axway.grapes.server.core.graphs.TreeNode;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SequoiaTest extends ResourceTest {

    private RepositoryHandler repositoryHandler;

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = mock(RepositoryHandler.class);
        Sequoia resource = new Sequoia(repositoryHandler, mock(GrapesServerConfig.class));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);
    }

    @Test
    public void getModuleGraph() throws UnknownHostException {
        when(repositoryHandler.getModule(anyString())).thenReturn(new DbModule());
        WebResource resource = client().resource("/" + ServerAPI.SEQUOIA_RESOURCE + "/graph/module/1");
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        AbstractGraph graph = response.getEntity(ModuleGraph.class);
        assertNotNull(graph);
    }

    @Test
    public void getTreeGraph() throws UnknownHostException {
        when(repositoryHandler.getModule(anyString())).thenReturn(new DbModule());
        WebResource resource = client().resource("/" + ServerAPI.SEQUOIA_RESOURCE + "/tree/module/1");
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        TreeNode tree = response.getEntity(TreeNode.class);
        assertNotNull(tree);
    }

    @Test
    public void notFound() throws UnknownHostException {
        when(repositoryHandler.getModule(anyString())).thenReturn(null);
        WebResource resource = client().resource("/" + ServerAPI.SEQUOIA_RESOURCE + "/graph/module/1");
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        resource = client().resource("/" + ServerAPI.SEQUOIA_RESOURCE + "/tree/module/1");
        response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }
}
