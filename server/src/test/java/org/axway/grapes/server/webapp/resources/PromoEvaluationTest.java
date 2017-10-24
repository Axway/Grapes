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
import org.axway.grapes.commons.datamodel.Tag;
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

import static org.axway.grapes.commons.datamodel.Tag.CRITICAL;
import static org.axway.grapes.commons.datamodel.Tag.MAJOR;
import static org.axway.grapes.commons.datamodel.Tag.MINOR;
import static org.axway.grapes.server.GrapesTestUtils.ARTIFACT_VERSION_4TEST;
import static org.axway.grapes.server.GrapesTestUtils.MISSING_LICENSE_ARTIFACTID_4TEST;
import static org.axway.grapes.server.promo.validations.PromotionValidation.DEPS_WITH_NO_LICENSES;
import static org.axway.grapes.server.promo.validations.PromotionValidation.DO_NOT_USE_DEPS;
import static org.axway.grapes.server.promo.validations.PromotionValidation.VERSION_IS_SNAPSHOT;
import static org.axway.grapes.server.webapp.resources.PromotionReportTranslator.*;
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
    private String[] validationEqualTags;
    private Function<PromoEvaluationTest, PromotionEvaluationReport> prepareAssert;
    private BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> assertPart;
    private boolean expectedPromotable = true;
    private int expectedMessages = 0;


    public PromoEvaluationTest(
            final String theTestName,
            final DbModule module,
            final PromotionValidation[] validationOfTypeError,
            final String[] validationEqualTags,
            final Function<PromoEvaluationTest, PromotionEvaluationReport> prepareAssert,
            final BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> assertConsumer,
            final boolean promotable,
            final int msgCount) {

        this.testName = theTestName;
        this.module = module;
        this.validationOfTypeError = validationOfTypeError;
        this.validationEqualTags = validationEqualTags;
        this.prepareAssert = prepareAssert;
        this.assertPart = assertConsumer;
        this.expectedPromotable = promotable;
        this.expectedMessages = msgCount;
    }

    @Override
    protected void setUpResources() throws Exception {

        repositoryHandler = GrapesTestUtils.getRepoHandlerMock();
        config = mock(GrapesServerConfig.class);

        PromoValidationConfig promoCfg = new PromoValidationConfig();
        when(config.getPromoValidationCfg()).thenReturn(promoCfg);

        withErrors(config, this.validationOfTypeError);

        if(validationEqualTags.length > 0) {
            Arrays.stream(validationEqualTags)
                    .map(pairStr -> pairStr.split("="))
                    .filter(pair -> pair.length == 2)
                    .filter(pair -> PromotionValidation.byName(pair[0]).isPresent())
                    .filter(pair -> Tag.byName(pair[1]) != null)
                    .map(pair -> new AbstractMap.SimpleEntry<>(PromotionValidation.byName(pair[0]).get(), Tag.byName(pair[1]) ))
                    .forEach(entry -> withTag(promoCfg, entry.getKey(), entry.getValue()));
        }

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
                        new String[] {},
                        modulePrepare(),
                        countersAssert().andThen(matchesDoableResponse()),
                        true,
                        0},
                {"Snapshot version - allowed",
                        makeModule("a-module", "1.0.0-SNAPSHOT"),
                        new PromotionValidation[]{},
                        new String[] {},
                        modulePrepare(),
                        countersAssert()
                                .andThen(reportContainsMsg(PromotionReportTranslator.SNAPSHOT_VERSION_MSG, Tag.MINOR))
                                .andThen(matchesDoableResponse()),
                        true,
                        1},
                {"Snapshot version - forbidden, critical tag",
                        makeModule("a-module", "1.0.1-SNAPSHOT"),
                        new PromotionValidation[]{VERSION_IS_SNAPSHOT},
                        new String[] {String.format("%s=%s", VERSION_IS_SNAPSHOT.name(), Tag.CRITICAL.name()) },
                        modulePrepare(),
                        countersAssert()
                                .andThen(reportContainsMsg(PromotionReportTranslator.SNAPSHOT_VERSION_MSG, Tag.CRITICAL))
                                .andThen(matchesDoableResponse()),
                        false,
                        1},
                {"No License on Artifacts - allowed, major tags",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{VERSION_IS_SNAPSHOT, PromotionValidation.DO_NOT_USE_DEPS},
                        new String[] {
                                String.format("%s=%s", DEPS_WITH_NO_LICENSES, MAJOR),
                                String.format("%s=%s", DO_NOT_USE_DEPS, CRITICAL)
                        },
                        prepareNoLicenseOnArtifacts(),
                        countersAssert()
                                .andThen(reportContainsMsg(MISSING_LICENSE_MSG, MAJOR))
                                .andThen(reportContainsMsg(GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST, MAJOR))
                                .andThen(reportContainsMsg(GrapesTestUtils.ARTIFACT_CLASSIFIER_4TEST, MAJOR))
                                .andThen(matchesDoableResponse()),
                        true,
                        1},
                {"No License on Artifacts - forbidden",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{VERSION_IS_SNAPSHOT, DEPS_WITH_NO_LICENSES},
                        new String[] {},
                        prepareNoLicenseOnArtifacts(),
                        countersAssert()
                                .andThen(reportContainsMsg(MISSING_LICENSE_MSG, Tag.MINOR))
                                .andThen(reportContainsMsg(GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST, Tag.MINOR))
                                .andThen(matchesDoableResponse()),
                        false,
                        1},
                {"Unknown Licenses on Artifacts - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{VERSION_IS_SNAPSHOT},
                        new String[] {},
                        prepareUnknownLicenseOnArtifacts(),
                        countersAssert()
                                .andThen(reportContainsMsg(MISSING_LICENSE_MSG, Tag.MINOR))
                                .andThen(matchesDoableResponse()),
                        true,
                        1},
                {"Unknown Licenses on Artifacts - forbidden, labeled with major tag",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{VERSION_IS_SNAPSHOT, DEPS_WITH_NO_LICENSES},
                        new String[] {
                                String.format("%s=%s", VERSION_IS_SNAPSHOT, CRITICAL),
                                String.format("%s=%s", DEPS_WITH_NO_LICENSES, MAJOR)
                        },
                        prepareUnknownLicenseOnArtifacts(),
                        countersAssert()
                                .andThen(reportContainsMsg(MISSING_LICENSE_MSG, MAJOR))
                                .andThen(matchesDoableResponse()),
                        false,
                        1},
                {"Unacceptable License Terms - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{VERSION_IS_SNAPSHOT},
                        new String[] {},
                        prepareUnacceptableLicenseTerms(),
                        countersAssert()
                                .andThen(reportContainsMsg(UNACCEPTABLE_LICENSE_MSG, Tag.MINOR))
                                .andThen(matchesDoableResponse()),
                        true,
                        1},
                {"Unacceptable License Terms - forbidden",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{VERSION_IS_SNAPSHOT, PromotionValidation.DEPS_UNACCEPTABLE_LICENSE},
                        new String[] {},
                        prepareUnacceptableLicenseTerms(),
                        countersAssert()
                                .andThen(reportContainsMsg(UNACCEPTABLE_LICENSE_MSG, Tag.MINOR))
                                .andThen(matchesDoableResponse()),
                        false,
                        1},
                {"Un-promoted dependencies - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{VERSION_IS_SNAPSHOT},
                        new String[] {},
                        prepareUnpromotedDependencies(),
                        countersAssert()
                                .andThen(reportContainsMsg(UNPROMOTED_MSG, Tag.MINOR))
                                .andThen(matchesDoableResponse()),
                        true,
                        1},
                {"Un-promoted dependencies - forbidden",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.UNPROMOTED_DEPS},
                        new String[] {},
                        prepareUnpromotedDependencies(),
                        countersAssert()
                                .andThen(reportContainsMsg(UNPROMOTED_MSG, Tag.MINOR))
                                .andThen(matchesDoableResponse()),
                        false,
                        1},
                {"Do not use dependencies - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{},
                        new String[] {},
                        prepareDoNotUseDependencies(),
                        countersAssert()
                                .andThen(reportContainsMsg(DO_NOT_USE_MSG, Tag.MINOR))
                                .andThen(matchesDoableResponse()),
                        true,
                        1},
                {"Do not use dependencies - forbidden",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{PromotionValidation.DO_NOT_USE_DEPS},
                        new String[] {},
                        prepareDoNotUseDependencies(),
                        countersAssert()
                                .andThen(reportContainsMsg(DO_NOT_USE_MSG, Tag.MINOR))
                                .andThen(matchesDoableResponse()),
                        false,
                        1},
                {"Snapshot dependencies - allowed",
                        makeModule("a-module", UUID.randomUUID().toString()),
                        new PromotionValidation[]{},
                        new String[] {},
                        prepareSnapshotDependencies(),
                        countersAssert()
                                .andThen(matchesDoableResponse()),
                        true,
                        1}
        });
    }

    // Actual prepare / assert tests
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> modulePrepare() {
        return parent -> {
            DbModule module = parent.module;
            when(parent.repositoryHandler.getModule(eq(module.getId()))).thenReturn(module);
            return execute(parent.client(), promotionReportEndpoint(module), PromotionEvaluationReport.class);
        };
    }

    // Testing the missing license information from module dependencies
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> prepareNoLicenseOnArtifacts() {
        return parent -> {
            final DbModule dbModule = parent.module;
            when(parent.repositoryHandler.getModule(eq(dbModule.getId()))).thenReturn(dbModule);

            final DbArtifact dbArtifact = new DbArtifact();
            dbArtifact.setGroupId(GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST);
            dbArtifact.setArtifactId(MISSING_LICENSE_ARTIFACTID_4TEST);
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

            return execute(parent.client(), promotionReportEndpoint(dbModule), PromotionEvaluationReport.class);
        };
    }

    // Test the artifact licenses contain license strings, but the licenses are not known
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> prepareUnknownLicenseOnArtifacts() {
        return parent -> {
            final DbModule dbModule = parent.module;
            when(parent.repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

            final DbArtifact dbArtifact = new DbArtifact();
            dbArtifact.setGroupId(GrapesTestUtils.MISSING_LICENSE_GROUPID_4TEST);
            dbArtifact.setArtifactId(MISSING_LICENSE_ARTIFACTID_4TEST);
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

            return execute(parent.client(), promotionReportEndpoint(dbModule), PromotionEvaluationReport.class);
        };
    }

    // Licenses not approved
    private static Function<PromoEvaluationTest, PromotionEvaluationReport> prepareUnacceptableLicenseTerms() {
        return parent -> {
            DbModule dbModule = parent.module;
            when(parent.repositoryHandler.getModule(dbModule.getId())).thenReturn(dbModule);

            final DbArtifact dbArtifact = new DbArtifact();
            dbArtifact.setGroupId("some.group.id");
            dbArtifact.setArtifactId("someArtifactID");
            dbArtifact.setVersion("12.2.1");
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
            // when(parent.repositoryHandler.getLicense(notApprovedLicense.getName())).thenReturn(notApprovedLicense);
            when(parent.repositoryHandler.getAllLicenses()).thenReturn(Arrays.asList(notApprovedLicense));

            return execute(parent.client(), promotionReportEndpoint(dbModule), PromotionEvaluationReport.class);
        };
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

            return execute(parent.client(), promotionReportEndpoint(dbModule), PromotionEvaluationReport.class);
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

            return execute(parent.client(), promotionReportEndpoint(dbModule), PromotionEvaluationReport.class);
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
            return execute(parent.client(), promotionReportEndpoint(dbModule), PromotionEvaluationReport.class);
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
            assertEquals(parent.expectedMessages, report.getMessages().size());
            assertEquals(parent.expectedPromotable, report.isPromotable());
        };
    }

    private static void withErrors(final GrapesServerConfig serverConfig,
                                   final PromotionValidation... validations) {

        final PromoValidationConfig cfg = serverConfig.getPromoValidationCfg();
        cfg.getErrors().clear();
        cfg.getErrors().addAll(Arrays.stream(validations).map(PromotionValidation::name).collect(Collectors.toList()));
    }

    private static void withTag(final PromoValidationConfig cfg,
                                final PromotionValidation v,
                                final Tag tag) {

        final List<String> listByTag = cfg.getTagConfig().getListByTag(tag);
        if(null != listByTag) {
            listByTag.add(v.name());
        }

    }

    private static String promotionReportEndpoint(final DbModule module) {
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

//    private static BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> reportContainsError(
//            final String msg) {
//        return (parent, report) -> {
//            assertSetContains(report.getMessages(), msg, Tag.CRITICAL);
//        };
//    }

    private static BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> matchesDoableResponse() {
        return (parent, report) -> {
            final Boolean isDoable = execute(parent.client(), doableEndpoint(parent.module), Boolean.class);
            assertEquals(report.isPromotable(), isDoable);
        };
    }

//    private static void assertSetContains(final Set<ReportMessage> msgs, final String msg, final Tag tag) {
//        assertTrue(msgs.stream().filter(w -> w.getBody().contains(msg)).count() > 0);
//    }

    private static BiConsumer<PromoEvaluationTest, PromotionEvaluationReport> reportContainsMsg(final String msg, final Tag tag) {
        return (parent, report) -> {
            assertTrue(report.getMessages()
                    .stream()
                    .filter(w -> w.getBody().contains(msg))
                    .filter(w -> w.getTag().equals(tag))
                    .count() > 0);
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
