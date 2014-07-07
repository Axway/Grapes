package org.axway.grapes.server.core;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbProduct;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Product Handler
 *
 * <p>Manages all operation regarding Products. It can, get/update Products of the database.</p>
 *
 * @author jdcoffre
 */
public class ProductHandler {


    private final RepositoryHandler repositoryHandler;

    public ProductHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }

    /**
     * Creates a new Product in Grapes database
     *
     * @param dbProduct DbProduct
     */
    public void create(final DbProduct dbProduct) {
        if(repositoryHandler.getProduct(dbProduct.getName()) != null){
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Product already exist!").build());
        }

        repositoryHandler.store(dbProduct);
    }

    /**
     * Update a product in Grapes database
     *
     * @param dbProduct DbProduct
     */
    public void update(final DbProduct dbProduct) {
        repositoryHandler.store(dbProduct);
    }

    /**
     * Returns all the product names
     *
     * @return List<String>
     */
    public List<String> getProductNames() {
        return repositoryHandler.getProductNames();
    }

    /**
     * Returns a product regarding its name
     *
     * @param name String
     * @return DbProduct
     */
    public DbProduct getProduct(final String name) {
        final DbProduct dbProduct = repositoryHandler.getProduct(name);

        if(dbProduct == null){
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Product " + name + " does not exist.").build());
        }

        return dbProduct;
    }

    /**
     * Deletes a product from the database
     *
     * @param name String
     */
    public void deleteProduct(final String name) {
        final DbProduct dbProduct = getProduct(name);
        repositoryHandler.deleteProduct(dbProduct.getName());
    }

    /**
     * Patches the product module names
     *
     * @param name String
     * @param moduleNames List<String>
     */
    public void setProductModules(final String name, final List<String> moduleNames) {
        final DbProduct dbProduct = getProduct(name);
        dbProduct.setModules(moduleNames);
        repositoryHandler.store(dbProduct);
    }
}
