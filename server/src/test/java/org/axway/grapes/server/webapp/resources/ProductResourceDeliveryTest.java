package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import javax.ws.rs.core.MediaType;

@RunWith(Parameterized.class)
public class ProductResourceDeliveryTest extends ResourceTest {

	private String commercialName = "";
	private String commercialVersion = "";
	private String commercialNameExpected = "";
	private String version;
	private String jenkinsBuildUrl;
	private boolean isOnlyPost = true;
    private RepositoryHandler repositoryHandler;

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final ProductResource resource = new ProductResource(repositoryHandler, mock(GrapesServerConfig.class));
        addProvider(new BasicAuthProvider<DbCredential>(new GrapesAuthenticator(repositoryHandler), "test auth"));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);

    }
    
    public ProductResourceDeliveryTest(String commercialName, String commercialNameExpected, String commercialVersion, final String version, final String jenkinsBuildUrl, boolean isOnlyPost){
        this.commercialName = commercialName;
		this.commercialNameExpected = commercialNameExpected;
        this.commercialVersion = commercialVersion;
        this.version = version;
        this.jenkinsBuildUrl = jenkinsBuildUrl;
        this.isOnlyPost = isOnlyPost;
    }
    

    @Parameterized.Parameters(name = "{index}: taskDelivery({0}) Name {1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
        	{"New Delivery", "New Delivery", "1.1", "1.0.0-1", "http://localhost:8080/job/20", true},
        	{"New Delivery ", "New Delivery", "1.2", "1.0.0-1", "http://localhost:8080/job/20", true},
        	{"New-Delivery", "New-Delivery", "1.2", "1.0.0-1", "http://localhost:8080/job/20", true},
        	{"New%20Delivery ", "New%20Delivery", "1.2", "1.0.0-1", "http://localhost:8080/job/20", true},
        	{"New-Delivery", "New-Delivery", "1.2", "1.0.0-1", "http://localhost:8080/job/20", false},
        	{"New Delivery", "New%20Delivery", "1.3", "1.0.0-1", "http://localhost:8080/job/20", false}
        });
    }
	
    @Test
    public void createADelivery(){
    	
    	if(!isOnlyPost){
    		return;
    	}
    	
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        // creating a dummy delivery
        Delivery delivery = new Delivery();
        delivery.setCommercialName(commercialName);
        delivery.setCommercialVersion(commercialVersion);
        delivery.setVersion(version);
        delivery.setJenkinsBuildUrl(jenkinsBuildUrl);
        
        // creating product in storage
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);
 
        // sending request
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, delivery);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());

        ArgumentCaptor<DbProduct> captor = ArgumentCaptor.forClass(DbProduct.class);
        
        // checks
        verify(repositoryHandler).store(captor.capture());
        assertEquals(1, captor.getValue().getDeliveries().size());
        assertEquals(commercialNameExpected, captor.getValue().getDeliveries().get(0).getCommercialName());
        
        System.out.println(captor.getValue().getDeliveries().get(0).getCommercialName());
    }
    
    @Test
    public void getDelivery(){
    	
    	if(isOnlyPost){
    		return;
    	}
    	
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        Delivery delivery = new Delivery();
        delivery.setCommercialName(commercialName);
        delivery.setCommercialVersion(commercialVersion);
        delivery.setVersion(version);
        delivery.setJenkinsBuildUrl(jenkinsBuildUrl);
        product.getDeliveries().add(delivery);
        

        // creating a dummy product in storage with delivery and its dependencies
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES + "/" + commercialNameExpected + "/" + commercialVersion);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        Delivery delivery1 = response.getEntity(Delivery.class);
        
        //checks
        assertNotNull(delivery1);
        assertEquals(commercialName, delivery.getCommercialName());

    }
}
