package org.axway.grapes.tests.acceptance.stories.module;


import net.thucydides.core.annotations.Steps;
import org.axway.grapes.tests.acceptance.materials.cases.TC01_SimpleModuleCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.util.ArrayList;
import java.util.List;

public class GetModuleVersionsStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;

    //@Given("SimpleModuleCase loaded in the database") implemented in AbstractStory

    @When("I look for SimpleModuleCase's module versions providing its name")
	public void ask_for_module_names() {
        moduleSteps.look_for_module_versions(TC01_SimpleModuleCase.MODULE_NAME);
		
	}

    @Then("I got a table that contains the version of SimpleModuleCase's module")
    public void shouldHaveMatchingVersion(){
        final List<String> versions = new ArrayList<String>();
        versions.add(TC01_SimpleModuleCase.MODULE_VERSION);
        moduleSteps.should_display_module_versions(versions);
    }

}
