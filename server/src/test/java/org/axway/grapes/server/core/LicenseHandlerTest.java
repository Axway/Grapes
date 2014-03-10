package org.axway.grapes.server.core;


import org.axway.grapes.server.db.datamodel.DbLicense;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class LicenseHandlerTest {

    @Test
    public void resolveLicense(){
        final DbLicense license = new DbLicense();
        license.setName("Test");
        license.setRegexp("\\w*");

        final List<DbLicense> licenses = new ArrayList<DbLicense>();
        licenses.add(license);

        final LicenseHandler licenseHandler = new LicenseHandler();
        licenseHandler.update(licenses);


        assertEquals(license, licenseHandler.resolve(license.getName()));
    }

    @Test
    public void ifLicenseDoesNotHaveRegexpItUsesLicenseName(){
        final DbLicense license = new DbLicense();
        license.setName("Test");

        final List<DbLicense> licenses = new ArrayList<DbLicense>();
        licenses.add(license);

        final LicenseHandler licenseHandler = new LicenseHandler();
        licenseHandler.update(licenses);


        assertEquals(license, licenseHandler.resolve(license.getName()));
        assertEquals(null, licenseHandler.resolve("Test2"));
    }

    @Test
    public void doesNotFailEvenWithWrongPattern(){
        final DbLicense license = new DbLicense();
        license.setName("Test");
        license.setRegexp("x^[");

        final List<DbLicense> licenses = new ArrayList<DbLicense>();
        licenses.add(license);

        final LicenseHandler licenseHandler = new LicenseHandler();
        licenseHandler.update(licenses);

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
    public void test(){
        System.out.println("The Apache Software License, Version 2.0".matches("^(ASL|Apache|The Apache Software License)(.*)(2\\.0|2)"));
    }
}
