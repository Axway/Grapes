package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LicenseIdFilterTest {

    @Test
    public void approveNull(){
        LicenseIdFilter filter = new LicenseIdFilter("test");
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact artifactWithLicense = new DbArtifact();
        final DbLicense license = new DbLicense();
        license.setName("licenseName");
        artifactWithLicense.addLicense(license);

        final DbArtifact artifactWithoutLicense = new DbArtifact();

        LicenseIdFilter filter = new LicenseIdFilter(license.getName());
        assertTrue(filter.filter(artifactWithLicense));
        assertFalse(filter.filter(artifactWithoutLicense));
    }

    @Test
    public void approveLicense(){
        final DbLicense license = new DbLicense();
        license.setName("licenseName");
        final DbLicense license2 = new DbLicense();
        license.setName("licenseName2");

        final DbArtifact artifactWithoutLicense = new DbArtifact();

        LicenseIdFilter filter = new LicenseIdFilter(license.getName());
        assertTrue(filter.filter(license));
        assertFalse(filter.filter(license2));
    }

}
