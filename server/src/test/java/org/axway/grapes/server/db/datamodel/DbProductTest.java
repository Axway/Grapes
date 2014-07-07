package org.axway.grapes.server.db.datamodel;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class DbProductTest {

    @Test
    public void checkThatIfWeAddManyTimeTheSameModuleNameItIsStoredOnlyOnce(){
        final DbProduct product = new DbProduct("product1");
        product.addModule("module1");
        product.addModule("module1");

        assertEquals(1, product.getModules().size());

    }

}
