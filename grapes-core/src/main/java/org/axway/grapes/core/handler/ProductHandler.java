package org.axway.grapes.core.handler;

import com.google.common.collect.Lists;
import org.axway.grapes.core.exceptions.DataValidationException;
import org.axway.grapes.core.service.ProductService;
import org.axway.grapes.model.datamodel.Product;
import org.wisdom.api.annotations.Model;
import org.wisdom.api.annotations.Service;
import org.wisdom.api.model.Crud;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by jennifer on 4/28/15.
 */
@Service
public class ProductHandler implements ProductService {
    @Model(value = Product.class)
   private Crud<Product, String> productCrud;

    /**
     *
     * @param product
     */
    @Override
    public void create(Product product) throws DataValidationException {
        if (productCrud.findOne(product.getName()) != null) {
            throw new DataValidationException("Product with the name "+'"'+product.getName()+'"'+" already exists" );

        }
        store(product);
    }

    /**
     * mh
     * @param product
     */
    @Override
    public void store(Product product) {
        productCrud.save(product);
    }

    /**
     * ph
     * @param product
     */
    @Override
    public void update(Product product) {
        store(product);
    }

    /**
     * mh + ph
     * @return
     */
    @Override
    public List<String> getProductNames() {
       Iterable<Product> list = productCrud.findAll();
        Set<String> listOfNames = new HashSet<>();
        for(Product product:list){
            listOfNames.add(product.getName());
        }
        return Lists.newArrayList(listOfNames);
    }

    /**
     * mh + ph
     * @param name
     * @return
     */
    @Override
    public Product getProduct(String name) {
        Product product = productCrud.findOne(name);
        if(product==null){
            throw new NoSuchElementException("Product with the name: "+name);
        }
        return product;
    }

    /**
     * mh +ph
     * @param name
     */
    @Override
    public void deleteProduct(String name) {
        Product product = getProduct(name);
        productCrud.delete(product);
    }

    /**
     * ph
     * @param name
     * @param moduleNames
     */
    @Override
    public void setProductModules(String name, List<String> moduleNames) {
        final Product product = getProduct(name);
        product.setModules(moduleNames);
        productCrud.save(product);
    }
}
