package org.axway.grapes.tests.acceptance.stories.module;


import net.thucydides.core.annotations.Steps;
import org.axway.grapes.tests.acceptance.materials.cases.TC01_SimpleModuleCase;
import org.axway.grapes.tests.acceptance.materials.datamodel.DbArtifact;
import org.axway.grapes.tests.acceptance.steps.ArtifactSteps;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.axway.grapes.utils.client.GrapesCommunicationException;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import javax.naming.AuthenticationException;

public class DeleteModuleStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;

    @Steps
    public ArtifactSteps artifactSteps;


    // Scenario 1
    // @Given("SimpleModuleCase loaded in the database") implemented in AbstractStory

    @When("I delete the module using grapes' client")
	public void ask_for_SimpleModuleCase_module() throws AuthenticationException, GrapesCommunicationException {
        moduleSteps.delete_module(TC01_SimpleModuleCase.MODULE_NAME, TC01_SimpleModuleCase.MODULE_VERSION);
		
	}

    @Then("SimpleModuleCase's module and its artifacts are not anymore in the database")
    public void shouldHaveModule(){
        moduleSteps.look_for_module(TC01_SimpleModuleCase.MODULE_NAME, TC01_SimpleModuleCase.MODULE_VERSION);
        moduleSteps.should_get_error_404();

        final String gavc = DbArtifact.generateGAVC(TC01_SimpleModuleCase.ARTIFACT_GROUPID, TC01_SimpleModuleCase.ARTIFACT_ID, TC01_SimpleModuleCase.ARTIFACT_VERSION, TC01_SimpleModuleCase.ARTIFACT_CLASSIFIER, TC01_SimpleModuleCase.ARTIFACT_EXTENSION);
        artifactSteps.look_for_artifact(gavc);
        artifactSteps.should_get_error_404();
    }
}
