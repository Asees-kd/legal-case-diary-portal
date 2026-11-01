package com.legal.diary.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object encapsulating case registry listing and search operations.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public class CaseListPage extends BasePage {

    private final By searchInput = By.name("query");
    private final By searchSubmit = By.cssSelector(".search-form button[type='submit']");
    private final By newCaseBtn = By.xpath("//a[contains(@href, '/cases/new')]");
    private final By alertSuccess = By.cssSelector(".alert-success");

    public CaseListPage(WebDriver driver) {
        super(driver);
    }

    public void open(int port) {
        driver.get("http://localhost:" + port + "/cases");
        waitForVisible(searchInput);
    }

    public void search(String query) {
        WebElement input = waitForVisible(searchInput);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles:true})); arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", input, query);
        WebElement btn = waitForClickable(searchSubmit);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public boolean hasCaseNumber(String caseNumber) {
        try {
            By link = By.xpath("//a[contains(@class,'case-number-link') and normalize-space(text())='" + caseNumber + "']");
            return waitForVisible(link).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public CaseDetailPage clickCase(String caseNumber) {
        By link = By.xpath("//a[contains(@class,'case-number-link') and normalize-space(text())='" + caseNumber + "']");
        clickElement(link);
        return new CaseDetailPage(driver);
    }

    public boolean isNoResultsDisplayed() {
        try {
            By emptyMsg = By.xpath("//*[contains(text(), 'No legal cases match')]");
            return waitForVisible(emptyMsg).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSuccessAlertDisplayed() {
        try {
            return waitForVisible(alertSuccess).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessAlertText() {
        return waitForVisible(alertSuccess).getText();
    }

    public CaseCreatePage clickFileNewCase() {
        clickElement(newCaseBtn);
        return new CaseCreatePage(driver);
    }
}
