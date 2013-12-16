package org.axway.grapes.tests.acceptance.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.components.HtmlTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertNotEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static net.thucydides.core.matchers.BeanMatchers.the;
import static org.hamcrest.Matchers.containsString;

@DefaultUrl("http://localhost:8080")
@NamedUrls(
    {
            @NamedUrl(name = "module.promotion.report" , url ="/module/{1}/{2}/promotion/report"),
            @NamedUrl(name = "module.promotion.report.recursive" , url ="/module/{1}/{2}/promotion/report?fullRecursive=true")
    }
)
public class PromotionReportPage extends PageObject{

    @FindBy(id = "promotion_ok")
    private WebElement promotionOk;

    @FindBy(id = "promotion_ko")
    private WebElement promotionKo;

    @FindBy(id = "has_to_be_promoted")
    private WebElement toBePromoted;

    @FindBy(id = "should_not_be_used")
    private WebElement shouldNotBeUsed;

    public PromotionReportPage(final WebDriver driver) {
        super(driver);
    }

    public void promotionIsPossible() {
        assertTrue(element(promotionOk).isDisplayed());
    }

    public void promotionIsNotPossible() {
        assertTrue(element(promotionKo).isDisplayed());
    }

    public void shouldNotBeUsed(final List<String> shouldNotBeUsedList) {
        for(String gavc : shouldNotBeUsedList){
            assertTrue(shouldNotBeUsed.getText().contains(gavc));
        }
    }

    public void hasToBePromoted(final List<String> hasToBePromoted) {
        for(String moduleName : hasToBePromoted){
            assertNotEquals(0, HtmlTable.filterRows(toBePromoted, the("Dependencies to promote", containsString(moduleName))).size());
        }
    }
}
