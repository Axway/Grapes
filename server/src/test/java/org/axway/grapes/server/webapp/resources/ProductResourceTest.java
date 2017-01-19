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
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ServiceHandler;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.MediaType;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ProductResourceTest extends ResourceTest {


    private RepositoryHandler repositoryHandler;
    private ServiceHandler serviceHandler;

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
//        serviceHandler = GrapesTestUtils.getServiceHandlerMock();
        final ProductResource resource = new ProductResource(repositoryHandler, mock(GrapesServerConfig.class));
        addProvider(new BasicAuthProvider<DbCredential>(new GrapesAuthenticator(repositoryHandler), "test auth"));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);

    }

    @Test
    public void getDocumentation(){
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE);
        ClientResponse response = resource.type(MediaType.TEXT_HTML).get(ClientResponse.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void createProduct(){
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, "product");
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());
    }

    @Test
    public void createProductWithEmptyName() throws AuthenticationException, UnknownHostException {
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, "");
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

        response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, null);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void getAllProductNames(){
        final List<String> names = Lists.newArrayList("product1", "product2", "product3");
        when(repositoryHandler.getProductNames()).thenReturn(names);

        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + ServerAPI.GET_NAMES);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        ArrayList<String> productNames = response.getEntity(ArrayList.class);
        assertNotNull(productNames);
        assertEquals(3, productNames.size());
    }

    @Test
    public void getAProduct(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName());
        ClientResponse response = resource.accept(MediaType.TEXT_HTML).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void deleteAProduct(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName());
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(repositoryHandler, times(1)).deleteProduct(captor.capture());
        assertEquals(product.getName(), captor.getValue());
    }

    @Test
    public void deleteAProductThatDoesNotExist(){
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/doesNotExist" );
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void deleteAProductWithoutDeletionRights(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.WRONG_USER_4TEST, GrapesTestUtils.WRONG_PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName());
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void getProductModules(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        product.setModules(Lists.newArrayList("module1", "module2", "module3"));
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_MODULES);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        final List<String> results = response.getEntity(new GenericType<List<String>>() {
        });
        assertNotNull(results);
        assertEquals(3, results.size());
    }

    @Test
    public void getProductModulesOfAProductThatDoesNotExist(){
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/doesNotExist" + ServerAPI.GET_MODULES);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void setProductModuleNames(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        final List<String> moduleNames = Lists.newArrayList("module1", "module2", "module3");

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_MODULES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, moduleNames);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        ArgumentCaptor<DbProduct> captor = ArgumentCaptor.forClass(DbProduct.class);
        verify(repositoryHandler).store(captor.capture());
        assertEquals(moduleNames.size(), captor.getValue().getModules().size());
    }

    @Test
    public void setProductModuleNamesWithoutEditionRights(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.WRONG_USER_4TEST, GrapesTestUtils.WRONG_PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_MODULES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, null);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void setProductModuleNamesWithNoModuleNamesInfo(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_MODULES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, null);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void setProductModuleNamesOnAProductThatDoesNotExist(){
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/doesNotExist" + ServerAPI.GET_MODULES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, new ArrayList<String>());
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void getDeliveries(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        // creating delivery
        Delivery delivery = new Delivery();
        delivery.setCommercialName("delivery1");
        delivery.setCommercialVersion("1.0.0");
        delivery.setVersion("1.0.0-1");
        delivery.setJenkinsBuildUrl("http://localhost:8080/job/20");
        
        product.getDeliveries().add(delivery);
        
        // creating delivery in storage
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
 
        // checking response status
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        List<Delivery> deliveries = response.getEntity(new GenericType<List<Delivery>>(){});
        
        // checks
        assertNotNull(deliveries);
        assertEquals(1, deliveries.size());
        assertEquals("delivery1", deliveries.get(0).getCommercialName());
        assertEquals("1.0.0-1", deliveries.get(0).getVersion());
        assertEquals("http://localhost:8080/job/20", deliveries.get(0).getJenkinsBuildUrl());
    }

    @Test
    public void getDeliveriesOfAProductThatDoesNotExist(){
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/doesNotExist" + ServerAPI.GET_DELIVERIES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void createANewDelivery(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        // creating a dummy delivery
        Delivery delivery = new Delivery();
        delivery.setCommercialName("delivery1");
        delivery.setCommercialVersion("1.0.0");
        delivery.setVersion("1.0.0-1");
        delivery.setJenkinsBuildUrl("http://localhost:8080/job/20");
        
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
        assertEquals("delivery1", captor.getValue().getDeliveries().get(0).getCommercialName());
        assertEquals("1.0.0-1", captor.getValue().getDeliveries().get(0).getVersion());
        assertEquals("http://localhost:8080/job/20", captor.getValue().getDeliveries().get(0).getJenkinsBuildUrl());
    }

    @Test
    public void createADeliveryButForgottingTheName(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        // creating a dummy product in storage
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, null);
        
        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void createADeliveryThatAlreadyExist(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        Delivery delivery = new Delivery();
        delivery.setCommercialName("delivery1");
        delivery.setCommercialVersion("1.0.0");        
        delivery.setVersion("1.0.0-1");
        delivery.setJenkinsBuildUrl("http://localhost:8080/job/20");
        product.getDeliveries().add(delivery);

        // creating a dummy product in storage with delivery
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, delivery);
        
        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT_409, response.getStatus());
    }

    @Test
    public void createANewDeliveryButWithoutEditionRights(){
        final DbProduct product = new DbProduct();
        product.setName("product1");

        Delivery delivery = new Delivery();
        delivery.setCommercialName("delivery1");
        delivery.setCommercialVersion("1.0.0");
        delivery.setVersion("1.0.0-1");
        delivery.setJenkinsBuildUrl("http://localhost:8080/job/20");

        // creating a dummy product in storage with delivery
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.WRONG_USER_4TEST, GrapesTestUtils.WRONG_PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES);
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, delivery);
        
        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void getDelivery(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        Delivery delivery = new Delivery();
        delivery.setCommercialName("delivery1");
        delivery.setCommercialVersion("1.0.0");     
        delivery.setVersion("1.0.0-1");
        delivery.setJenkinsBuildUrl("http://localhost:8080/job/20");
        delivery.setDependencies(Lists.newArrayList("module1:1.0.0", "module2:1.0.0"));
        product.getDeliveries().add(delivery);
        

        // creating a dummy product in storage with delivery and its dependencies
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES + "/delivery1/1.0.0");
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        Delivery deliveries = response.getEntity(Delivery.class);
        
        //checks
        assertNotNull(deliveries);
        assertEquals(2, deliveries.getDependencies().size());
        assertEquals("1.0.0-1", deliveries.getVersion());
        assertEquals("http://localhost:8080/job/20", deliveries.getJenkinsBuildUrl());

    }

    @Test
    public void getDeliveryOfAProductThatDoesNotExist(){
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/doesNotExist" + ServerAPI.GET_DELIVERIES+ "/delivery1/1.0.0");
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

    }

    @Test
    public void getDeliveryThatDoesNotExistOnAProductThatExist(){
        final DbProduct product = new DbProduct();
        product.setName("product1");

        // creating a dummy product in storage
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        // request
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES+ "/doesNotExist/1.0.0");
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        
        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

    }

    @Test
    public void setModulesDelivery(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        Delivery delivery = new Delivery();
        delivery.setCommercialName("delivery1");
        delivery.setCommercialVersion("1.0.0");   
        delivery.setVersion("1.0.0-1");
        delivery.setJenkinsBuildUrl("http://localhost:8080/job/20");
        product.getDeliveries().add(delivery);
        
        List<String> modules = new ArrayList<String>();
        modules.add("module1:1.0.0");
        modules.add("module2:1.0.0");
        
        // creating a dummy product in storage with delivery and its dependencies
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        // request
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES + "/delivery1/1.0.0" );
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, modules);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED_201, response.getStatus());

        ArgumentCaptor<DbProduct> captor = ArgumentCaptor.forClass(DbProduct.class);
        
        // checks
        verify(repositoryHandler).store(captor.capture());
        assertEquals(1, captor.getValue().getDeliveries().size());
        assertEquals("delivery1", captor.getValue().getDeliveries().get(0).getCommercialName());
        assertNotNull(captor.getValue().getDeliveries().get(0));
        assertEquals(modules, captor.getValue().getDeliveries().get(0).getDependencies());

    }

    @Test
    public void setModulesDeliveryWithDeliveryThatDoesNotExist(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        List<String> modules = new ArrayList<String>();
        modules.add("module1:1.0.0");
        modules.add("module2:1.0.0");

        // creating a dummy product in storage with delivery
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        // request
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES + "/doesNotExist" );
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, modules);
        
        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

    }

    @Test
    public void setModulesDeliveryWithProductThatDoesNotExist(){
        List<String> modules = new ArrayList<String>();
        modules.add("module1:1.0.0");
        modules.add("module2:1.0.0");

        // request
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/doesNotExist" + ServerAPI.GET_DELIVERIES + "/delivery1");
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, modules);
        
        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

    }

    @Test
    public void setModulesDeliveryWithoutEditionRights(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        Delivery delivery = new Delivery();
        delivery.setCommercialName("delivery1");
        delivery.setCommercialVersion("1.0.0");   
        delivery.setVersion("1.0.0-1");
        delivery.setJenkinsBuildUrl("http://localhost:8080/job/20");
        product.getDeliveries().add(delivery);
        
        List<String> modules = new ArrayList<String>();
        modules.add("module1:1.0.0");
        modules.add("module2:1.0.0");
        
        // creating a dummy product in storage with delivery
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        // request
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.WRONG_USER_4TEST, GrapesTestUtils.WRONG_PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES + "/delivery1/1.0.0" );
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, modules);

        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

    @Test
    public void deleteDelivery(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        
        Delivery delivery = new Delivery();
        delivery.setCommercialName("delivery1");
        delivery.setCommercialVersion("1.0.0");   
        delivery.setVersion("1.0.0-1");
        delivery.setJenkinsBuildUrl("http://localhost:8080/job/20");
        product.getDeliveries().add(delivery);

        // creating a dummy product in storage with delivery
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        // request
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES + "/delivery1/1.0.0" );
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        
        // check response
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());

        ArgumentCaptor<DbProduct> captor = ArgumentCaptor.forClass(DbProduct.class);
        
        // checks
        verify(repositoryHandler).store(captor.capture());
        assertEquals(0, captor.getValue().getDeliveries().size());
    }

    @Test
    public void deleteDeliveryThatDoesNotExist(){
        final DbProduct product = new DbProduct();
        product.setName("product1");

        // creating a dummy product in storage with delivery
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);

        // request
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES + "/delivery1/1.0.0" );
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        
        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void deleteDeliveryFromProductThatDoesNotExist(){
        // request
    	client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/doesNotExist" + ServerAPI.GET_DELIVERIES + "/delivery1/1.0.0" );
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        
        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void deleteDeliveryWithoutDeletionRights(){
        final DbProduct product = new DbProduct();
        product.setName("product1");

        Delivery delivery = new Delivery();
        delivery.setCommercialName("delivery1");
        delivery.setCommercialVersion("1.0.0");   
        delivery.setVersion("1.0.0-1");
        delivery.setJenkinsBuildUrl("http://localhost:8080/job/20");
        product.getDeliveries().add(delivery);
        

        // creating a dummy product in storage with delivery
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);    
        
        // request
        client().addFilter(new HTTPBasicAuthFilter(GrapesTestUtils.WRONG_USER_4TEST, GrapesTestUtils.WRONG_PASSWORD_4TEST));
        WebResource resource = client().resource("/" + ServerAPI.PRODUCT_RESOURCE + "/" + product.getName() + ServerAPI.GET_DELIVERIES + "/delivery1/1.0.0" );
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        
        // checks
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
    }

}
