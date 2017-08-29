package org.axway.grapes.server.db.mongo;

import com.mongodb.DB;
import com.mongodb.WriteResult;
import org.axway.grapes.server.config.DataBaseConfig;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.util.InjectionUtils;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.Update;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 *  Class for testing the mongo query strings remain the same upon refactoring
 */
public class MongodbHandlerLicTest<T> {

    private MongodbHandler sut = new MongodbHandler(mock(DataBaseConfig.class), mock(DB.class));

    @Test
    public void testLicenseMatchName() {

        final DbLicense license = makeLicense("toto 2.0", "(.*)Alexa(.*)");
        sut = withLicenses(license);

        final Set<DbLicense> matchingLicenses = sut.getMatchingLicenses("toto 2.0");
        assertEquals(1, matchingLicenses.size());
        assertEquals(license.getName(), matchingLicenses.iterator().next().getName());
    }

    @Test
    public void testLicenseMatchNameCI() {
        final DbLicense license = makeLicense("toto 2.0", "(.*)Alexa(.*)");
        sut = withLicenses(license);

        final Set<DbLicense> matchingLicenses = sut.getMatchingLicenses("TOTO 2.0");
        assertEquals(1, matchingLicenses.size());
        assertEquals(license.getName(), matchingLicenses.iterator().next().getName());
    }

    @Test
    public void testMatchRegexp() {
        final DbLicense license = makeLicense("toto 2.0", "(.*)Alexa(.*)");
        sut = withLicenses(license);

        final Set<DbLicense> matchingLicenses = sut.getMatchingLicenses("Hey, Alexa, make me a sandwich");
        assertEquals(1, matchingLicenses.size());
        assertEquals(license.getName(), matchingLicenses.iterator().next().getName());
    }

    @Test
    public void testMatchRegexpCI() {
        final DbLicense license = makeLicense("toto 2.0", "(.*)alexa(.*)");
        sut = withLicenses(license);

        final Set<DbLicense> matchingLicenses = sut.getMatchingLicenses("Hey, AlexA, make me a sandwich");
        assertEquals(1, matchingLicenses.size());
        assertEquals(license.getName(), matchingLicenses.iterator().next().getName());
    }

    @Test
    public void testArtifactStringHasRegExElements() {
        final DbLicense license = makeLicense("toto 2.0", "(.*)alexa(.*)");
        sut = withLicenses(license);

        final Set<DbLicense> matchingLicenses = sut.getMatchingLicenses("Hey, AlexA (*.bots)");
        assertEquals(1, matchingLicenses.size());
        assertEquals(license.getName(), matchingLicenses.iterator().next().getName());
    }

    @Test
    public void testMultipleMatches() {
        sut = withLicenses(
                makeLicense("toto license", "(.*)toto(.*)"),
                makeLicense("Apache 1.1", "(.*)(apache|the apache|asf)(.*)(1.1)(.*)"),
                makeLicense("Apache 1.1 - bis", "(.*)(apache)(.*)"),
                makeLicense("Apache 1.1 - again", "(.*)(1.1)(.*)")
        );

        final Set<DbLicense> matchingLicenses = sut.getMatchingLicenses("Apache 1.1 - (asf)");
        assertEquals(3, matchingLicenses.size());
        assertEquals(3, matchingLicenses.stream().filter(lic -> lic.getName().contains("Apache")).count());
    }

    @Test
    public void testNoLicenseMatchedReturnsEmptyList() {
        sut = withLicenses(
                makeLicense("toto license", "(.*)toto(.*)"),
                makeLicense("BSD 2", "^(?!.*(Net)).*$(BSD|Berkeley)+(.*)"),
                makeLicense("BSD 3", "(.*)(BSD)(.*)(3-clause)(.*)"),
                makeLicense("Apache 2", "(((.*)(Apache|apache|asf)(.*)(2)(.*))|(.*)(apache license|apache|Software Licenses))")
        );
        final Set<DbLicense> matchingLicenses = sut.getMatchingLicenses("IBM Commercial");
        assertNotNull(matchingLicenses);
        assertTrue(matchingLicenses.isEmpty());
    }

    @Test
    public void testNoLicenseAtAllDoesNotInteractWithDB() throws NoSuchFieldException, IllegalAccessException {

        sut = withLicenses(
                makeLicense("toto license", "(.*)toto(.*)")
        );
        final DbArtifact artifact = makeArtifact("a", "b", new String[]{});

        sut.removeLicenseFromArtifact(artifact, "Apache 2.0");

        final Supplier<Jongo> jongoSupplier =
                InjectionUtils.getFieldValue(sut,
                        MongodbHandler.class,
                        Supplier.class, "jongoSupplier");

        verify(jongoSupplier.get().getCollection(DbCollections.DB_ARTIFACTS), never()).update(anyString());
    }

    @Test
    public void testRemoveOrphanString() throws NoSuchFieldException, IllegalAccessException {
        sut = withLicenses(
                makeLicense("toto license", "(.*)toto(.*)")
        );
        final DbArtifact artifact = makeArtifact("a", "b", new String[]{"Some Orphan License Name"});
        sut.removeLicenseFromArtifact(artifact, "Some Orphan License Name");

        assertTrue(artifact.getLicenses().isEmpty());
    }

    @Test
    public void testRemoveAllMatchingStrings() {
        sut = withLicenses(
                makeLicense("toto license", "(.*)toto(.*)")
        );

        final DbArtifact artifact = makeArtifact("a", "b",
                new String[]{
                        "A good soccer player, toto schilacci",
                        "Toto, the soccer player",
                        "They got TOTO",
                        "Asprilia"
                });

        sut.removeLicenseFromArtifact(artifact, "toto license");

        assertEquals(1, artifact.getLicenses().size());
        assertEquals("Asprilia", artifact.getLicenses().get(0));
    }

    private DbArtifact makeArtifact(final String groupId,
                                    final String artifactId,
                                    final String[] licenseStrings) {
        DbArtifact result = new DbArtifact();
        result.setGroupId(groupId);
        result.setArtifactId(artifactId);

        for(String lic : licenseStrings) {
            result.addLicense(lic);
        }

        return result;
    }

    private DbLicense makeLicense(final String name, final String regexp) {
        final DbLicense license = new DbLicense();
        license.setName(name);
        license.setLongName(String.format("%s Public License", name.toUpperCase()));
        license.setRegexp(regexp);

        return license;
    }

    private MongodbHandler withLicenses(DbLicense... licenses) {
        final Supplier<Jongo> jongoSupplier = prepareJongoSupplier(
                DbCollections.DB_LICENSES,
                DbLicense.class,
                Arrays.asList(licenses));


        sut.setJongoSupplier(jongoSupplier);

        return sut;
    }

    private <T> Supplier<Jongo> prepareJongoSupplier(final String collectionName,
                                                     final Class<T> oneClass,
                                                     final List<T> entries) {
        final Jongo result = mock(Jongo.class);

        initArtifactsCollection(result);

        final MongoCollection collection = mock(MongoCollection.class);
        when(result.getCollection(eq(collectionName))).thenReturn(collection);

        final Find find = mock(Find.class);
        when(collection.find()).thenReturn(find);
        when(find.as(eq(oneClass))).thenReturn(entries);


        final Update update = mock(Update.class);
        when(collection.update(anyString())).thenReturn(update);

        final WriteResult writeResult = mock(WriteResult.class);
        when(update.with(anyString())).thenReturn(writeResult);
        when(update.with(anyString(), anyCollection(), any())).thenReturn(writeResult);

        return () -> result;
    }


    public void initArtifactsCollection(Jongo result) {
        //
        //  License collection calls are common
        //
        final MongoCollection artifactsCollection = mock(MongoCollection.class);
        when(result.getCollection(eq(DbCollections.DB_ARTIFACTS))).thenReturn(artifactsCollection);

        final Find find = mock(Find.class);
        when(artifactsCollection.find()).thenReturn(find);
        when(find.as(eq(DbArtifact.class))).thenReturn(Collections.emptyList());

        final Update update = mock(Update.class);
        when(artifactsCollection.update(anyString())).thenReturn(update);

        final WriteResult wr = mock(WriteResult.class);
        when(update.with(anyString())).thenReturn(wr);
        when(update.with(anyString(), anyCollection())).thenReturn(wr);
    }


}
