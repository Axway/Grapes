package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.PromotionEvaluationReport;
import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.config.PromoValidationConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.axway.grapes.server.GrapesTestUtils.ARTIFACT_VERSION_4TEST;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class PromoEvaluationTest extends ResourceTest {



    private RepositoryHandler repositoryHandler;
    private GrapesServerConfig config;

    private String testName;
    private DbModule module;
    private PromotionValidation[] validationOfTypeError;
    private Function<PromoEvaluationTest, PromotionEvaluationReport> prepareAssert;
    private BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> assertPart;
    private int expectedErrorCount = 0;
    private int expectedWarningCount = 0;


    public PromoEvaluationTest(
            final String theTestName,
            final DbModule module,
            final PromotionValidation[] validationOfTypeError,
            final Function<PromoEvaluationTest, PromotionEvaluationReport> prepareAssert,
            final BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> assertConsumer,
            final int warnCount,
            final int errCount) {

        this.testName = theTestName;
        this.module = module;
        this.validationOfTypeError = validationOfTypeError;
        this.prepareAssert = prepareAssert;
        this.assertPart = assertConsumer;
        this.expectedWarningCount = warnCount;
        this.expectedErrorCount = errCount;
    }

    @Override
    protected void setUpResources() throws Exception {
        repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        config = mock(GrapesServerConfig.class);

        final ModuleResource resource = new ModuleResource(repositoryHandler, config);
        addProvider(new BasicAuthProvider<>(new GrapesAuthenticator(repositoryHandler), "test auth"));
        addProvider(ViewMessageBodyWriter.class);
        addResource(resource);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"No violations - Warnings Only",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{},
                        modulePrepare(),
                        countersAssert().andThen(matchesDoableResponse()),
                        0,
                        0},
                {"Snapshot version - allowed",
                        makeModule("a-module", "1.0.0-SNAPSHOT"),
                        new PromotionValidation[]{},
                        modulePrepare(),
                        countersAssert()
                                .andThen(reportContainsWarning("Version is SNAPSHOT"))
                                .andThen(matchesDoableResponse()),
                        1,
                        0},
                {"Snapshot version - forbidden",
                        makeModule("a-module", "1.0.1-SNAPSHOT"),
                        new PromotionValidation[]{PromotionValidation.VERSION_IS_SNAPSHOT},
                        modulePrepare(),
                        countersAssert()
                                .andThen(reportContainsError("Version is SNAPSHOT"))
                                .andThen(matchesDoableResponse()),
                        0,
                        1},
                {"No License on Artifacts - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.VERSION_IS_SNAPSHOT, PromotionValidation.DO_NOT_USE_DEPS},
                        prepareNoLicenseOnArtifacts(),
                        countersAssert()
                                .andThen(reportContainsWarning(noLicenseMsg()))
                                .andThen(matchesDoableResponse()),
                        1,
                        0},
                {"No License on Artifacts - forbidden",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.VERSION_IS_SNAPSHOT, PromotionValidation.DEPS_WITH_NO_LICENSES},
                        prepareNoLicenseOnArtifacts(),
                        countersAssert()
                                .andThen(reportContainsError(noLicenseMsg()))
                                .andThen(matchesDoableResponse()),
                        0,
                        1},
                {"Unknown Licenses on Artifacts - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.VERSION_IS_SNAPSHOT},
                        prepareUnknownLicenseOnArtifacts(),
                        countersAssert()
                                .andThen(reportContainsWarning(unknownLicenseMsg()))
                                .andThen(matchesDoableResponse()),
                        1,
                        0},
                {"Unknown Licenses on Artifacts - forbidden",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.VERSION_IS_SNAPSHOT, PromotionValidation.DEPS_WITH_NO_LICENSES},
                        prepareUnknownLicenseOnArtifacts(),
                        countersAssert()
                                .andThen(reportContainsError(unknownLicenseMsg()))
                                .andThen(matchesDoableResponse()),
                        0,
                        1},
                {"Unacceptable License Terms - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.VERSION_IS_SNAPSHOT},
                        prepareUnacceptableLicenseTerms(),
                        countersAssert()
                                .andThen(reportContainsWarning(unacceptableLicenseErrorMsg()))
                                .andThen(matchesDoableResponse()),
                        1,
                        0},
                {"Unacceptable License Terms - forbidden",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.VERSION_IS_SNAPSHOT, PromotionValidation.DEPS_UNACCEPTABLE_LICENSE},
                        prepareUnacceptableLicenseTerms(),
                        countersAssert()
                                .andThen(reportContainsError(unacceptableLicenseErrorMsg()))
                                .andThen(matchesDoableResponse()),
                        0,
                        1},
                {"Un-promoted dependencies - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.VERSION_IS_SNAPSHOT},
                        prepareUnpromotedDependencies(),
                        countersAssert()
                                .andThen(reportContainsWarning(getUnpromotedMessage("secure-translate", ARTIFACT_VERSION_4TEST)))
                                .andThen(matchesDoableResponse()),
                        1,
                        0},
                {"Un-promoted dependencies - forbidden",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.UNPROMOTED_DEPS},
                        prepareUnpromotedDependencies(),
                        countersAssert()
                                .andThen(reportContainsError(getUnpromotedMessage("secure-translate", ARTIFACT_VERSION_4TEST)))
                                .andThen(matchesDoableResponse()),
                        0,
                        1},
                {"Do not use dependencies - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{},
                        prepareDoNotUseDependencies(),
                        countersAssert()
                                .andThen(reportContainsWarning(doNotUseMessage("com.corporate.test:toto:1.2.3:classifier:extension:maven")))
                                .andThen(matchesDoableResponse()),
                        1,
                        0},
                {"Do not use dependencies - forbidden",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.DO_NOT_USE_DEPS},
                        prepareDoNotUseDependencies(),
                        countersAssert()
                                .andThen(reportContainsError(doNotUseMessage("com.corporate.test:toto:1.2.3:classifier:extension:maven")))
                                .andThen(matchesDoableResponse()),
                        0,
                        1},
                {"Snapshot dependencies - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{},
                        prepareSnapshotDependencies(),
                        countersAssert()
                                .andThen(matchesDoableResponse()),
                        1,
                        0}
        });
    }

    private static String unknownLicenseMsg() {
        return "The module you are trying to promote has dependencies that miss the license information: org.missing.license:MissingLicense:1.2.3:classifier:extension";
    }

    // Actual prepare / assert tests
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> modulePrepare() {
        return parent -> {
            DbModule module = parent.module;
            when(parent.repositoryHandler.getModule(eq(module.getId()))).thenReturn(module);
            withErrors(parent.config, parent.validationOfTypeError);

            return execute(parent.client(), promotionReportEnpoint(module), PromotionEvaluationReport.class);
        };
    }

    // Testing the missing license information from module dependencies
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> prepareNoLicenseOnArtifacts() {
        return parent -> {
            final DbModule dbModule = parent.module;
            when(parent.repositoryHandler.getModule(eq(dbModule.getId()))).thenReturn(dbModule);

            final DbArtifact dbArtifact = new DbArtifact();
            dbArtifact.setGroupId(GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST);
            dbArtifact.setArtifactId(GrapesTestUtils.MISSING_LICENSE_ARTIFACTID_4TEST);
            dbArtifact.setVersion(ARTIFACT_VERSION_4TEST);
            dbArtifact.setClassifier(GrapesTestUtils.ARTIFACT_CLASSIFIER_4TEST);
            dbArtifact.setExtension(GrapesTestUtils.ARTIFACT_EXTENSION_4TEST);

            // Setting empty license list to simulate missing license
            dbArtifact.setLicenses(Collections.emptyList());

            dbModule.addArtifact(dbArtifact);
            dbModule.addDependency(dbArtifact.getGavc(), Scope.COMPILE);

            // get the module dependency
            when(parent.repositoryHandler.getArtifact(dbModule.getDependencies().get(0).getTarget())).thenReturn(dbArtifact);
            when(parent.repositoryHandler.getRootModuleOf(dbArtifact.getGavc())).thenReturn(dbModule);

            withErrors(parent.config, parent.validationOfTypeError);

            return execute(parent.client(), promotionReportEnpoint(dbModule), PromotionEvaluationReport.class);
        };
    }

    private static String noLicenseMsg() {
        return GrapesTestUtils.MISSING_LICENSE_MESSAGE_4TEST + GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST + GrapesTestUtils.COLON
                + GrapesTestUtils.MISSING_LICENSE_ARTIFACTID_4TEST + GrapesTestUtils.COLON + ARTIFACT_VERSION_4TEST + GrapesTestUtils.COLON
                + GrapesTestUtils.ARTIFACT_CLASSIFIER_4TEST + GrapesTestUtils.COLON + GrapesTestUtils.ARTIFACT_EXTENSION_4TEST;
    }

    // Test the artifact licenses contain license strings, but the licenses are not known
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> prepareUnknownLicenseOnArtifacts() {
        return parent -> {
            final DbModule dbModule = parent.module;
            when(parent.repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

            final DbArtifact dbArtifact = new DbArtifact();
            dbArtifact.setGroupId(GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST);
            dbArtifact.setArtifactId(GrapesTestUtils.MISSING_LICENSE_ARTIFACTID_4TEST);
            dbArtifact.setVersion(ARTIFACT_VERSION_4TEST);
            dbArtifact.setClassifier(GrapesTestUtils.ARTIFACT_CLASSIFIER_4TEST);
            dbArtifact.setExtension(GrapesTestUtils.ARTIFACT_EXTENSION_4TEST);

            // Setting a license text not identified in the list of licenses
            final DbLicense unknownLicense = new DbLicense();
            unknownLicense.setName("Super-Creative License");

            dbArtifact.addLicense(unknownLicense);

            dbModule.addArtifact(dbArtifact);
            dbModule.addDependency(dbArtifact.getGavc(), Scope.COMPILE);

            // get the module dependency
            when(parent.repositoryHandler.getArtifact(dbModule.getDependencies().get(0).getTarget())).thenReturn(dbArtifact);
            when(parent.repositoryHandler.getRootModuleOf(dbArtifact.getGavc())).thenReturn(dbModule);
            when(parent.repositoryHandler.getLicense(unknownLicense.getName())).thenReturn(null);

            withErrors(parent.config, parent.validationOfTypeError);

            return execute(parent.client(), promotionReportEnpoint(dbModule), PromotionEvaluationReport.class);
        };
    }

    // Licenses not approved
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> prepareUnacceptableLicenseTerms() {
        return parent -> {
            DbModule dbModule = parent.module;
            when(parent.repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

            final DbArtifact dbArtifact = new DbArtifact();
            dbArtifact.setGroupId(GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST);
            dbArtifact.setArtifactId(GrapesTestUtils.MISSING_LICENSE_ARTIFACTID_4TEST);
            dbArtifact.setVersion(ARTIFACT_VERSION_4TEST);
            dbArtifact.setClassifier(GrapesTestUtils.ARTIFACT_CLASSIFIER_4TEST);
            dbArtifact.setExtension(GrapesTestUtils.ARTIFACT_EXTENSION_4TEST);
            // Setting empty license list to simulate the rejected license
            final DbLicense notApprovedLicense = new DbLicense();

            notApprovedLicense.setName("NotApproved");
            notApprovedLicense.setApproved(false);
            notApprovedLicense.setLongName("NotApproved");


            dbArtifact.addLicense(notApprovedLicense);

            dbModule.addArtifact(dbArtifact);
            dbModule.addDependency(dbArtifact.getGavc(), Scope.COMPILE);

            // get the module dependency
            when(parent.repositoryHandler.getArtifact(dbModule.getDependencies().get(0).getTarget())).thenReturn(dbArtifact);
            when(parent.repositoryHandler.getRootModuleOf(dbArtifact.getGavc())).thenReturn(dbModule);
            when(parent.repositoryHandler.getLicense(notApprovedLicense.getName())).thenReturn(notApprovedLicense);

            withErrors(parent.config, parent.validationOfTypeError);

            return execute(parent.client(), promotionReportEnpoint(dbModule), PromotionEvaluationReport.class);
        };
    }

    private static String unacceptableLicenseErrorMsg() {
        return "The module you try to promote makes use of third party dependencies whose licenses are not accepted by Axway: org.missing.license:MissingLicense:1.2.3:classifier:extension (NotApproved)";
    }

    // Test unpromoted dependencies
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> prepareUnpromotedDependencies() {
        return parent -> {
            final String orgID = "ACME Inc.";

            DbModule dbModule = parent.module;
            dbModule.setOrganization(orgID);

            DbArtifact moduleArtifact = new DbArtifact();
            moduleArtifact.setVersion(dbModule.getVersion());
            moduleArtifact.setArtifactId(dbModule.getName());
            dbModule.addArtifact(moduleArtifact);


            final DbModule corporateDepModule = makeModule("secure-translate", ARTIFACT_VERSION_4TEST);
            corporateDepModule.setPromoted(false);

            // register modules
            when(parent.repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);
            when(parent.repositoryHandler.getModule(corporateDepModule.getId())).thenReturn(corporateDepModule);


            final DbArtifact corporateDepArtifact = new DbArtifact();
            corporateDepArtifact.setPromoted(false);
            corporateDepArtifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
            corporateDepArtifact.setArtifactId("toto");
            corporateDepArtifact.setVersion(ARTIFACT_VERSION_4TEST);
            corporateDepArtifact.setClassifier(GrapesTestUtils.ARTIFACT_CLASSIFIER_4TEST);
            corporateDepArtifact.setExtension(GrapesTestUtils.ARTIFACT_EXTENSION_4TEST);


            dbModule.addDependency(corporateDepArtifact.getGavc(), Scope.COMPILE);

            DbOrganization dbOrganization = new DbOrganization();
            dbOrganization.setCorporateGroupIdPrefixes(Arrays.asList(corporateDepArtifact.getGroupId()));

            when(parent.repositoryHandler.getOrganization(eq(orgID))).thenReturn(dbOrganization);
            when(parent.repositoryHandler.getArtifact(dbModule.getDependencies().get(0).getTarget())).thenReturn(corporateDepArtifact);

            when(parent.repositoryHandler.getRootModuleOf(moduleArtifact.getGavc())).thenReturn(dbModule);
            when(parent.repositoryHandler.getRootModuleOf(corporateDepArtifact.getGavc())).thenReturn(corporateDepModule);

            withErrors(parent.config, parent.validationOfTypeError);

            return execute(parent.client(), promotionReportEnpoint(dbModule), PromotionEvaluationReport.class);
        };
    }

    private static String getUnpromotedMessage(final String name, final String version) {
        return String.format("Corporate dependencies not promoted were detected: %s:%s", name, version);
    }

    // Test usage of dependencies which are marked as DO_NOT_USE
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> prepareDoNotUseDependencies() {
        return parent -> {
            final String orgID = "ACME Inc.";

            DbModule dbModule = parent.module;
            dbModule.setOrganization(orgID);

            DbArtifact moduleArtifact = new DbArtifact();
            moduleArtifact.setVersion(dbModule.getVersion());
            moduleArtifact.setArtifactId(dbModule.getName());
            dbModule.addArtifact(moduleArtifact);

            // register modules
            when(parent.repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

            final DbArtifact corporateDepArtifact = new DbArtifact();
            corporateDepArtifact.setPromoted(false);
            corporateDepArtifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
            corporateDepArtifact.setArtifactId("toto");
            corporateDepArtifact.setVersion(ARTIFACT_VERSION_4TEST);
            corporateDepArtifact.setClassifier(GrapesTestUtils.ARTIFACT_CLASSIFIER_4TEST);
            corporateDepArtifact.setExtension(GrapesTestUtils.ARTIFACT_EXTENSION_4TEST);
            corporateDepArtifact.setDoNotUse(true);

            dbModule.addDependency(corporateDepArtifact.getGavc(), Scope.COMPILE);

            DbOrganization dbOrganization = new DbOrganization();
            dbOrganization.setCorporateGroupIdPrefixes(Arrays.asList(corporateDepArtifact.getGroupId()));

            when(parent.repositoryHandler.getOrganization(eq(orgID))).thenReturn(dbOrganization);
            when(parent.repositoryHandler.getArtifact(dbModule.getDependencies().get(0).getTarget())).thenReturn(corporateDepArtifact);
            when(parent.repositoryHandler.getRootModuleOf(moduleArtifact.getGavc())).thenReturn(dbModule);

            withErrors(parent.config, parent.validationOfTypeError);

            return execute(parent.client(), promotionReportEnpoint(dbModule), PromotionEvaluationReport.class);
        };
    }

    private static String doNotUseMessage(final String gavc) {
        return String.format("DO_NOT_USE marked dependencies detected: %s", gavc);
    }

    // Test the module which contains snapshot dependency
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> prepareSnapshotDependencies() {
        return parent -> {
            DbModule dbModule = parent.module;
            when(parent.repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

            DbArtifact snapshotDep = new DbArtifact();
            snapshotDep.setArtifactId("toto");
            snapshotDep.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
            snapshotDep.setVersion("1.2.3-SNAPSHOT");
            snapshotDep.addLicense("Creating Commons");

            dbModule.addDependency(snapshotDep.getGavc(), Scope.COMPILE);

            DbArtifact moduleArtifact = new DbArtifact();
            moduleArtifact.setVersion(dbModule.getVersion());
            moduleArtifact.setArtifactId(dbModule.getName());
            dbModule.addArtifact(moduleArtifact);

            when(parent.repositoryHandler.getRootModuleOf(moduleArtifact.getGavc())).thenReturn(dbModule);
            when(parent.repositoryHandler.getArtifact(snapshotDep.getGavc())).thenReturn(snapshotDep);

            withErrors(parent.config, parent.validationOfTypeError);
            return execute(parent.client(), promotionReportEnpoint(dbModule), PromotionEvaluationReport.class);
        };
    };


    @Test
    public void test() {
        final PromotionEvaluationReport report = this.prepareAssert.apply(this);
        assertPart.accept(this, report);
    }


    private static BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> countersAssert() {
        return (parent, report) -> {
            assertNotNull(report);
            assertEquals(parent.expectedWarningCount, report.getWarnings().size());
            assertEquals(parent.expectedErrorCount, report.getErrors().size());
            assertEquals(parent.expectedErrorCount == 0, report.isPromotable());
        };
    }

    private static void withErrors(final GrapesServerConfig serverConfig, PromotionValidation... validations) {
        final PromoValidationConfig promoValidationMock = mock(PromoValidationConfig.class);
        final List<String> errors = Arrays.stream(validations).map(PromotionValidation::name).collect(Collectors.toList());
        when(promoValidationMock.getErrors()).thenReturn(errors);

        when(serverConfig.getPromotionValidationConfiguration()).thenReturn(promoValidationMock);
    }

    private static String promotionReportEnpoint(final DbModule module) {
        return "/" + ServerAPI.MODULE_RESOURCE + "/" + module.getName() + "/" + module.getVersion() + ServerAPI.PROMOTION + ServerAPI.GET_REPORT;
    }

    private static String doableEndpoint(final DbModule module) {
        return "/" + ServerAPI.MODULE_RESOURCE + "/" + module.getName() + "/" + module.getVersion() + ServerAPI.PROMOTION + ServerAPI.GET_FEASIBLE;
    }

    private static DbModule makeModule(final String name, final String version) {
        final DbModule result = new DbModule();
        result.setName(name);
        result.setVersion(version);
        return result;
    }

    private static BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> reportContainsError(final String msg) {
        return (parent, report) -> {
            assertTrue(report.getErrors().contains(msg));
        };
    }

    private static BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> matchesDoableResponse() {
        return (parent, report) -> {
            final Boolean isDoable = execute(parent.client(), doableEndpoint(parent.module), Boolean.class);
            assertEquals(report.isPromotable(), isDoable);
        };
    }

    private static BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> reportContainsWarning(final String msg) {
        return (parent, report) -> {
            assertTrue(report.getWarnings().contains(msg));
        };
    }


    /**
     * Executes the request and provides back the response entity
     * @param client
     * @param url
     * @return
     */
    private static final <T> T execute(final Client client, final String url, Class<T> entityClass) {
        final WebResource resource = client.resource(url);
        final ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        return response.getEntity(entityClass);
    }

}
