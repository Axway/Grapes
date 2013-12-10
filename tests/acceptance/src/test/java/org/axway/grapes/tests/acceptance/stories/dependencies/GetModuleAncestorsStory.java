package org.axway.grapes.tests.acceptance.stories.dependencies;

import net.thucydides.core.annotations.Steps;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.tests.acceptance.materials.cases.TC03_SimpleAncestorCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class GetModuleAncestorsStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;


    //@Given("SimpleAncestorCase loaded in the database") implemented in AbstractStory

    @When("I look for SimpleAncestorCase's module ancestors")
	public void ask_for_module_dependencies() {
        moduleSteps.look_for_module_ancestors(TC03_SimpleAncestorCase.MODULE_NAME, TC03_SimpleAncestorCase.MODULE_VERSION);
		
	}

    @Then("I got the SimpleAncestorCase's ancestor")
    public void checkTheDependencies(){
        final Module ancestor = TC03_SimpleAncestorCase.getAncestor();
        final Dependency dependency = ancestor.getDependencies().iterator().next();
        moduleSteps.should_display_module_ancestor(ancestor, dependency.getTarget().getGavc(), dependency.getScope() );
    }

}
