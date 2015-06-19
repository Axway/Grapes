package org.axway.grapes.core.service;


import org.axway.grapes.core.exceptions.DataValidationException;
import org.axway.grapes.model.datamodel.Product;

import java.util.List;

/**
 * Created by jennifer on 4/24/15.
 */
public interface ProductService {


   public void create(Product product) throws DataValidationException;

    public void store(Product product);

    public void update(Product product);

    public List<String> getProductNames();

    public Product getProduct(String name);

    public  void deleteProduct(String name);

    public void setProductModules(String name, List<String> moduleNames);
}
