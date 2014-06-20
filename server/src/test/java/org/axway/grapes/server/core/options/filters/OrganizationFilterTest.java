package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrganizationFilterTest {

    @Test
    public void filterAModuleThatIsNull(){
        final OrganizationFilter filter = new OrganizationFilter("test");
        assertFalse(filter.filter(null));
    }

    @Test
    public void filterAModuleThatHaveItsOrganizationIsNull(){
        final OrganizationFilter filter = new OrganizationFilter("test");
        final DbModule module = new DbModule();
        module.setOrganization(null);

        assertFalse(filter.filter(module));
    }

    @Test
    public void filterAModuleThatHavAnOrganizationThatDoesNotMatchTheFilter(){
        final OrganizationFilter filter = new OrganizationFilter("test");
        final DbModule module = new DbModule();
        module.setOrganization("no");

        assertFalse(filter.filter(module));
    }

    @Test
    public void filterAModuleThatHavAnOrganizationThatMatchesTheFilter(){
        final OrganizationFilter filter = new OrganizationFilter("test");
        final DbModule module = new DbModule();
        module.setOrganization("test");

        assertTrue(filter.filter(module));
    }


    @Test
    public void checkMongoRegExpGenerationForModules(){
        final OrganizationFilter filter = new OrganizationFilter("organization1");

        Map<String, Object> params = filter.moduleFilterFields();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals(DbModule.ORGANIZATION_DB_FIELD, params.keySet().iterator().next());
        assertEquals("organization1", params.values().iterator().next());
    }

}
