package com.legal.diary.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true })); arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", u, username);

        WebElement p = waitForVisible(passwordInput);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true })); arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", p, password);
    }

    public void submit() {
        WebElement btn = waitForClickable(submitButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public DashboardPage loginAs(String username, String password, int port) {
        open(port);
        enterCredentials(username, password);
        submit();
        wait.until(d -> !d.getCurrentUrl().contains("/login"));
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
