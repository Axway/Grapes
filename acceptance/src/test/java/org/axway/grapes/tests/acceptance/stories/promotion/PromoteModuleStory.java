package org.axway.grapes.tests.acceptance.stories.promotion;


import net.thucydides.core.annotations.Steps;
import org.axway.grapes.tests.acceptance.materials.cases.TC01_SimpleModuleCase;
import org.axway.grapes.tests.acceptance.steps.ArtifactSteps;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.axway.grapes.utils.client.GrapesCommunicationException;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import javax.naming.AuthenticationException;

public class PromoteModuleStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;

    @Steps
    public ArtifactSteps artifactSteps;


    //@Given("SimpleModuleCase loaded in the database") implemented in AbstractStory

    @When("I promote the module using grapes' client")
    public void promote_module() throws AuthenticationException, GrapesCommunicationException {
        moduleSteps.promoteModule(TC01_SimpleModuleCase.MODULE_NAME, TC01_SimpleModuleCase.MODULE_VERSION);
	}

    @Then("I want to be able to check the module has been promoted")
    public void shouldHaveModule(){
        // check module
        moduleSteps.look_for_module(TC01_SimpleModuleCase.MODULE_NAME, TC01_SimpleModuleCase.MODULE_VERSION);
        moduleSteps.should_be_promoted();

    }
}
