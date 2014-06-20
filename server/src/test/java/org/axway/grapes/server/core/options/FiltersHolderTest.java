package org.axway.grapes.server.core.options;

import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.core.options.filters.ApprovedFilter;
import org.axway.grapes.server.core.options.filters.CorporateFilter;
import org.axway.grapes.server.core.options.filters.ToBeValidatedFilter;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.junit.Test;

import java.net.UnknownHostException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FiltersHolderTest {

    @Test
    public void checkIfALicenseShouldBeInReport(){
        final DbLicense licenseToValidate= new DbLicense();
        licenseToValidate.setName("tovalidate");

        final DbLicense licenseValidated= new DbLicense();
        licenseValidated.setName("validated");
        licenseValidated.setApproved(true);

        final DbLicense licenseUnvalidated= new DbLicense();
        licenseUnvalidated.setName("unvalidated");
        licenseUnvalidated.setApproved(false);

        FiltersHolder filters = new FiltersHolder();
        assertTrue(filters.shouldBeInReport(licenseToValidate));
        assertTrue(filters.shouldBeInReport(licenseValidated));
        assertTrue(filters.shouldBeInReport(licenseUnvalidated));

        ToBeValidatedFilter toBeValidatedFilter = new ToBeValidatedFilter(true);
        filters.addFilter(toBeValidatedFilter);
        assertTrue(filters.shouldBeInReport(licenseToValidate));
        assertFalse(filters.shouldBeInReport(licenseValidated));
        assertFalse(filters.shouldBeInReport(licenseUnvalidated));

        toBeValidatedFilter = new ToBeValidatedFilter(false);
        filters.addFilter(toBeValidatedFilter);
        assertFalse(filters.shouldBeInReport(licenseToValidate));
        assertTrue(filters.shouldBeInReport(licenseValidated));
        assertTrue(filters.shouldBeInReport(licenseUnvalidated));

        filters = new FiltersHolder();
        ApprovedFilter approvedFilter = new ApprovedFilter(true);
        filters.addFilter(approvedFilter);
        assertFalse(filters.shouldBeInReport(licenseToValidate));
        assertTrue(filters.shouldBeInReport(licenseValidated));
        assertFalse(filters.shouldBeInReport(licenseUnvalidated));

        approvedFilter = new ApprovedFilter(false);
        filters.addFilter(approvedFilter);
        assertFalse(filters.shouldBeInReport(licenseToValidate));
        assertFalse(filters.shouldBeInReport(licenseValidated));
        assertTrue(filters.shouldBeInReport(licenseUnvalidated));
    }

    @Test
    public void checkIfADependencyShouldBeInTheReport() throws UnknownHostException {
        final FiltersHolder filters = new FiltersHolder();

        final DbOrganization organization = new DbOrganization();
        organization.setName("corp");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        filters.setCorporateFilter(new CorporateFilter(organization));

        assertFalse(filters.shouldBeInReport((DbDependency) null));
        assertFalse(filters.shouldBeInReport(new DbDependency("", "", Scope.COMPILE)));
        assertFalse(filters.shouldBeInReport(new DbDependency("", "org.apache:lambda:1:", Scope.COMPILE)));
        assertFalse(filters.shouldBeInReport(new DbDependency("test:1", "org.apache:lambda:1:", Scope.COMPILE)));
        assertTrue(filters.shouldBeInReport(new DbDependency("test:1", GrapesTestUtils.CORPORATE_GROUPID_4TEST + ":1:", Scope.COMPILE)));
        assertTrue(filters.shouldBeInReport(new DbDependency("test:1", GrapesTestUtils.CORPORATE_GROUPID_4TEST + ":1:", Scope.PROVIDED)));
        assertFalse(filters.shouldBeInReport(new DbDependency("test:1", GrapesTestUtils.CORPORATE_GROUPID_4TEST + ":1:", Scope.RUNTIME)));
        assertFalse(filters.shouldBeInReport(new DbDependency("test:1", GrapesTestUtils.CORPORATE_GROUPID_4TEST + ":1:", Scope.TEST)));



        filters.getDecorator().setShowThirdparty(true);
        assertTrue(filters.shouldBeInReport(new DbDependency("test:1", "org.apache:lambda:1:", Scope.COMPILE)));
        filters.getScopeHandler().setScopeComp(false);
        assertFalse(filters.shouldBeInReport(new DbDependency("test:1", "org.apache:lambda:1:", Scope.COMPILE)));
        filters.getScopeHandler().setScopePro(false);
        assertFalse(filters.shouldBeInReport(new DbDependency("test:1", "org.apache:lambda:1:", Scope.PROVIDED)));
        filters.getScopeHandler().setScopeRun(true);
        assertTrue(filters.shouldBeInReport(new DbDependency("test:1", "org.apache:lambda:1:", Scope.RUNTIME)));
        filters.getScopeHandler().setScopeTest(true);
        assertTrue(filters.shouldBeInReport(new DbDependency("test:1", "org.apache:lambda:1:", Scope.TEST)));
    }

    @Test
    public void checkIfShouldGoDeeper(){
        final FiltersHolder filters = new FiltersHolder();
        assertTrue(filters.getDepthHandler().shouldGoDeeper(0));
        assertFalse(filters.getDepthHandler().shouldGoDeeper(1));

        filters.getDepthHandler().setDepth(2);
        assertTrue(filters.getDepthHandler().shouldGoDeeper(1));
        assertFalse(filters.getDepthHandler().shouldGoDeeper(2));

        filters.getDepthHandler().setFullRecursive(true);
        assertTrue(filters.getDepthHandler().shouldGoDeeper(20));

    }
}
