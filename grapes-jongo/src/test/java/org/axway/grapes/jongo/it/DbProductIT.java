package org.axway.grapes.jongo.it;

import org.axway.grapes.jongo.datamodel.DbProduct;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DbProductIT  extends WisdomTest {

    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbProduct)")
    Crud<DbProduct, String> productCrud;

    @Test
    public void testProducts() {
        Long count = productCrud.count();
        Date date = new Date();
        String name = "TestProduct"+date.getTime();
        List<String> moduleList = new ArrayList<String>();
        moduleList.add("module1");
        moduleList.add("module2");

        //create one
        DbProduct dbProduct = new DbProduct();
        dbProduct.setName(name);
        dbProduct.setOrganization("testOrg");
        dbProduct = productCrud.save(dbProduct);
        assertThat(productCrud.count()).isEqualTo(count +1);
        //find it
        DbProduct actual = productCrud.findOne(dbProduct.getName());
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(dbProduct.getName());
        assertThat(actual.getOrganization()).isEqualTo("testOrg");
        //update it
        actual.setModules(moduleList);
        productCrud.save(actual);
        assertThat(productCrud.count()).isEqualTo(count + 1);

        actual = productCrud.findOne(dbProduct.getName());
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(dbProduct.getName());
        assertThat(actual.getModules()).isNotEmpty();
        assertThat(actual.getModules()).hasSize(2);

        //dispose it
        productCrud.delete(actual);
        actual = productCrud.findOne(dbProduct.getName());
        assertThat(actual).isNull();
        assertThat(productCrud.count()).isEqualTo(count);
    }


}