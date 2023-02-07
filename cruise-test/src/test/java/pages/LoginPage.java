package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    @FindBy(className = "d-h1")
    private WebElement pageTitle;

    @FindBy(name = "email")
    private WebElement emailField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(css = "input[type='submit'")
    private WebElement loginButton;


    @FindBy(css = "#pop-up p")
    private WebElement loginUnsucessfull;


    private WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public void fillLoginForm(String email, String password) {
        emailField.clear();
        emailField.sendKeys(email);
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public boolean isPageOpened(){
        return (new WebDriverWait(driver, Duration.ofSeconds(10))).until(ExpectedConditions.textToBePresentInElement(pageTitle, "LogIn"));
    }

    public boolean isLoginUnsucessfull() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(loginUnsucessfull));
        return (new WebDriverWait(driver, Duration.ofSeconds(10))).until(ExpectedConditions.textToBePresentInElement(loginUnsucessfull, "Login unsucessfull"));
    }

}
