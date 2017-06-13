package org.axway.grapes.server.webapp.resources;

import com.yammer.dropwizard.auth.Auth;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.webapp.views.ListView;
import org.axway.grapes.server.webapp.views.ProductView;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Product Resource
 *
 * <p>This server resource handles all the request about products.<br/>
 * This resource extends DepManViews to holds its own documentation.
 * The documentation is available in ProductResourceDocumentation.ftl file.</p>
 * @author jdcoffre
 */
@Path(ServerAPI.PRODUCT_RESOURCE)
public class ProductResource extends AbstractResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductResource.class);
    private static final String DELIVERY_MSG_TEMPLATE = "Delivery [%s %s] %s [%s]";  // name, version, details, product name

    public ProductResource(final RepositoryHandler repositoryHandler, final GrapesServerConfig configuration) {
        super(repositoryHandler, "ProductResourceDocumentation.ftl", configuration);
    }

    /**
     * Handle product posts when the server got a request POST /product & MIME that contains an organization.
     *
     * @param productName String Product's name to add to Grapes database
     * @return Response An acknowledgment:<br/>- 400 if the MIME is malformed<br/>- 409 if product is already existing<br/>- 500 if internal error<br/>- 201 if ok
     */
    @POST
    public Response createProduct(@Auth final DbCredential credential, final String productName){
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a create product request.");

        if(productName == null || productName.isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Product name should neither be null nor empty.").build());
        }

        final DbProduct dbProduct = new DbProduct();
        dbProduct.setName(productName);
        getProductHandler().create(dbProduct);

        return Response.ok().status(HttpStatus.CREATED_201).build();
    }

    /**
     * Return the list of available product name.
     * This method is call via GET <dm_url>/product/names
     *
     * @return Response A list of product name in HTML or JSON
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path(ServerAPI.GET_NAMES)
    public Response getNames(){
        LOG.info("Got a get product names request.");

        final ListView view = new ListView("Product Ids list", "Products");
        final List<String> names = getProductHandler().getProductNames();
        Collections.sort(names);
        view.addAll(names);

        return Response.ok(view).build();
    }

    /**
     * Returns a product
     *
     * @param name String
     * @return Response A product in HTML
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{name}")
    public Response get(@PathParam("name") final String name){
        LOG.info("Got a get product request.");

        final DbProduct dbProduct = getProductHandler().getProduct(name);
        final ProductView view = new ProductView(dbProduct);

        return Response.ok(view).build();
    }

    /**
     * Delete a product
     *
     * @param credential DbCredential
     * @param name String product name
     * @return Response
     */
    @DELETE
    @Path("/{name}")
    public Response delete(@Auth final DbCredential credential, @PathParam("name") final String name){
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DATA_DELETER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        LOG.info("Got a delete organization request.");
        getProductHandler().deleteProduct(name);

        return Response.ok("done").build();
    }

    /**
     * Get the project configured list of module names
     *
     * @return Response
     */
    @GET
    @Path("/{name}" + ServerAPI.GET_MODULES)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModuleNames(@PathParam("name") final String name){
        if(LOG.isInfoEnabled()) {
            LOG.info(String.format("Got a get module names for organization %s", name));
        }
        final DbProduct dbProduct = getProductHandler().getProduct(name);

        return Response.ok(dbProduct.getModules()).build();
    }

    /**
     * Sets a list of module names to a product
     *
     * @param credential DbCredential
     * @param name String product name
     * @param moduleNames List<String>
     * @return Response
     */
    @POST
    @Path("/{name}" + ServerAPI.GET_MODULES)
    public Response setModuleNames(@Auth final DbCredential credential, @PathParam("name") final String name, final List<String> moduleNames){
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        if(LOG.isInfoEnabled()) {
            LOG.info(String.format("Got a set module names request for product %s", name));
        }

        if(moduleNames == null){
            throw new WebApplicationException(Response.serverError().status(HttpStatus.BAD_REQUEST_400)
                    .entity("Query content should contains a list of module names.").build());
        }
        
        Collections.sort(moduleNames);
        getProductHandler().setProductModules(name, moduleNames);
        return Response.ok().build();
    }

    /**
     * Returns the list of existing deliveries of a product
     *
     * @param name String product name
     * @return Response
     */
    @GET
    @Path("/{name}" + ServerAPI.GET_DELIVERIES)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeliveries(@PathParam("name") final String name){
        if(LOG.isInfoEnabled()) {
            LOG.info(String.format("Got a get deliveries request for product [%s]", name));
        }

        final DbProduct dbProduct = getProductHandler().getProduct(name);
        final List<Delivery> delivery = dbProduct.getDeliveries();
        delivery.sort(Comparator.comparing(Delivery::getCommercialName));

        return Response.ok(delivery).build();
    }

    /**
     * Create a product delivery
     *
     * @param credential DbCredential
     * @param name String product name
     * @param delivery The product delivery
     * @return Response
     */
    @POST
    @Path("/{name}" + ServerAPI.GET_DELIVERIES)
    public Response createNewDelivery(@Auth final DbCredential credential, @PathParam("name") final String name, final Delivery delivery){
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DEPENDENCY_NOTIFIER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        if(LOG.isInfoEnabled()) {
            LOG.info(String.format("Got a create delivery request for product [%s]", name));
        }

        if(delivery == null){
            throw new WebApplicationException(Response.serverError().status(HttpStatus.BAD_REQUEST_400)
                    .entity("Query content should contains the name of the new delivery.").build());
        }

        final DbProduct dbProduct = getProductHandler().getProduct(name);

        final Delivery existingDelivery = dbProduct.getDelivery(delivery.getCommercialName(), delivery.getCommercialVersion());

        if(existingDelivery != null){
            throw new WebApplicationException(Response.serverError().status(HttpStatus.CONFLICT_409)
                    .entity(String.format(DELIVERY_MSG_TEMPLATE, delivery.getCommercialName(), delivery.getCommercialVersion(), " already exists for ", name)).build());
        }

        dbProduct.getDeliveries().add(delivery);
        getProductHandler().update(dbProduct);

        getReportsHandler().refreshDelivery3rdParty(dbProduct);

        return Response.ok().status(Response.Status.CREATED).build();
    }

    /**
     * Returns the list of module ids embedded inside a delivery
     *
     * @param name String product name
     * @param delivery String delivery name
     * @return Response
     */
    @GET
    @Path("/{name}" + ServerAPI.GET_DELIVERIES+"/{commercialName}"+"/{commercialVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDelivery(@PathParam("name") final String name, @PathParam("commercialName") final String commercialName, @PathParam("commercialVersion") final String commercialVersion) {
        if(LOG.isInfoEnabled()) {
            LOG.info(String.format("Got a get delivery request for product [%s] [%s]", name, commercialName));
        }

        final DbProduct dbProduct = getProductHandler().getProduct(name);

        final Delivery delivery = dbProduct.getDelivery(commercialName, commercialVersion);
        if (delivery == null) {
            throw new WebApplicationException(Response.serverError().status(HttpStatus.NOT_FOUND_404)
                    .entity(String.format(DELIVERY_MSG_TEMPLATE, commercialName, commercialVersion, " does not exist for product ", name)).build());
        }

        return Response.ok(delivery).build();
    }

    /**
     * Delete a delivery
     *
     * @param credential DbCredential
     * @param name String product name
     * @param delivery String delivery name
     * @return Response
     */
    @DELETE
    @Path("/{name}" + ServerAPI.GET_DELIVERIES+"/{commercialName}"+"/{commercialVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDelivery(@Auth final DbCredential credential, @PathParam("name") final String name, @PathParam("commercialName") final String commercialName, @PathParam("commercialVersion") final String commercialVersion){
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DATA_DELETER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        if(LOG.isInfoEnabled()) {
            LOG.info(String.format("Got a delete delivery request for product [%s]", name));
        }

        final DbProduct dbProduct = getProductHandler().getProduct(name);

        final Delivery delivery = dbProduct.getDelivery(commercialName, commercialVersion);
        
        if(! dbProduct.getDeliveries().contains(delivery)){
            throw new WebApplicationException(Response.serverError().status(HttpStatus.NOT_FOUND_404)
                    .entity(String.format(DELIVERY_MSG_TEMPLATE, commercialName, commercialVersion, " does not exist for ", name)).build());
        }

        dbProduct.getDeliveries().remove(delivery);
        getProductHandler().update(dbProduct);

        return Response.ok().build();
    }

    /**
     * Sets the exhaustive list of modules that are embedded in a delivery
     *
     * @param credential DbCredential
     * @param name String product name
     * @param commercialName The commercial name of the product
     * @param commercialVersion The commercial version of the product
     * @param dependencies The commercial version dependencies
     * @return Response
     */
    @POST
    @Path("/{name}" + ServerAPI.GET_DELIVERIES+"/{commercialName}"+"/{commercialVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setDeliveryDependency(@Auth final DbCredential credential,
                                          @PathParam("name") final String name,
                                          @PathParam("commercialName") final String commercialName,
                                          @PathParam("commercialVersion") final String commercialVersion,
                                          List<String> dependencies){
        if(!credential.getRoles().contains(DbCredential.AvailableRoles.DATA_UPDATER)){
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        if(LOG.isInfoEnabled()) {
            LOG.info(String.format("Got a set delivery modules request for product [%s]", name));
        }

        final DbProduct dbProduct = getProductHandler().getProduct(name);
        
        final Delivery delivery = dbProduct.getDelivery(commercialName, commercialVersion);

        if(! dbProduct.getDeliveries().contains(delivery)){
            throw new WebApplicationException(Response.serverError().status(HttpStatus.NOT_FOUND_404)
                    .entity(String.format(DELIVERY_MSG_TEMPLATE, commercialName, commercialVersion, " does not exist for product ", name)).build());
        }

        Collections.sort(dependencies);
        
        dbProduct.getDelivery(commercialName, commercialVersion).setDependencies(dependencies);
        getProductHandler().update(dbProduct);

        return Response.ok().status(Response.Status.CREATED).build();
    }


}
