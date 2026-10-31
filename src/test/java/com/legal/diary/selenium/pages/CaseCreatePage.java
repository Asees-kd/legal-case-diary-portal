package com.legal.diary.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Page Object encapsulating case creation requisition on the /cases/new view.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public class CaseCreatePage extends BasePage {

    private final By caseNumberInput = By.id("caseNumber");
    private final By caseTypeSelect = By.id("caseType");
    private final By titleInput = By.id("title");
    private final By courtNameInput = By.id("courtName");
    private final By petitionerInput = By.id("petitionerName");
    private final By respondentInput = By.id("respondentName");
    private final By filedDateInput = By.id("filedDate");
    private final By submitBtn = By.cssSelector("button[type='submit']");

    public CaseCreatePage(WebDriver driver) {
        super(driver);
    }

    public void open(int port) {
        driver.get("http://localhost:" + port + "/cases/new");
        waitForVisible(caseNumberInput);
    }

    private void setValueViaJs(By locator, String value) {
        WebElement el = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; "
            + "arguments[0].dispatchEvent(new Event('input', {bubbles:true})); "
            + "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
            el, value);
    }

    public void fillForm(String caseNumber, String typeValue, String title, String court, String petitioner, String respondent, LocalDate filedDate) {
        setValueViaJs(caseNumberInput, caseNumber);
        new Select(waitForVisible(caseTypeSelect)).selectByValue(typeValue);
        setValueViaJs(titleInput, title);
        setValueViaJs(courtNameInput, court);
        setValueViaJs(petitionerInput, petitioner);
        setValueViaJs(respondentInput, respondent);
        String dateStr = filedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        setValueViaJs(filedDateInput, dateStr);
    }

    public CaseListPage submitForm() {
        WebElement btn = waitForVisible(submitBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].closest('form').submit();", btn);
        return new CaseListPage(driver);
    }
}
