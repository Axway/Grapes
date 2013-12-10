package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbLicense;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApprovedFilterTest {

    @Test
    public void approveNull(){
        ApprovedFilter filter = new ApprovedFilter(true);
        assertFalse(filter.filter(null));

        filter = new ApprovedFilter(false);
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveLicense(){
        final DbLicense approvedLicense = new DbLicense();
        approvedLicense.setApproved(true);
        final DbLicense rejectLicense = new DbLicense();
        rejectLicense.setApproved(false);
        final DbLicense toBeValidatedLicense = new DbLicense();


        ApprovedFilter filter = new ApprovedFilter(true);
        assertTrue(filter.filter(approvedLicense));
        assertFalse(filter.filter(rejectLicense));
        assertFalse(filter.filter(toBeValidatedLicense));

        filter = new ApprovedFilter(false);
        assertFalse(filter.filter(approvedLicense));
        assertTrue(filter.filter(rejectLicense));
        assertFalse(filter.filter(toBeValidatedLicense));
    }

}
