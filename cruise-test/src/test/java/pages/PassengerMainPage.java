package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PassengerMainPage {

    @FindBy(id = "map")
    private WebElement pageMap;

    @FindBy(name = "departure")
    private WebElement departureField;

    @FindBy(name = "destination")
    private WebElement destinationField;

    @FindBy(name = "time")
    private WebElement timeField;

    @FindBy(css = "mat-select[formcontrolname='vehicleType'")
    private WebElement vehicleTypeField;

    @FindBy(id = "ask_for_ride")
    private WebElement submitFormButton;

    @FindBy(id = "main-form")
    private WebElement requestRideForm;

    @FindBy(id = "offer0")
    private WebElement firstOfferCard;

    @FindBy(css = "#nav5 a")
    private WebElement logout;

    @FindBy(css = "h2:nth-child(1)")
    private WebElement createANewRideRequest;

    private WebDriver driver;

    public PassengerMainPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(pageMap));
    }

    public void fillInRideRequestForm(String departureAddress, String destinationAddress, String isoDateTime, String vehicleType) {
        departureField.clear();
        departureField.sendKeys(departureAddress);
        destinationField.clear();
        destinationField.sendKeys(destinationAddress);
        timeField.clear();
        String[] dateTimeTokens = isoDateTime.split("\\.")[0].split("T");
        String[] dateTokens = dateTimeTokens[0].split("-");
        String[] timeTokens = dateTimeTokens[1].split(":");
        timeField.sendKeys(String.format("%s%s%s", dateTokens[1], dateTokens[2], dateTokens[0]));
        timeField.sendKeys(Keys.TAB);
        timeField.sendKeys(String.format("%s%s", timeTokens[0], timeTokens[1]));
        vehicleTypeField.click();
        WebElement vehicleTypeOption = driver.findElement(By.cssSelector("mat-option[value='" + vehicleType.toUpperCase() + "']"));
        vehicleTypeOption.click();
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.TAB).perform();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(submitFormButton));
        requestRideForm.submit();
    }

    public void chooseFirstOffer() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(firstOfferCard));
        new Actions(driver)
                .scrollByAmount(0, 300)
                .moveToElement(firstOfferCard)
                .click()
                .perform();
    }

    public boolean isPageOpened() {
        return (new WebDriverWait(driver, Duration.ofSeconds(10))).until(ExpectedConditions.textToBePresentInElement(createANewRideRequest, "Create a new ride request"));
    }

    public void logout() {
        logout.click();
    }


}
