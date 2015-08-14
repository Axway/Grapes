package org.axway.grapes.core.webapi.resources;
//todo all of the controllers should check json of incoming stuff?
//todo basically done

import com.google.common.collect.Lists;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.exceptions.DataValidationException;
import org.axway.grapes.core.service.ModuleService;
import org.axway.grapes.core.service.ProductService;
import org.axway.grapes.model.api.ServerAPI;
import org.axway.grapes.model.datamodel.Credential;
import org.axway.grapes.model.datamodel.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Body;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Path;
import org.wisdom.api.annotations.PathParameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.security.Authenticated;
import org.wisdom.api.templates.Template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by jennifer on 4/28/15.
 */
@Controller
@Path(ServerAPI.PRODUCT_RESOURCE)
public class ProductController extends DefaultController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);
    @Requires
    ProductService productService;
    @Requires
    ModuleService moduleService;
    @View("ProductResourceDocumentation")
    Template ProductResourceDocumentation;

    /**
     * The action method returning the welcome page. It handles
     * HTTP GET request on the "/" URL.
     *
     * @return the welcome page
     */
    @Route(method = HttpMethod.GET, uri = "")
    public Result welcome() {
        return ok(render(ProductResourceDocumentation, "welcome", "Welcome to The New Grapes Under Construction!"));
    }

    /**
     * Handle product posts when the server got a request POST /product & MIME that contains an organization.
     *
     * @param productName String Product's name to add to Grapes database.
     * @return Result An acknowledgment:<br/>- 400 if the MIME is malformed<br/>- 409 if product is already existing<br/>- 500 if internal error<br/>- 201 if ok.
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "")
    public Result createProduct(@Body final String productName) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a create product request.");
        if (productName == null || productName.isEmpty()) {
            return ok("Product name should neither be null nor empty").status(Result.BAD_REQUEST);
        }
        final Product product = new Product();
        product.setName(productName);
        try {
            productService.create(product);
        } catch (DataValidationException e) {
            return ok(e.getMessage()).status(Result.CONFLICT);
        }
        return ok().status(Result.CREATED);
    }

    /**
     * Get the list of available product names.
     * This method is call via GET <dm_url>/product/names.
     *
     * @return Result A list of product names in JSON.
     */
    @Route(method = HttpMethod.GET, uri = ServerAPI.GET_NAMES)
    public Result getNames() {
        LOG.info("Got a get product names request.");
        final List<String> names = productService.getProductNames();
        Collections.sort(names);
        return ok(names).json();
    }

    /**
     * Get a product based on its name.
     *
     * @param name String.
     * @return Result A product in Json.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}")
    public Result get(@PathParameter("name") final String name) {
        LOG.info("Got a get product request.");
        if ("names".equalsIgnoreCase(name)){
            return getNames();
        }
        try {
            final Product dbProduct = productService.getProduct(name);
            return ok(dbProduct);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The Product " + name + " does not exist.");
        }
    }

    /**
     * Delete a product.
     *
     * @param name String product name
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.DELETE, uri = "/{name}")
    public Result delete(@PathParameter("name") final String name) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_DELETER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a delete Product request.");
        try {
            productService.deleteProduct(name);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The Product " + name + " does not exist.");
        }
        return ok("done");
    }

    /**
     * Get the modules associated with the product.
     *
     * @param name of the product.
     * @return Result 404 not found, or list of modules in json.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}" + ServerAPI.GET_MODULES)
    public Result getModuleNames(@PathParameter("name") final String name) {
        LOG.info("Got a get module names for product " + name + ".");
        try {
            final Product dbProduct = productService.getProduct(name);
            return ok(dbProduct.getModules()).json();
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The Product " + name + " does not exist.");
        }
    }

    /**
     * todo never checks to see if these modules exsist in the database.
     * todo but done
     * Sets a list of module names to a product.
     *
     * @param name        String product name.
     * @param moduleNames List<String>.
     * @return Result 400 Bad request if modulename list is missing from query. 404 if the product doesnt exist.
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "/{name}" + ServerAPI.GET_MODULES)
    public Result setModuleNames(@PathParameter("name") final String name, @Body final List<String> moduleNames) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a set module names request for product " + name + ".");
        if (moduleNames == null) {
            return ok().status(Result.BAD_REQUEST).render("Query content should contain a list of module names");
        }
        Collections.sort(moduleNames);
        try {
            productService.setProductModules(name, moduleNames);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The Product " + name + " does not exist.");
        }
        return ok("done");
    }

    /**
     * Returns the list of existing deliveries of a product.
     *
     * @param name String product name.
     * @return Result list of deliveries in json or 404 if the product does not exist.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}" + ServerAPI.GET_DELIVERIES)
    public Result getDeliveries(@PathParameter("name") final String name) {
        LOG.info("Got a get deliveries request for product " + name + ".");
        try {
            final Product dbProduct = productService.getProduct(name);
            final List<String> deliveryNames = Lists.newArrayList(dbProduct.getDeliveries().keySet());
            Collections.sort(deliveryNames);
            return ok(deliveryNames);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The Product " + name + " does not exist.");
        }
    }

    /**
     * Create a product delivery.
     *
     * @param name         String product name.
     * @param deliveryName String that will be created.
     * @return Result 201 is successfully created.
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "/{name}" + ServerAPI.GET_DELIVERIES)
    public Result createNewDelivery(@PathParameter("name") final String name, @Body final String deliveryName) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a create delivery request for product " + name + ".");
        if (deliveryName == null || deliveryName.isEmpty()) {
            return ok("Query content should contains the name of the new delivery.").status(Result.BAD_REQUEST);
        }
        try {
            final Product dbProduct = productService.getProduct(name);
            if (dbProduct.getDeliveries().containsKey(deliveryName)) {
                return ok("Delivery " + deliveryName + " already exist for product " + name + ".").status(Result.CONFLICT);
            }
            dbProduct.getDeliveries().put(deliveryName, new ArrayList<String>());
            productService.update(dbProduct);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("The Product " + name + " does not exist.");
        }
        return ok().status(Result.CREATED);
    }

    /**
     * Returns the list of module ids embedded inside a delivery.
     *
     * @param name     String product name
     * @param delivery String delivery name
     * @return Result list of module id as json.
     */
    @Route(method = HttpMethod.GET, uri = "/{name}" + ServerAPI.GET_DELIVERIES + "/{delivery}")
    public Result getDelivery(@PathParameter("name") final String name, @PathParameter("delivery") final String delivery) {
        LOG.info("Got a get delivery request for product " + name + ".");
        try {
            final Product dbProduct = productService.getProduct(name);
            final List<String> modules = dbProduct.getDeliveries().get(delivery);
            if (modules == null) {
                return ok("Delivery " + delivery + " does not exist for product " + name + ".").status(Result.NOT_FOUND);
//
            }
            Collections.sort(modules);
            return ok(modules);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("Product " + name + " does not exist for delivery " + delivery + ".");
        }
    }

    /**
     * Sets the exhaustive list of modules that are embedded in a delivery.
     *
     * @param name     String product name
     * @param delivery String delivery name
     * @param modules  List<String> list of modules Ids
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri = "/{name}" + ServerAPI.GET_DELIVERIES + "/{delivery}")
    public Result setDelivery(@PathParameter("name") final String name, @PathParameter("delivery") final String delivery, @Body final List<String> modules) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_UPDATER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a set delivery modules request for product " + name + ".");
        try {
            final Product dbProduct = productService.getProduct(name);
            if (dbProduct.getDeliveries().get(delivery) == null) {
                return ok("Delivery " + delivery + " does not exist for product " + name + ".").status(Result.NOT_FOUND);
            }
            //all or nothing!
            for (String moduleId : modules) {
                try {
                    moduleService.getModule(moduleId);
                } catch (NoSuchElementException e) {
                    return ok().status(Result.NOT_FOUND).render("The Module " + moduleId + " does not exist.");
                }
            }
            Collections.sort(modules);
            dbProduct.getDeliveries().put(delivery, modules);
            productService.update(dbProduct);
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("Product " + name + " does not exist for delivery " + delivery + ".");
        }
        return ok().status(Result.CREATED);
    }

    /**
     * Delete a delivery.
     *
     * @param
     * @param name     String product name
     * @param delivery String delivery name
     * @return Result
     */
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.DELETE, uri = "/{name}" + ServerAPI.GET_DELIVERIES + "/{delivery}")
    public Result deleteDelivery(@PathParameter("name") final String name, @PathParameter("delivery") final String delivery) {
        if (!session("roles").contains(String.valueOf(Credential.AvailableRoles.DATA_DELETER))) {
            return ok().status(Result.UNAUTHORIZED);
        }
        LOG.info("Got a delete delivery request for product " + name + ".");
        try {
            final Product dbProduct = productService.getProduct(name);
            if (!dbProduct.getDeliveries().containsKey(delivery)) {
                return ok("Delivery " + delivery + " does not exist for product " + name + ".").status(Result.NOT_FOUND);
            }
            dbProduct.getDeliveries().remove(delivery);
            productService.update(dbProduct);
            return ok();
        } catch (NoSuchElementException e) {
            return ok().status(Result.NOT_FOUND).render("Product " + name + " does not exist for delivery " + delivery + ".");
        }
    }
}
