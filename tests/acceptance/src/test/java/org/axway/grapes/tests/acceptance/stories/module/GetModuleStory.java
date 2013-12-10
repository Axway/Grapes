package org.axway.grapes.tests.acceptance.stories.module;

import net.thucydides.core.annotations.Steps;
import org.axway.grapes.tests.acceptance.materials.cases.TC01_SimpleModuleCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class GetModuleStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;


    // Scenario 1
    // @Given("SimpleModuleCase loaded in the database")implemented in AbstractStory

    @When("I look for SimpleModuleCase's module")
	public void ask_for_SimpleModuleCase_module() {
        moduleSteps.look_for_module(TC01_SimpleModuleCase.MODULE_NAME, TC01_SimpleModuleCase.MODULE_VERSION);
		
	}

    @Then("I got the SimpleModuleCase's module information")
    public void shouldHaveModule(){
        moduleSteps.should_display_module_info(TC01_SimpleModuleCase.MODULE_NAME, TC01_SimpleModuleCase.MODULE_VERSION);
        moduleSteps.should_display_module_artifact(TC01_SimpleModuleCase.ARTIFACT_GROUPID, TC01_SimpleModuleCase.ARTIFACT_ID, TC01_SimpleModuleCase.ARTIFACT_VERSION, TC01_SimpleModuleCase.ARTIFACT_CLASSIFIER, TC01_SimpleModuleCase.ARTIFACT_TYPE, TC01_SimpleModuleCase.ARTIFACT_EXTENSION);
    }

    // Scenario 2
    @Given("A database without any module")
    public void init(){
        // Nothing to do
    }

    @When("I look for a module")
    public void ask_for_module() {
        moduleSteps.look_for_module("name", "version");

    }

    @Then("I got a 404 NOT FOUND exception")
    public void should_have_404() {
        moduleSteps.should_get_error_404();
    }
}
