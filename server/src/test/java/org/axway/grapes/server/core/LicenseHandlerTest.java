package org.axway.grapes.server.core;


import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class LicenseHandlerTest {

    @Test
    public void resolveLicense(){
        final DbLicense license = new DbLicense();
        license.setName("Test");
        license.setRegexp("\\w*");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));

        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        assertEquals(license, licenseHandler.resolve(license.getName()));
    }

    @Test
    public void ifLicenseDoesNotHaveRegexpItUsesLicenseName(){
        final DbLicense license = new DbLicense();
        license.setName("Test");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));

        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        assertEquals(license, licenseHandler.resolve(license.getName()));
        assertEquals(null, licenseHandler.resolve("Test2"));
    }

    @Test
    public void doesNotFailEvenWithWrongPattern(){
        final DbLicense license = new DbLicense();
        license.setName("Test");
        license.setRegexp("x^[");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));

        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

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

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));

        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        assertEquals(1, licenseHandler.getLicenses().size());
    }

    @Test
    public void addALicenseToAnArtifactThatDoesNotExist(){
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        licenseHandler.updateArtifact("gavc", Collections.singletonList("license"));
        verify(repoHandler).getArtifact("gavc");
    }

    @Test
    public void addEmptyLicenseListToAnArtifactThatDoesNotExist(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        licenseHandler.updateArtifact(artifact.getGavc(), Collections.EMPTY_LIST);
        verify(repoHandler).getAllLicenses();
    }

    @Test
    public void addALicenseToAnArtifactThatHasNoLicenseYet(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1");

        final DbLicense license = new DbLicense();
        license.setName("license");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));
        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        licenseHandler.updateArtifact(artifact.getGavc(), Collections.singletonList(license.getName()));
        verify(repoHandler).addLicenseToArtifact(artifact, license.getName());

    }

    @Test
    public void addALicenseToAnArtifactThatAlreadyHasTheLicense(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1");

        final DbLicense license = new DbLicense();
        license.setName("license");
        artifact.addLicense(license);

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));
        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        licenseHandler.updateArtifact(artifact.getGavc(), Collections.singletonList(license.getName()));
        verify(repoHandler, never()).addLicenseToArtifact(artifact, license.getName());

    }

    @Test
    public void addALicenseToAnArtifactThatHasAnOtherLicense(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1");

        final DbLicense license1 = new DbLicense();
        license1.setName("license1");
        artifact.addLicense(license1);

        final DbLicense license2 = new DbLicense();
        license2.setName("license2");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repoHandler.getAllLicenses()).thenReturn(Arrays.asList(license1, license2));
        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        licenseHandler.updateArtifact(artifact.getGavc(), Collections.singletonList(license2.getName()));
        verify(repoHandler, never()).addLicenseToArtifact(artifact, license1.getName());
        verify(repoHandler, times(1)).addLicenseToArtifact(artifact, license2.getName());
    }

    @Test
    public void addALicenseThatCannotBeFoundInTheDataBaseOnArtifactThatHasNoLicenseYet(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.EMPTY_LIST);
        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        licenseHandler.updateArtifact(artifact.getGavc(), Collections.singletonList("license"));
        verify(repoHandler, times(1)).addLicenseToArtifact(artifact, "license");
    }

    @Test
    public void addALicenseThatCannotBeFoundInTheDataBase(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1");
        artifact.addLicense("aLicense");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.EMPTY_LIST);
        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        licenseHandler.updateArtifact(artifact.getGavc(), Collections.singletonList("AnOtherLicense"));
        verify(repoHandler, never()).addLicenseToArtifact(artifact, "AnOtherLicense");
    }

    @Test
    public void addALicenseThatMatchARegex(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1");

        final DbLicense license = new DbLicense();
        license.setName("license");
        license.setRegexp("^license(.*)");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));
        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        licenseHandler.updateArtifact(artifact.getGavc(), Collections.singletonList("license blabla"));
        verify(repoHandler, times(1)).addLicenseToArtifact(artifact, license.getName());
    }

    @Test
    public void addALicenseThatMatchARegexButThatAlreadyExist(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1");

        final DbLicense license = new DbLicense();
        license.setName("license");
        license.setRegexp("^license(.*)");
        artifact.addLicense(license);

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getArtifact(artifact.getGavc())).thenReturn(artifact);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));
        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        licenseHandler.updateArtifact(artifact.getGavc(), Collections.singletonList("license blabla"));
        verify(repoHandler, never()).addLicenseToArtifact(artifact, license.getName());
    }
}
