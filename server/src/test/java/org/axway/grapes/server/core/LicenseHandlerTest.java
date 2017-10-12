package org.axway.grapes.server.core;


import org.axway.grapes.server.core.interfaces.LicenseMatcher;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.axway.grapes.server.reports.ReportsRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class LicenseHandlerTest {


    @Test
    public void checkStoreLicense(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final LicenseHandler handler = new LicenseHandler(repositoryHandler);

        final DbLicense dbLicense = new DbLicense();
        handler.store(dbLicense);

        verify(repositoryHandler, times(1)).store(dbLicense);
    }

    @Test
    public void getLicenseNames(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final LicenseHandler handler = new LicenseHandler(repositoryHandler);
        final FiltersHolder filters = mock(FiltersHolder.class);

        handler.getLicensesNames(filters);

        verify(repositoryHandler, times(1)).getLicenseNames(filters);
    }

    @Test
    public void getAnExistingLicense(){
        final DbLicense license = new DbLicense();
        license.setName("test");
        license.setLongName("Grapes Test License");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);
        final LicenseHandler handler = new LicenseHandler(repositoryHandler);

        final DbLicense gotLicense = handler.getLicense(license.getName());

        assertNotNull(gotLicense);
        assertEquals(license, gotLicense);
        verify(repositoryHandler, times(1)).getLicense(license.getName());
    }

    @Test
    public void getALicenseThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final LicenseHandler handler = new LicenseHandler(repositoryHandler);
        WebApplicationException exception = null;

        try {
            handler.getLicense("doesNotExist");
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void deleteAnExistingLicense(){
        final DbLicense license = new DbLicense();
        license.setName("test");
        license.setLongName("Grapes Test License");

        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("org.axway.grapes.test");
        artifact.setArtifactId("tested");
        artifact.setVersion("1.5.9");
        artifact.addLicense(license.getName());

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);
        when(repositoryHandler.getArtifacts(any(FiltersHolder.class))).thenReturn(Collections.singletonList(artifact));
        final LicenseHandler handler = new LicenseHandler(repositoryHandler);

        handler.deleteLicense(license.getName());

        verify(repositoryHandler, times(1)).deleteLicense(license.getName());
        //verify(repositoryHandler, never()).removeLicenseFromArtifact(artifact, license.getName(), any());
        verify(repositoryHandler, times(1)).removeLicenseFromArtifact(
                eq(artifact), eq(license.getName()), any(LicenseMatcher.class));
    }

    @Test
    public void deleteAnArtifactThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final LicenseHandler handler = new LicenseHandler(repositoryHandler);
        WebApplicationException exception = null;

        try {
            handler.deleteLicense("doesNotExist");
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    public void approveALicense(){
        final DbLicense license = new DbLicense();
        license.setName("test");
        license.setLongName("Grapes Test License");

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getLicense(license.getName())).thenReturn(license);
        final LicenseHandler handler = new LicenseHandler(repositoryHandler);

        handler.approveLicense(license.getName(), true);
        verify(repositoryHandler, times(1)).approveLicense(license, true);

        handler.approveLicense(license.getName(), false);
        verify(repositoryHandler, times(1)).approveLicense(license, false);
    }

    @Test
    public void approveALicenseThatDoesNotExist(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final LicenseHandler handler = new LicenseHandler(repositoryHandler);
        WebApplicationException exception = null;

        try {
            handler.approveLicense("doesNotExist", true);
        }catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

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
    public void findMatchingLicenses(){
        final DbLicense license = new DbLicense();
        license.setName("LGPL-3.0");
        license.setRegexp("((.*)(GNU)(.*)(lesser)(.*)|(LGPL)*)(?!.*(GPL|gpl|BSD|SQL|COMM|W|CC|GNU)).*(3)+(.*)");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(license));

        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        assertEquals(1, licenseHandler.getMatchingLicenses("LGPL-3.0").size());
    }

}
