package org.axway.grapes.tests.acceptance.stories.module;


import net.thucydides.core.annotations.Steps;
import org.axway.grapes.tests.acceptance.materials.cases.TC01_SimpleModuleCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.util.ArrayList;
import java.util.List;

public class GetModuleNamesStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;

    // @Given("SimpleModuleCase loaded in the database") implemented in AbstractStory

    @When("I look for module names")
	public void ask_for_module_names() {
        moduleSteps.look_for_module_names();
		
	}

    @Then("I got a table that contains the name of SimpleModuleCase's module")
    public void shouldHaveModule(){
        final List<String> names = new ArrayList<String>();
        names.add(TC01_SimpleModuleCase.MODULE_NAME);
        moduleSteps.should_display_module_names(names);
    }

}
