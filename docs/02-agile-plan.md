# 02. Agile Planning & DevOps Workflow
**Project:** CI/CD Pipeline for a Legal Case Diary Portal  
**Student:** Asees Kaur Dham  
**Roll Number:** 23102C0050  
**Workspace:** `C:\Users\ssing\Desktop\devops`  
**Date:** 2026-10-29  

---

## 1. User Stories & Acceptance Criteria

### User Story 1: New Case Filing (Clerk)
- **ID:** `US-01`
- **Story:** *As a Court Clerk (`CLERK`), I want to record newly instituted legal matters with court details, party names, and case category so that the firm maintains a centralized, audited docket of incoming litigation.*
- **Acceptance Criteria:**
  1. User authenticated with role `CLERK` can access the `/cases/new` form.
  2. Form requires non-empty inputs for Case Number, Title, Case Type (Civil, Criminal, Constitutional, Corporate, Family), Court Name, Petitioner Name, and Respondent Name.
  3. Upon submission, the case is automatically persisted with initial status `FILED` and current creation timestamp.
  4. Form validation displays field-specific error messages if required data is missing.
  5. The clerk is redirected to the case list with a success confirmation toast banner.

---

### User Story 2: Case Intake Review & Assignment (Lawyer)
- **ID:** `US-02`
- **Story:** *As an Associate Lawyer (`LAWYER`), I want to review matters in `FILED` status and advance them to `UNDER_REVIEW` so that the litigation team can draft pleadings and scrutinize evidence.*
- **Acceptance Criteria:**
  1. User authenticated with role `LAWYER` or `ADMIN` can view the "Move to Under Review" action on case detail.
  2. If a user with role `CLERK` attempts this transition, the action button is not rendered and any direct POST triggers HTTP 403 Forbidden.
  3. Successful transition updates the database state to `UNDER_REVIEW` and records the acting user.
  4. The case detail timeline reflects the transition timestamp.

---

### User Story 3: Court Hearing Scheduling (Lawyer)
- **ID:** `US-03`
- **Story:** *As an Associate Lawyer (`LAWYER`), I want to assign a next hearing date and courtroom notes to an active case in `UNDER_REVIEW` status and transition it to `HEARING_SCHEDULED` so that advocates never miss court appearances.*
- **Acceptance Criteria:**
  1. Lawyer or Admin can select a future hearing date and optional courtroom notes.
  2. Case status transitions from `UNDER_REVIEW` to `HEARING_SCHEDULED`.
  3. Hearing date must be validated to ensure it is in the present or future.
  4. The scheduled date is highlighted in oxblood red if within 48 hours to denote urgency.

---

### User Story 4: Formal Case Closure & Disposal (Admin Exclusivity)
- **ID:** `US-04`
- **Story:** *As a Managing Partner (`ADMIN`), I want exclusive authority to formally close a matter from `HEARING_SCHEDULED` to `CLOSED` after final decree so that junior staff cannot prematurely or mistakenly archive active litigation.*
- **Acceptance Criteria:**
  1. Only users with role `ADMIN` see the "Close Case" action.
  2. Transition requires case to currently be in `HEARING_SCHEDULED` status.
  3. If a `CLERK` or `LAWYER` attempts to invoke the `/cases/{id}/close` endpoint, Spring Security enforces access denial (HTTP 403).
  4. Closed cases display a distinct sage-green/oxblood archived pill and disable further routine edits.

---

### User Story 5: Multi-Field Search & Docket Filtering
- **ID:** `US-05`
- **Story:** *As a Clerk, Lawyer, or Admin, I want to search the case register by case number, case title, court name, or filter by status so that I can locate briefs and files within two seconds.*
- **Acceptance Criteria:**
  1. A dedicated search bar is available on `/cases`.
  2. Search accepts partial matching (case-insensitive) across `caseNumber`, `title`, and `courtName`.
  3. Dropdown filter allows restricting results to a single status (`FILED`, `UNDER_REVIEW`, `HEARING_SCHEDULED`, `CLOSED`) or `ALL`.
  4. Query response renders in under 2 seconds.
  5. If no records match, an informative empty-state message is displayed.

---

### User Story 6: Executive Status Dashboard
- **ID:** `US-06`
- **Story:** *As a Managing Partner or Advocate, I want to view a real-time summary dashboard with aggregate counts for each status stage so that I can assess chamber workload and resource bottlenecks at a glance.*
- **Acceptance Criteria:**
  1. Dashboard at `/dashboard` presents four primary metric cards: Total Filed, Under Review, Hearing Scheduled, and Closed.
  2. Cards dynamically reflect database aggregations (`SELECT COUNT(c) FROM LegalCase c WHERE c.status = :status`).
  3. Clicking any status card redirects to the case list pre-filtered by that status.
  4. Table of "Upcoming Hearings" shows the next 5 chronological hearings.

---

### User Story 7: Case Detail View & Lifecycle Timeline
- **ID:** `US-07`
- **Story:** *As a Chamber Litigator, I want to view a full case brief page with an interactive visual lifecycle timeline so that I can see the matter's chronological progression from filing to disposal.*
- **Acceptance Criteria:**
  1. Route `/cases/{id}` displays full case metadata (CNR number, filing date, court, judge/forum, parties, notes).
  2. Visual 4-step timeline highlights the current stage in brass `#B08D3C` with completed stages marked in sage green `#4F6F52`.
  3. Role-permitted action buttons for next valid transitions appear directly adjacent to the timeline.

---

### User Story 8: Role-Based Authentication & Guarded Access
- **ID:** `US-08`
- **Story:** *As a System Administrator, I want in-memory user authentication with role-based URL protection and CSRF defense so that unauthorized visitors cannot access or mutate legal records.*
- **Acceptance Criteria:**
  1. Unauthenticated requests to `/dashboard`, `/cases/**` redirect to `/login`.
  2. In-memory credentials supported: `clerk/clerk123` (`ROLE_CLERK`), `lawyer/lawyer123` (`ROLE_LAWYER`), `admin/admin123` (`ROLE_ADMIN`).
  3. Passwords encrypted using BCrypt.
  4. CSRF tokens enforced on all POST/PUT/DELETE forms.
  5. Logout link invalidates HTTP session and clears security context.

---

### User Story 9: Automated Build & Test on Commit (DevOps CI)
- **ID:** `US-09`
- **Story:** *As a DevOps Engineer, I want every Git commit to automatically trigger a clean compile, unit test suite (JUnit 5), and a Selenium WebDriver suite of 8 test cases (login valid/invalid, create case, case in list, update, search, role rule clerk cannot close/admin can, dashboard counts) in Jenkins so that regressions are caught before artifact release.*
- **Acceptance Criteria:**
  1. Jenkins pipeline executes on port `9090` using Windows batch (`bat`) commands.
  2. Tools invoked: JDK 21 runner compiling to Java 17 target, Maven 3.9.16.
  3. Continuous testing executes JUnit 5 unit tests and the 8 Selenium test cases in headless Chrome.
  4. Build fails immediately if any JUnit or Selenium test fails.
  5. Surefire test reports are archived and visible in the Jenkins build summary.

---

### User Story 10: Automated Artifact Release & Deployment (DevOps CD)
- **ID:** `US-10`
- **Story:** *As a DevOps Engineer, I want the Jenkins pipeline to package a production WAR and automatically deploy it to Tomcat 10.1 (`C:\tomcat10`) and publish a Docker image to Docker Hub (`aseeskaurd/legal-diary`) so that deployments are fully repeatable and hands-free.*
- **Acceptance Criteria:**
  1. Pipeline packages `target/legal-diary.war` with Jakarta EE 10 compliance.
  2. Deployment stage copies WAR into `C:\tomcat10\webapps\legal-diary.war` without manual file transfer.
  3. Pipeline builds Docker image tagged `aseeskaurd/legal-diary:latest` and validates container launch on port `8082`.
  4. Deployment verification smoke tests endpoint `http://localhost:8080/legal-diary/login` returning HTTP 200.

---

## 2. Data Model Notes

- **`LegalCase` Entity:** In addition to primary litigation metadata (Case Number, Title, Case Type, Court Name, Petitioner Name, Respondent Name, Filing Date, and Status), `LegalCase` must include:
  - `hearingDate` (`LocalDate` / `LocalDateTime`): Target scheduled court appearance date.
  - `hearingNotes` (`String` / text): Procedural directions, courtroom notes, and bench orders.
- **`CaseStatusHistory` Entity:** Dedicated lifecycle transition entity used to render the dynamic chronological status timeline on the case detail page (`/cases/{id}`):
  - `caseId` (`Long`): Foreign key association to the parent `LegalCase`.
  - `fromStatus` (`CaseStatus`): The previous status before transition.
  - `toStatus` (`CaseStatus`): The updated status after transition.
  - `changedBy` (`String`): Authenticated username of the user who initiated the change (`clerk`, `lawyer`, or `admin`).
  - `changedAt` (`LocalDateTime`): Exact timestamp of the status transition.

---

## 3. Product Backlog Matrix (MoSCoW & Story Points)

| Story ID | User Story Title | Linked Backlog Item | MoSCoW Priority | Story Points (Fibonacci) |
| :--- | :--- | :--- | :---: | :---: |
| **US-01** | New Case Filing & Form Validation | **B-04, B-05, B-07, B-09** | **Must Have** | 5 |
| **US-02** | Case Intake Review Transition | **B-05, B-07, B-09** | **Must Have** | 3 |
| **US-03** | Court Hearing Scheduling & Urgency Flag | **B-05, B-07, B-09** | **Must Have** | 5 |
| **US-04** | Formal Case Closure (Admin Only) | **B-05, B-06, B-07** | **Must Have** | 3 |
| **US-05** | Case Registry Search & Filter | **B-04, B-05, B-07** | **Must Have** | 5 |
| **US-06** | Executive Status Dashboard & Metric Cards | **B-05, B-07, B-09** | **Should Have** | 5 |
| **US-07** | Case Detail Brief & Lifecycle Timeline | **B-07, B-08, B-09** | **Should Have** | 5 |
| **US-08** | In-Memory Security & RBAC Enforcement | **B-06, B-10** | **Must Have** | 3 |
| **US-09** | Automated CI Build & Continuous Testing | **B-10, B-13** | **Must Have** | 8 |
| **US-10** | Automated CD to Tomcat 10 & Docker Registry | **B-11, B-13, B-14** | **Must Have** | 8 |
| **Total Story Points** | | | | **50 SP** |

---

## 4. Kanban Board Plan (15 Backlog Items: B-01 to B-15)

```
+------------------------------------+--------------------------+------------------------------------+
|            TO DO (14)              |     IN PROGRESS (0)      |              DONE (1)              |
+------------------------------------+--------------------------+------------------------------------+
| [B-02] Architecture & System Design|                          | [B-01] Problem Definition &        |
| [B-03] Maven Scaffolding (WAR)     |                          |        Requirements Specification  |
| [B-04] Domain Entities & JPA Repos |                          |        (docs/01-problem-definition)|
| [B-05] RBAC State Transition Engine|                          |                                    |
| [B-06] Spring Security Config      |                          |                                    |
| [B-07] MVC Web Controllers         |                          |                                    |
| [B-08] Handwritten CSS Theme       |                          |                                    |
| [B-09] Thymeleaf Views & Timeline  |                          |                                    |
| [B-10] Automated Testing (JUnit 5 +|                          |                                    |
|        8 Selenium Test Cases)      |                          |                                    |
| [B-11] WAR Build & Tomcat Deploy   |                          |                                    |
| [B-12] Git Version Control Setup   |                          |                                    |
| [B-13] Jenkins Pipeline as Code    |                          |                                    |
| [B-14] Docker Containerization     |                          |                                    |
| [B-15] Ansible via WSL2 & Audit    |                          |                                    |
+------------------------------------+--------------------------+------------------------------------+
```

### Backlog Status Ledger:
- **`B-01` Problem Definition & Requirements Specification:** **DONE** (Logged in `docs/01-problem-definition.md`).
- **`B-02` Architecture & System Design Document:** **TO DO** (Next design deliverable).
- **`B-03` to `B-09`:** **TO DO** (Scheduled across Sprint 1).
- **`B-10` Automated Testing: JUnit 5 unit tests, plus Selenium WebDriver suite of 8 test cases (login valid/invalid, create case, case in list, update, search, role rule clerk cannot close/admin can, dashboard counts):** **TO DO** (Scheduled in Sprint 2).
- **`B-11` to `B-15`:** **TO DO** (Scheduled in Sprint 2).

---

## 5. Two-Sprint Implementation Plan

### Sprint 1: Architectural Foundation & Core Backend Application
- **Duration:** 1 Week (7 Calendar Days)
- **Goal:** Deliver an end-to-end working domain model, persistent file-mode H2 storage, strict RBAC state transition rules, and authenticated Spring Security endpoints.
- **Included Backlog Items:**
  - `B-01` Problem Definition (Done)
  - `B-02` Architecture & System Design Document
  - `B-03` Maven Scaffolding (Java 17, Spring Boot 3, WAR, Tomcat 10.1 dependencies)
  - `B-04` Domain Entities (`LegalCase`, `CaseStatus`, `CaseType`, `CaseStatusHistory`) & JPA Repositories
  - `B-05` Business Logic Service & Strict RBAC State Transition Rules
  - `B-06` Spring Security Configuration & In-Memory RBAC (`clerk`, `lawyer`, `admin`)
  - `B-07` MVC Web Controllers & Query Parameters
- **Story Points Committed:** 24 SP
- **Sprint 1 Key Risks & Mitigations:**
  1. *Risk:* Jakarta EE 10 namespace incompatibility with legacy servlet libraries.  
     *Mitigation:* Use strictly Spring Boot 3.3+ and `jakarta.*` imports; declare `provided` scope for embedded Tomcat servlet API.
  2. *Risk:* Database reset between server restarts causing lost test data.  
     *Mitigation:* Configure H2 in file persistence mode (`jdbc:h2:file:./data/casediary`) instead of purely in-memory mode.

---

### Sprint 2: Bespoke UI, Test Suites, CI/CD Pipeline & Infrastructure Automation
- **Duration:** 1 Week (7 Calendar Days)
- **Goal:** Author handcrafted legal firm CSS templates, execute local Selenium/JUnit tests, configure Jenkins CI/CD pipeline on port 9090, package Docker container, and run Ansible automation via WSL2.
- **Included Backlog Items:**
  - `B-08` Bespoke Legal Handcrafted CSS Design System (`#F7F4EE`, `#14213D`, `#B08D3C`, `#7A2E2E`, `#4F6F52`)
  - `B-09` Responsive Thymeleaf View Templates (Login, Dashboard, Registry, Detail Timeline, Filing Form)
  - `B-10` Automated Testing: JUnit 5 unit tests, plus Selenium WebDriver suite of 8 test cases (login valid/invalid, create case, case in list, update, search, role rule clerk cannot close/admin can, dashboard counts)
  - `B-11` Local WAR Packaging & Deployment to `C:\tomcat10\webapps` (Port `8080`)
  - `B-12` Git Version Control Setup & Branching Strategy (`main`, `develop`, feature branches)
  - `B-13` Jenkins Pipeline as Code (`Jenkinsfile` with Windows `bat` commands, Port `9090`)
  - `B-14` Docker Containerization (`Dockerfile`, Docker Compose, Port `8082`, Hub push to `aseeskaurd`)
  - `B-15` Ansible Automation via WSL2 (`asees` user, Port `8083`) & Final System Verification
- **Story Points Committed:** 26 SP
- **Sprint 2 Key Risks & Mitigations:**
  1. *Risk:* Jenkins running as a Windows Service failing on batch path lookups for Maven or Git.  
     *Mitigation:* Reference Jenkins Global Tool Configuration names (`JDK21`, `Maven3`) and explicit absolute paths in Windows batch steps.
  2. *Risk:* WSL2 Ubuntu environment unable to reach Windows host Tomcat port.  
     *Mitigation:* Use host gateway IP (`/etc/resolv.conf` or `host.docker.internal`) in Ansible inventory.

---

## 6. Definition of Done (DoD) Checklist

A backlog item or user story is considered **DONE** only when all of the following conditions are verified:

- [ ] **Code Compilation:** Source code compiles with zero errors using JDK 17 and Maven 3.9.16 (`mvn clean compile`).
- [ ] **Code Review & Pull Request:** Code developed on a designated feature branch (`feature/*`) and merged into `develop` through a reviewed Pull Request.
- [ ] **Automated Testing:** All unit tests (`JUnit 5`) and browser automation tests (`Selenium WebDriver`) execute and pass ($100\%$ success rate).
- [ ] **Static Code Quality:** Zero warnings for deprecated APIs, zero hardcoded database passwords, and strict adherence to `jakarta.*` packages.
- [ ] **CI Pipeline Validation:** Jenkins CI job on port 9090 triggers, executes all stages (`bat`), and reports a **GREEN** build status.
- [ ] **Artifact Deployment:** WAR file deploys successfully to Apache Tomcat 10.1 (`C:\tomcat10\webapps`), and application responds with HTTP 200 at `http://localhost:8080/legal-diary`.
- [ ] **Documentation & Traceability:** Code comments preserved, [`PROGRESS.md`](file:///c:/Users/ssing/Desktop/devops/PROGRESS.md) updated with exact commands, files, ports, and manual verification steps.

