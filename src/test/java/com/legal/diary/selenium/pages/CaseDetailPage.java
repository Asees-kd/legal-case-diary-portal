package com.legal.diary.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Page Object encapsulating case dossier inspection and status transitions.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public class CaseDetailPage extends BasePage {

    private final By titleHeading = By.cssSelector(".main-content h2.serif-heading");
    private final By statusPills = By.cssSelector(".main-content .status-pill");
    private final By editBtn = By.xpath("//a[contains(@href, '/edit')]");
    private final By advanceReviewBtn = By.xpath("//form[contains(@action, '/status')][.//input[@value='UNDER_REVIEW']]//button");
    private final By hearingDateInput = By.name("hearingDate");
    private final By commitHearingBtn = By.xpath("//form[contains(@action, '/status')][.//input[@value='HEARING_SCHEDULED']]//button");
    private final By closeRemarksInput = By.name("remarks");
    private final By closeSubmitBtn = By.xpath("//form[contains(@action, '/status')][.//input[@value='CLOSED']]//button");
    private final By unauthorizedCloseNotice = By.xpath("//div[contains(@class,'alert-warning') and contains(.,'Only the Chamber Administrator')]");
    private final By alertSuccess = By.cssSelector(".alert-success");

    public CaseDetailPage(WebDriver driver) {
        super(driver);
    }

    public void open(int port, Long id) {
        driver.get("http://localhost:" + port + "/cases/" + id);
        waitForVisible(titleHeading);
    }

    public String getCaseTitle() {
        return waitForVisible(titleHeading).getText().trim();
    }

    public String getStatusBadgeText() {
        var pills = driver.findElements(statusPills);
        if (pills.size() >= 2) {
            return pills.get(1).getText().trim();
        }
        return waitForVisible(statusPills).getText().trim();
    }

    public boolean isCloseActionAvailable() {
        try {
            return driver.findElements(closeSubmitBtn).size() > 0 && driver.findElement(closeSubmitBtn).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUnauthorizedCloseNoticeDisplayed() {
        try {
            return waitForVisible(unauthorizedCloseNotice).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void advanceToUnderReview(String remarks) {
        if (remarks != null && !remarks.isBlank()) {
            WebElement rem = driver.findElement(By.name("remarks"));
            rem.clear();
            rem.sendKeys(remarks);
        }
        clickElement(advanceReviewBtn);
        waitForVisible(titleHeading);
    }

    public void scheduleHearing(LocalDate date, String notes) {
        WebElement dateEl = waitForVisible(hearingDateInput);
        dateEl.clear();
        dateEl.sendKeys(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (notes != null && !notes.isBlank()) {
            WebElement notesEl = driver.findElement(By.name("hearingNotes"));
            notesEl.clear();
            notesEl.sendKeys(notes);
        }
        clickElement(commitHearingBtn);
        waitForVisible(titleHeading);
    }

    public void closeCase(String remarks) {
        WebElement rem = waitForVisible(closeRemarksInput);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; "
            + "arguments[0].dispatchEvent(new Event('input', {bubbles:true})); "
            + "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
            rem, remarks);
        WebElement btn = waitForVisible(closeSubmitBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].closest('form').submit();", btn);
        waitForVisible(titleHeading);
    }

    public CaseEditPage clickEdit() {
        clickElement(editBtn);
        return new CaseEditPage(driver);
    }

    public boolean isSuccessAlertDisplayed() {
        try {
            return waitForVisible(alertSuccess).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
