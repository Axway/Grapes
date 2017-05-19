package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yammer.dropwizard.testing.ResourceTest;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.webapp.views.PromotionReportView;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


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
        Map<String, Object> testResult = ResourcesUtils.checkPromotionErrors(promotionViewTest);

        // create expected result data
        Map<String, Object> expectedResult = new HashMap<String, Object>();
        expectedResult.put("canBePromoted", false);
        List<String> expectedErrorsList = new ArrayList<String>();
        expectedErrorsList.add("DO_NOT_USE marked dependencies detected: CheckPromotion:DoNotUse:version:classifier:extension");
        expectedErrorsList.add("Un promoted dependencies detected: CheckPromotion:UnpromotedDependency:version:classifier:extension");
        expectedErrorsList.add("The module you are trying to promote has dependencies that miss the license information: CheckPromotion:MissingLicense:version:classifier:extension");
        expectedResult.put("errors", expectedErrorsList);

        // assert if the output from the method equals to the expected data
        assertEquals(expectedResult, testResult);
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
        Map<String, Object> testResult = ResourcesUtils.checkPromotionErrors(promotionViewTest);

        // initialize expected result map
        Map<String, Object> expectedResult = new HashMap<String, Object>();
        expectedResult.put("canBePromoted", true);
        List<String> expectedErrorsList = Collections.emptyList();
        expectedResult.put("errors", expectedErrorsList);

        assertEquals(expectedResult, testResult);
    }
}