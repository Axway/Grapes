package org.axway.grapes.tests.acceptance.stories.license;

import net.thucydides.core.annotations.Steps;
import org.axway.grapes.tests.acceptance.materials.cases.TC04_ProductCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class GetProductLicensesStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;


    // @Given("ProductCase loaded in the database") implemented in AbstractStory

    @When("I look for ProductCase's licenses")
	public void ask_for_product_licenses() {
        moduleSteps.look_for_product_licenses(TC04_ProductCase.MODULE1_NAME, TC04_ProductCase.MODULE1_VERSION);
		
	}

    @Then("I should see all the licenses used by the product's thirdparty used in COMPILE and PROVIDED scopes")
    public void check_compile_and_provided_licenses(){
        moduleSteps.should_see_license(TC04_ProductCase.GPL_LICENSE_NAME);
        moduleSteps.should_see_license(TC04_ProductCase.MIT_LICENSE_NAME);
    }

}
