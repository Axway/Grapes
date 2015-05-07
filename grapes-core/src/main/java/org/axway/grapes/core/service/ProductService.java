package org.axway.grapes.core.service;


import org.axway.grapes.model.datamodel.Product;

import java.util.List;

/**
 * Created by jennifer on 4/24/15.
 */
public interface ProductService {
    void create(Product product);

    void update(Product product);

    List<String> getProductNames();

    Product getProduct(String name);

    void deleteProduct(String name);

    void setProductModules(String name, List<String> moduleNames);
}
