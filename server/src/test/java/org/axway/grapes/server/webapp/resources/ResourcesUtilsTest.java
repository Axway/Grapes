package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.datamodel.PromotionDetails;
import org.axway.grapes.server.webapp.views.PromotionReportView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ResourcesUtilsTest {


    @Test
    public void checkPromotionErrors() throws Exception {

        // Create sample promotion report
        PromotionReportView promotionViewTest = new PromotionReportView();

        final Module module = DataModelFactory.createModule("module", "1.0.0");
        final Artifact artifactMissingLicense = DataModelFactory.createArtifact("CheckPromotion", "MissingLicense", "version", "classifier", "type", "extension");
        final Artifact artifactDoNotUse = DataModelFactory.createArtifact("CheckPromotion", "DoNotUse", "version", "classifier", "type", "extension");
        final Artifact artifactUnpromotedDependency = DataModelFactory.createArtifact("CheckPromotion", "UnpromotedDependency", "version", "classifier", "type", "extension");

        // add dependencies to the module
        module.addArtifact(artifactMissingLicense);
        module.addArtifact(artifactDoNotUse);
        module.addArtifact(artifactUnpromotedDependency);

        // add the sample artifacts to the promotion view
        promotionViewTest.setRootModule(module);
        promotionViewTest.addMissingThirdPartyDependencyLicenses(artifactMissingLicense);
        promotionViewTest.addDoNotUseArtifact(artifactDoNotUse);
        promotionViewTest.addUnPromotedDependency(artifactUnpromotedDependency.getGavc());

        // pass data to the method
        PromotionReportView testResult = ResourcesUtils.checkPromotionErrors(promotionViewTest);

        // create expected result data
        PromotionDetails expectedResult = new PromotionDetails();
        expectedResult.canBePromoted = false;

        List<String> expectedErrorsList = new ArrayList<String>();
        expectedErrorsList.add("DO_NOT_USE marked dependencies detected: CheckPromotion:DoNotUse:version:classifier:extension");
        expectedErrorsList.add("Un promoted dependencies detected: CheckPromotion:UnpromotedDependency:version:classifier:extension");
        expectedErrorsList.add("The module you are trying to promote has dependencies that miss the license information: CheckPromotion:MissingLicense:version:classifier:extension");

        expectedResult.setDependencyProblems(expectedErrorsList);

        // assert if the output from the method equals to the expected data
        assertEquals(expectedResult.canBePromoted, testResult.promotionDetails().canBePromoted);
        assertEquals(expectedResult.getDependencyProblems(), testResult.getDependencyProblems());
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
        PromotionReportView testResult = ResourcesUtils.checkPromotionErrors(promotionViewTest);

        // initialize expected result map
        PromotionDetails expectedResult = new PromotionDetails();
        expectedResult.canBePromoted = true;
        List<String> expectedErrorsList = Collections.emptyList();
        expectedResult.setDependencyProblems(expectedErrorsList);

        assertEquals(expectedResult.canBePromoted, testResult.promotionDetails().canBePromoted);
        assertEquals(expectedResult.getDependencyProblems(), testResult.getDependencyProblems());
    }
}