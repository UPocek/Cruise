package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AdminMainPage
{

    private WebDriver driver;

    @FindBy(css = "#nav7 a")
    private WebElement logout;

    @FindBy(css = "#nav2 a")
    private WebElement createDriver;

    @FindBy(id = "map")
    private WebElement pageMap;

    public AdminMainPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(pageMap));
    }

    public boolean isPageOpened(){
        return (new WebDriverWait(driver, Duration.ofSeconds(10))).until(ExpectedConditions.textToBePresentInElement(createDriver, "Create driver"));
    }

    public void logout(){
        logout.click();
    }
}
