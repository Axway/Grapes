package org.axway.grapes.server.core;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.axway.grapes.server.reports.ReportsRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


public class LicenseValidationTest {

    @Rule
    public ExpectedException exc = ExpectedException.none();

    @Test
    public void addLicenseGeneratingMultipleMatches() {
        // New license will conflict with existing because the expression is too general
        final DbLicense newLic = makeLicense("New Conflicting License", "(.*)");

        RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getAllLicenses()).thenReturn(toWritableList(makeLicense("Apache 1.0", "(.*)1")));
        when(repositoryHandler.getListByQuery(
                eq(DbCollections.DB_ORGANIZATION),
                anyString(),
                eq(DbOrganization.class)))
                .thenReturn(makeOrgList("Axway", "com.axway"));

        doAnswer(invocation -> {
            Consumer fwdConsumer = (Consumer) invocation.getArguments()[3];
            fwdConsumer.accept(makeArtifactWithLicenses("Apache 1"));
            return null;
        }).when(repositoryHandler)
                .consumeByQuery(anyString(), anyString(), any(Class.class), any(Consumer.class));

        ReportsRegistry.init();
        actAssertException(new LicenseHandler(repositoryHandler), newLic, "Apache 1");
    }

    @Test
    public void editLicenseGeneratingMultipleMatches() {
        // Editing the expression to a value which will interfere with dbLicense2
        final DbLicense lic1Updated = makeLicense("Test 1", "Commercial (.*)2");

        //Db license
        final DbLicense toUpdate = makeLicense("Test 1", "Commercial (.*)1");
        final DbLicense dbLicense2 = makeLicense("Test 2", "Commercial (.*)42");

        final String licString = "Commercial 142";

        //fake artifact
        final DbArtifact a = makeArtifactWithLicenses(licString);
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(toWritableList(toUpdate, dbLicense2));
        when(repoHandler.getLicense(anyString())).thenReturn(toUpdate);

        doAnswer(invocation -> {
            Consumer<DbArtifact> fakeConsumer = (Consumer<DbArtifact>) invocation.getArguments()[3];
            fakeConsumer.accept(a);
            return null;
        }).when(repoHandler).consumeByQuery(anyString(), anyString(), any(Class.class), any(Consumer.class));

        ReportsRegistry.init();
        actAssertException(new LicenseHandler(repoHandler), lic1Updated, licString);
    }

    private void actAssertException(final LicenseHandler sut, final DbLicense lic, final String str) {
        WebApplicationException exception = null;

        try{
            sut.store(lic);
        }
        catch (WebApplicationException e){
            exception = e;
            assertEquals(400, e.getResponse().getStatus());
            assertTrue(e.getResponse().getEntity().toString()
                            .contains(String.format("Pattern conflict for string entry %s matching multiple licenses", str)));
        }

        assertNotNull(exception);
    }

    @Test
    public void addSucceedsWhenConflictIsAmongOtherLicenses() {
        final DbLicense newLicense = makeLicense("New License", "100"); // nice and clean

        final DbLicense conflictingLic1 = makeLicense("Conflicting 1", "Commercial (.*)2");
        final DbLicense conflictingLic2 = makeLicense("Conflicting 2", "Commercial (.*)42");

        final String licString = "Commercial Flight 442";

        final DbArtifact a = makeArtifactWithLicenses(licString);
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(toWritableList(conflictingLic1, conflictingLic2));

        doAnswer(invocation -> {
            Consumer<DbArtifact> fakeConsumer = (Consumer<DbArtifact>) invocation.getArguments()[3];
            fakeConsumer.accept(a);
            return null;
        }).when(repoHandler).consumeByQuery(anyString(), anyString(), any(Class.class), any(Consumer.class));

        ReportsRegistry.init();
        final LicenseHandler sut = new LicenseHandler(repoHandler);

        sut.store(newLicense);

        // expect not to throw any error
    }

    @Test
    public void editSucceedsWhenConflictIsAmongOtherLicenses() {
        // Editing the expression to a value which does not generate multiple matching, but
        // other conflicts are already active between different licenses
        final DbLicense updated = makeLicense("Updating Lic", "101");

        //Db license
        final DbLicense toUpdate = makeLicense("Updating Lic", "100");
        final DbLicense dbLicense1 = makeLicense("Test 2", "Commercial (.*)2");
        final DbLicense dbLicense2 = makeLicense("Test 3", "Commercial (.*)42");

        final String licString = "Commercial 142";

        //fake artifact
        final DbArtifact a = makeArtifactWithLicenses(licString);
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(toWritableList(dbLicense1, dbLicense2, toUpdate));
        when(repoHandler.getLicense(eq(toUpdate.getName()))).thenReturn(toUpdate);

        doAnswer(invocation -> {
            Consumer<DbArtifact> fakeConsumer = (Consumer<DbArtifact>) invocation.getArguments()[3];
            fakeConsumer.accept(a);
            return null;
        }).when(repoHandler).consumeByQuery(anyString(), anyString(), any(Class.class), any(Consumer.class));

        ReportsRegistry.init();
        LicenseHandler sut = new LicenseHandler(repoHandler);

        sut.store(updated);
        // expect no error being thrown
    }

    @Test
    public void addWithNoConflicts() {
        final DbLicense newLicense = makeLicense("New License", "100"); // nice and clean

        final DbLicense lic1 = makeLicense("Lic 1", "(.*)1");
        final DbLicense lic2 = makeLicense("Lic 2", "(.*)2");
        final DbLicense lic3 = makeLicense("Lic 3", "(.*)3");

        final String licString1 = "Apache 1";
        final String licString2 = "Apache 2";
        final String licString3 = "Apache 3";

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(toWritableList(lic1, lic2, lic3));

        doAnswer(invocation -> {
            Consumer<DbArtifact> fakeConsumer = (Consumer<DbArtifact>) invocation.getArguments()[3];
            fakeConsumer.accept(makeArtifactWithLicenses(licString1));
            fakeConsumer.accept(makeArtifactWithLicenses(licString2));
            fakeConsumer.accept(makeArtifactWithLicenses(licString3));
            return null;
        }).when(repoHandler).consumeByQuery(anyString(), anyString(), any(Class.class), any(Consumer.class));

        ReportsRegistry.init();
        final LicenseHandler sut = new LicenseHandler(repoHandler);

        sut.store(newLicense);

        // expect not to throw any error
    }


    private DbLicense makeLicense(final String name, final String regexp) {
        DbLicense lic = new DbLicense();
        lic.setName(name);
        lic.setLongName("Long " + name);
        lic.setRegexp(regexp);
        return lic;
    }

    private List<DbOrganization> makeOrgList(final String name, final String... prefixes) {
        DbOrganization org = new DbOrganization();
        org.setName(name);
        org.setCorporateGroupIdPrefixes(Arrays.asList(prefixes));

        return Arrays.asList(org);
    }

    private DbArtifact makeArtifactWithLicenses(final String... lics) {
        long id = System.currentTimeMillis();
        DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("generated.group.id." + id);
        artifact.setArtifactId("artifact-id-" + id);
        artifact.setVersion("1.23.4");

        for(String lic : lics) {
            artifact.addLicense(lic);
        }

        return artifact;
    }

    private <T> List<T> toWritableList(T... elements) {
        List<T> result = new ArrayList<>();
        Collections.addAll(result, elements);
        return result;
    }
}
