package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yammer.dropwizard.testing.ResourceTest;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.reports.models.ReportRequest;
import org.axway.grapes.server.reports.writer.CsvReportWriter;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class ReportsResourceTest extends ResourceTest {

    private RepositoryHandler repositoryHandler;

    private Delivery dummyDelivery = makeDummyDelivery("Product Secure", "4.5.9");


    private Delivery makeDummyDelivery(final String commercialName, final String commercialVersion) {
        Delivery result = new Delivery();
        result.setCommercialName(commercialName);
        result.setCommercialVersion(commercialVersion);

        return result;
    }

    @Override
	protected void setUpResources() throws Exception {

	    DbProduct dummyProduct = new DbProduct();
	    List<Delivery> deliveries = new ArrayList<>();
	    deliveries.add(dummyDelivery);

	    dummyProduct.setDeliveries(deliveries);

		repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler
                .getOneByQuery(anyString(), anyString(), eq(DbProduct.class)))
                .thenReturn(Optional.of(dummyProduct));

        ReportResource resource = new ReportResource(repositoryHandler, mock(GrapesServerConfig.class));

		addProvider(new CsvReportWriter());
		addResource(resource);	
	}
    
    @Test
	public void getListOfReportsEnpoint() {
		WebResource resource = client().resource(ServerAPI.GET_REPORTS);
		ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        
		assertNotNull(response);
		assertEquals(HttpStatus.OK_200, response.getStatus());
	}

    @Test
    public void noPayloadReturnsBadRequest() {
        WebResource resource = client().resource(ServerAPI.GET_REPORTS + "/execution");
        ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void listAvailableReportsReturnsTheCorrectCode() {
        WebResource resource = client().resource(ServerAPI.GET_REPORTS);
        ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void executeInexistentReportShouldReturn_404() {
        WebResource resource = client().resource(ServerAPI.GET_REPORTS + "/execution");
        ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, makeReportRequest(-1));

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

        assertTrue(response.getEntity(String.class).contains("Invalid report id"));
    }

    @Test
    public void notAllParamsSet_400() {
        WebResource resource = client().resource(ServerAPI.GET_REPORTS + "/execution");
        ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, makeReportRequest(1, "name=Some Name"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400,  response.getStatus());
        assertTrue(response.getEntity(String.class).contains("version"));
    }

    @Test
    public void reportExecutionReturnsCSV() {
        WebResource resource = client().resource(ServerAPI.GET_REPORTS + "/execution");
        ClientResponse response = resource
                .accept("text/csv")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class,
                        makeReportRequest(1, "name=" + dummyDelivery.getCommercialName(),
                                "version=" + dummyDelivery.getCommercialVersion()));

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200,  response.getStatus());
    }

    @Test
    public void reportExecutionReturnsJSON() {
        WebResource resource = client().resource(ServerAPI.GET_REPORTS + "/execution");
        ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class,
                        makeReportRequest(1, "name=" + dummyDelivery.getCommercialName(),
                                "version=" + dummyDelivery.getCommercialVersion()));

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200,  response.getStatus());
    }


    private ReportRequest makeReportRequest(int requestId, String... nameEqualValues) {
        ReportRequest req = new ReportRequest();
        req.setReportId(requestId);

        if(nameEqualValues.length > 0) {
            Map<String, String> params = new HashMap<>();

            for(String entry : nameEqualValues) {
                final String[] parts = entry.split("=");

                if(parts.length == 2) {
                    params.put(parts[0], parts[1]);
                }
            }

            req.setParamValues(params);
        }

        return req;
    }

}
