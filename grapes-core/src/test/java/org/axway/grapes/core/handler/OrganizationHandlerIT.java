package org.axway.grapes.core.handler;

import org.axway.grapes.core.GrapesTestUtils;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.ModuleService;
import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by jennifer on 5/7/15.
 */
public class OrganizationHandlerIT extends WisdomTest {

    @Inject
    OrganizationService organizationService;
    @Inject
    ModuleService moduleService;
    @Inject
    ArtifactService artifactService;
    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbOrganization)")
    Crud<Organization, String> organizationStringCrud;
    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbModule)")
    Crud<Module, String> moduleStringCrud;

    @Before
    public void clearDBCollection() {
        Iterable<Organization> list = organizationStringCrud.findAll();
        organizationStringCrud.delete(list);
        Iterable<Module> list2 = moduleStringCrud.findAll();
        moduleStringCrud.delete(list2);
    }

    @Test
    public void saveNewAndRetrieve() {
        Organization organization = new Organization();
        organization.setName("test");
        organizationService.store(organization);
        Organization orgFromDb = organizationService.getOrganization("test");
        assertThat(orgFromDb).isNotNull();
        assertThat(orgFromDb.getName()).isEqualTo("test");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).isEmpty();
    }

    @Test
    public void getAllOrganizationNames() {
        Organization organization = new Organization();
        organization.setName("test");
        organizationService.store(organization);
        List<String> namesList = organizationService.getOrganizationNames();
        assertThat(namesList).isNotEmpty();
        assertThat(namesList).contains("test");
    }

    @Test
    public void getAnOrganizationThatDoesNotExist() {
        assertThatThrownBy(() -> organizationService.getOrganization("doesNotExist")).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void deleteAnExistingOrganization() {
        Organization organization = new Organization();
        organization.setName("org.testcorp");
        organizationService.store(organization);
        Module module = new Module();
        module.setOrganization(organization.getName());
        module.setName("module1");
        moduleService.store(module);
        organization = organizationService.getOrganization("org.testcorp");
        module = moduleService.getModule(module.getId());
        assertThat(module).isNotNull();
        assertThat(organization).isNotNull();
        organizationService.deleteOrganization("org.testcorp");
        assertThatThrownBy(() -> organizationService.getOrganization("org.testcorp")).isInstanceOf(NoSuchElementException.class);
        assertThat(moduleService.getModule(module.getId()).getOrganization()).isEmpty();
    }

    @Test(expected = NoSuchElementException.class)
    public void deleteAnOrganizationThatDoesNotExist() {
        organizationService.deleteOrganization("doesNotExist");
    }

    @Test
    public void addCorporateGroupId() {
        final Organization organization = new Organization();
        organization.setName("organization1");
        organization.getCorporateGroupIdPrefixes().add("org.test");
        organizationService.store(organization);
        Organization orgFromDb = organizationService.getOrganization("organization1");
        organizationService.addCorporateGroupId(orgFromDb.getName(), "org.test2");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).contains("org.test");
        assertThat(organizationService.getOrganization("organization1").getCorporateGroupIdPrefixes().contains("org.test2"));
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).hasSize(1);
    }

    @Test
    /*todo arraylist allow duplicates solutions use a set instead,
    or use an arraylist but the get and set use a set which converts it to and from the arraylist.*/
    public void addCorporateGroupIdThatAlreadyExist() {
        final Organization organization = new Organization();
        organization.setName("organization1");
        organization.getCorporateGroupIdPrefixes().add("org.test");
        organizationService.store(organization);
        Organization orgFromDb = organizationService.getOrganization("organization1");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).contains("org.test");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).hasSize(1);
        organizationService.addCorporateGroupId("organization1", "org.test");
        Organization orgFromDb2 = organizationService.getOrganization("organization1");
        assertThat(orgFromDb2.getCorporateGroupIdPrefixes()).contains("org.test");
        assertThat(orgFromDb2.getCorporateGroupIdPrefixes()).hasSize(1);
    }

    @Test
    public void removeCorporateGroupId() {
        final Organization organization = new Organization();
        organization.setName("organization1");
        organization.getCorporateGroupIdPrefixes().add("org.test");
        organizationService.store(organization);
        organizationService.removeCorporateGroupId("organization1", "org.test");
        Organization orgFromDb = organizationService.getOrganization("organization1");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).doesNotContain("org.test");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).hasSize(0);
    }

    @Test
    public void removeCorporateGroupIdThatDoesNotExist() {
        final Organization organization = new Organization();
        organization.setName("organization1");
        organization.getCorporateGroupIdPrefixes().add("org.test");
        organizationService.store(organization);
        organizationService.removeCorporateGroupId(organization.getName(), "com.test");
        assertThat(organization.getName()).isEqualTo("organization1");
        assertThat((organizationService.getOrganization(organization.getName())).getCorporateGroupIdPrefixes()).hasSize(1);
    }

    @Test
    public void removeOrgFromModules() {
        Organization organization = new Organization();
        organization.setName("org.testcorp");
        organizationService.store(organization);
        Module module = new Module();
        module.setOrganization(organization.getName());
        module.setName("module1");
        moduleService.store(module);
        Module module1 = new Module();
        module1.setOrganization(organization.getName());
        module1.setName("module2");
        moduleService.store(module1);
        organization = organizationService.getOrganization("org.testcorp");
        module = moduleService.getModule(module.getId());
        module1 = moduleService.getModule(module1.getId());
        assertThat(module).isNotNull();
        assertThat(organization).isNotNull();
        assertThat(moduleService.getModule(module.getId()).getOrganization()).isEqualTo("org.testcorp");
        assertThat(moduleService.getModule(module1.getId()).getOrganization()).isEqualTo("org.testcorp");
        organizationService.removeModulesOrganization(organization);
        assertThat(moduleService.getModule(module.getId()).getOrganization()).isEmpty();
        assertThat(moduleService.getModule(module1.getId()).getOrganization()).isEmpty();
    }

    @Test
    //todo double check logic of method in handler
    public void removeOrgFromModulesPrefix() {
        Artifact artifact = new Artifact();
        artifact.setArtifactId("testart");
        artifact.setGroupId("org.test");

        Organization organization = new Organization();
        organization.setName("org.testcorp");
        organizationService.store(organization);

        Module module = new Module();
        module.setOrganization(organization.getName());
        module.setName("module1");
        module.addArtifact(artifact);
        moduleService.store(module);

        Module module1 = new Module();
        module1.setOrganization(organization.getName());
        module1.setName("module2");
        //todo if this is "morg.test it is found and deleted but should not be?
        artifact.setGroupId("me.test");
        module1.addArtifact(artifact);
        moduleService.store(module1);

        organization = organizationService.getOrganization("org.testcorp");
        module = moduleService.getModule(module.getId());
        module1 = moduleService.getModule(module1.getId());
        assertThat(module).isNotNull();
        assertThat(organization).isNotNull();
        assertThat(moduleService.getModule(module.getId()).getOrganization()).isEqualTo("org.testcorp");
        assertThat(moduleService.getModule(module1.getId()).getOrganization()).isEqualTo("org.testcorp");
        organizationService.removeModulesOrganization("org", organization);
        assertThat(moduleService.getModule(module.getId()).getOrganization()).isEmpty();
        assertThat(moduleService.getModule(module1.getId()).getOrganization()).isNotEmpty();
    }

    @Test
    public void getMatchingOrganizationOfAModuleWhenIsAlreadyOne() {
        final Organization organization = new Organization();
        organization.setName("test");
        organizationService.store(organization);
        final Module module = new Module();
        module.setName("testmodule");
        module.setOrganization(organization.getName());
        moduleService.store(module);
        final Organization gotOrganization = organizationService.getMatchingOrganization(module);
        assertThat(organization.getName()).isEqualTo(gotOrganization.getName());
    }

   // @Test
    //todo super messy methods tinkering
    //idea is if the module doesnt have an org check its artifacts and sub modules and still one of thoses.
    public void getMatchingOrganizationOfAModule() {
        List<String> cidslist = new ArrayList<>();
        cidslist.add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final Organization organization = new Organization();
        organization.setName(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        organization.setCorporateGroupIdPrefixes(cidslist);
        organizationService.store(organization);
        final Module module = new Module();
        module.setName("matchingmod");
        final Artifact artifact = new Artifact();
        artifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        module.addArtifact(artifact);//todo should this set the modules organization?
        moduleService.store(module);
artifactService.store(artifact);

        final Organization gotOrganization = organizationService.getMatchingOrganization(module);

        assertThat(GrapesTestUtils.CORPORATE_GROUPID_4TEST).isEqualTo( gotOrganization.getName());
    }

    @Test
    public void getMatchingOrganizationOfAModuleWhenThereIsNone() {
        final Module module = new Module();
        final Artifact artifact = new Artifact();
        artifact.setGroupId("unknown.production");
        artifactService.store(artifact);
        module.addArtifact(artifact);
        module.setName("mod2");
        moduleService.store(module);

     Organization organization= organizationService.getMatchingOrganization(new Module());
       assertThat(organization).isNull();
    }
}
