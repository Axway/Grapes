package org.axway.grapes.core.handler;

import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.LicenseService;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.License;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jennifer on 5/7/15.
 */
public class LicenseHandlerIT extends WisdomTest {

    @Inject
    LicenseService licenseService;
    @Inject
    ArtifactService artifactService;
    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbLicense)")
    Crud<License, String> licenseStringCrud;




    public void clearDBCollection(){
        Iterable<License> list = licenseStringCrud.findAll();
        licenseStringCrud.delete(list);

    }

    @Test
    public void saveNewAndRetrieve() {

        final License license = new License();
        license.setName("testlic");
        licenseService.store(license);
        License license1 = licenseService.getLicense("testlic");
        assertThat(license1).isNotNull();
        assertThat(license1.getName()).isEqualTo("testlic");
        assertThat(license1.getComments()).isEmpty();
    }

    @Test
    public void getAllLicenses() {
        final License license = new License();
        license.setName("testlic");
        licenseService.store(license);
        license.setName("testlic2");
        licenseService.store(license);
        List<License> namesList = licenseService.getLicenses();
        assertThat(namesList).isNotEmpty();
    }

    @Test
    public void getAnExistingLicense() {
        final License license = new License();
        license.setName("test");
        license.setLongName("Grapes Test License");
        licenseService.store(license);
        final License gotLicense = licenseService.getLicense(license.getName());
        assertNotNull(gotLicense);
        assertEquals(license.getName(), gotLicense.getName());
        assertEquals(license.getLongName(), gotLicense.getLongName());
    }

    @Test(expected = NoSuchElementException.class)
    public void getALicenseThatDoesNotExist() {
        licenseService.getLicense("doesNotExist");
    }

    @Test
    public void deleteAnExistingLicense() {
        final License license = new License();
        license.setName("test");
        license.setLongName("Grapes Test License");
        licenseService.store(license);
        final Artifact artifact = new Artifact();
        artifact.setGroupId("org.axway.grapes.test");
        artifact.setArtifactId("tested");
        artifact.setVersion("1.5.9");
        artifact.addLicense(license.getName());
        artifactService.store(artifact);
        //todo store artifact delete license from artifact retrieve artifact verfiy license is gone
        licenseService.deleteLicense(license.getName());
        assertThatThrownBy(() -> licenseService.getLicense(license.getName())).isInstanceOf(NoSuchElementException.class);
    }

    @Test(expected = NoSuchElementException.class)
    public void deleteALicenseThatDoesNotExist() {
        licenseService.deleteLicense("doesNotExist");
    }

    @Test
    public void approveALicense() {
        final License license = new License();
        license.setName("test");
        license.setLongName("Grapes Test License");
        licenseService.store(license);
        licenseService.approveLicense(license.getName(), true);
        License license1 = licenseService.getLicense(license.getName());
        assertThat(license1.isApproved()).isTrue();
        licenseService.approveLicense(license.getName(), false);
        license1 = licenseService.getLicense(license.getName());
        assertThat(license1.isApproved()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void approveALicenseThatDoesNotExist() {
        licenseService.approveLicense("doesNotExist", true);
    }

    @Test
    public void resolveLicense() {
        final License license = new License();
        license.setName("Test");
        license.setRegexp("\\w*");
        licenseService.store(license);
        assertEquals(license.getName(), licenseService.resolve(license.getName()).getName());
    }


    @Test
    public void ifLicenseDoesNotHaveRegexpItUsesLicenseName() {
        final License license = new License();
        license.setName("Test");
        licenseService.store(license);
        assertEquals(license.getName(), licenseService.resolve(license.getName()).getName());
        assertThat(licenseService.resolve("Test2")).isNull();
    }

    @Test
    public void doesNotFailEvenWithWrongPattern() {
        final License license = new License();
        license.setName("Test");
        license.setRegexp("x^[");
        licenseService.store(license);
        License resolvedLicense = licenseService.resolve(license.getName());
        assertThat(resolvedLicense).isNull();
    }


}
