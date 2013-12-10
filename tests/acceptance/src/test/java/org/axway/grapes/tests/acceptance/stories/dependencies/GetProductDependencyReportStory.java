package org.axway.grapes.tests.acceptance.stories.dependencies;

import net.thucydides.core.annotations.Steps;
import org.axway.grapes.tests.acceptance.materials.cases.TC04_ProductCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class GetProductDependencyReportStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;


    // @Given("ProductCase loaded in the database") implemented in AbstractStory

    @When("I look for ProductCase's dependency report")
	public void ask_for_product_dependency_report() {
        moduleSteps.look_for_product_dependency_report(TC04_ProductCase.MODULE1_NAME, TC04_ProductCase.MODULE1_VERSION);
		
	}

    @Then("I should see that module4 is not up-to-date")
    public void checkTheModuleIsNotUpToDate(){
        moduleSteps.should_see_not_up_to_date(TC04_ProductCase.ARTIFACT41_GROUPID, TC04_ProductCase.ARTIFACT41_ID, TC04_ProductCase.MODULE4_VERSION, TC04_ProductCase.LAST_MODULE4_VERSION);
    }

}
