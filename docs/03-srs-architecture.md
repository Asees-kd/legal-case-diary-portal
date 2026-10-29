# 03. Software Requirements Specification & System Architecture
**Project:** CI/CD Pipeline for a Legal Case Diary Portal  
**Student:** Asees Kaur Dham  
**Roll Number:** 23102C0050  
**Workspace:** `C:\Users\ssing\Desktop\devops`  
**Date:** 2026-10-29  

---

## 1. Executive Summary & Project Context

The **Legal Case Diary Portal** is a specialized enterprise litigation management system designed for law chambers, senior advocates, and legal aid bureaus. It replaces vulnerable paper registers with an audited, role-governed digital ledger. The system enforces strict judicial workflow lifecycles across court clerks, associate advocates, and managing partners while being continuously built, tested, and deployed via a comprehensive DevOps toolchain.

---

## 2. Software Requirements Specification (SRS)

### 2.1 Functional Requirements (FR)

| Requirement ID | Module | Specification Description | Permitted Role(s) |
| :--- | :--- | :--- | :--- |
| **FR-01** | **Authentication & Session** | System shall provide form-based login authenticating users against in-memory credentials (`clerk`, `lawyer`, `admin`) with BCrypt encryption and HTTP session management. | All Users |
| **FR-02** | **Case Docket Intake** | System shall allow recording a new case brief containing Case Number, Title, Case Type, Court Name, Petitioner Name, and Respondent Name. Case automatically initializes in `FILED` status with current timestamp. | `CLERK`, `ADMIN` |
| **FR-03** | **Search & Filter Registry** | System shall provide multi-field case search supporting partial case-insensitive matching across `caseNumber`, `title`, and `courtName`, with optional dropdown status filtering (`ALL`, `FILED`, `UNDER_REVIEW`, `HEARING_SCHEDULED`, `CLOSED`). | All Authenticated |
| **FR-04** | **Intake Scrutiny (Review)** | System shall allow advocates to scrutinize filed pleadings and transition cases from `FILED` to `UNDER_REVIEW`. Clerks are strictly prevented from initiating review. | `LAWYER`, `ADMIN` |
| **FR-05** | **Hearing Scheduling** | System shall allow lawyers to schedule court hearings, assign a `hearingDate` (must be present or future), enter `hearingNotes`, and advance status to `HEARING_SCHEDULED`. | `LAWYER`, `ADMIN` |
| **FR-06** | **Exclusive Case Closure** | System shall permit only managing partners (`ADMIN`) to formally dispose of and close matters from `HEARING_SCHEDULED` to `CLOSED`. Any attempt by `CLERK` or `LAWYER` shall be blocked with HTTP 403 Forbidden. | `ADMIN` Only |
| **FR-07** | **Lifecycle Audit Timeline** | For every status transition, the system shall append an immutable record to `CaseStatusHistory` (`caseId`, `fromStatus`, `toStatus`, `changedBy`, `changedAt`) and render a visual progression timeline on the case details page. | All Authenticated |
| **FR-08** | **Executive Dashboard** | System shall display real-time metric cards showing total case counts partitioned by status (`FILED`, `UNDER_REVIEW`, `HEARING_SCHEDULED`, `CLOSED`) and list the next 5 upcoming scheduled hearings. | All Authenticated |
| **FR-09** | **Case Profile Brief** | System shall display an exhaustive case detail page presenting all party metadata, filing dates, hearing dates, notes, and contextual state transition buttons permitted for the current user's role. | All Authenticated |

---

### 2.2 Non-Functional Requirements (NFR)

| Requirement ID | Category | Specification Metric & Criteria |
| :--- | :--- | :--- |
| **NFR-01** | **Performance & Latency** | Search and dashboard aggregate queries shall execute in under 200 ms locally; total page render response shall stay under 2 seconds. |
| **NFR-02** | **Data Persistence & Integrity** | Application data must persist across container/server restarts using an embedded file-mode H2 database (`./data/casediary.mv.db`) with foreign key constraints. |
| **NFR-03** | **Platform Portability** | Application shall compile as a standard Jakarta EE 10 WAR file (`legal-diary.war`) runnable on Apache Tomcat 10.1 (`jakarta.*` namespace) as well as embedded standalone runtime. |
| **NFR-04** | **Security & Defense** | Passwords hashed using BCrypt (strength 10). Cross-Site Request Forgery (CSRF) tokens enforced on all state-modifying requests. Frame options configured for local H2 console access. |
| **NFR-05** | **Bespoke Visual Standards** | User interface must strictly adhere to the bespoke legal design system (warm paper `#F7F4EE`, ink navy `#14213D`, muted brass `#B08D3C`, oxblood `#7A2E2E`, sage green `#4F6F52`). Pure handcrafted CSS without Bootstrap, Tailwind, or gradients. |
| **NFR-06** | **Build & Pipeline Reliability** | Maven build and packaging must complete deterministically under 2 minutes; full Jenkins CI/CD execution under 10 minutes with 100% automated test pass rate. |

---

### 2.3 System Assumptions & Operational Dependencies

1. **Host Environment:** Windows 11 host workstation with JDK 17 installed for source compilation, Maven 3.9.16, and Apache Tomcat 10.1 installed at `C:\tomcat10`.
2. **Jenkins Automation:** Jenkins version 2.580.1 running as a Windows Service on port `9090`, executing build steps via Windows batch (`bat`).
3. **Database Simplicity:** File-mode H2 eliminates external RDBMS installation overhead while guaranteeing persistent disk storage across application lifecycles.
4. **Authentication Boundary:** In-memory user store provides deterministic, self-contained evaluation for academic review without external LDAP or OAuth dependencies.

---

## 3. Three-Tier Architectural Overview

The application follows the classic enterprise **Three-Tier Architectural Pattern**, ensuring clear separation of concerns, high cohesion, and loose coupling:

```
+---------------------------------------------------------------------------------------------------+
|                                      PRESENTATION TIER (WEB)                                      |
|                                                                                                   |
|   [ Browser Client ] <==== HTTP / HTML5 ====> [ Spring MVC Controllers & Thymeleaf Views ]        |
|   - Handcrafted CSS (#F7F4EE, #14213D)        - AuthController (/login)                           |
|   - Source Serif 4 & Inter Google Fonts       - DashboardController (/dashboard)                  |
|   - Inline SVGs & Status Timelines            - CaseController (/cases/**)                        |
+---------------------------------------------------------------------------------------------------+
                                                  |
                                                  | DTOs / Method Invocations
                                                  v
+---------------------------------------------------------------------------------------------------+
|                                    BUSINESS / SERVICE TIER                                        |
|                                                                                                   |
|   [ Spring Security Filter Chain ]  ====>  [ CaseService & CaseServiceImpl ]                     |
|   - In-Memory User Authentication          - Strict RBAC Transition Validator                     |
|   - Role Evaluation (CLERK/LAWYER/ADMIN)   - Hearing Date Business Logic                          |
|   - CSRF & Session Guards                  - History Audit Appender                               |
+---------------------------------------------------------------------------------------------------+
                                                  |
                                                  | Spring Data JPA Queries / JPQL
                                                  v
+---------------------------------------------------------------------------------------------------+
|                                     DATA / PERSISTENCE TIER                                       |
|                                                                                                   |
|   [ Spring Data JPA Repositories ]  ====>  [ Hibernate 6 ORM ]  ====>  [ H2 File Database ]      |
|   - CaseRepository                         - Entity Mappings            - File: ./data/casediary  |
|   - CaseStatusHistoryRepository            - DDL Schema Auto-Update     - Tables: legal_cases,    |
|                                                                           case_status_history     |
+---------------------------------------------------------------------------------------------------+
```

1. **Presentation Tier:**
   - Handles HTTP requests, form bindings, and server-side HTML rendering via Thymeleaf 3.
   - Applies the bespoke legal palette with editorial typography without reliance on third-party CSS frameworks.
2. **Business / Service Tier:**
   - Contains core business rules, status transition validation, and transaction boundaries (`@Transactional`).
   - Ensures that status moves obey the strict state machine and logs every transition into the audit history.
3. **Data / Persistence Tier:**
   - Spring Data JPA interfaces generate type-safe SQL queries over Hibernate 6.
   - H2 Database engine persists data into file mode at `./data/casediary.mv.db`.

---

## 4. H2 Relational Data Model

### Entity 1: `LegalCase` (`legal_cases` table)

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | Primary Key, Auto-Increment | Unique internal case record identifier. |
| `case_number` | `VARCHAR(64)` | `NOT NULL`, `UNIQUE` | Court docket / CNR registration number (e.g., `DL-HC-2026-1042`). |
| `title` | `VARCHAR(255)` | `NOT NULL` | Formal case title / subject matter brief. |
| `case_type` | `VARCHAR(32)` | `NOT NULL` | Enum: `CIVIL`, `CRIMINAL`, `CONSTITUTIONAL`, `CORPORATE`, `FAMILY`. |
| `court_name` | `VARCHAR(128)` | `NOT NULL` | Court or tribunal (e.g., `Delhi High Court`, `Supreme Court of India`). |
| `petitioner_name` | `VARCHAR(128)` | `NOT NULL` | Primary petitioner / plaintiff party. |
| `respondent_name` | `VARCHAR(128)` | `NOT NULL` | Primary respondent / defendant party. |
| `status` | `VARCHAR(32)` | `NOT NULL` | Enum: `FILED`, `UNDER_REVIEW`, `HEARING_SCHEDULED`, `CLOSED`. |
| `filed_date` | `DATE` | `NOT NULL` | Official date when the matter was filed in court. |
| `hearing_date` | `DATE` | Nullable | Next scheduled court appearance date. |
| `hearing_notes` | `TEXT` | Nullable | Judicial directions, courtroom bench, and hearing summary. |
| `created_at` | `TIMESTAMP` | `NOT NULL` | System creation timestamp. |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | System last modification timestamp. |

---

### Entity 2: `CaseStatusHistory` (`case_status_history` table)

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | Primary Key, Auto-Increment | Unique status history record identifier. |
| `case_id` | `BIGINT` | `NOT NULL`, Foreign Key | Reference to parent `legal_cases.id`. |
| `from_status` | `VARCHAR(32)` | `NOT NULL` | Previous status state. |
| `to_status` | `VARCHAR(32)` | `NOT NULL` | New updated status state. |
| `changed_by` | `VARCHAR(64)` | `NOT NULL` | Authenticated username executing transition (`clerk`, `lawyer`, `admin`). |
| `changed_at` | `TIMESTAMP` | `NOT NULL` | Exact timestamp when the transition occurred. |
| `transition_notes`| `VARCHAR(255)` | Nullable | Optional brief explaining reason for status shift. |

---

### In-Memory User Security Store

| Username | Password (BCrypt Encoded) | Granted Role | Functional Scope |
| :--- | :--- | :--- | :--- |
| `clerk` | `clerk123` | `ROLE_CLERK` | Docket creation (`FILED`), case registry search, view case briefs. |
| `lawyer` | `lawyer123` | `ROLE_LAWYER` | View cases, advance to `UNDER_REVIEW`, schedule hearings (`HEARING_SCHEDULED`). |
| `admin` | `admin123` | `ROLE_ADMIN` | Full authority across all transitions; exclusive authority to advance to `CLOSED`. |

---

## 5. Web Application Endpoints & RBAC Authorization Matrix

| HTTP Method | Route URL | Controller Method | Allowed Roles | Description |
| :---: | :--- | :--- | :--- | :--- |
| `GET` | `/` | `HomeController.index()` | Anonymous / All | Root landing page; redirects authenticated users to `/dashboard`. |
| `GET` | `/login` | `AuthController.login()` | Anonymous / All | Bespoke styled authentication form. |
| `POST` | `/login` | Spring Security Filter | Anonymous / All | Form submission handling credential validation. |
| `GET` | `/logout` | Spring Security Filter | All Authenticated | Session invalidation and redirect to `/login?logout`. |
| `GET` | `/dashboard` | `DashboardController.dashboard()` | `CLERK`, `LAWYER`, `ADMIN` | Metric cards with status counts and upcoming court hearings. |
| `GET` | `/cases` | `CaseController.listCases()` | `CLERK`, `LAWYER`, `ADMIN` | Tabular case registry with multi-field search and status filter. |
| `GET` | `/cases/new` | `CaseController.newCaseForm()` | `CLERK`, `ADMIN` | Form to file a newly instituted legal case. |
| `POST` | `/cases/new` | `CaseController.createCase()` | `CLERK`, `ADMIN` | Validates and persists new case with initial status `FILED`. |
| `GET` | `/cases/{id}` | `CaseController.caseDetail()` | `CLERK`, `LAWYER`, `ADMIN` | Detailed case brief, status progression timeline, and action buttons. |
| `POST` | `/cases/{id}/review` | `CaseController.moveToReview()` | `LAWYER`, `ADMIN` | Transitions status from `FILED` to `UNDER_REVIEW`. |
| `POST` | `/cases/{id}/schedule`| `CaseController.scheduleHearing()`| `LAWYER`, `ADMIN` | Sets `hearingDate` & `hearingNotes`, transitions to `HEARING_SCHEDULED`. |
| `POST` | `/cases/{id}/close` | `CaseController.closeCase()` | `ADMIN` Only | Formal case disposal; transitions status to `CLOSED`. |
| `GET` | `/h2-console/**` | H2 Web Console Servlet | `ADMIN` (Dev Profile) | Database browser for local debugging and table inspection. |

---

## 6. Formal State Machine Specification

$$\text{FILED} \xrightarrow[\text{LAWYER / ADMIN}]{\text{Scrutiny}} \text{UNDER\_REVIEW} \xrightarrow[\text{LAWYER / ADMIN}]{\text{Set Hearing Date}} \text{HEARING\_SCHEDULED} \xrightarrow[\text{ADMIN ONLY}]{\text{Final Decree}} \text{CLOSED}$$

### State Transition Matrix

| Initial State | Transition Event | Destination State | Permitted Roles | Invariants & Business Validations |
| :--- | :--- | :--- | :--- | :--- |
| *(None)* | **Docket Intake** | `FILED` | `CLERK`, `ADMIN` | All mandatory fields populated; unique `caseNumber`. Initial audit logged. |
| `FILED` | **Scrutinize Filing** | `UNDER_REVIEW` | `LAWYER`, `ADMIN` | Case must be currently in `FILED`. Transition logged in `CaseStatusHistory`. |
| `UNDER_REVIEW` | **Schedule Hearing** | `HEARING_SCHEDULED` | `LAWYER`, `ADMIN` | `hearingDate` must be present and $\ge \text{today}$. `hearingNotes` optional. |
| `HEARING_SCHEDULED`| **Dispose / Close** | `CLOSED` | `ADMIN` Only | Strict check: only `ADMIN` role permitted. Prevents premature case disposal. |

*Note: Illegal state transitions (e.g. attempting to jump directly from `FILED` to `CLOSED`, or a `CLERK` attempting to schedule a hearing) are rejected at both controller and service levels with validation exceptions or HTTP 403 Forbidden responses.*

---

## 7. Security Architecture

1. **Authentication:**
   - Configured via Spring Security 6 `SecurityFilterChain` bean.
   - In-memory `UserDetailsService` defining `clerk`, `lawyer`, and `admin` with `PasswordEncoder` using `BCryptPasswordEncoder`.
2. **Authorization:**
   - Role-Based Access Control (RBAC) enforced through URL path patterns (`hasRole('CLERK')`, `hasAnyRole('LAWYER', 'ADMIN')`, `hasRole('ADMIN')`).
   - Method security (`@PreAuthorize`) enabled as a secondary defense layer on service methods.
3. **CSRF & Web Defense:**
   - Spring Security CSRF token embedded in all Thymeleaf forms (`<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />`).
   - Content Security Policy and frame options tailored to allow local H2 console debugging (`frameOptions().sameOrigin()`).

---

## 8. CI/CD Deployment Topology

```
+----------------------------------------------------------------------------------------------------+
|                                    DEVELOPER WORKSTATION                                           |
|                                                                                                    |
|   [ Windows 11 Host ] ----> Local Dev Server (Port 8081)                                           |
|   [ Source Code ] --------> Git Commit & Push                                                      |
+----------------------------------------------------------------------------------------------------+
                                      |
                                      v
+----------------------------------------------------------------------------------------------------+
|                                    SOURCE CONTROL (GITHUB)                                         |
|                                                                                                    |
|   Remote Repository: github.com/<username>/legal-diary                                             |
|   Branches: main, develop, feature/*                                                               |
+----------------------------------------------------------------------------------------------------+
                                      |
                                      v Webhook / SCM Poll
+----------------------------------------------------------------------------------------------------+
|                                 JENKINS CI/CD SERVER (PORT 9090)                                   |
|                                                                                                    |
|   Windows Service Execution via Windows Batch (bat):                                               |
|   1. SCM Checkout (Git Default)                                                                    |
|   2. Compile & Validate (JDK 21 Runner, Java 17 Target, Maven 3.9)                                 |
|   3. Automated Testing (JUnit 5 + Headless Selenium WebDriver Suite)                              |
|   4. WAR Packaging (target/legal-diary.war)                                                        |
+----------------------------------------------------------------------------------------------------+
                                      |
        +-----------------------------+-----------------------------+
        | Deploy Step (bat)                                         | Docker Pipeline
        v                                                           v
+----------------------------------+                      +----------------------------------+
|   APACHE TOMCAT 10.1 CONTAINER   |                      |          DOCKER DESKTOP          |
|                                  |                      |                                  |
|   Path: C:\tomcat10\webapps\     |                      |   Image: aseeskaurd/legal-diary  |
|   Port: 8080                     |                      |   Port: 8082                     |
|   Runtime: Jakarta EE 10         |                      |   Registry: Docker Hub           |
+----------------------------------+                      +----------------------------------+
                                                                    |
                                                                    v
                                                          +----------------------------------+
                                                          |         ANSIBLE ON WSL2          |
                                                          |                                  |
                                                          |   User: asees                    |
                                                          |   Target Port: 8083              |
                                                          |   Configuration Playbooks        |
                                                          +----------------------------------+
```

### Port Allocation Summary:
- **Port 8080:** Production Standalone Apache Tomcat 10.1 (`C:\tomcat10`)
- **Port 8081:** Local Development / Test Embedded Server
- **Port 8082:** Containerized Docker Instance (`aseeskaurd/legal-diary`)
- **Port 8083:** Ansible Target Managed Deployment
- **Port 9090:** Jenkins Continuous Integration & Deployment Automation Server

