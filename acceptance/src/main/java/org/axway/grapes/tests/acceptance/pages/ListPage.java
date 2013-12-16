package org.axway.grapes.tests.acceptance.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.matchers.BeanMatcher;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.components.HtmlTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.thoughtworks.selenium.SeleneseTestBase.assertNotEquals;

@DefaultUrl("http://localhost:8080")
@NamedUrls(
        {
                @NamedUrl(name = "module.names" , url = "/module/names"),
                @NamedUrl(name = "module.versions" , url = "/module/{1}/versions"),
                @NamedUrl(name = "module.dependencies" , url = "/module/{1}/{2}/dependencies?scopeTest=true&scopeRun=true&showThirdparty=true"),
                @NamedUrl(name = "product.dependency.report" , url = "/module/{1}/{2}/dependencies/report?fullRecursive=true"),
                @NamedUrl(name = "product.licenses" , url = "/module/{1}/{2}/licenses?fullRecursive=true"),
                @NamedUrl(name = "product.thirdparty" , url = "/module/{1}/{2}/dependencies?scopeTest=true&scopeRun=true&showThirdparty=true&corporate=false&fullRecursive=true"),
                @NamedUrl(name = "module.ancestors" , url = "/module/{1}/{2}/ancestors")
        }
)
public class ListPage extends PageObject{

    @FindBy(id = "list")
    private WebElement table;

    public ListPage(final WebDriver driver) {
        super(driver);
    }

    public void has_info(BeanMatcher... matchers){
        assertNotEquals(0, HtmlTable.filterRows(table, matchers).size());
    }
}
