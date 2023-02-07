package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    @FindBy(id = "nav1")
    private WebElement loginButton;

    private WebDriver driver;
    String PAGE_URL = "http://localhost:4200/";

    public HomePage(WebDriver driver) {
        this.driver = driver;
        driver.get(PAGE_URL);
        PageFactory.initElements(driver, this);
    }

    public void goToLoginScreen() {
        loginButton.click();
    }

}
