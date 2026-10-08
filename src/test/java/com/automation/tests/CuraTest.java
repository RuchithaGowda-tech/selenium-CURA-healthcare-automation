package com.automation.tests;

import com.automation.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class CuraTest extends BaseTest {

    private static final String BASE = "https://katalon-demo-cura.herokuapp.com/";
    private static final String USER = "John Doe";
    private static final String PASS = "ThisIsNotAPassword";

    // ---------- helpers ----------
    private WebElement waitFor(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void login(String user, String pass) {
        waitFor(By.id("btn-make-appointment")).click();
        waitFor(By.id("txt-username")).sendKeys(user);
        driver.findElement(By.id("txt-password")).sendKeys(pass);
        driver.findElement(By.id("btn-login")).click();
    }

    private void loginValid() {
        login(USER, PASS);
        waitFor(By.id("combo_facility"));
    }

    private void bookAppointment() {
        new Select(waitFor(By.id("combo_facility")))
                .selectByVisibleText("Seoul CURA Healthcare Center");
        driver.findElement(By.id("radio_program_medicare")).click();
        driver.findElement(By.id("txt_visit_date")).sendKeys("25/12/2026");
        driver.findElement(By.id("txt_comment")).click();
        driver.findElement(By.id("txt_comment")).sendKeys("Selenium test");
        driver.findElement(By.id("btn-book-appointment")).click();
    }

    private void openMenu(String linkText) {
        waitFor(By.id("menu-toggle")).click();
        waitFor(By.linkText(linkText)).click();
    }

    private String heading() {
        return waitFor(By.tagName("h2")).getText();
    }

    // ---------- LOGIN ----------
    @Test(priority = 1)
    public void validLogin() {
        loginValid();
        Assert.assertTrue(driver.getCurrentUrl().contains("appointment"));
    }

    @Test(priority = 2)
    public void wrongPassword() {
        login(USER, "wrong123");
        Assert.assertTrue(waitFor(By.cssSelector("p.text-danger")).getText()
                .contains("Login failed"));
    }

    @Test(priority = 3)
    public void wrongUsername() {
        login("NoSuchUser", PASS);
        Assert.assertTrue(waitFor(By.cssSelector("p.text-danger")).getText()
                .contains("Login failed"));
    }

    @Test(priority = 4)
    public void emptyFields() {
        login("", "");
        Assert.assertTrue(waitFor(By.cssSelector("p.text-danger")).getText()
                .contains("Login failed"));
    }

    // ---------- MAKE APPOINTMENT ----------
    @Test(priority = 5)
    public void appointmentFormShown() {
        loginValid();
        Assert.assertEquals(heading(), "Make Appointment");
    }

    @Test(priority = 6)
    public void selectFacility() {
        loginValid();
        Select facility = new Select(waitFor(By.id("combo_facility")));
        facility.selectByVisibleText("Hongkong CURA Healthcare Center");
        Assert.assertEquals(facility.getFirstSelectedOption().getText(),
                "Hongkong CURA Healthcare Center");
    }

    // ---------- CONFIRMATION ----------
    @Test(priority = 7)
    public void confirmationHeading() {

        loginValid();

        new Select(waitFor(By.id("combo_facility")))
                .selectByVisibleText("Seoul CURA Healthcare Center");

        waitFor(By.id("radio_program_medicare")).click();

        waitFor(By.id("txt_visit_date"))
                .sendKeys("25/12/2026");

        waitFor(By.id("txt_comment"))
                .sendKeys("Selenium test");

        waitFor(By.id("btn-book-appointment")).click();

        Assert.assertEquals(
                waitFor(By.tagName("h2")).getText(),
                "Appointment Confirmation"
        );
    }
    

    @Test(priority = 8)
    public void confirmationShowsFacility() {
        loginValid();
        bookAppointment();
        Assert.assertEquals(waitFor(By.id("facility")).getText(),
                "Seoul CURA Healthcare Center");
    }

    // ---------- HISTORY ----------
    @Test(priority = 9)
    public void historyPageOpens() {
        loginValid();
        driver.get(BASE + "history.php#history");
        Assert.assertEquals(heading(), "History");
    }

    @Test(priority = 10)
    public void historyPageOpensAfterBooking() {
        loginValid();
        bookAppointment();
        waitFor(By.id("facility"));
        driver.get(BASE + "history.php#history");
        Assert.assertEquals(heading(), "History");
    }
    
    // ---------- LOGOUT ----------
    @Test(priority = 11)
    public void logoutGoesToHomePage() {
        loginValid();
        openMenu("Logout");
        Assert.assertTrue(waitFor(By.id("btn-make-appointment")).isDisplayed());
    }

    @Test(priority = 12)
    public void appointmentBlockedAfterLogout() {
        loginValid();
        openMenu("Logout");
        waitFor(By.id("btn-make-appointment"));
        driver.get(BASE + "profile.php#appointment");
        Assert.assertTrue(waitFor(By.id("txt-username")).isDisplayed());
    }
}