package org.axway.grapes.tests.acceptance.stories.dependencies;

import net.thucydides.core.annotations.Steps;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.tests.acceptance.materials.cases.TC02_ModuleWithAllKindOfDependenciesCase;
import org.axway.grapes.tests.acceptance.materials.cases.TestCase;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.util.ArrayList;
import java.util.List;

public class GetModuleDependenciesStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;


    // @Given("ModuleWithAllKindOfDependenciesCase loaded in the database") implemented in AbstractStory

    @When("I look for module dependencies")
	public void ask_for_module_dependencies() {
        moduleSteps.look_for_module_dependencies(TC02_ModuleWithAllKindOfDependenciesCase.MODULE_NAME, TC02_ModuleWithAllKindOfDependenciesCase.MODULE_VERSION);
		
	}

    @Then("I got the ModuleWithAllKindOfDependenciesCase's dependencies")
    public void checkTheDependencies(){
        final TestCase testCase = new TC02_ModuleWithAllKindOfDependenciesCase();
        final Module module = testCase.getModules().iterator().next();
        final List<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.addAll(module.getDependencies());

        moduleSteps.should_display_module_dependencies(dependencies);
    }

}
