package org.axway.grapes.server.reports.utils;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.core.DependencyHandler;
import org.axway.grapes.server.core.options.Decorator;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.util.InjectionUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class DataFetchingUtilsTest {

    private final String SAMPLE_NAME = "Axway Enterprise";
    private final String SAMPLE_VERSION = "4.5.9";


    @Test
    public void testCorrectArgsForCommercialDeliveryList() {
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);

        final DataFetchingUtils sut = new DataFetchingUtils();
        sut.getProductWithCommercialDeliveries(repoHandler);

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);

        verify(repoHandler, times(1)).getListByQuery(
                nameCaptor.capture(),
                queryCaptor.capture(),
                classCaptor.capture());

        assertEquals(DbCollections.DB_PRODUCT, nameCaptor.getValue());
        assertEquals("{'deliveries.0' : {$exists: true}}", queryCaptor.getValue());
        assertEquals(DbProduct.class, classCaptor.getValue());
    }

    @Test
    public void getCDNoProduct() {
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getOneByQuery(anyString(), anyString(), any(Class.class))).thenReturn(Optional.empty());

        final DataFetchingUtils sut = new DataFetchingUtils();

        final Optional<Delivery> result = sut.getCommercialDelivery(repoHandler, "a name", "a version");
        assertFalse(result.isPresent());
    }

    @Test
    public void getCDNoCommercialDelivery() {
        final DbProduct fakeProduct = mock(DbProduct.class);
        when(fakeProduct.getDeliveries()).thenReturn(Collections.emptyList());

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getOneByQuery(anyString(), anyString(), any(Class.class))).thenReturn(Optional.of(fakeProduct));

        final DataFetchingUtils sut = new DataFetchingUtils();

        final Optional<Delivery> result = sut.getCommercialDelivery(repoHandler, "a name", "a version");
        assertFalse(result.isPresent());
    }

    @Test
    public void getCDTooManyCommercialDeliveries() throws NoSuchFieldException, IllegalAccessException {

        final DbProduct fakeProduct = mock(DbProduct.class);
        when(fakeProduct.getDeliveries()).thenReturn(Arrays.asList(
                DataModelFactory.createDelivery(SAMPLE_NAME, SAMPLE_VERSION, LocalDate.now().toString(), Arrays.asList("one")),
                DataModelFactory.createDelivery(SAMPLE_NAME, SAMPLE_VERSION, "does-not-matter", Collections.emptyList())
        ));

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getOneByQuery(anyString(), anyString(), any(Class.class))).thenReturn(Optional.of(fakeProduct));

        final DataFetchingUtils sut = new DataFetchingUtils();

        final Logger fakeLogger = mock(Logger.class);
        when(fakeLogger.isWarnEnabled()).thenReturn(true);

        InjectionUtils.injectField(sut, DataFetchingUtils.class, "LOG", fakeLogger);

        final Optional<Delivery> result = sut.getCommercialDelivery(repoHandler, SAMPLE_NAME, SAMPLE_VERSION);

        // An entry is selected
        assertTrue(result.isPresent());

        // A warning message is printed to log file
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        verify(fakeLogger, times(1)).warn(msgCaptor.capture());

        assertEquals(String.format("Multiple commercial version entries found for [%s] [%s]", SAMPLE_NAME, SAMPLE_VERSION), msgCaptor.getValue());

        // The first matching entry is picked
        assertEquals(1, result.get().getDependencies().size());
    }

    @Test
    public void getDDArtifactOnlyFullGAVC() {
        final Delivery delivery = DataModelFactory.createDelivery(SAMPLE_NAME,
                SAMPLE_VERSION,
                LocalDate.now().toString(),
                Arrays.asList("a1:a2:a3:a4", "b1:b2:b3:b4", "c1:c2:c3:c4"));

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getModule(anyString())).thenReturn(null);

        final DataFetchingUtils sut = new DataFetchingUtils();


        final Set<String> deliveryDependencies = sut.getDeliveryDependencies(repoHandler,
                                                                            mock(DependencyHandler.class), // not used
                                                                            delivery);
        assertEquals(3, deliveryDependencies.size());
        assertTrue(deliveryDependencies.contains("a1:a2"));
        assertTrue(deliveryDependencies.contains("b1:b2"));
        assertTrue(deliveryDependencies.contains("c1:c2"));
    }

    @Test
    public void getDDArtifactOnlyNonFullGAVC() throws NoSuchFieldException, IllegalAccessException {

        final Delivery delivery = DataModelFactory.createDelivery(
                SAMPLE_NAME,
                SAMPLE_VERSION,
                LocalDate.now().toString(),
                Arrays.asList("one", "two", "three"));

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getModule(anyString())).thenReturn(null);

        final DataFetchingUtils sut = new DataFetchingUtils();

        // Act
        final Set<String> deliveryDependencies = sut.getDeliveryDependencies(repoHandler,
                                                                             mock(DependencyHandler.class), // not used
                                                                             delivery);

        // Assert
        assertEquals(3, deliveryDependencies.size());
    }

    @Test
    public void getDDMultipleModulesAvoidDuplicates() {
        final Delivery delivery = DataModelFactory.createDelivery(
                SAMPLE_NAME,
                SAMPLE_VERSION,
                "Today",
                Arrays.asList("one", "two"));

        final DbModule m1 = makeModule("one");
        final DbModule m2 = makeModule("two");

        final RepositoryHandler repoHandler = makeRepoHandler(m1, m2);
        final DependencyHandler depHandler = mock(DependencyHandler.class);

        withDeps(depHandler, "one", "one_a", "common_dep");
        withDeps(depHandler, "two", "two_a", "two_b", "common_dep");

        final DataFetchingUtils sut = new DataFetchingUtils();

        // Act
        final Set<String> results = sut.getDeliveryDependencies(repoHandler, depHandler, delivery);

        // Assert
        assertEquals(2 + 3 - 1, results.size());
        assertTrue(results.contains(makeGAVC("one_a")));
        assertTrue(results.contains(makeGAVC("two_a")));
        assertTrue(results.contains(makeGAVC("two_b")));
        assertTrue(results.contains(makeGAVC("common_dep")));
    }

    @Test
    public void getDDMultipleModulesJoinDeps() {
        final Delivery delivery = DataModelFactory.createDelivery(
                SAMPLE_NAME,
                SAMPLE_VERSION,
                LocalDate.now().toString(),
                Arrays.asList("one", "two", "three"));

        final DbModule m1 = makeModule("one");
        final DbModule m2 = makeModule("two");
        final DbModule m3 = makeModule("three");

        final RepositoryHandler repoHandler = makeRepoHandler(m1, m2, m3);
        final DependencyHandler depHandler = mock(DependencyHandler.class);

        withDeps(depHandler, "one", "one_a");
        withDeps(depHandler, "two", "two_a", "two_b");
        withDeps(depHandler, "three", "three_a", "three_b", "three_c");

        final DataFetchingUtils sut = new DataFetchingUtils();

        // Act
        final Set<String> deliveryDependencies = sut.getDeliveryDependencies(repoHandler, depHandler, delivery);

        // Assert
        assertEquals(1 + 2 + 3, deliveryDependencies.size());
        assertTrue(deliveryDependencies.contains(makeGAVC("one_a")));
        assertTrue(deliveryDependencies.contains(makeGAVC("two_a")));
        assertTrue(deliveryDependencies.contains(makeGAVC("two_b")));
        assertTrue(deliveryDependencies.contains(makeGAVC("three_a")));
        assertTrue(deliveryDependencies.contains(makeGAVC("three_b")));
        assertTrue(deliveryDependencies.contains(makeGAVC("three_c")));
    }

    @Test
    public void appropriateFiltersAreUsedOnModuleDepRetrieval() {
        final Delivery delivery = DataModelFactory.createDelivery(
                SAMPLE_NAME,
                SAMPLE_VERSION,
                LocalDate.now().toString(),
                Arrays.asList("one"));

        final DbModule m1 = makeModule("one");

        final RepositoryHandler repoHandler = makeRepoHandler(m1);
        final DependencyHandler depHandler = mock(DependencyHandler.class);

        withDeps(depHandler, "one", "one_a");

        final DataFetchingUtils sut = new DataFetchingUtils();

        // Act
        final Set<String> deliveryDependencies = sut.getDeliveryDependencies(repoHandler, depHandler, delivery);

        // Assert
        ArgumentCaptor<FiltersHolder> filterCollector = ArgumentCaptor.forClass(FiltersHolder.class);

        verify(depHandler, times(1)).getModuleDependencies(eq("one"), filterCollector.capture());

        Decorator d = filterCollector.getValue().getDecorator();
        assertFalse(d.getShowCorporate());
        assertTrue(d.getShowThirdparty());
    }

    private String makeGAVCTOE(String a) {
        return String.format("groupId_%s:artifactId_%s:version_%s:clsf_%s:type_%s:ext_%s", a, a, a, a, a, a);
    }

    private String makeGAVC(String a) {
        return String.format("groupId_%s:artifactId_%s:version_%s:clsf_%s:ext_%s", a, a, a, a, a);
    }

    private List<Dependency> makeListOfGAVCTOE(String... entries) {
        return Arrays.stream(entries)
                .map(this::makeGAVCTOE)
                .map(entry -> entry.split(":"))
                .map(pieces -> DataModelFactory.createArtifact(pieces[0],
                        pieces[1],
                        pieces[2],
                        pieces[3],
                        pieces[4],
                        pieces[5])
                )
                .map(a -> DataModelFactory.createDependency(a, Scope.COMPILE))
                .collect(Collectors.toList());
    }

    private DbModule makeModule(String moduleId) {
        final DbModule result = mock(DbModule.class);

        when(result.getId()).thenReturn(moduleId);
        when(result.getOrganization()).thenReturn(moduleId + " Org Inc.");

        return result;
    }

    private RepositoryHandler makeRepoHandler(DbModule... modules) {
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getOrganization(anyString())).thenReturn(new DbOrganization());

        Arrays.stream(modules).forEach(module -> when(repoHandler.getModule(eq(module.getId()))).thenReturn(module) );

        return repoHandler;
    }

    private void withDeps(final DependencyHandler depHandler, final String moduleId, String... entries) {
        final List<Dependency> mOneDeps = makeListOfGAVCTOE(entries);
        when(depHandler.getModuleDependencies(eq(moduleId), anyObject())).thenReturn(mOneDeps);
    }
}

