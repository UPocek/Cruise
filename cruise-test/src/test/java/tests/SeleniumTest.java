package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeleniumTest extends TestBase {

    private final String emailOfExistingUser = "selenium@gmail.com";
    private final String passwordOfExistingUser = "selenium";
    private final String emailOfExistingDemandingUser = "seleniumDemanding@gmail.com";
    private final String passwordOfExistingDemandingUser = "seleniumDemanding";
    private final String emailOfExistingUserWithRideInProgress = "seleniumWithRide@gmail.com";
    private final String passwordOfExistingUserWithRideInProgress = "seleniumWithRide";
    private final String emailOfExistingDriver = "driver@gmail.com";
    private final String passwordOfExistingDriver = "driver";
    private final String emailOfExistingDriverWithRideActive = "driverActive@gmail.com";
    private final String passwordOfExistingDriverWithRideActive = "driverActive";
    private final String usernameOfAdmin = "sadmin";
    private final String passwordOfAdmin = "sadmin";
    private final String departureAddress = "Bulevar Oslobodjenja 40";
    private final String destinationAddress = "Fruskogorska 1";
    private final String rideDateTime = LocalDateTime.now().plusMinutes(1).toString();
    private final String wantedVehicleType = "Standard";
    private final String vehicleTypeWithNoDriver = "Van";

    @Test
    @DisplayName("happy flow: Successfully ride request")
    public void shouldRequestRideSuccessfullyAndAssignDriver() throws InterruptedException {
        HomePage homePagePassenger = new HomePage(driverPassenger);
        homePagePassenger.goToLoginScreen();
        LoginPage loginPagePassenger = new LoginPage(driverPassenger);
        loginPagePassenger.waitForPageToLoad();
        loginPagePassenger.fillLoginForm(emailOfExistingUser, passwordOfExistingUser);
        loginPagePassenger.clickLoginButton();
        HomePage homePageDriver = new HomePage(driverDriver);
        homePageDriver.goToLoginScreen();
        LoginPage loginPageDriver = new LoginPage(driverDriver);
        loginPageDriver.waitForPageToLoad();
        loginPageDriver.fillLoginForm(emailOfExistingDriver, passwordOfExistingDriver);
        loginPageDriver.clickLoginButton();
        DriverMainPage driverMainPage = new DriverMainPage(driverDriver);
        driverMainPage.waitForPageToLoad();
        PassengerMainPage passengerMainPage = new PassengerMainPage(driverPassenger);
        passengerMainPage.waitForPageToLoad();
        passengerMainPage.fillInRideRequestForm(departureAddress, destinationAddress, rideDateTime, wantedVehicleType);
        passengerMainPage.chooseFirstOffer();
        driverMainPage.waitForNewRideToDrive();
        RideProcessingPage rideProcessingPage = new RideProcessingPage(driverPassenger);
        rideProcessingPage.waitForPageToLoad();
        driverMainPage.acceptRideInvite();
        rideProcessingPage.waitForRequestResult();
        Assertions.assertEquals("Cruise confirmed", rideProcessingPage.getRequestResultTitle());
    }

    @Test
    public void shouldRejectRequestBecausePassengerAlreadyHasARideInProgress() {
        HomePage homePagePassenger = new HomePage(driverPassenger);
        homePagePassenger.goToLoginScreen();
        LoginPage loginPagePassenger = new LoginPage(driverPassenger);
        loginPagePassenger.waitForPageToLoad();
        loginPagePassenger.fillLoginForm(emailOfExistingUserWithRideInProgress, passwordOfExistingUserWithRideInProgress);
        loginPagePassenger.clickLoginButton();
        PassengerMainPage passengerMainPage = new PassengerMainPage(driverPassenger);
        passengerMainPage.waitForPageToLoad();
        passengerMainPage.fillInRideRequestForm(departureAddress, destinationAddress, rideDateTime, wantedVehicleType);
        passengerMainPage.chooseFirstOffer();
        RideProcessingPage rideProcessingPage = new RideProcessingPage(driverPassenger);
        rideProcessingPage.waitForPageToLoad();
        rideProcessingPage.waitForRequestResult();
        Assertions.assertEquals("You already have ride in process", rideProcessingPage.getRequestResultTitle());
    }

    @Test
    public void shouldNotFindDriverForRideRequest() {
        HomePage homePagePassenger = new HomePage(driverPassenger);
        homePagePassenger.goToLoginScreen();
        LoginPage loginPagePassenger = new LoginPage(driverPassenger);
        loginPagePassenger.waitForPageToLoad();
        loginPagePassenger.fillLoginForm(emailOfExistingDemandingUser, passwordOfExistingDemandingUser);
        loginPagePassenger.clickLoginButton();
        PassengerMainPage passengerMainPage = new PassengerMainPage(driverPassenger);
        passengerMainPage.waitForPageToLoad();
        passengerMainPage.fillInRideRequestForm(departureAddress, destinationAddress, rideDateTime, vehicleTypeWithNoDriver);
        passengerMainPage.chooseFirstOffer();
        RideProcessingPage rideProcessingPage = new RideProcessingPage(driverPassenger);
        rideProcessingPage.waitForPageToLoad();
        rideProcessingPage.waitForRequestResult();
        Assertions.assertEquals("No driver available", rideProcessingPage.getRequestResultTitle());
    }

    @Test
    public void shouldEndRideWhenDriverRequestsIt(){
        HomePage homePageDriver = new HomePage(driverDriver);
        homePageDriver.goToLoginScreen();
        LoginPage loginPageDriver = new LoginPage(driverDriver);
        loginPageDriver.waitForPageToLoad();
        loginPageDriver.fillLoginForm(emailOfExistingDriverWithRideActive, passwordOfExistingDriverWithRideActive);
        loginPageDriver.clickLoginButton();
        DriverMainPage driverMainPage = new DriverMainPage(driverDriver);
        driverMainPage.waitForPageToLoad();
        driverMainPage.navigateToCurrentRidePage();
        DriverCurrentRidePage driverCurrentRidePage = new DriverCurrentRidePage(driverDriver);
        driverCurrentRidePage.waitForPageToLoad();
        driverCurrentRidePage.waitForCurrentRideToLoad();
        driverCurrentRidePage.clickOnStartRideBtn();
        driverCurrentRidePage.clickOnEndRideBtn();
        Assertions.assertEquals("Hey, you don't have any current rides at this moment", driverCurrentRidePage.getConfirmationTextThatDriverHasNoCurrentRide());
    }

    @Test
    @DisplayName("Passenger login")
    public void shouldLogPassenger()
    {
        HomePage homePage = new HomePage(driverPassenger);
        homePage.goToLoginScreen();
        LoginPage loginPage = new LoginPage(driverPassenger);
        loginPage.waitForPageToLoad();
        loginPage.fillLoginForm(emailOfExistingUser, passwordOfExistingUser);
        loginPage.clickLoginButton();
        PassengerMainPage passengerMainPage = new PassengerMainPage(driverPassenger);
        passengerMainPage.waitForPageToLoad();
        assertTrue(passengerMainPage.isPageOpened());
        passengerMainPage.logout();
        assertTrue(loginPage.isPageOpened());
    }

    @Test
    @DisplayName("Driver login")
    public void shouldLogDriver()
    {
        HomePage homePage = new HomePage(driverDriver);
        homePage.goToLoginScreen();
        LoginPage loginPage = new LoginPage(driverDriver);
        loginPage.waitForPageToLoad();
        loginPage.fillLoginForm(emailOfExistingDriver, passwordOfExistingDriver);
        loginPage.clickLoginButton();
        DriverMainPage driverMainPage = new DriverMainPage(driverDriver);
        driverMainPage.waitForPageToLoad();
        assertTrue(driverMainPage.isPageOpened());
        driverMainPage.logout();
        assertTrue(loginPage.isPageOpened());
    }

    @Test
    @DisplayName("Admin login")
    public void shouldLogAdmin()
    {
        HomePage homePage = new HomePage(driverDriver);
        homePage.goToLoginScreen();
        LoginPage loginPage = new LoginPage(driverDriver);
        loginPage.waitForPageToLoad();
        loginPage.fillLoginForm(usernameOfAdmin, passwordOfAdmin);
        loginPage.clickLoginButton();
        AdminMainPage adminMainPage = new AdminMainPage(driverDriver);
        adminMainPage.waitForPageToLoad();
        assertTrue(adminMainPage.isPageOpened());
        adminMainPage.logout();
        assertTrue(loginPage.isPageOpened());
    }

    @Test
    @DisplayName("Unsucessfull login")
    public void shouldNotLogin()
    {
        HomePage homePage = new HomePage(driverDriver);
        homePage.goToLoginScreen();
        LoginPage loginPage = new LoginPage(driverDriver);
        loginPage.waitForPageToLoad();
        loginPage.fillLoginForm(usernameOfAdmin, passwordOfExistingUser);
        loginPage.clickLoginButton();
        assertTrue(loginPage.isLoginUnsucessfull());
    }

}
