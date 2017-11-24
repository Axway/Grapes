package org.axway.grapes.server.db.datamodel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DBLicenseTest {

    private DbArtifact sut = new DbArtifact();

    @Rule
    public ExpectedException exc = ExpectedException.none();

    @Test
    public void testAddNullLicenseIsPrevented_1() {
        sut.addLicense((String)null);
        assertEquals(0, sut.getLicenses().size());
    }

    @Test
    public void testAddNullLicenseIsPrevented_2() {
        sut.addLicense((DbLicense) null);
        assertEquals(0, sut.getLicenses().size());
    }

    @Test
    public void testAddNullNamedLicense() {
        DbLicense lic = new DbLicense();
        lic.setName(null);
        sut.addLicense(lic);
        assertEquals(0, sut.getLicenses().size());
    }

    @Test
    public void setLicensesPreventsAddingNullLicenses() {
        final List<String> licenses = Arrays.asList("one", null, "three", null, "five");
        sut.setLicenses(licenses);

        assertEquals(3, sut.getLicenses().size());
        assertFalse(sut.getLicenses().contains(null));
    }

    @Test
    public void testGetLicensesIsNotMutable() {
        exc.expect(UnsupportedOperationException.class);
        sut.getLicenses().add("Some value");
    }

}
