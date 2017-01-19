package org.axway.grapes.server.webapp.resources;


import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;

import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ServiceHandler;
import org.axway.grapes.server.db.RepositoryHandler;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class RootResourceTest extends ResourceTest {

    private RepositoryHandler repositoryHandler;
    private ServiceHandler serviceHandler;

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = mock(RepositoryHandler.class);
//        serviceHandler = GrapesTestUtils.getServiceHandlerMock();
        RootResource resource = new RootResource(repositoryHandler, mock(GrapesServerConfig.class));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);
    }

    @Test
    public void getMainPage() throws UnknownHostException {
        WebResource resource = client().resource("");
        ClientResponse response = resource.accept(MediaType.TEXT_HTML).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }
}
