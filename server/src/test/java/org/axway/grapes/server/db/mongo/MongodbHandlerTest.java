package org.axway.grapes.server.db.mongo;

import com.mongodb.DB;
import com.mongodb.WriteResult;
import org.axway.grapes.server.config.DataBaseConfig;
import org.axway.grapes.server.core.interfaces.LicenseMatcher;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbCredential;

import org.axway.grapes.server.db.datamodel.DbLicense;
import org.jongo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.UnknownHostException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles.DATA_UPDATER;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 *  Class for testing the mongo query strings remain the same upon refactoring
 */
@RunWith(Parameterized.class)
public class MongodbHandlerTest<T> {

    private String collectionName;
    private Class<T> tClass;
    private T tInstance;
    private BiConsumer<MongodbHandler, MongoCollection> actAssert;

    public MongodbHandlerTest(String colName,
                              final Class<T> oneClass,
                              final T instance,
                              final BiConsumer<MongodbHandler, MongoCollection> actAssert,
                              final String testName) throws UnknownHostException {

        this.collectionName = colName;
        this.tClass = oneClass;
        this.tInstance = instance;
        this.actAssert = actAssert;
    }

    @Parameterized.Parameters(name="{index}: {4}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { DbCollections.DB_CREDENTIALS, DbCredential.class, makeCredentials("toto"), addUserRoleTest(), "Add user role" },
                { DbCollections.DB_CREDENTIALS, DbCredential.class, makeCredentials("toto-modifier", DATA_UPDATER), removeUserRoleTest(DATA_UPDATER), "Remove user role" },
                { DbCollections.DB_ARTIFACTS, DbArtifact.class, new DbArtifact(), addLicenseTest(), "Add license to artifact" },
                { DbCollections.DB_ARTIFACTS, DbArtifact.class, new DbArtifact(), removeLicenseTestLicenseExists(), "Remove license from artifact; artifact contains license" },
                { DbCollections.DB_ARTIFACTS, DbArtifact.class, new DbArtifact(), removeLicenseEmptyLicenseList(), "Remove license from artifact having empty list of licenses" },
                { DbCollections.DB_LICENSES, DbLicense.class, new DbLicense(), approveLicenseTest(), "Approve license" },
                { DbCollections.DB_ARTIFACTS, DbArtifact.class, makeSampleArtifact(), updateDoNotUseFlagTest(), "Update DO_NOT_USE flag" },
                { DbCollections.DB_ARTIFACTS, DbArtifact.class, makeSampleArtifact(), updateDownloadURLTest(), "Update download url flag" },
                { DbCollections.DB_ARTIFACTS, DbArtifact.class, makeSampleArtifact(), updateProvider(), "Update provider" }
        });
    }

    private static BiConsumer<MongodbHandler, MongoCollection> addUserRoleTest() {
        return (sut, collection) -> {
            sut.addUserRole("some-user", DbCredential.AvailableRoles.ARTIFACT_CHECKER);

            verify(collection.update(anyString()),
                    times(1))
                    .with(eq("{ $set: { \"roles\": #}} "), anyList());
        };
    }

    private static BiConsumer<MongodbHandler, MongoCollection> removeUserRoleTest(final DbCredential.AvailableRoles role) {
        return (sut, collection) -> {
            sut.removeUserRole("some-user", role);
            verify(collection.update(anyString()),
                    times(1))
                    .with(eq("{ $set: { \"roles\": #}} "), anyList());
        };
    }

    private static BiConsumer<MongodbHandler, MongoCollection> addLicenseTest() {
        return (sut, collection) -> {
            sut.addLicenseToArtifact(makeSampleArtifact(), "123");

            verify(collection.update(anyString()),
                    times(1))
                    .with(eq("{ $set: { \"licenses\": #, \"updatedDateTime\": #}} "), anyList(), any(Date.class));
        };

    }

    private static BiConsumer<MongodbHandler, MongoCollection> removeLicenseTestLicenseExists() {
        return (sut, collection) -> {
            DbArtifact d = makeSampleArtifact();
            d.addLicense("123");

            sut.removeLicenseFromArtifact(d, "123", mock(LicenseMatcher.class));

            verify(collection.update(anyString()),
                    times(1))
                    .with(eq("{ $set: { \"licenses\": #, \"updatedDateTime\": #}} "), anyList(), any(Date.class));
        };
    }

    private static BiConsumer<MongodbHandler, MongoCollection> removeLicenseEmptyLicenseList() {
        return (sut, collection) -> {
            DbArtifact d = makeSampleArtifact();

            final LicenseMatcher matcherMock = mock(LicenseMatcher.class);
            sut.removeLicenseFromArtifact(d, "123", matcherMock);

            verify(matcherMock, never()).getMatchingLicenses(anyString());
//            verify(collection.update(anyString()),
//                    times(1))
//                    .with(eq("{ $set: { \"licenses\": #, \"updatedDateTime\": #}} "), anyList(), any(Date.class));
        };
    }


    private static BiConsumer<MongodbHandler, MongoCollection> approveLicenseTest() {
        return (sut, collection) -> {
            sut.approveLicense(new DbLicense(), Boolean.TRUE);

            verify(collection.update(anyString()),
                    times(1))
                    .with(eq("{ $set: { \"approved\": #}} "), eq(Boolean.TRUE));
        };
    }

    private static BiConsumer<MongodbHandler, MongoCollection> updateDoNotUseFlagTest() {
        return (sut, collection) -> {
            sut.updateDoNotUse(makeSampleArtifact(), Boolean.TRUE);

            verify(collection.update(anyString()),
                    times(1))
                    .with(eq("{ $set: { \"doNotUse\": #, \"updatedDateTime\": #}} "), eq(Boolean.TRUE), any(Date.class));
        };
    }

    private static BiConsumer<MongodbHandler, MongoCollection> updateDownloadURLTest() {
        return (sut, collection) -> {
            sut.updateDownloadUrl(makeSampleArtifact(), "www.twitter.com");

            verify(collection.update(anyString()),
                    times(1))
                    .with(eq("{ $set: { \"downloadUrl\": #, \"updatedDateTime\": #}} "), eq("www.twitter.com"), any(Date.class));
        };
    }

    private static BiConsumer<MongodbHandler, MongoCollection> updateProvider() {
        return (sut, collection) -> {
            sut.updateProvider(makeSampleArtifact(), "some-provider");

            verify(collection.update(anyString()),
                    times(1))
                    .with(eq("{ $set: { \"provider\": #, \"updatedDateTime\": #}} "), eq("some-provider"), any(Date.class));
        };
    }

    @Test
    public void test() throws UnknownHostException {
        MongodbHandler sut = new MongodbHandler(mock(DataBaseConfig.class), mock(DB.class));

        final Supplier<Jongo> jongoSupplier = prepareJongoSupplier(collectionName, tClass, tInstance);
        sut.setJongoSupplier(jongoSupplier);

        actAssert.accept(sut, jongoSupplier.get().getCollection(collectionName));
    }



    private <T> Supplier<Jongo> prepareJongoSupplier(final String collectionName,
                                                     final Class<T> oneClass,
                                                     final T one) {
        final Jongo result = mock(Jongo.class);

        setupLicenseCollection(result);

        final MongoCollection collection = mock(MongoCollection.class);
        when(result.getCollection(eq(collectionName))).thenReturn(collection);


        final Find find = mock(Find.class);
        when(collection.find()).thenReturn(find);
        when(find.as(eq(oneClass))).thenReturn(Arrays.asList(one));

        final FindOne findOne = mock(FindOne.class);
        when(collection.findOne(anyString())).thenReturn(findOne);
        when(findOne.as(eq(oneClass))).thenReturn(one);

        final Update update = mock(Update.class);
        when(collection.update(anyString())).thenReturn(update);

        final WriteResult writeResult = mock(WriteResult.class);
        when(update.with(anyString())).thenReturn(writeResult);
        when(update.with(anyString(), anyCollection())).thenReturn(writeResult);

        return () -> result;
    }

    private void setupLicenseCollection(Jongo result) {
        //
        //  License collection calls are common
        //
        final MongoCollection licCollection = mock(MongoCollection.class);
        when(result.getCollection(eq(DbCollections.DB_LICENSES))).thenReturn(licCollection);

        final Find licFind = mock(Find.class);
        when(licCollection.find()).thenReturn(licFind);
        when(licFind.as(eq(DbLicense.class))).thenReturn(Collections.emptyList());

        final Update licUpdate = mock(Update.class);
        when(licCollection.update(anyString())).thenReturn(licUpdate);

        final WriteResult licWriteResult = mock(WriteResult.class);
        when(licUpdate.with(anyString())).thenReturn(licWriteResult);
        when(licUpdate.with(anyString(), anyCollection())).thenReturn(licWriteResult);
    }

    private static DbCredential makeCredentials(String name, DbCredential.AvailableRoles... roles) {
        final DbCredential result = new DbCredential();
        result.setUser(name);
        for(DbCredential.AvailableRoles role : roles) {
            result.addRole(role);
        }

        return result;
    }

    private static DbArtifact makeSampleArtifact() {
        DbArtifact d = new DbArtifact();
        d.setGroupId("a");
        d.setArtifactId("b");
        d.setVersion("1.5");
        d.setClassifier("jar");
        return d;
    }
}
