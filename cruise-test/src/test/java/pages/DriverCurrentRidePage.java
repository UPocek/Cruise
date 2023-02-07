package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DriverCurrentRidePage {
    @FindBy(css = "h1")
    private WebElement pageTitle;
    @FindBy(id = "driver-start-ride-btn")
    private WebElement startRideBtn;
    @FindBy(id = "driver-end-ride-btn")
    private WebElement endRideBtn;
    @FindBy(id = "no-current-ride-txt")
    private WebElement noCurrentRideText;
    private WebDriver driver;

    public DriverCurrentRidePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public void waitForCurrentRideToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(startRideBtn));
    }

    public void clickOnStartRideBtn() {
        startRideBtn.click();
    }

    public void clickOnEndRideBtn() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(endRideBtn));
        endRideBtn.click();
    }

    public String getConfirmationTextThatDriverHasNoCurrentRide() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(noCurrentRideText));
        return noCurrentRideText.getText();
    }
}
