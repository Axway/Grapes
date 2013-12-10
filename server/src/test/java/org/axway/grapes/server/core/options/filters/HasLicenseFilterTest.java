package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HasLicenseFilterTest {

    @Test
    public void approveNull(){
        HasLicenseFilter filter = new HasLicenseFilter(true);
        assertFalse(filter.filter(null));

        filter = new HasLicenseFilter(false);
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact artifactWithLicense = new DbArtifact();
        final DbLicense license = new DbLicense();
        license.setName("licenseName");
        artifactWithLicense.addLicense(license);

        final DbArtifact artifactWithoutLicense = new DbArtifact();


        HasLicenseFilter filter = new HasLicenseFilter(true);
        assertTrue(filter.filter(artifactWithLicense));
        assertFalse(filter.filter(artifactWithoutLicense));

        filter = new HasLicenseFilter(false);
        assertFalse(filter.filter(artifactWithLicense));
        assertTrue(filter.filter(artifactWithoutLicense));
    }

}
