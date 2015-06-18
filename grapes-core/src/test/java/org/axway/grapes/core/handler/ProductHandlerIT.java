package org.axway.grapes.core.handler;

import com.google.common.collect.Lists;
import org.axway.grapes.core.exceptions.DataValidationException;
import org.axway.grapes.core.service.ProductService;
import org.axway.grapes.model.datamodel.Product;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductHandlerIT extends WisdomTest {

    @Inject
    ProductService productService;
    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbProduct)")
    Crud<Product, String> productCrud;



    @Before
    public void clearDBCollection(){
        Iterable<Product> list = productCrud.findAll();

        productCrud.delete(list);

    }
    @Test
    public void storeANewProduct() {
        final Product product = new Product();
        product.setName("product7");
        try {
            productService.create(product);
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void storeAProductThatAlreadyExist() {
        final Product product = new Product();
        product.setName("product1");
        productService.store(product);

        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(DataValidationException.class);
    }

    @Test
    public void getProductNames() {
        final Product product = new Product();
        product.setName("product1");
        productService.store(product);
        List<String> list = productService.getProductNames();
        assertThat(list.size()).isGreaterThan(0);
        assertThat(list).contains("product1");
    }

    @Test
    public void getAnExistingProduct() {
        final Product product = new Product();
        product.setName("product1");
        productService.store(product);
        final Product gotProduct = productService.getProduct(product.getName());
        assertThat(gotProduct).isNotNull();
        assertThat(product.getName()).isEqualTo(gotProduct.getName());
    }

    @Test(expected = NoSuchElementException.class)
    public void getAProductThatDoesNotExist() {
        productService.getProduct("doesNotExist");
    }

    @Test
    public void deleteAProduct() {
        final Product product = new Product();
        product.setName("product1");
        productService.store(product);
        productService.deleteProduct(product.getName());
        assertThatThrownBy(() -> productService.getProduct(product.getName())).isInstanceOf(NoSuchElementException.class);
    }

    @Test(expected = NoSuchElementException.class)
    public void deleteAProductThatDoesNotExist() {
        productService.deleteProduct("doesNotExist");
    }

    @Test
    public void setProductModules() {
        final Product product = new Product();
        product.setName("product1");
        productService.store(product);
        Product product1 = productService.getProduct(product.getName());
        final List<String> moduleNames = Lists.newArrayList("module1", "module2", "module3");
        productService.setProductModules(product.getName(), moduleNames);
        product1 = productService.getProduct(product.getName());
        assertThat(product1.getModules().size()).isEqualTo(3);
        assertThat(product1.getModules()).contains("module1");
        assertThat(product1.getModules()).contains("module2");
        assertThat(product1.getModules()).contains("module3");
    }

    @Test(expected = NoSuchElementException.class)
    public void setProductModulesForAProductThatDoesNotExist() {
        final List<String> moduleNames = Lists.newArrayList("module1", "module2", "module3");
        productService.setProductModules("doesNotExist", moduleNames);
    }
}
