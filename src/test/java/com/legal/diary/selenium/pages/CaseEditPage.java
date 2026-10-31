package com.legal.diary.selenium.pages;

import org.openqa.selenium.By;
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

    public void updateTitle(String newTitle) {
        WebElement el = waitForVisible(titleInput);
        el.clear();
        el.sendKeys(newTitle);
    }

    public void updateCourt(String newCourt) {
        WebElement el = waitForVisible(courtInput);
        el.clear();
        el.sendKeys(newCourt);
    }

    public CaseDetailPage submitChanges() {
        clickElement(submitBtn);
        return new CaseDetailPage(driver);
    }
}
