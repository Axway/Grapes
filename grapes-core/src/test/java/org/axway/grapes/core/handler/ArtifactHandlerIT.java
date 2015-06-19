package org.axway.grapes.core.handler;

import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.options.filters.ArtifactIdFilter;
import org.axway.grapes.core.options.filters.Filter;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.LicenseService;
import org.axway.grapes.core.service.ModuleService;
import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.axway.grapes.model.datamodel.Scope;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static junit.framework.TestCase.assertNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class ArtifactHandlerIT extends WisdomTest {

    @Inject
    ArtifactService artifactService;
    @Inject
    LicenseService licenseService;
    @Inject
    ModuleService moduleService;
    @Inject
    OrganizationService organizationService;

    @Inject
    @org.wisdom.test.parents.Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbArtifact)")
    Crud<Artifact, String> artifactStringCrud;

    @Inject
    @org.wisdom.test.parents.Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbLicense)")
    Crud<License, String> licenseStringCrud;

    @Inject
    @org.wisdom.test.parents.Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbModule)")
    Crud<Module, String> moduleStringCrud;

    @Inject
    @org.wisdom.test.parents.Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbOrganization)")
    Crud<Organization, String> organizationStringCrud;

    @Before
    public void clearDBCollection() {
        Iterable<Artifact> list = artifactStringCrud.findAll();
        artifactStringCrud.delete(list);

        Iterable<Module> modules = moduleStringCrud.findAll();
        moduleStringCrud.delete(modules);

        Iterable<License> licenses = licenseStringCrud.findAll();
        licenseStringCrud.delete(licenses);

        Iterable<Organization> organizations = organizationStringCrud.findAll();
        organizationStringCrud.delete(organizations);
    }


    @Test
    public void checkStoreArtifact() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("abc");
        artifactService.store(artifact);
        Artifact artifact1 = artifactService.getArtifact(artifact.getGavc());
        assertThat(artifact1).isNotNull();
        assertThat(artifact1.getArtifactId()).isEqualTo("abc");
        assertThat(artifact1.getGavc()).isEqualTo(artifact.getGavc());
    }

    @Test
    public void addAnExistingLicenseToAnArtifactThatDoesNotHoldAnyLicenseYet() {
        final License license = new License();
        license.setName("testLicense");
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("abcd");
        licenseService.store(license);
        artifactService.store(artifact);
        License license1 = licenseService.getLicense("testLicense");
        Artifact artifact1 = artifactService.getArtifact(artifact.getGavc());
        assertThat(artifact1.getArtifactId()).isEqualTo("abcd");
        assertThat(artifact1.getGavc()).isEqualTo(artifact.getGavc());
        assertThat(license.getName()).isEqualTo(license1.getName());
        artifactService.addLicense(artifact.getGavc(), license.getName());
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).contains(license.getName());
    }

    @Test
    public void addAnExistingLicenseToAnArtifactThatHoldsAnOtherLicense() {
        final License license = new License();
        license.setName("testLicense");
        licenseService.store(license);
        Artifact artifact = new Artifact();
        artifact.setArtifactId("abcde");
        artifactService.store(artifact);
        //doesnt add duplicate
        artifactService.addLicense(artifact.getGavc(), license.getName());
        artifact = artifactService.getArtifact(artifact.getGavc());
        artifact.addLicense("AnotherLicense");
        artifactService.store(artifact);
        //because it doesnt exsist in the database
        artifactService.addLicense(artifact.getGavc(), "testLicense3");
        Artifact artifact1 = artifactService.getArtifact(artifact.getGavc());
        assertThat(artifact1.getLicenses().size()).isEqualTo(2);
        assertThat(artifact1.getLicenses()).contains("testLicense");
        assertThat(artifact1.getLicenses()).doesNotContain("testLicense3");
        assertThat(artifact1.getLicenses()).contains("AnotherLicense");
    }

    @Test
    public void addANoneExistingLicenseToAnArtifactThatDoesNotHoldAnyLicenseYet() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("1234");
        artifactService.store(artifact);
        artifactService.addLicense(artifact.getGavc(), "testLicense");
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses().size()).isEqualTo(1);
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).contains("testLicense");
    }

    @Test
    public void addANoneExistingLicenseToAnArtifactThatAlreadyHoldALicense() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("12345");
        artifact.addLicense("Test License");
        artifactService.store(artifact);
        artifactService.addLicense(artifact.getGavc(), "new license");
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses().size()).isEqualTo(1);
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).contains("Test License");
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).doesNotContain("new license");
    }

    @Test
    public void addAnExistingLicenseToAnArtifactThatAlreadyHoldTheLicense() {
        final License license = new License();
        license.setName("testLicense");
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test1");
        artifact.addLicense(license.getName());
        artifactService.store(artifact);
        artifactService.addLicense(artifact.getGavc(), license.getName());
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses().size()).isEqualTo(1);
    }

    @Test(expected = NoSuchElementException.class)
    public void addAnExistingLicenseToAnArtifactThatDoesNotExist() {

        final License license = new License();
        license.setName("testLicense");
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test2");
        artifactService.addLicense(artifact.getGavc(), license.getName());
    }

    @Test
    public void checkGetGavcs() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("testgavcs");
        artifact.setVersion("1.0.0-SNAPSHOT");
        String gavc1 = artifact.getGavc();
        artifactService.store(artifact);
        artifact.setVersion("2.0.0-SNAPSHOT");
        String gavc2 = artifact.getGavc();
        artifactService.store(artifact);
        artifact.setVersion("3.0.0");
        String gavc3 = artifact.getGavc();
        artifactService.store(artifact);
        FiltersHolder filtersHolder = new FiltersHolder();
        Filter filter = new ArtifactIdFilter("testgavcs");
        filter.artifactFilterFields();
        filtersHolder.addFilter(filter);
        List<String> list = artifactService.getArtifactGavcs(filtersHolder);
        assertThat(list).contains(gavc1);
        assertThat(list).contains(gavc2);
        assertThat(list).contains(gavc3);
    }

    @Test
    public void checkGetGroupIds() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("testgroupids");
        artifact.setGroupId("org.test");
        String groupid1 = artifact.getGroupId();
        artifactService.store(artifact);
        artifact.setGroupId("org.me");
        String groupid2 = artifact.getGroupId();
        artifactService.store(artifact);
        artifact.setGroupId("org.other");
        String groupid3 = artifact.getGroupId();
        artifactService.store(artifact);
        FiltersHolder filtersHolder = new FiltersHolder();
        Filter filter = new ArtifactIdFilter("testgroupids");
        filter.artifactFilterFields();
        filtersHolder.addFilter(filter);
        List<String> list = artifactService.getArtifactGroupIds(filtersHolder);
        assertThat(list).contains(groupid1);
        assertThat(list).contains(groupid2);
        assertThat(list).contains(groupid3);
    }

    @Test
    public void checkAvailableVersionsOfAnExistingArtifact() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test5");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        artifact.setVersion("1.0.1");
        artifactService.store(artifact);
        final List<String> versions = artifactService.getArtifactVersions(artifact.getGavc());
        assertThat(versions.size()).isEqualTo(2);
        assertThat(versions).contains("1.0.0-SNAPSHOT");
        assertThat(versions).contains("1.0.1");
    }

    @Test(expected = NoSuchElementException.class)
    public void checkAvailableVersionsOfAnArtifactThatDoesNotExist() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test3");
        artifact.setVersion("1.0.0-SNAPSHOT");
        final List<String> versions = artifactService.getArtifactVersions(artifact.getGavc());
    }

    @Test
    public void getTheLastVersionOfAnExistingArtifactThatHaveComparableVersions() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test6");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        artifact.setVersion("2.0.0-SNAPSHOT");
        artifactService.store(artifact);
        artifact.setVersion("3.0.0");
        artifactService.store(artifact);
        final String lastVersion = artifactService.getArtifactLastVersion(artifact.getGavc());
        assertThat("3.0.0").isEqualTo(lastVersion);
    }

    @Test
    public void getTheLastVersionOfAnExistingArtifactThatHaveVersionsThatCannotBeComparedWithVersionHandler() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("AAAAA");
        artifactService.store(artifact);
        artifact.setVersion("ZZZZZ");
        artifactService.store(artifact);
        artifact.setVersion("EEEEE");
        artifactService.store(artifact);
        final String lastVersion = artifactService.getArtifactLastVersion(artifact.getGavc());
        assertThat("ZZZZZ").isEqualTo(lastVersion);
    }

    @Test(expected = NoSuchElementException.class)
    public void getTheLastVersionOfAnArtifactThatDoesNotExist() {
        artifactService.getArtifactLastVersion("doesNotExist");
    }

    @Test
    public void getAnExistingArtifact() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        final Artifact gotArtifact = artifactService.getArtifact(artifact.getGavc());
        assertThat(gotArtifact).isNotNull();
        assertThat(artifact.getArtifactId()).isEqualTo(gotArtifact.getArtifactId());
        assertThat(artifact.getVersion()).isEqualTo(gotArtifact.getVersion());
    }

    @Test(expected = NoSuchElementException.class)
    public void getAnArtifactThatDoesNotExist() {
        artifactService.getArtifact("doesNotExist");
    }

    @Test
    public void getTheOrganizationOfAnArtifactThatDoesNotHaveModule() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        final Organization organization = artifactService.getOrganization(artifact);
        assertThat(organization).isNull();
    }

    @Test
    public void getTheOrganizationOfAnArtifactWhichIsInAModuleThatDoesNotHaveOrganization() {
        Set<String> artlist = new HashSet<>();
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);

        artlist.add(artifact.getGavc());
        final Module module = new Module();
        module.setName("testmodule");
        module.setArtifacts(artlist);
        moduleService.store(module);
        final Organization organization = artifactService.getOrganization(artifact);
        assertNull(organization);
    }

    @Test
    public void getTheOrganizationOfAnArtifactWhichIsInAModuleThatHasAnOrganization() {
        Organization organization = new Organization();
        organization.setName("Test Organization");
        organizationService.store(organization);
        Set<String> artlist = new HashSet<>();

        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);

        artlist.add(artifact.getGavc());
        final Module module = new Module();
        module.setName("testmodule");
        module.setVersion("1.0.0");
        module.setArtifacts(artlist);
        module.setOrganization(organization.getName());
        moduleService.store(module);
        final Organization gotOrganization = artifactService.getOrganization(artifact);
        assertThat(gotOrganization).isNotNull();
        System.out.println(organization.getName());
        System.out.println(gotOrganization.getName());
        assertThat(organization.getName()).isEqualTo(gotOrganization.getName());
    }

    @Test
    public void updateTheProviderOfAnExistingArtifact() {
        Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        artifact = artifactService.getArtifact(artifact.getGavc());
        artifactService.updateProvider(artifact.getGavc(), "me");
        assertThat(artifactService.getArtifact(artifact.getGavc()).getProvider()).isEqualTo("me");
    }

    @Test(expected = NoSuchElementException.class)
    public void updateTheProviderOfAnArtifactThatDoesNotExist() {
            artifactService.updateProvider("doesNotExist", "me");
       }

    @Test
    public void updateTheDownloadUrlOfAnExistingArtifact() {
        Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        artifact = artifactService.getArtifact(artifact.getGavc());
        artifactService.updateDownLoadUrl(artifact.getGavc(), "http://download.url");
        assertThat(artifactService.getArtifact(artifact.getGavc()).getDownloadUrl()).isEqualTo("http://download.url");
    }

    @Test(expected = NoSuchElementException.class)
    public void updateTheDownloadUrlOfAnArtifactThatDoesNotExist() {
            artifactService.updateDownLoadUrl("doesNotExist", "http://download.url");
       }

    @Test
    public void deleteAnArtifact() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);

        artifactService.deleteArtifact(artifact.getGavc());
       assertThatThrownBy(()->artifactService.getArtifact(artifact.getGavc())).isInstanceOf(NoSuchElementException.class);
    }

    @Test (expected = NoSuchElementException.class)
    public void deleteAnArtifactThatDoesNotExist() {
            artifactService.deleteArtifact("doesNotExist");
        }

    @Test
    public void updateDoNotUseFlagOfAnExistingArtifact() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);

        artifactService.updateDoNotUse(artifact.getGavc(), true);
        assertThat(artifactService.getArtifact(artifact.getGavc()).getDoNotUse()).isTrue();
    }

    @Test(expected = NoSuchElementException.class)
    public void updateDoNotUseFlagOfAnArtifactThatDoesNotExist() {
            artifactService.updateDoNotUse("doesNotExit", true);
    }

    @Test
    //todo seems that it only finds dependencies
    public void getAncestorsOfAnExistingArtifact() {

        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        final Module module = new Module();
        module.setName("testmodule");
        module.setVersion("1.0.0");
       // module.addArtifact(artifact);
        module.addDependency(artifact, Scope.COMPILE);
        module.updateHasAndUse();
        moduleService.store(module);
        final FiltersHolder filters = mock(FiltersHolder.class);

        List<Module> list =artifactService.getAncestors(artifact.getGavc(), filters);
        assertThat(list).isNotEmpty();
    }

    @Test(expected = NoSuchElementException.class)
    public void getAncestorsOfAnArtifactThatDoesNotExist() {
            artifactService.getAncestors("doesNotExist", mock(FiltersHolder.class));
       }

    @Test
    public void addALicenseToAnArtifact() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        final License license = new License();
        license.setName("licenseTest");
        licenseService.store(license);

        artifactService.addLicenseToArtifact(artifact.getGavc(), license.getName());
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).contains(license.getName());
    }

    @Test
    public void addALicenseToAnArtifactThatAlreadyHasThisLicense() {
        final License license = new License();
        license.setName("licenseTest");
        licenseService.store(license);
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifact.addLicense(license.getName());
        artifactService.store(artifact);

        artifactService.addLicenseToArtifact(artifact.getGavc(), license.getName());
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).hasSize(1);
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).contains(license.getName());
    }

    @Test(expected = NoSuchElementException.class)
    public void addALicenseToAnArtifactThatDoesNotExist() {
            artifactService.addLicenseToArtifact("doesNotExist", "licences");
       }

    @Test
    public void addALicenseThatDoesNotExistToArtifact() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
            artifactService.addLicenseToArtifact(artifact.getGavc(), "doesNotExist");
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).contains("doesNotExist");
        }

    @Test
    public void getAllLicenses() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        artifactService.addLicenseToArtifact(artifact.getGavc(), "doesNotExist");
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).contains("doesNotExist");
        List<License> list = artifactService.getArtifactLicenses(artifact.getGavc(), new FiltersHolder());
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getName()).isEqualTo("doesNotExist");
    }

    @Test
    public void removeALicenseFromAnArtifact() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifact.addLicense("licenseTest");
        artifactService.store(artifact);
        artifactService.removeLicenseFromArtifact(artifact.getGavc(), "licenseTest");
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).doesNotContain("licenseTest");
    }

    @Test
    public void removeALicenseAnArtifactThatDoesNotHaveThisLicense() {
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        artifactService.removeLicenseFromArtifact(artifact.getGavc(), "licenseTest");
        assertThat(artifactService.getArtifact(artifact.getGavc()).getLicenses()).doesNotContain("licenseTest");

    }

    @Test(expected = NoSuchElementException.class)
    public void removeALicenseFromAnArtifactThatDoeNotExist() {
            artifactService.addLicenseToArtifact("doesNotExist", "doesNotExist");
       }

    @Test
    public void checkGetAllArtifact() {
        final FiltersHolder filtersHolder = mock(FiltersHolder.class);
        final Artifact artifact = new Artifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifactService.store(artifact);
        List<Artifact> list = artifactService.getArtifacts(filtersHolder);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getArtifactId()).isEqualTo("test");
    }

    @Test
    public  void getGavcsBasedOnGrouId(){

    }
}
