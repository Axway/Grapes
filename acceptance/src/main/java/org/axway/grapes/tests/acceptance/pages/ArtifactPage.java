package org.axway.grapes.tests.acceptance.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@DefaultUrl("http://localhost:8080")
@NamedUrls(@NamedUrl(name = "artifact.info" , url ="/artifact/{1}"))
public class ArtifactPage extends PageObject{

    @FindBy(id = "artifact.info")
    private WebElement artifactInfo;

    public ArtifactPage(final WebDriver driver) {
        super(driver);
    }

    public void displays_groupId(final String goupId) {
        element(artifactInfo).isCurrentlyVisible();
        element(artifactInfo).containsText("GroupId: " + goupId);
    }

    public void displays_artifactId(final String artifactId) {
        element(artifactInfo).isCurrentlyVisible();
        element(artifactInfo).containsText("ArtifactId: " + artifactId);
    }

    public void displays_verison(final String verison) {
        element(artifactInfo).isCurrentlyVisible();
        element(artifactInfo).containsText("Version: " + verison);
    }

    public void displays_classifier(final String classifier) {
        element(artifactInfo).isCurrentlyVisible();
        element(artifactInfo).containsText("Classifier: " + classifier);
    }

    public void displays_type(final String type) {
        element(artifactInfo).isCurrentlyVisible();
        element(artifactInfo).containsText("Type: " + type);
    }

    public void displays_extension(final String extension) {
        element(artifactInfo).isCurrentlyVisible();
        element(artifactInfo).containsText("Extension: " + extension);
    }

    public void displays_module(final String module) {
        element(artifactInfo).isCurrentlyVisible();
        element(artifactInfo).containsText("Module: " + module);
    }
}
