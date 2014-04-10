package org.axway.grapes.server.core;


import org.axway.grapes.server.db.datamodel.DbLicense;
import org.junit.Test;

import java.util.Collections;

import static junit.framework.TestCase.assertEquals;

public class LicenseHandlerTest {

    @Test
    public void resolveLicense(){
        final DbLicense license = new DbLicense();
        license.setName("Test");
        license.setRegexp("\\w*");

        final LicenseHandler licenseHandler = new LicenseHandler();
        licenseHandler.update(Collections.singletonList(license));


        assertEquals(license, licenseHandler.resolve(license.getName()));
    }

    @Test
    public void ifLicenseDoesNotHaveRegexpItUsesLicenseName(){
        final DbLicense license = new DbLicense();
        license.setName("Test");

        final LicenseHandler licenseHandler = new LicenseHandler();
        licenseHandler.update(Collections.singletonList(license));


        assertEquals(license, licenseHandler.resolve(license.getName()));
        assertEquals(null, licenseHandler.resolve("Test2"));
    }

    @Test
    public void doesNotFailEvenWithWrongPattern(){
        final DbLicense license = new DbLicense();
        license.setName("Test");
        license.setRegexp("x^[");

        final LicenseHandler licenseHandler = new LicenseHandler();
        licenseHandler.update(Collections.singletonList(license));

        Exception exception = null;
        DbLicense resolvedLicense = null;

        try{
            resolvedLicense = licenseHandler.resolve(license.getName());
        }
        catch (Exception e){
            exception = e;
        }

        assertEquals(null, exception);
        assertEquals(null, resolvedLicense);
    }

    @Test
    public void getAllTheAvailableLicenses(){
        final DbLicense license = new DbLicense();
        license.setName("Test");

        final LicenseHandler licenseHandler = new LicenseHandler();
        licenseHandler.update(Collections.singletonList(license));


        assertEquals(1, licenseHandler.getLicenses().size());
    }
}
