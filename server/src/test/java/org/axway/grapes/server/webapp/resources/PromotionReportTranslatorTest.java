package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.config.PromoValidationConfig;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.views.PromotionReportView;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.axway.grapes.server.promo.validations.PromotionValidation.*;
import static org.axway.grapes.server.webapp.resources.PromotionReportTranslator.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PromotionReportTranslatorTest {


    @Test
    public void checkPromotionErrors() throws Exception {

        // Create sample promotion report
        PromotionReportView promotionViewTest = new PromotionReportView();

        final Module module = DataModelFactory.createModule("module", "1.0.0");
        final Artifact artifactMissingLicense = DataModelFactory.createArtifact("CheckPromotion", "MissingLicense", "version", "classifier", "type", "extension");
        final Artifact artifactDoNotUse = DataModelFactory.createArtifact("CheckPromotion", "DoNotUse", "version", "classifier", "type", "extension");
        final Artifact artifactUnpromotedDependency = DataModelFactory.createArtifact("CheckPromotion", "UnpromotedDependency", "version", "classifier", "type", "extension");
        final Comment comment = DataModelFactory.createComment("CheckPromotion:DoNotUse:version:classifier:extension",
                Artifact.class.getSimpleName(),
                "sample action",
                "comment test",
                "test",
                new Date());

        // add dependencies to the module
        module.addArtifact(artifactMissingLicense);
        module.addArtifact(artifactDoNotUse);
        module.addArtifact(artifactUnpromotedDependency);

        // add the sample artifacts to the promotion view
        promotionViewTest.setRootModule(module);
        promotionViewTest.addMissingThirdPartyDependencyLicenses(artifactMissingLicense);
        promotionViewTest.addDoNotUseArtifact(artifactDoNotUse, comment);
        promotionViewTest.addUnPromotedDependency(artifactUnpromotedDependency.getGavc());

        PromotionReportTranslator.setConfig(cfgWithErrors(DO_NOT_USE_DEPS, UNPROMOTED_DEPS, DEPS_WITH_NO_LICENSES));
        final PromotionEvaluationReport report = PromotionReportTranslator.toReport(promotionViewTest);

        // assert if the output from the method equals to the expected data
        assertFalse(report.isPromotable());

        assertEquals(0, report.getMessages()
                .stream()
                .map(ReportMessage::getBody)
                .filter(isPartOf(DO_NOT_USE_MSG, UNPROMOTED_MSG, MISSING_LICENSE_MSG))
                .count());
    }

    @Test
    public void checkNotApprovedLicensePromotion() {

        // Create sample promotion report
        PromotionReportView promotionViewTest = new PromotionReportView();

        // create sample module with artifact
        final Module module = DataModelFactory.createModule("module", "1.0.0");
        final Artifact artifactNotApprovedLicense = DataModelFactory.createArtifact("CheckPromotion", "artifactId", "version", "classifier", "type", "extension");
        final License notApprovedLicense = DataModelFactory.createLicense("NotApproved", "NotApproved", "", "", "");

        module.addArtifact(artifactNotApprovedLicense);

        promotionViewTest.setRootModule(module);
        promotionViewTest.addUnacceptedLicenseEntry(artifactNotApprovedLicense.getGavc(), notApprovedLicense.getName());

        // pass data to the method
        PromotionReportTranslator.setConfig(cfgWithErrors(DEPS_UNACCEPTABLE_LICENSE));
        final PromotionEvaluationReport report = PromotionReportTranslator.toReport(promotionViewTest);

        // create expected result data
        assertFalse(report.isPromotable());
        assertEquals(0,
                report.getMessages()
                        .stream()
                        .map(ReportMessage::getBody)
                        .filter(isPartOf(UNACCEPTABLE_LICENSE_MSG))
                        .count());
    }

    @Test
    public void checkPromotionWithoutErrors() throws Exception {
        // Create sample promotion report
        PromotionReportView promotionViewTest = new PromotionReportView();

        // create sample module with artifact
        final Module module = DataModelFactory.createModule("module", "1.0.0");
        final Artifact artifactMissingLicense = DataModelFactory.createArtifact("CheckPromotionWithoutErrors", "artifactId", "version", "classifier", "type", "extension");

        module.addArtifact(artifactMissingLicense);
        promotionViewTest.setRootModule(module);

        PromotionReportTranslator.setConfig(cfgWithErrors(PromotionValidation.values()));
        final PromotionEvaluationReport report = PromotionReportTranslator.toReport(promotionViewTest);

        assertTrue(report.isPromotable());
        assertTrue(report.getMessages().size() == 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testErrorSetIsImmutable() {
        PromotionEvaluationReport report = new PromotionEvaluationReport();
        report.addMessage("Sample text", Tag.MAJOR);
        report.getMessages().clear();
    }

    private PromoValidationConfig cfgWithErrors(PromotionValidation... validations) {
        PromoValidationConfig result = new PromoValidationConfig();
        result.setErrors(asList(validations));
        return result;
    }

    private List<String> asList(PromotionValidation... validations) {
        return Arrays.stream(validations).map(PromotionValidation::name).collect(Collectors.toList());
    }

    private Predicate<String> isPartOf(String... msgs) {
        return x -> Arrays.stream(msgs).filter(msg -> msg.contains(x)).count() > 0;
    }
}