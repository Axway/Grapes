package org.axway.grapes.tests.acceptance.stories.dependencies;

import net.thucydides.core.annotations.Steps;
import org.axway.grapes.tests.acceptance.materials.cases.TC04_ProductCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class GetProductThirdPartyStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;


    // @Given("ProductCase loaded in the database") implemented in AbstractStory

    @When("I look for ProductCase's module thirdparty")
	public void ask_for_product_thirdparty() {
        moduleSteps.look_for_product_thirdparty(TC04_ProductCase.MODULE1_NAME, TC04_ProductCase.MODULE1_VERSION);
		
	}

    @Then("I got ProductCase's thirdparty")
    public void checkTheDependencies(){
        final TC04_ProductCase testCase = new TC04_ProductCase();
        moduleSteps.should_display_module_dependencies(testCase.getDependencies());
    }

}
