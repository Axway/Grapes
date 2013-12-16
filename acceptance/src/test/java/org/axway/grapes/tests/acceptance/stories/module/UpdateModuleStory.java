package org.axway.grapes.tests.acceptance.stories.module;


import net.thucydides.core.annotations.Steps;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.tests.acceptance.materials.cases.TC01_SimpleModuleCase;
import org.axway.grapes.tests.acceptance.materials.cases.TestCase;
import org.axway.grapes.tests.acceptance.steps.ArtifactSteps;
import org.axway.grapes.tests.acceptance.steps.ModuleSteps;
import org.axway.grapes.tests.acceptance.stories.AbstractStory;
import org.axway.grapes.utils.client.GrapesCommunicationException;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import javax.naming.AuthenticationException;

public class UpdateModuleStory extends AbstractStory {

    @Steps
    public ModuleSteps moduleSteps;

    @Steps
    public ArtifactSteps artifactSteps;

    // data
    private Module module;
    private Artifact existingArtifact;
    private Artifact newArtifact;


    // @Given("SimpleModuleCase loaded in the database") implemented in AbstractStory

    @When("I send SimpleModuleCase's module adding a new artifact and changing the type of the existing one")
	public void update_then_send_module() throws AuthenticationException, GrapesCommunicationException {
        final TestCase testCase = new TC01_SimpleModuleCase();
        module = testCase.getModules().iterator().next();
        existingArtifact = module.getArtifacts().iterator().next();
        existingArtifact.setType("newType");

        newArtifact = DataModelFactory.createArtifact(TC01_SimpleModuleCase.ARTIFACT_GROUPID, "newArtifact", TC01_SimpleModuleCase.ARTIFACT_VERSION, null, "jar", "jar");
        module.addArtifact(newArtifact);

        moduleSteps.postModule(module);
		
	}

    @Then("I am able to check that the module has these two artifacts and that the information of the first artifact are updated")
    public void shouldHaveModule(){
        // check module
        moduleSteps.look_for_module(TC01_SimpleModuleCase.MODULE_NAME, TC01_SimpleModuleCase.MODULE_VERSION);
        moduleSteps.should_display_module_artifact(existingArtifact.getGroupId(), existingArtifact.getArtifactId(), existingArtifact.getVersion(), existingArtifact.getClassifier(), existingArtifact.getType(), existingArtifact.getExtension());
        moduleSteps.should_display_module_artifact(newArtifact.getGroupId(), newArtifact.getArtifactId(), newArtifact.getVersion(), newArtifact.getClassifier(), newArtifact.getType(), newArtifact.getExtension());

        // check the new Artifact
        artifactSteps.look_for_artifact(newArtifact.getGavc());
        artifactSteps.should_display_artifact_info(newArtifact);

        // check the update of the old one
        artifactSteps.look_for_artifact(existingArtifact.getGavc());
        artifactSteps.should_display_artifact_info(existingArtifact);
    }
}
