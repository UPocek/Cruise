package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RideProcessingPage {

    @FindBy(id = "processing")
    private WebElement pageTitle;

    @FindBy(className = "ride-result")
    private WebElement rideResultTitle;

    private WebDriver driver;

    public RideProcessingPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public void waitForRequestResult() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOf(pageTitle));
    }

    public String getRequestResultTitle() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(rideResultTitle));
        return rideResultTitle.getText();
    }
}
