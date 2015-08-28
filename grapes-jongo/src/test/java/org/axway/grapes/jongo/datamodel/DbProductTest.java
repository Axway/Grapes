package org.axway.grapes.jongo.datamodel;

import org.axway.grapes.jongo.datamodel.DbCollections;
import org.axway.grapes.jongo.datamodel.DbProduct;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DbProductTest {

    List<String> moduleList = new ArrayList<String>();
    List<String> deliveries = new ArrayList<String>();
    Map<String,List<String>> deliveryMap = new HashMap<>();

    DbProduct dbProduct= new DbProduct();

    @Before
    public void testSetProduct() throws Exception {
        moduleList.add("module1");
        moduleList.add("module2");
        deliveries.add("dev1");
        deliveries.add("dev2");
        deliveryMap.put("TestDeliveries",deliveries);

        dbProduct.setName("TestProduct");
        dbProduct.setDatamodelVersion(DbCollections.datamodelVersion);
        dbProduct.setModules(moduleList);
        dbProduct.setDeliveries(deliveryMap);
        dbProduct.setOrganization("TestOrg");

    }


    @Test
    public void testGetDatamodelVersion() throws Exception {
        assertThat(dbProduct.getDatamodelVersion()).isEqualTo(DbCollections.datamodelVersion);
    }



    @Test
    public void testGetOrganization() throws Exception {
        assertThat(dbProduct.getOrganization()).isEqualTo("TestOrg");


    }



    @Test
    public void testGetModules() throws Exception {
        assertThat(dbProduct.getModules()).hasSize(2);
    }



    @Test
    public void testGetDeliveries() throws Exception {
        assertThat(dbProduct.getDeliveries().get("TestDeliveries")).hasSize(2);
    }


}