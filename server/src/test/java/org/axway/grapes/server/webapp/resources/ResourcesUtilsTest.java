package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.webapp.views.PromotionReportView;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class ResourcesUtilsTest {


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

        // pass data to the method
        final PromotionEvaluationReport report = ResourcesUtils.checkPromotionErrors(promotionViewTest);

        List<String> expectedErrorsList = new ArrayList<>();
        expectedErrorsList.add("DO_NOT_USE marked dependencies detected: CheckPromotion:DoNotUse:version:classifier:type:extension:maven. " + commentAsString(comment));
        expectedErrorsList.add("Un promoted dependencies detected: CheckPromotion:UnpromotedDependency:version:classifier:extension");
//        expectedErrorsList.add("The module you are trying to promote has dependencies that miss the license information: CheckPromotion:MissingLicense:version:classifier:extension");

        // assert if the output from the method equals to the expected data
        assertFalse(report.isPromotable());
        assertEquals(expectedErrorsList.size(), report.getErrors().size());
        final List<String> strings = listMinusSet(expectedErrorsList, report.getErrors());
        assertTrue(strings.isEmpty());
    }

    @Test
    public void checkNotApprovedLicensePromotion() {
        // TODO: Come back here with warnings instead of errors

        // Create sample promotion report
        PromotionReportView promotionViewTest = new PromotionReportView();

        // create sample module with artifact
        final Module module = DataModelFactory.createModule("module", "1.0.0");
        final Artifact artifactNotApprovedLicense = DataModelFactory.createArtifact("CheckPromotion", "artifactId", "version", "classifier", "type", "extension");
        final License notApprovedLicense = DataModelFactory.createLicense("NotApproved", "NotApproved", "", "", "");

        module.addArtifact(artifactNotApprovedLicense);

        promotionViewTest.setRootModule(module);

        Pair pair = Pair.create(artifactNotApprovedLicense.getGavc(), notApprovedLicense.getName());

        promotionViewTest.setDependenciesWithNotAcceptedLicenses(pair);

        // pass data to the method
        final PromotionEvaluationReport report = ResourcesUtils.checkPromotionErrors(promotionViewTest);

        // create expected result data
        List<String> expectedErrorsList = new ArrayList<>();
        expectedErrorsList.add("The module you try to promote makes use of third party dependencies whose licenses are not accepted by Axway: CheckPromotion:artifactId:version:classifier:extension (NotApproved)");

        // assert if the output from the method equals to the expected data

        assertTrue(report.isPromotable());

        //assertFalse(report.isPromotable());
        //assertTrue(listMinusSet (expectedErrorsList, report.getErrors()).isEmpty());
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

        // check promotion status
        final PromotionEvaluationReport report = ResourcesUtils.checkPromotionErrors(promotionViewTest);

        assertTrue(report.isPromotable());
        assertTrue(report.getErrors().isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testErrorSetIsImmutable() {
        PromotionEvaluationReport report = new PromotionEvaluationReport();
        report.addError("Sample text");
        report.getErrors().clear();
    }

    private List<String> listMinusSet(List<String> list, Set<String> set) {
        return list.stream().filter(entry -> !set.contains(entry)).collect(Collectors.toList());
    }


    private String commentAsString(Comment comment) {
        DateFormat df = SimpleDateFormat.getDateInstance();

        return String.format("%s (%s on %s) %s",
                comment.getCommentedBy(),
                comment.getAction(),
                df.format(comment.getCreatedDateTime()),
                comment.getCommentText());
    }

}