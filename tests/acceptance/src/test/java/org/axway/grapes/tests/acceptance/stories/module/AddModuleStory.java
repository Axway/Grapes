package org.axway.grapes.tests.acceptance.stories.module;


import net.thucydides.core.annotations.Steps;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.tests.acceptance.materials.cases.TC01_SimpleModuleCase;
import org.axway.grapes.tests.acceptance.materials.cases.TestCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.axway.grapes.utils.client.GrapesCommunicationException;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import javax.naming.AuthenticationException;

public class AddModuleStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;

    public Module module;

    // Scenario 1
    @Given("SimpleModuleCase's module")
    public void loadSimpleModuleCase(){
        final TestCase testCase = new TC01_SimpleModuleCase();
        module = testCase.getModules().iterator().next();
    }

    @When("I send the module using grapes' client to the dependency manager server")
	public void send_module() throws AuthenticationException, GrapesCommunicationException {
        moduleSteps.postModule(module);
	}

    @Then("SimpleModuleCase's module is in the database")
    public void shouldHaveModule(){
        moduleSteps.look_for_module(TC01_SimpleModuleCase.MODULE_NAME, TC01_SimpleModuleCase.MODULE_VERSION);
        moduleSteps.should_display_module_info(TC01_SimpleModuleCase.MODULE_NAME, TC01_SimpleModuleCase.MODULE_VERSION);
    }

}
