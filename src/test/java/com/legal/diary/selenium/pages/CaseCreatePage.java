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

    public void fillForm(String caseNumber, String typeValue, String title, String court, String petitioner, String respondent, LocalDate filedDate) {
        WebElement cn = waitForVisible(caseNumberInput);
        cn.clear();
        cn.sendKeys(caseNumber);

        new Select(waitForVisible(caseTypeSelect)).selectByValue(typeValue);

        WebElement t = waitForVisible(titleInput);
        t.clear();
        t.sendKeys(title);

        WebElement c = waitForVisible(courtNameInput);
        c.clear();
        c.sendKeys(court);

        WebElement p = waitForVisible(petitionerInput);
        p.clear();
        p.sendKeys(petitioner);

        WebElement r = waitForVisible(respondentInput);
        r.clear();
        r.sendKeys(respondent);

        WebElement d = waitForVisible(filedDateInput);
        String dateStr = filedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", d, dateStr);
        } catch (Exception e) {
            d.clear();
            d.sendKeys(dateStr);
        }
    }

    public CaseListPage submitForm() {
        clickElement(submitBtn);
        return new CaseListPage(driver);
    }
}
