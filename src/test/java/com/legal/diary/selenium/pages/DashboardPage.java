package com.legal.diary.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object encapsulating metrics and navigation on the /dashboard view.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public class DashboardPage extends BasePage {

    private final By totalMatters = By.xpath("//div[contains(@class,'metric-card')][.//div[contains(text(),'Total Registered')]]//div[contains(@class,'metric-value')]");
    private final By filedMatters = By.xpath("//div[contains(@class,'metric-card')][.//div[contains(text(),'Filed')]]//div[contains(@class,'metric-value')]");
    private final By underReviewMatters = By.xpath("//div[contains(@class,'metric-card')][.//div[contains(text(),'Under Scrutiny')]]//div[contains(@class,'metric-value')]");
    private final By hearingScheduledMatters = By.xpath("//div[contains(@class,'metric-card')][.//div[contains(text(),'Hearing Scheduled')]]//div[contains(@class,'metric-value')]");
    private final By closedMatters = By.xpath("//div[contains(@class,'metric-card')][.//div[contains(text(),'Disposed')]]//div[contains(@class,'metric-value')]");
    private final By casesLink = By.xpath("//nav[contains(@class,'sidebar-nav')]//a[contains(@href, '/cases') and not(contains(@href, '/new'))]");
    private final By newCaseLink = By.xpath("//nav[contains(@class,'sidebar-nav')]//a[contains(@href, '/cases/new')]");
    private final By logoutBtn = By.xpath("//form[contains(@action, '/logout')]//button");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOnDashboard() {
        return wait.until(d -> d.getCurrentUrl().contains("/dashboard") || (d.getCurrentUrl().endsWith("/") && !d.getCurrentUrl().contains("/login")));
    }

    public int getTotalCount() {
        return Integer.parseInt(waitForVisible(totalMatters).getText().trim());
    }

    public int getFiledCount() {
        return Integer.parseInt(waitForVisible(filedMatters).getText().trim());
    }

    public int getUnderReviewCount() {
        return Integer.parseInt(waitForVisible(underReviewMatters).getText().trim());
    }

    public int getHearingScheduledCount() {
        return Integer.parseInt(waitForVisible(hearingScheduledMatters).getText().trim());
    }

    public int getClosedCount() {
        return Integer.parseInt(waitForVisible(closedMatters).getText().trim());
    }

    public CaseListPage goToCases() {
        clickElement(casesLink);
        return new CaseListPage(driver);
    }

    public CaseCreatePage goToCreateCase() {
        clickElement(newCaseLink);
        return new CaseCreatePage(driver);
    }

    public LoginPage logout(int port) {
        try {
            clickElement(logoutBtn);
        } catch (Exception e) {
            driver.get("http://localhost:" + port + "/logout");
        }
        return new LoginPage(driver);
    }
}
