package org.axway.grapes.server.core;


import org.axway.grapes.server.core.interfaces.LicenseMatcher;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * License specific tests for the artifact handler
 */
public class ArtifactHandlerLicenseTest {

    @Test
    public void testArtifactLicensesUsesMatchByRegexp() {
        final DbLicense license = new DbLicense();
        license.setName("Toto License");
        license.setLongName("Toto Schilacci Public License");
        license.setRegexp("(.*)toto(.*)");

        final String artifactLicenseString = "something containing the toto string";

        final DbArtifact artifact = new DbArtifact();
        artifact.addLicense(artifactLicenseString);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final LicenseMatcher matcherMock = mock(LicenseMatcher.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, matcherMock);

        // Act
        handler.getArtifactLicenses(artifact.getGavc(), new FiltersHolder());

        verify(matcherMock, times(1)).getMatchingLicenses(eq(artifactLicenseString));
    }

    @Test
    public void addAnExistingLicenseToAnArtifactThatDoesNotHoldAnyLicenseYet(){
        final DbLicense license = new DbLicense();
        license.setName("testLicense");

        final DbArtifact artifact = new DbArtifact();

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final LicenseMatcher matcherMock = mock(LicenseMatcher.class);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, matcherMock);
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

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, mock(LicenseMatcher.class));
        handler.addLicense(artifact.getGavc(), license.getName());

        verify(repositoryHandler, times(1)).addLicenseToArtifact(artifact, license.getName());
    }

    @Test
    public void addANoneExistingLicenseToAnArtifactThatDoesNotHoldAnyLicenseYet(){
        final DbArtifact artifact = new DbArtifact();

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler,
                mock(LicenseMatcher.class));
        handler.addLicense(artifact.getGavc(), "testLicense");

        verify(repositoryHandler, times(1)).addLicenseToArtifact(artifact, "testLicense");
    }

    @Test
    public void addANoneExistingLicenseToAnArtifactThatAlreadyHoldALicense(){
        final DbArtifact artifact = new DbArtifact();
        artifact.addLicense("Test License");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, mock(LicenseMatcher.class));
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

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, mock(LicenseMatcher.class));
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

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, mock(LicenseMatcher.class));
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
    public void addALicenseToAnArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final DbLicense license = new DbLicense();
        license.setName("licenseTest");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, mock(LicenseMatcher.class));
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

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, mock(LicenseMatcher.class));
        handler.addLicenseToArtifact(artifact.getGavc(), license.getName());

        verify(repositoryHandler, never()).addLicenseToArtifact(artifact, license.getName());
    }

    @Test
    public void addALicenseToAnArtifactThatDoesNotExist(){
        final DbLicense license = new DbLicense();
        license.setName("licenseTest");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, mock(LicenseMatcher.class));
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

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, mock(LicenseMatcher.class));
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

        final LicenseMatcher matcherMock = mock(LicenseMatcher.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, matcherMock);
        handler.removeLicenseFromArtifact(artifact.getGavc(), "licenseTest");

        verify(repositoryHandler, times(1)).removeLicenseFromArtifact(artifact, "licenseTest", matcherMock);
    }

    @Test
    public void removeALicenseAnArtifactThatDoesNotHaveThisLicense(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-SNAPSHOT");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);

        final LicenseMatcher matcherMock = mock(LicenseMatcher.class);

        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, matcherMock);
        handler.removeLicenseFromArtifact(artifact.getGavc(), "licenseTest");

        verify(repositoryHandler, times(1)).removeLicenseFromArtifact(
                eq(artifact),
                eq("licenseTest"),
                eq(matcherMock));
    }

    @Test
    public void removeALicenseFromAnArtifactThatDoeNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ArtifactHandler handler = new ArtifactHandler(repositoryHandler, mock(LicenseMatcher.class));
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
    public void licensesAreUpdatedToOriginalArtifact() {
        final DbArtifact original = new DbArtifact();
        original.setGroupId("toto");
        original.setArtifactId("test-utils");
        original.setVersion("1.0.0-SNAPSHOT");
        original.setDoNotUse(true);
        original.setPromoted(true);

        // The new artifact has information related to licenses
        final DbArtifact fromClient = new DbArtifact();
        fromClient.setGroupId("toto");
        fromClient.setArtifactId("test-utils");
        fromClient.setVersion("1.0.0-SNAPSHOT");
        fromClient.setLicenses(Arrays.asList("a", "b"));
        fromClient.setDoNotUse(false);
        fromClient.setPromoted(false);

        final RepositoryHandler repoHandlerMock = mock(RepositoryHandler.class);
        when(repoHandlerMock.getArtifact(any(String.class))).thenReturn(original);

        final ArtifactHandler sut = new ArtifactHandler(repoHandlerMock, mock(LicenseMatcher.class));

        ArgumentCaptor<DbArtifact> captor = ArgumentCaptor.forClass(DbArtifact.class);
        sut.storeIfNew(fromClient);

        verify(repoHandlerMock).store(captor.capture());

        final DbArtifact stored = captor.getValue();
        assertTrue(stored.getDoNotUse());
        assertTrue(stored.isPromoted());
        assertEquals(2, stored.getLicenses().size());
        assertTrue(stored.getLicenses().contains("a"));
        assertTrue(stored.getLicenses().contains("b"));
    }
}
