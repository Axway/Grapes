package org.axway.grapes.tests.acceptance.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.matchers.BeanMatcher;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static net.thucydides.core.pages.components.HtmlTable.filterRows;

@DefaultUrl("http://localhost:8080")
@NamedUrls(
    {
        @NamedUrl(name = "module.info" , url ="/module/{1}/{2}")
    }
)
public class ModulePage extends PageObject{

    @FindBy(id = "module.info")
    private WebElement moduleInfo;

    @FindBy(id = "module.artifacts")
    private WebElement artifactTable;

    public ModulePage(final WebDriver driver) {
        super(driver);
    }

    public void has_title(final String title) {
        getTitle().equals(title);
    }

    public void displays_name(final String name) {
        element(moduleInfo).isCurrentlyVisible();
        element(moduleInfo).containsText("Name: " + name);
    }

    public void displays_version(final String version) {
        element(moduleInfo).isCurrentlyVisible();
        element(moduleInfo).containsText("Version: " + version);
    }

    public void has_artifact(BeanMatcher... matchers){
        assertEquals(1, filterRows(artifactTable, matchers).size());
    }

    public void is_promoted() {
        element(moduleInfo).containsText("Promoted");
    }
}
