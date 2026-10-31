package com.legal.diary.selenium;

import com.legal.diary.model.CaseStatus;
import com.legal.diary.model.CaseType;
import com.legal.diary.model.LegalCase;
import com.legal.diary.repository.CaseRepository;
import com.legal.diary.selenium.extension.ScreenshotOnFailureExtension;
import com.legal.diary.selenium.model.TestDataGenerator;
import com.legal.diary.selenium.pages.*;
import com.legal.diary.service.CaseService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end automated Selenium 4 test suite for Legal Case Diary Portal.
 * Runs in headless Chrome on a random port with an isolated in-memory H2 database.
 * Exactly 8 test cases validating RBAC, case registration, workflow transitions, search, and dashboard telemetry.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Tag("selenium")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:seleniumtestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver"
})
@ExtendWith(ScreenshotOnFailureExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LegalDiarySeleniumTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CaseService caseService;

    @Autowired
    private CaseRepository caseRepository;

    private static WebDriver driver;

    @BeforeAll
    static void setupDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1400,900");
        options.addArguments("--remote-allow-origins=*");

        File chromeWin = new File("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        if (chromeWin.exists()) {
            options.setBinary(chromeWin);
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        ScreenshotOnFailureExtension.setDriver(driver);
    }

    @AfterAll
    static void tearDownDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
        }
    }

    @BeforeEach
    void resetSession() {
        driver.manage().deleteAllCookies();
    }

    @Test
    @Order(1)
    @DisplayName("TC1: Valid login for each role (clerk, lawyer, admin) reaches the dashboard")
    void testTC1_validLoginForEachRoleReachesDashboard() {
        LoginPage loginPage = new LoginPage(driver);

        // 1. Clerk login
        DashboardPage clerkDashboard = loginPage.loginAs("clerk", "clerk123", port);
        assertTrue(clerkDashboard.isOnDashboard(), "Clerk should reach the chamber dashboard upon successful authentication");
        driver.manage().deleteAllCookies();

        // 2. Lawyer login
        DashboardPage lawyerDashboard = loginPage.loginAs("lawyer", "lawyer123", port);
        assertTrue(lawyerDashboard.isOnDashboard(), "Lawyer should reach the chamber dashboard upon successful authentication");
        driver.manage().deleteAllCookies();

        // 3. Admin login
        DashboardPage adminDashboard = loginPage.loginAs("admin", "admin123", port);
        assertTrue(adminDashboard.isOnDashboard(), "Admin should reach the chamber dashboard upon successful authentication");
    }

    @Test
    @Order(2)
    @DisplayName("TC2: Invalid login shows an error message and stays on the login page")
    void testTC2_invalidLoginShowsErrorMessageAndStaysOnLoginPage() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(port);
        loginPage.enterCredentials("unauthorized_user", "badpassword");
        loginPage.submit();

        assertTrue(loginPage.isOnLoginPage(), "User must remain on the login page after failed authentication");
        assertTrue(loginPage.isErrorAlertDisplayed(), "Error alert banner must be displayed");
        assertTrue(loginPage.getErrorAlertText().contains("Invalid credentials"),
                "Alert message should state invalid credentials");
    }

    @Test
    @Order(3)
    @DisplayName("TC3: Clerk creates a new case with valid data and sees a success outcome")
    void testTC3_clerkCreatesNewCaseWithValidDataAndSeesSuccessOutcome() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("clerk", "clerk123", port);

        CaseCreatePage createPage = new CaseCreatePage(driver);
        createPage.open(port);

        String uniqueCaseNum = TestDataGenerator.generateUniqueCaseNumber();
        String title = TestDataGenerator.generateTitle();
        String court = TestDataGenerator.generateCourt();
        String petitioner = TestDataGenerator.generatePetitioner();
        String respondent = TestDataGenerator.generateRespondent();
        LocalDate filedDate = LocalDate.now();

        createPage.fillForm(uniqueCaseNum, "CIVIL", title, court, petitioner, respondent, filedDate);
        CaseListPage listPage = createPage.submitForm();

        assertTrue(listPage.isSuccessAlertDisplayed(), "Success banner must be displayed upon filing");
        assertTrue(listPage.getSuccessAlertText().contains(uniqueCaseNum),
                "Success message must cite the newly registered case number");
    }

    @Test
    @Order(4)
    @DisplayName("TC4: The created case appears in the cases list")
    void testTC4_createdCaseAppearsInCasesList() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("clerk", "clerk123", port);

        String uniqueCaseNum = TestDataGenerator.generateUniqueCaseNumber();
        CaseCreatePage createPage = new CaseCreatePage(driver);
        createPage.open(port);
        createPage.fillForm(uniqueCaseNum, "CRIMINAL", TestDataGenerator.generateTitle(),
                TestDataGenerator.generateCourt(), TestDataGenerator.generatePetitioner(),
                TestDataGenerator.generateRespondent(), LocalDate.now());
        CaseListPage listPage = createPage.submitForm();

        listPage.open(port);
        assertTrue(listPage.hasCaseNumber(uniqueCaseNum),
                "Newly registered case " + uniqueCaseNum + " must appear in the case registry docket");
    }

    @Test
    @Order(5)
    @DisplayName("TC5: Lawyer or admin updates an existing case and the change is visible")
    void testTC5_lawyerOrAdminUpdatesExistingCaseAndChangeIsVisible() {
        String caseNum = TestDataGenerator.generateUniqueCaseNumber();
        LegalCase newCase = new LegalCase(
                caseNum,
                "Original Pre-Amendment Cause Title",
                CaseType.CORPORATE,
                "National Company Law Tribunal",
                "Petitioner Commercial",
                "Respondent Finance",
                LocalDate.now()
        );
        LegalCase saved = caseService.createCase(newCase, "clerk");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("lawyer", "lawyer123", port);

        CaseDetailPage detailPage = new CaseDetailPage(driver);
        detailPage.open(port, saved.getId());

        CaseEditPage editPage = detailPage.clickEdit();
        String updatedTitle = "Amended Title Validated via Selenium Automation";
        editPage.updateTitle(updatedTitle);
        CaseDetailPage updatedDetailPage = editPage.submitChanges();

        assertEquals(updatedTitle, updatedDetailPage.getCaseTitle(),
                "Case detail view must reflect the amended title");
        assertTrue(updatedDetailPage.isSuccessAlertDisplayed(),
                "Success notification must confirm case amendment");
    }

    @Test
    @Order(6)
    @DisplayName("TC6: Search by case number finds the case, and a nonsense search shows the no-results state")
    void testTC6_searchByCaseNumberFindsCaseAndNonsenseShowsNoResults() {
        String caseNum = TestDataGenerator.generateUniqueCaseNumber();
        LegalCase testCase = new LegalCase(
                caseNum,
                "Search Target Discovery Matter",
                CaseType.CIVIL,
                "Delhi District Court",
                "Party Alpha",
                "Party Beta",
                LocalDate.now()
        );
        caseService.createCase(testCase, "clerk");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("clerk", "clerk123", port);

        CaseListPage listPage = new CaseListPage(driver);
        listPage.open(port);

        // 1. Search existing case number
        listPage.search(caseNum);
        assertTrue(listPage.hasCaseNumber(caseNum),
                "Search for " + caseNum + " must return the matching case row");

        // 2. Search nonsense string
        listPage.search("ZZZ-NONEXISTENT-99999");
        assertTrue(listPage.isNoResultsDisplayed(),
                "Nonsense search must render the empty state notice");
    }

    @Test
    @Order(7)
    @DisplayName("TC7: Role rule: a clerk and a lawyer cannot close a case, and an admin can close a scheduled case")
    void testTC7_roleRuleClerkAndLawyerCannotCloseCaseAndAdminCanCloseScheduledCase() {
        String caseNum = TestDataGenerator.generateUniqueCaseNumber();
        LegalCase testCase = new LegalCase(
                caseNum,
                "State Transition Closure Rule Test Case",
                CaseType.CONSTITUTIONAL,
                "Supreme Court of India",
                "Petitioner Citizen",
                "Respondent Union",
                LocalDate.now().minusDays(5)
        );
        LegalCase saved = caseService.createCase(testCase, "clerk");

        // Transition through lifecycle: FILED -> UNDER_REVIEW -> HEARING_SCHEDULED
        var lawyerAuth = List.of(new SimpleGrantedAuthority("ROLE_LAWYER"));
        caseService.transitionStatus(saved.getId(), CaseStatus.UNDER_REVIEW, null, null, "Audited", "lawyer", lawyerAuth);
        caseService.transitionStatus(saved.getId(), CaseStatus.HEARING_SCHEDULED, LocalDate.now().plusDays(4), "Bench 1", "Scheduled", "lawyer", lawyerAuth);

        LoginPage loginPage = new LoginPage(driver);

        // 1. Verify Clerk cannot close
        loginPage.loginAs("clerk", "clerk123", port);
        CaseDetailPage clerkDetailPage = new CaseDetailPage(driver);
        clerkDetailPage.open(port, saved.getId());
        assertFalse(clerkDetailPage.isCloseActionAvailable(), "Clerk must not have access to close case action");
        assertTrue(clerkDetailPage.isUnauthorizedCloseNoticeDisplayed(), "Clerk should see administrator restriction notice");
        driver.manage().deleteAllCookies();

        // 2. Verify Lawyer cannot close
        loginPage.loginAs("lawyer", "lawyer123", port);
        CaseDetailPage lawyerDetailPage = new CaseDetailPage(driver);
        lawyerDetailPage.open(port, saved.getId());
        assertFalse(lawyerDetailPage.isCloseActionAvailable(), "Lawyer must not have access to close case action");
        assertTrue(lawyerDetailPage.isUnauthorizedCloseNoticeDisplayed(), "Lawyer should see administrator restriction notice");
        driver.manage().deleteAllCookies();

        // 3. Verify Admin CAN close
        loginPage.loginAs("admin", "admin123", port);
        CaseDetailPage adminDetailPage = new CaseDetailPage(driver);
        adminDetailPage.open(port, saved.getId());
        assertTrue(adminDetailPage.isCloseActionAvailable(), "Admin must have access to formal closure action");

        adminDetailPage.closeCase("Disposed & closed via consent order by Admin");
        assertEquals("Closed", adminDetailPage.getStatusBadgeText(),
                "Case status must transition to Closed following Admin action");
    }

    @Test
    @Order(8)
    @DisplayName("TC8: The dashboard status counts match the real data")
    void testTC8_dashboardStatusCountsMatchRealData() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("admin", "admin123", port);

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.navigateTo("/dashboard", port);
        assertTrue(dashboardPage.isOnDashboard());

        int uiTotal = dashboardPage.getTotalCount();
        int uiFiled = dashboardPage.getFiledCount();
        int uiUnderReview = dashboardPage.getUnderReviewCount();
        int uiHearingScheduled = dashboardPage.getHearingScheduledCount();
        int uiClosed = dashboardPage.getClosedCount();

        long dbTotal = caseRepository.count();
        long dbFiled = caseRepository.findByStatus(CaseStatus.FILED).size();
        long dbUnderReview = caseRepository.findByStatus(CaseStatus.UNDER_REVIEW).size();
        long dbHearingScheduled = caseRepository.findByStatus(CaseStatus.HEARING_SCHEDULED).size();
        long dbClosed = caseRepository.findByStatus(CaseStatus.CLOSED).size();

        assertEquals(dbTotal, uiTotal, "Dashboard total count must match database entity count");
        assertEquals(dbFiled, uiFiled, "Dashboard FILED count must match database FILED count");
        assertEquals(dbUnderReview, uiUnderReview, "Dashboard UNDER_REVIEW count must match database count");
        assertEquals(dbHearingScheduled, uiHearingScheduled, "Dashboard HEARING_SCHEDULED count must match database count");
        assertEquals(dbClosed, uiClosed, "Dashboard CLOSED count must match database CLOSED count");
    }
}

