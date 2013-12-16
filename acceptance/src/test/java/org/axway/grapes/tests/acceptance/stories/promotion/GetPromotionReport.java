package org.axway.grapes.tests.acceptance.stories.promotion;


import net.thucydides.core.annotations.Steps;
import org.axway.grapes.tests.acceptance.materials.cases.TestCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import javax.naming.AuthenticationException;

public class GetPromotionReport  extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;



    //@Given("SimpleModuleCase loaded in the database") implemented in AbstractStory
    //@Given("ProductCase loaded in the database") implemented in AbstractStory

    @When("I look for $moduleName's promotion report in version $moduleVersion")
    public void get_promotion_report(final String moduleName, final String moduleVersion) throws AuthenticationException {
        moduleSteps.getPromotionReport(moduleName, moduleVersion);
    }

    @Then("The report says that I can promote the module")
    public void should_have_a_report_saying_ok(){
        moduleSteps.checkThatTheReportSayingOk();
    }

    @Then("I see in the report the artifacts that I should not use and the module I should promote for $testCaseName")
    public void check_product_report(final String testCaseName){
        final TestCase testcase = resolveTestCase(testCaseName);
        moduleSteps.checkPromotionReportFailures(testcase.getArtifactsToNotUse(), testcase.getModulesToPromote());
    }

}
