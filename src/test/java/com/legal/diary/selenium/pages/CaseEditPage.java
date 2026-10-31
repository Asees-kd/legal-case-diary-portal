package com.legal.diary.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object encapsulating case metadata modification on the /cases/{id}/edit view.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public class CaseEditPage extends BasePage {

    private final By titleInput = By.id("title");
    private final By courtInput = By.id("courtName");
    private final By submitBtn = By.cssSelector("button[type='submit']");

    public CaseEditPage(WebDriver driver) {
        super(driver);
    }

    private void setValueViaJs(By locator, String value) {
        WebElement el = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; "
            + "arguments[0].dispatchEvent(new Event('input', {bubbles:true})); "
            + "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
            el, value);
    }

    public void updateTitle(String newTitle) {
        setValueViaJs(titleInput, newTitle);
    }

    public void updateCourt(String newCourt) {
        setValueViaJs(courtInput, newCourt);
    }

    public CaseDetailPage submitChanges() {
        WebElement btn = waitForVisible(submitBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].closest('form').submit();", btn);
        return new CaseDetailPage(driver);
    }
}
