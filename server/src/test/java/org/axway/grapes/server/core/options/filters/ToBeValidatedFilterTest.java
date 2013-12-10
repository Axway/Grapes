package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbLicense;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ToBeValidatedFilterTest {

    @Test
    public void approveNull(){
        ToBeValidatedFilter filter = new ToBeValidatedFilter(true);
        assertFalse(filter.filter(null));

        filter = new ToBeValidatedFilter(false);
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbLicense unvalidatedLicense = new DbLicense();
        unvalidatedLicense.setApproved(false);
        final DbLicense validatedLicense = new DbLicense();
        validatedLicense.setApproved(true);
        final DbLicense toBeValidatedLicense = new DbLicense();

        ToBeValidatedFilter filter = new ToBeValidatedFilter(true);
        assertTrue(filter.filter(toBeValidatedLicense));
        assertFalse(filter.filter(validatedLicense));
        assertFalse(filter.filter(unvalidatedLicense));

        filter = new ToBeValidatedFilter(false);
        assertFalse(filter.filter(toBeValidatedLicense));
        assertTrue(filter.filter(validatedLicense));
        assertTrue(filter.filter(unvalidatedLicense));
    }

}
