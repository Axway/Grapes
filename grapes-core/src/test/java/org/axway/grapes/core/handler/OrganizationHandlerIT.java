package org.axway.grapes.core.handler;

import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.junit.Assert;
import org.junit.Test;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jennifer on 5/7/15.
 */
public class OrganizationHandlerIT extends WisdomTest {

    @Inject
    OrganizationService organizationService;


    @Test
    public void saveNewAndRetrieve(){
        Organization organization= new Organization();
        organization.setName("test");
        organizationService.store(organization);
        Organization orgFromDb = organizationService.getOrganization("test");
        assertThat(orgFromDb).isNotNull();
        assertThat(orgFromDb.getName()).isEqualTo("test");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).isEmpty();

    }


    @Test
    public void getAllOrganizationNames(){

        Organization organization = new Organization();
        organization.setName("test");
        organizationService.store(organization);
        List<String> namesList = organizationService.getOrganizationNames();
        assertThat(namesList).isNotEmpty();
        assertThat(namesList).contains("test");


    }



    @Test
    public void getAnOrganizationThatDoesNotExist() {
       Organization organization= organizationService.getOrganization("doesNotExist");
        //todo should throw an exception and not just null?
        assertNull(organization);
    }

    @Test
    public void deleteAnExistingOrganization(){
        Organization organization = new Organization();
        organization.setName("org.testcorp");
        organizationService.store(organization);
        organization = organizationService.getOrganization("org.testcorp");
        assertThat(organization).isNotNull();
        organizationService.deleteOrganization("org.testcorp");
        organization = organizationService.getOrganization("org.testcorp");
        assertThat(organization).isNull();
        //todo it should assert that they were deleted from modules as well


    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteAnOrganizationThatDoesNotExist(){
       organizationService.deleteOrganization("doesNotExist");
   }

    @Test
    public void addCorporateGroupId(){
        final Organization organization= new Organization();
        organization.setName("organization1");
        organization.getCorporateGroupIdPrefixes().add("org.test");


        organizationService.store(organization);
        Organization orgFromDb = organizationService.getOrganization("organization1");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).contains("org.test");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).hasSize(1);

    }

    @Test
    /*todo arraylist allow duplicates solutions use a set instead,
    or use an arraylist but the get and set use a set which converts it to and from the arraylist.*/
    public void addCorporateGroupIdThatAlreadyExist(){
        final Organization organization= new Organization();
        organization.setName("organization1");
        organization.getCorporateGroupIdPrefixes().add("org.test");


        organizationService.store(organization);
        Organization orgFromDb = organizationService.getOrganization("organization1");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).contains("org.test");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).hasSize(1);

        organizationService.addCorporateGroupId("organization1","org.test");
       // orgFromDb.getCorporateGroupIdPrefixes().add("org.test");
        //organizationService.store(orgFromDb);
        Organization orgFromDb2 = organizationService.getOrganization("organization1");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).contains("org.test");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).hasSize(1);

    }

    @Test
    public void removeCorporateGroupId(){
        final Organization organization= new Organization();
        organization.setName("organization1");
        organization.getCorporateGroupIdPrefixes().add("org.test");
        organizationService.store(organization);
        organizationService.removeCorporateGroupId("organization1","org.test");
        Organization orgFromDb = organizationService.getOrganization("organization1");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).doesNotContain("org.test");
        assertThat(orgFromDb.getCorporateGroupIdPrefixes()).hasSize(0);


    }

    @Test
    public void removeCorporateGroupIdThatDoesNotExist(){
        final Organization organization= new Organization();
        organization.setName("organization1");
        organization.getCorporateGroupIdPrefixes().add("org.test");
        organizationService.store(organization);
       organizationService.removeCorporateGroupId(organization.getName(), "com.test");
        assertThat(organization.getName()).isEqualTo("organization1");
        assertThat((organizationService.getOrganization(organization.getName())).getCorporateGroupIdPrefixes()).hasSize(1);

    }

    @Test
    //todo I have no idea what this does
    public void getMatchingOrganizationOfAModuleWhenIsAlreadyOne(){
//        final Organization organization = new Organization();
//        organization.setName("test");
//
//        final Module module = new Module();
//        module.setOrganization(organization.getName());
//
//
//        final Organization gotOrganization = organizationService.getMatchingOrganization(module);

       }
/*
    @Test
    public void getMatchingOrganizationOfAModule(){
        final Module module = new Module();
        final Artifact artifact = new Artifact();
        artifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        module.addArtifact(artifact);

        final OrganizationService repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final OrganizationHandler handler = new OrganizationHandler(repositoryHandler);

        final Organization gotOrganization = handler.getMatchingOrganization(module);

        verify(repositoryHandler, times(1)).getAllOrganizations();
        assertEquals(GrapesTestUtils.ORGANIZATION_NAME_4TEST, gotOrganization.getName());
    }

    @Test
    public void getMatchingOrganizationOfAModuleWhenThereIsNone(){
        final Module module = new Module();
        final Artifact artifact = new Artifact();
        artifact.setGroupId("unknown.production");
        module.addArtifact(artifact);

        final OrganizationService repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        final OrganizationHandler handler = new OrganizationHandler(repositoryHandler);

        final Organization organization = handler.getMatchingOrganization(new Module());

        assertNull(organization);
    }*/
}
