package com.legal.diary.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object encapsulating authentication actions on the /login view.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public class LoginPage extends BasePage {

    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By dangerAlert = By.cssSelector(".alert-danger");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open(int port) {
        driver.get("http://localhost:" + port + "/login");
        waitForVisible(usernameInput);
    }

    public void enterCredentials(String username, String password) {
        WebElement u = waitForVisible(usernameInput);
        u.clear();
        u.sendKeys(username);

        WebElement p = waitForVisible(passwordInput);
        p.clear();
        p.sendKeys(password);
    }

    public void submit() {
        clickElement(submitButton);
    }

    public DashboardPage loginAs(String username, String password, int port) {
        open(port);
        enterCredentials(username, password);
        submit();
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.isOnDashboard();
        return dashboardPage;
    }

    public boolean isErrorAlertDisplayed() {
        try {
            return waitForVisible(dangerAlert).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorAlertText() {
        return waitForVisible(dangerAlert).getText();
    }

    public boolean isOnLoginPage() {
        return wait.until(d -> d.getCurrentUrl().contains("/login"));
    }
}
