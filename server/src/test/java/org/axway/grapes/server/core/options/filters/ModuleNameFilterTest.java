package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModuleNameFilterTest {

    @Test
    public void approveNull(){
        ModuleNameFilter filter = new ModuleNameFilter("test");
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveModule(){
        final DbModule module = new DbModule();
        module.setName("nameTest");


        ModuleNameFilter filter = new ModuleNameFilter(module.getName());
        assertTrue(filter.filter(module));

        filter = new ModuleNameFilter("test");
        assertFalse(filter.filter(module));
    }

}
