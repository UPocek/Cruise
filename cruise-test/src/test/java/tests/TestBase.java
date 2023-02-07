package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBase {
    public static WebDriver driverPassenger;
    public static WebDriver driverDriver;

    @BeforeEach
    public void initializeWebDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driverPassenger = new ChromeDriver();
        driverPassenger.manage().window().maximize();
        driverPassenger.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
        driverPassenger.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        ChromeOptions options = new ChromeOptions();
        options.addArguments("incognito");
        driverDriver = new ChromeDriver(options);
        driverDriver.manage().window().maximize();
        driverDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
        driverDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
    }

    @AfterEach
    public void quitDriver() {
        driverPassenger.quit();
        driverDriver.quit();
    }
}

