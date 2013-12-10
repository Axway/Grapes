package org.axway.grapes.tests.acceptance.steps;


import net.thucydides.core.pages.Pages;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.tests.acceptance.TestConfiguration;
import org.axway.grapes.tests.acceptance.pages.ArtifactPage;
import org.axway.grapes.tests.acceptance.pages.ListPage;

import static com.thoughtworks.selenium.SeleneseTestCase.assertEquals;
import static net.thucydides.core.pages.PageObject.withParameters;

public class ArtifactSteps extends GrapesAbstractSteps {

    private final TestConfiguration config = TestConfiguration.getInstance();
    private ArtifactPage artifactPage = getPages().get(ArtifactPage.class);
    private ListPage listPage = getPages().get(ListPage.class);

	public ArtifactSteps(final Pages pages) {
		super(pages);
        artifactPage.setDefaultBaseUrl(config.getGrapesBaseUrl());
        listPage.setDefaultBaseUrl(config.getGrapesBaseUrl());
	}

    public void look_for_artifact(final String gavc) {
        artifactPage.open("artifact.info", withParameters(gavc));
    }


    public void should_get_error_404() {
        assertEquals("Error 404 Not Found", artifactPage.getTitle());
    }

    public void should_display_artifact_info(final Artifact artifact) {
        artifactPage.displays_groupId(artifact.getGroupId());
        artifactPage.displays_artifactId(artifact.getArtifactId());
        artifactPage.displays_verison(artifact.getVersion());
        artifactPage.displays_classifier(artifact.getClassifier());
        artifactPage.displays_type(artifact.getType());
        artifactPage.displays_extension(artifact.getExtension());
    }
}