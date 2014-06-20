package org.axway.grapes.server.core;


import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ArtifactHandlerTest {

    @Test
    public void checkStoreArtifact(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        final DbArtifact artifact = new DbArtifact();
        handler.store(artifact);

        verify(repositoryHandler, times(1)).store(artifact);
    }

    @Test
    public void checkStoreIfNewWithNewArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        handler.storeIfNew(artifact);

        verify(repositoryHandler, times(1)).store(artifact);
    }

    @Test
    public void checkStoreIfNewWithExistingArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        handler.storeIfNew(artifact);

        verify(repositoryHandler, never()).store(artifact);
    }

    @Test
    public void addAnExistingLicenseToAnArtifactThatDoesNotHoldAnyLicenseYet(){
        final DbLicense license = new DbLicense();
        license.setName("testLicense");

        final DbArtifact artifact = new DbArtifact();

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.addLicense(artifact.getGavc(), license.getName());

        verify(repositoryHandler, times(1)).addLicenseToArtifact(artifact, license.getName());
    }

    @Test
    public void addAnExistingLicenseToAnArtifactThatHoldsAnOtherLicense(){
        final DbLicense license = new DbLicense();
        license.setName("testLicense");

        final DbArtifact artifact = new DbArtifact();
        artifact.addLicense("AnotherLicense");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.addLicense(artifact.getGavc(), license.getName());

        verify(repositoryHandler, times(1)).addLicenseToArtifact(artifact, license.getName());
    }

    @Test
    public void addANoneExistingLicenseToAnArtifactThatDoesNotHoldAnyLicenseYet(){
        final DbArtifact artifact = new DbArtifact();

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.addLicense(artifact.getGavc(), "testLicense");

        verify(repositoryHandler, times(1)).addLicenseToArtifact(artifact, "testLicense");
    }

    @Test
    public void addANoneExistingLicenseToAnArtifactThatAlreadyHoldALicense(){
        final DbArtifact artifact = new DbArtifact();
        artifact.addLicense("Test License");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.addLicense(artifact.getGavc(), "testLicense");

        verify(repositoryHandler, never()).addLicenseToArtifact(artifact, "testLicense");
    }

    @Test
    public void addAnExistingLicenseToAnArtifactThatAlreadyHoldTheLicense(){
        final DbLicense license = new DbLicense();
        license.setName("testLicense");

        final DbArtifact artifact = new DbArtifact();
        artifact.addLicense(license.getName());

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.addLicense(artifact.getGavc(), license.getName());

        verify(repositoryHandler, never()).addLicenseToArtifact(artifact, license.getName());
    }

    @Test
    public void addAnExistingLicenseToAnArtifactThatDoesNotExist(){
        final DbLicense license = new DbLicense();
        license.setName("testLicense");

        final DbArtifact artifact = new DbArtifact();

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        WebApplicationException exception = null;

        try{
            handler.addLicense(artifact.getGavc(), license.getName());
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void checkGetGavcs(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.getArtifactGavcs(mock(FiltersHolder.class));

        verify(repositoryHandler, times(1)).getGavcs(any(FiltersHolder.class));
    }

    @Test
    public void checkGetGroupIds(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.getArtifactGroupIds(mock(FiltersHolder.class));

        verify(repositoryHandler, times(1)).getGroupIds(any(FiltersHolder.class));
    }

    @Test
    public void checkAvailableVersionsOfAnExistingArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getArtifactVersions(artifact)).thenReturn(Collections.singletonList(artifact.getVersion()));

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        final List<String> versions = handler.getArtifactVersions(artifact.getGavc());

        verify(repositoryHandler, times(1)).getArtifact(artifact.getGavc());
        verify(repositoryHandler, times(1)).getArtifactVersions(artifact);
        assertNotNull(versions);
        assertEquals(1, versions.size());
        assertEquals(artifact.getVersion(), versions.get(0));
    }

    @Test
    public void checkAvailableVersionsOfAnArtifactThatDoesNotExist(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        WebApplicationException exception = null;

        try {
            handler.getArtifactVersions(artifact.getGavc());
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void getTheLastVersionOfAnExistingArtifactThatHaveComparableVersions(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final List<String> versions = new ArrayList<String>();
        versions.add("1.0.0-SNAPSHOT");
        versions.add("2.0.0-SNAPSHOT");
        versions.add("3.0.0");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getArtifactVersions(artifact)).thenReturn(versions);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        final String lastVersion = handler.getArtifactLastVersion(artifact.getGavc());

        assertEquals("3.0.0", lastVersion);
    }

    @Test
    public void getTheLastVersionOfAnExistingArtifactThatHaveVersionsThatCannotBeComparedWithVersionHandler(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("AAAAA");

        final List<String> versions = new ArrayList<String>();
        versions.add("AAAAA");
        versions.add("ZZZZZ");
        versions.add("EEEEE");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getArtifactVersions(artifact)).thenReturn(versions);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        final String lastVersion = handler.getArtifactLastVersion(artifact.getGavc());

        assertEquals("ZZZZZ", lastVersion);
    }

    @Test
    public void getTheLastVersionOfAnArtifactThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        WebApplicationException exception = null;

        try{
            handler.getArtifactLastVersion("doesNotExist");
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void getAnExistingArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        final DbArtifact gotArtifact = handler.getArtifact(artifact.getGavc());

        assertNotNull(gotArtifact);
        assertEquals(artifact, gotArtifact);
        verify(repositoryHandler, times(1)).getArtifact(artifact.getGavc());
    }

    @Test
    public void getAnArtifactThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        WebApplicationException exception = null;

        try {
            handler.getArtifact("doesNotExist");
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void getTheOrganizationOfAnArtifactThatDoesNotHaveModule(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        final DbOrganization organization = handler.getOrganization(artifact);

        assertNull(organization);
    }

    @Test
    public void getTheOrganizationOfAnArtifactWhichIsInAModuleThatDoesNotHaveOrganization(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getRootModuleOf(artifact.getGavc())).thenReturn(new DbModule());
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        final DbOrganization organization = handler.getOrganization(artifact);

        assertNull(organization);
    }

    @Test
    public void getTheOrganizationOfAnArtifactWhichIsInAModuleThatHasAnOrganization(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final DbOrganization organization = new DbOrganization();
        organization.setName("Test Organization");

        final DbModule module = new DbModule();
        module.setOrganization(organization.getName());

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getRootModuleOf(artifact.getGavc())).thenReturn(module);
        when(repositoryHandler.getOrganization(organization.getName())).thenReturn(organization);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        final DbOrganization gotOrganization = handler.getOrganization(artifact);

        assertNotNull(gotOrganization);
        assertEquals(organization, gotOrganization);
    }

    @Test
    public void updateTheProviderOfAnExistingArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.updateProvider(artifact.getGavc(), "me");

        verify(repositoryHandler, times(1)).updateProvider(artifact, "me");
    }

    @Test
    public void updateTheProviderOfAnArtifactThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        WebApplicationException exception = null;

        try {
            handler.updateProvider("doesNotExist", "me");
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void updateTheDownloadUrlOfAnExistingArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.updateDownLoadUrl(artifact.getGavc(), "http://download.url");

        verify(repositoryHandler, times(1)).updateDownloadUrl(artifact, "http://download.url");
    }

    @Test
    public void updateTheDownloadUrlOfAnArtifactThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        WebApplicationException exception = null;

        try {
            handler.updateDownLoadUrl("doesNotExist", "http://download.url");
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void deleteAnArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.deleteArtifact(artifact.getGavc());

        verify(repositoryHandler, times(1)).deleteArtifact(artifact.getGavc());
    }

    @Test
    public void deleteAnArtifactThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        WebApplicationException exception = null;

        try {
            handler.deleteArtifact("doesNotExist");
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void updateDoNotUseFlagOfAnExistingArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.updateDoNotUse(artifact.getGavc(), true);

        verify(repositoryHandler, times(1)).updateDoNotUse(artifact, true);
    }

    @Test
    public void updateDoNotUseFlagOfAnArtifactThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        WebApplicationException exception = null;

        try {
            handler.updateDoNotUse("doesNotExit", true);
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void getAncestorsOfAnExistingArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final FiltersHolder filters = mock(FiltersHolder.class);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.getAncestors(artifact.getGavc(), filters);

        verify(repositoryHandler, times(1)).getAncestors(artifact, filters);
    }

    @Test
    public void getAncestorsOfAnArtifactThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);

        WebApplicationException exception = null;

        try {
            handler.getAncestors("doesNotExist", mock(FiltersHolder.class));
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void addALicenseToAnArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final DbLicense license = new DbLicense();
        license.setName("licenseTest");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.addLicenseToArtifact(artifact.getGavc(), license.getName());

        verify(repositoryHandler, times(1)).addLicenseToArtifact(artifact, license.getName());
    }

    @Test
    public void addALicenseToAnArtifactThatAlreadyHasThisLicense(){
        final DbLicense license = new DbLicense();
        license.setName("licenseTest");

        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifact.addLicense(license.getName());

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.addLicenseToArtifact(artifact.getGavc(), license.getName());

        verify(repositoryHandler, never()).addLicenseToArtifact(artifact, license.getName());
    }

    @Test
    public void addALicenseToAnArtifactThatDoesNotExist(){
        final DbLicense license = new DbLicense();
        license.setName("licenseTest");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        WebApplicationException exception = null;

        try {
            handler.addLicenseToArtifact("doesNotExist", license.getName());
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void addALicenseThatDoesNotExistToArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        WebApplicationException exception = null;

        try {
            handler.addLicenseToArtifact(artifact.getGavc(), "doesNotExist");
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void removeALicenseFromAnArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifact.addLicense("licenseTest");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.removeLicenseFromArtifact(artifact.getGavc(), "licenseTest");

        verify(repositoryHandler, times(1)).removeLicenseFromArtifact(artifact, "licenseTest");
    }

    @Test
    public void removeALicenseAnArtifactThatDoesNotHaveThisLicense(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        handler.removeLicenseFromArtifact(artifact.getGavc(), "licenseTest");

        verify(repositoryHandler, never()).removeLicenseFromArtifact(artifact, "licenseTest");
    }

    @Test
    public void removeALicenseFromAnArtifactThatDoeNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        WebApplicationException exception = null;

        try {
            handler.addLicenseToArtifact("doesNotExist", "doesNotExist");
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }


    @Test
    public void checkGetAllArtifact(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler);
        final FiltersHolder filtersHolder = mock(FiltersHolder.class);

        final DbArtifact artifact = new DbArtifact();
        handler.getArtifacts(filtersHolder);

        verify(repositoryHandler, times(1)).getArtifacts(filtersHolder);
    }

}
