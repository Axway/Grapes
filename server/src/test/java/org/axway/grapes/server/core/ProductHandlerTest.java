package org.axway.grapes.server.core;

import com.google.common.collect.Lists;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

public class ProductHandlerTest {

    @Test
    public void storeANewProduct(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        final RepositoryHandler repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final ProductHandler productHandler = new ProductHandler(repositoryHandler);

        Exception exception = null;

        try {
            productHandler.create(product);
        }
        catch(Exception e){
            exception = e;
        }

        assertNull(exception);
        verify(repositoryHandler,times(1)).store(product);
    }

    @Test
    public void storeAProductThatAlreadyExist(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        final RepositoryHandler repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);
        final ProductHandler productHandler = new ProductHandler(repositoryHandler);

        Exception exception = null;

        try {
            productHandler.create(product);
        }
        catch(Exception e){
            exception = e;
        }

        assertNotNull(exception);
        verify(repositoryHandler,never()).store(product);
    }

    @Test
    public void getProductNames(){
        final RepositoryHandler repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final ProductHandler productHandler = new ProductHandler(repositoryHandler);

        productHandler.getProductNames();

        verify(repositoryHandler,times(1)).getProductNames();
    }

    @Test
    public void getAnExistingProduct(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        final RepositoryHandler repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);
        final ProductHandler productHandler = new ProductHandler(repositoryHandler);

        final DbProduct gotProduct = productHandler.getProduct(product.getName());

        assertNotNull(gotProduct);
        assertEquals(product, gotProduct);
    }

    @Test
    public void getAProductThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final ProductHandler productHandler = new ProductHandler(repositoryHandler);

        Exception exception = null;

        try {
            productHandler.getProduct("doesNotExist");
        }
        catch(Exception e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void deleteAProduct(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        final RepositoryHandler repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);
        final ProductHandler productHandler = new ProductHandler(repositoryHandler);

        productHandler.deleteProduct(product.getName());

        verify(repositoryHandler, times(1)).deleteProduct(product.getName());
    }

    @Test
    public void deleteAProductThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final ProductHandler productHandler = new ProductHandler(repositoryHandler);

        Exception exception = null;

        try {
            productHandler.deleteProduct("doesNotExist");
        }
        catch(Exception e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void setProductModules(){
        final DbProduct product = new DbProduct();
        product.setName("product1");
        final RepositoryHandler repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        when(repositoryHandler.getProduct(product.getName())).thenReturn(product);
        final ProductHandler productHandler = new ProductHandler(repositoryHandler);

        final List<String> moduleNames = Lists.newArrayList("module1", "module2", "module3");
        productHandler.setProductModules(product.getName(), moduleNames);

        final ArgumentCaptor<DbProduct> captor = ArgumentCaptor.forClass(DbProduct.class);
        verify(repositoryHandler, times(1)).store(captor.capture());
        assertEquals(moduleNames, captor.getValue().getModules());
    }

    @Test
    public void setProductModulesForAProductThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final ProductHandler productHandler = new ProductHandler(repositoryHandler);

        Exception exception = null;

        try {

            final List<String> moduleNames = Lists.newArrayList("module1", "module2", "module3");
            productHandler.setProductModules("doesNotExist", moduleNames);
        }
        catch(Exception e){
            exception = e;
        }

        assertNotNull(exception);
    }



}
