# Task 9: Selenium Automated Test Plan & Test Case Specifications

**Project:** CI/CD Pipeline for a Legal Case Diary Portal  
**Student:** Asees Kaur Dham  
**Roll Number:** 23102C0050  
**Test Framework:** Selenium 4.26, JUnit 5, Spring Boot Test (`RANDOM_PORT`), Headless Chrome  
**Maven Profile:** `selenium` (`mvn.cmd test -Pselenium`)  
**Artifact Directory:** `target/screenshots/` (on failure)  

---

## 1. Test Architecture & Design Principles

1. **Page Object Model (POM):**
   - Clean encapsulation of UI interaction logic away from test assertions.
   - Page Objects: `LoginPage`, `DashboardPage`, `CaseListPage`, `CaseCreatePage`, `CaseDetailPage`, `CaseEditPage` (inheriting from `BasePage`).
2. **Deterministic & Isolated Execution:**
   - Isolated in-memory H2 database (`jdbc:h2:mem:seleniumtestdb`) for each test run.
   - Dynamic port binding via `@SpringBootTest(webEnvironment = RANDOM_PORT)`.
   - `TestDataGenerator` provides uniquely sequenced case numbers (`DL/HC/2026/SEL-...`) to eliminate collision.
3. **Explicit Synchronization:**
   - `WebDriverWait` with `ExpectedConditions` (no brittle `Thread.sleep` calls).
4. **Failure Artifact Diagnostics:**
   - Custom JUnit 5 `ScreenshotOnFailureExtension` (`TestWatcher`) automatically captures PNG screenshots to `target/screenshots/<testName>.png` upon any test failure.

---

## 2. Test Case Specifications (TC1 to TC8)

### TC1: Multi-Role Authentication & Dashboard Authorization
- **Test ID:** `TC1`
- **Objective:** Verify that users authenticated under all three authorized chamber roles (`CLERK`, `LAWYER`, `ADMIN`) successfully reach the chamber dashboard.
- **Preconditions:** Server started on random port; browser initialized in headless Chrome.
- **Test Steps:**
  1. Open `http://localhost:<port>/login`.
  2. Input Docket Username `clerk`, Passcode `clerk123`, and click "Sign In to Chambers".
  3. Verify current URL points to `/dashboard` or root `/` and dashboard layout is rendered.
  4. Clear session cookies and repeat login steps with `lawyer` / `lawyer123`.
  5. Verify dashboard reached; clear cookies and repeat login steps with `admin` / `admin123`.
  6. Verify dashboard reached.
- **Test Data:**
  - `clerk` / `clerk123`
  - `lawyer` / `lawyer123`
  - `admin` / `admin123`
- **Expected Result:** All three roles are authenticated and navigate to the dashboard with HTTP 200 without access denial.

---

### TC2: Invalid Authentication & Error Feedback
- **Test ID:** `TC2`
- **Objective:** Verify that submitting invalid user credentials displays an error alert and restricts the user to the login view.
- **Preconditions:** Fresh unauthenticated session on `http://localhost:<port>/login`.
- **Test Steps:**
  1. Open login view.
  2. Input Docket Username `unauthorized_user` and Passcode `badpassword`.
  3. Submit the form.
  4. Inspect the resulting page URL.
  5. Check presence and text content of `.alert-danger`.
- **Test Data:** `username: "unauthorized_user"`, `password: "badpassword"`
- **Expected Result:** Browser stays on `/login?error=true`, `.alert-danger` is displayed containing "Invalid credentials".

---

### TC3: Case Registration Requisition by Clerk
- **Test ID:** `TC3`
- **Objective:** Verify that an authorized `CLERK` can file a new legal matter with valid metadata and receive a confirmation success alert.
- **Preconditions:** Authenticated as `clerk`.
- **Test Steps:**
  1. Navigate to `/cases/new`.
  2. Generate unique docket number from `TestDataGenerator`.
  3. Fill in Jurisdictional Category `CIVIL`, Cause Title, Court Name, Petitioner, Respondent, and today's date.
  4. Submit form via "Register & File Matter".
  5. Observe redirect to `/cases`.
  6. Inspect `.alert-success`.
- **Test Data:** Unique case number `DL/HC/2026/SEL-<id>`, category `CIVIL`, court `High Court of Delhi`.
- **Expected Result:** Case is created in `FILED` status; user is redirected to `/cases` with `.alert-success` citing the new case number.

---

### TC4: Case Registry Docket Visibility
- **Test ID:** `TC4`
- **Objective:** Verify that a newly registered legal matter appears in the primary case registry tabular view.
- **Preconditions:** Authenticated as `clerk`; case created via registration form.
- **Test Steps:**
  1. File a case with unique case number via `CaseCreatePage`.
  2. Navigate directly to `/cases`.
  3. Locate row in `.data-table` containing the case number link.
- **Test Data:** Newly registered case number `DL/HC/2026/SEL-<id>`.
- **Expected Result:** The case number is visible in the registry table as a clickable hyperlink with status badge "Filed".

---

### TC5: Case Metadata Modification by Legal Counsel
- **Test ID:** `TC5`
- **Objective:** Verify that a `LAWYER` or `ADMIN` can amend an existing case's cause title and court directives, and that updates persist and display immediately.
- **Preconditions:** Case exists in database; user authenticated as `lawyer`.
- **Test Steps:**
  1. Create a test case.
  2. Log in as `lawyer` and navigate to `/cases/{id}`.
  3. Click "Edit Details" button.
  4. Amend Cause Title to "Amended Title Validated via Selenium Automation".
  5. Submit changes via "Save Docket Changes".
  6. Inspect case title heading on the resulting dossier view.
- **Test Data:** `updatedTitle: "Amended Title Validated via Selenium Automation"`.
- **Expected Result:** Case dossier displays the updated cause title, along with a success banner confirming the amendment.

---

### TC6: Case Number Search & Negative Empty State Query
- **Test ID:** `TC6`
- **Objective:** Verify that searching by exact case number retrieves the target record, while a non-existent search query displays the graceful empty-state alert.
- **Preconditions:** Authenticated as `clerk` on `/cases`.
- **Test Steps:**
  1. Query exact case number of a known registered case into search input and submit.
  2. Verify matching row is returned in the table.
  3. Query nonsense string `ZZZ-NONEXISTENT-99999` and submit.
  4. Verify table shows empty-state message.
- **Test Data:**
  - Positive query: Valid case number `DL/HC/2026/SEL-<id>`
  - Negative query: `ZZZ-NONEXISTENT-99999`
- **Expected Result:** Positive query displays matching row; negative query displays "No legal cases match the specified query or status filter."

---

### TC7: Role-Based Workflow Closure Restrictions
- **Test ID:** `TC7`
- **Objective:** Enforce the business rule that `CLERK` and `LAWYER` cannot close a case (action not visible or denied), whereas `ADMIN` can formally close a case in `HEARING_SCHEDULED` status.
- **Preconditions:** A case transitioned through `FILED` &rarr; `UNDER_REVIEW` &rarr; `HEARING_SCHEDULED`.
- **Test Steps:**
  1. Log in as `clerk` and open `/cases/{id}`: verify close action button is NOT available; warning banner is displayed.
  2. Clear session, log in as `lawyer` and open `/cases/{id}`: verify close action button is NOT available; warning banner is displayed.
  3. Clear session, log in as `admin` and open `/cases/{id}`: verify close action button IS available.
  4. Admin enters closure remarks and submits "Dispose & Close Case Permanently".
  5. Inspect case status badge.
- **Test Data:** Closure remarks: "Disposed & closed via consent order by Admin".
- **Expected Result:** `clerk` and `lawyer` have no closure authorization; `admin` successfully transitions status to `CLOSED` / "Closed".

---

### TC8: Dashboard Telemetry & Metric Alignment
- **Test ID:** `TC8`
- **Objective:** Verify that the dashboard metric cards (Total, Filed, Under Review, Hearing Scheduled, Closed) accurately reflect real persistent entity counts.
- **Preconditions:** Authenticated as `admin` on `/dashboard`.
- **Test Steps:**
  1. Query actual entity counts directly from `CaseRepository` for each status.
  2. Read metric card numbers rendered in the DOM on `/dashboard`.
  3. Compare UI values against database counts.
- **Test Data:** Live entity records in in-memory test database.
- **Expected Result:** UI values exactly equal JPA repository counts for Total Registered Matters, Filed, Under Review, Hearing Scheduled, and Closed.

