package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DriverMainPage {

    @FindBy(id = "map")
    private WebElement pageMap;

    @FindBy(id = "new-ride-to-drive")
    private WebElement rideRequestTitle;

    @FindBy(id = "yes-btn")
    private WebElement yesButton;

    @FindBy(id = "no-btn")
    private WebElement noButton;

    @FindBy(name = "reason")
    private WebElement declineReasonField;

    @FindBy(id = "nav1")
    private WebElement currentRideOptionInNavbar;

    @FindBy(css = "#nav5 a")
    private WebElement logout;

    @FindBy(css = "span.mat-slide-toggle-content")
    private WebElement activeLabel;

    private WebDriver driver;

    public DriverMainPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(pageMap));
    }

    public void waitForNewRideToDrive() {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.visibilityOf(rideRequestTitle));
    }

    public void acceptRideInvite() {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.elementToBeClickable(yesButton));
        yesButton.click();
    }

    public void declineRideInvite(String declineReason) {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.elementToBeClickable(noButton));
        declineReasonField.clear();
        declineReasonField.sendKeys(declineReason);
        noButton.click();
    }

    public void navigateToCurrentRidePage(){
        currentRideOptionInNavbar.click();
    }

    public boolean isPageOpened(){
        return (new WebDriverWait(driver, Duration.ofSeconds(10))).until(ExpectedConditions.textToBePresentInElement(activeLabel, "ACTIVE"));
    }

    public void logout(){
        logout.click();
    }
}
