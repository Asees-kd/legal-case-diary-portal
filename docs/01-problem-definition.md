# 01. Problem Definition & Requirements Specification
**Project:** CI/CD Pipeline for a Legal Case Diary Portal  
**Student:** Asees Kaur Dham  
**Roll Number:** 23102C0050  
**Workspace:** `C:\Users\ssing\Desktop\devops`  
**Date:** 2026-10-29  

---

## 1. Problem Statement

In Indian and global legal practice, law chambers, advocates, and legal aid clinics manage dozens to hundreds of active litigation matters simultaneously across district courts, high courts, tribunals, and appellate forums. Traditionally, these legal matters are tracked using physical paper registers, paper diaries, disparate notebooks, fragmented spreadsheets, and WhatsApp message threads.

This manual workflow causes critical vulnerabilities:
1. **Critical Hearing Misses:** Daily court lists ("cause lists") change rapidly. Without centralized scheduling and immediate status transitions, advocates miss appearances, leading to procedural dismissals or adverse *ex-parte* orders.
2. **Data Fragmentation and Physical Degradation:** Paper diaries are susceptible to loss, physical wear, misplacement, and single-location bottlenecks where only one staff member can access the register at a time.
3. **Lack of Role Governance:** Physical registers have no access controls. A junior clerk can accidentally alter or erase a case notation, while senior partners have no real-time visibility into whether matters are pending review, listed for hearing, or ready for formal closure.
4. **Deployment & Operational Overhead:** Most legal tech applications are cumbersome, require expensive cloud subscriptions, or lack automated build, test, and release mechanisms, leading to configuration drift and downtime during updates.

The **Legal Case Diary Portal** solves this by providing a lightweight, robust, role-governed web application running on Spring Boot 3 and Jakarta EE 10, packaged for Apache Tomcat 10.1, and supported by a continuous integration and continuous deployment (CI/CD) pipeline that automates testing, artifact generation, and deployment.

---

## 2. Target Users

| Target User Group | Description | Primary Operational Focus |
| :--- | :--- | :--- |
| **Court Clerks (`CLERK`)** | Administrative and registry staff in the law office responsible for docket management and client intake. | Entering newly filed suits, recording CNR (Case Number Record) details, logging petitioner/respondent names, and searching case numbers. |
| **Associate Lawyers (`LAWYER`)** | Practicing litigation associates and legal counsel handling substantive court hearings. | Reviewing case filings, examining evidence and pleadings, transitioning cases to `UNDER_REVIEW`, and setting hearing dates (`HEARING_SCHEDULED`). |
| **Managing Partners (`ADMIN`)** | Senior partners, chamber heads, and system administrators. | Full firm-wide oversight, case reassignment, auditing status changes, and exercising exclusive authority to close (`CLOSED`) finalized matters. |

---

## 3. Existing Pain Points & Systematic Analysis

```
+------------------------------------------------------------------------------------------------+
|                                    TRADITIONAL WORKFLOW                                        |
|                                                                                                |
|   [ Physical Paper Diary ] ---> [ Manual Cause List Lookup ] ---> [ Phone Calls / WhatsApp ]   |
|              |                                                               |                 |
|              v                                                               v                 |
|     Torn/Lost Registers                                             Missed Court Dates         |
|     No Search Capability                                            Ex-Parte Orders            |
|     Zero Access Control                                             No Status Accountability   |
+------------------------------------------------------------------------------------------------+
                                               |
                                               v  TRANSFORMATION
+------------------------------------------------------------------------------------------------+
|                                 LEGAL CASE DIARY PORTAL (CI/CD)                                |
|                                                                                                |
|   [ Digital Case Filing ]  ---> [ Role-Based State Machine ]  ---> [ Real-Time Dashboard ]    |
|   - Instant Search (<2s)        - Clerk: File Matters              - Status Breakdown Counts   |
|   - Persistent H2 Storage        - Lawyer: Review & Schedule        - Hearing Date Tracking     |
|   - Automated CI/CD             - Admin: Exclusive Closure         - Responsive Desktop UI     |
+------------------------------------------------------------------------------------------------+
```

### Key Shortcomings Addressed:
1. **Paper Diaries vs. Centralized Digital Ledger:** Eliminates handwritten illegibility, physical degradation, and duplicate entry across multiple registers.
2. **Missed Hearing Dates vs. Scheduled Hearing Timeline:** Explicitly models hearing dates and advances cases through dedicated workflow stages.
3. **No Status Visibility vs. Executive Dashboard:** Real-time dashboard displays instantaneous counts for matters Filed, Under Review, Hearing Scheduled, and Closed.
4. **Unauthorized Status Manipulation vs. Strict RBAC Enforcement:** Prevents unauthorized case closure; only users authenticated as `ADMIN` can transition a case to `CLOSED`.

---

## 4. Stakeholder Matrix

| Stakeholder | Interest / Concern | Deliverable |
| :--- | :--- | :--- |
| **Law Firm Partners** | Firm reputation, litigation tracking, zero missed court appearances. | Real-time status dashboard, case lifecycle audit trail, role governance. |
| **Court Litigators** | Instant case brief lookup, upcoming hearing agenda. | Fast search, structured case details view, state transition buttons. |
| **Filing Clerks** | Fast, frictionless case data entry without clutter. | Streamlined case registration form with validation and preset case types. |
| **Clients & Petitioners** | Reliable handling of their legal matters without administrative delay. | Timely progress from filing through hearings to disposal. |
| **DevOps / SysAdmin Engineers** | Automated testing, reproducible builds, zero-downtime deployment. | Jenkins CI/CD pipeline, Tomcat 10.1 WAR deployment, Docker containerization, Ansible automation. |
| **Academic Examiners (Viva)** | Architectural correctness, Jakarta EE compliance, CI/CD pipeline robustness. | Clean codebase, standard documentation, verifiable CI/CD logs and artifacts. |

---

## 5. Objectives

1. **Functional Objectives:**
   - Implement a digital case registry supporting creation, viewing, updating, and multi-field search (by case number, title, court, or status).
   - Enforce a formal 4-stage legal state machine:
     FILED -> UNDER_REVIEW -> HEARING_SCHEDULED -> CLOSED
   - Restrict transitions strictly by role:
     - `CLERK`: Create cases in `FILED` status.
     - `LAWYER`: Move `FILED` &rarr; `UNDER_REVIEW`, and `UNDER_REVIEW` &rarr; `HEARING_SCHEDULED`.
     - `ADMIN`: Move any valid previous status, with exclusive authority for `HEARING_SCHEDULED` &rarr; `CLOSED`.
   - Provide an executive dashboard displaying metric cards with counts for each status.

2. **Technical & DevOps Objectives:**
   - Package application as a clean Web Application Archive (`WAR`) compliant with Jakarta EE 10 (`jakarta.*`) running on Apache Tomcat 10.1 (`C:\tomcat10`).
   - Implement continuous integration with automated JUnit 5 tests covering domain rules and RBAC state transitions.
   - Configure a Jenkins pipeline (`bat` scripts) executing on Windows (port `9090`) to compile, test, package, and deploy artifacts to Tomcat 10.1.
   - Provide a Dockerized deployment path for portable container execution.
   - Create an Ansible playbook executed via WSL2 Ubuntu (`asees` user) for automated configuration and remote deployment.

3. **Design & Aesthetic Objectives:**
   - Present an authentic, bespoke legal atmosphere using warm paper (`#F7F4EE`), deep ink navy (`#14213D`), muted brass (`#B08D3C`), oxblood red (`#7A2E2E`), and sage green (`#4F6F52`).
   - Pure handcrafted CSS using typography pairing of *"Source Serif 4"* and *"Inter"*, clean inline SVGs, and subtle borders.

---

## 6. Constraints

| Category | Constraint Specification | Justification |
| :--- | :--- | :--- |
| **Operating System** | Windows 11 host environment with WSL2 Ubuntu. | Match student development workstation. |
| **Java Platform** | Source & Target: Java 17. Build Runner: Java 21 (Jenkins). | Compliance with Spring Boot 3 and student's installed JDKs. |
| **Servlet Specification** | Jakarta EE 10 (`jakarta.servlet.*`, `jakarta.persistence.*`). | Apache Tomcat 10.1 dropped legacy `javax.*` packages. |
| **Deployment Location** | Local standalone Tomcat 10.1 located at `C:\tomcat10` (port `8080`). | Required target container for artifact delivery. |
| **CI/CD Orchestration** | Jenkins running as a Windows Service on port `9090`. | Pipeline must utilize `bat` steps for Windows shell execution. |
| **Database** | Embedded file-mode H2 (`jdbc:h2:file:./data/casediary`). | Zero external database dependency, data persists across server restarts. |
| **Authentication** | In-memory Spring Security (`clerk`, `lawyer`, `admin`). | Deterministic, self-contained evaluation without external directory dependencies. |
| **Configuration Tooling** | Ansible executed strictly inside WSL2 Ubuntu under user `asees`. | Native Ansible Unix runtime compatibility on Windows. |
| **Frontend Styling** | Zero CSS frameworks (no Bootstrap, no Tailwind, no Sass). Pure handwritten CSS. | Professional, tailored legal visual identity meeting strict rubric rules. |

---

## 7. Measurable Success Criteria

| Metric | Target Goal | Verification Method |
| :--- | :--- | :--- |
| **Case Registration Latency** | < 60 seconds from form open to submission. | User timing test on `create.html`. |
| **Case Search Latency** | < 2.0 seconds across 100+ case records (sub-100ms expected locally). | Network tab in browser DevTools and Spring Boot query execution time. |
| **CI/CD Pipeline Build Time** | < 10 minutes (target: 2–3 minutes for local build, test, package, and Tomcat copy). | Jenkins build execution duration on port `9090`. |
| **Automated Test Coverage** | 100% pass rate on all unit, controller, and security state machine tests. | Maven Surefire test report (`mvn test`). |
| **State Machine Integrity** | 0 unauthorized transitions (e.g. `CLERK` attempting to close a case receives HTTP 403 Forbidden). | Automated Spring Security integration tests. |
| **Tomcat Deployment Reliability** | WAR auto-explodes into `C:\tomcat10\webapps\legal-diary` and serves HTTP 200 within 15 seconds. | HTTP GET verification on `http://localhost:8080/legal-diary/login`. |

---

## 8. Frozen MVP Scope (15 Backlog Items: B-01 to B-15)

The implementation is partitioned into exactly 15 sequential, independently verifiable backlog items:

```
[Phase 1: Requirements & Architecture]
  ├── B-01: Problem Definition & Requirements Specification (docs/01-problem-definition.md) [DONE]
  └── B-02: Architecture & System Design Document (docs/02-architecture-and-design.md)

[Phase 2: Core Application Implementation]
  ├── B-03: Maven Project Scaffolding & Configuration (pom.xml, Java 17, Spring Boot 3, WAR)
  ├── B-04: Domain Entities, Enums & JPA Repositories (LegalCase, CaseStatus, CaseType, CaseRepository)
  ├── B-05: Business Logic Service & Strict RBAC State Transition Rules (CaseService, CaseServiceImpl)
  ├── B-06: Spring Security Configuration & In-Memory Role-Based Access Control (SecurityConfig)
  ├── B-07: MVC Web Controllers & Dynamic Query Handler (CaseController, DashboardController, AuthController)
  ├── B-08: Bespoke Legal Handcrafted CSS Design System (styles.css, fonts, palette, inline SVGs)
  └── B-09: Responsive Thymeleaf View Templates (Login, Dashboard, Case List, Detail Timeline, Create Form)

[Phase 3: Verification & Packaging]
  ├── B-10: Automated Testing Suite (JUnit 5 + Selenium WebDriver 8 Test Cases)
  └── B-11: Local Build, WAR Generation & Standalone Tomcat 10.1 Verification (C:\tomcat10\webapps)

[Phase 4: CI/CD & Infrastructure Automation]
  ├── B-12: Git Version Control Setup & Branching Strategy (.gitignore, commits, branching)
  ├── B-13: Jenkins CI/CD Pipeline as Code (Jenkinsfile with bat steps for port 9090)
  ├── B-14: Docker Containerization for Tomcat 10 (Dockerfile, docker-compose.yml, test run)
  └── B-15: Ansible Automation via WSL2 (asees user) & Final Comprehensive Audit
```

### Detailed Breakdown of the 15 Backlog Items:

1. **B-01: Problem Definition & Requirements Specification**  
   Draft `docs/01-problem-definition.md` outlining the problem, target personas, pain points, stakeholder matrix, objectives, constraints, measurable metrics, and frozen 15-item scope. Update `PROGRESS.md`.
2. **B-02: Architecture & System Design Document**  
   Create `docs/02-architecture-and-design.md` detailing the 3-tier Spring Boot architecture, component diagrams, database schema for H2, state machine transition diagram, security model, and CI/CD deployment topology.
3. **B-03: Maven Project Scaffolding & Configuration**  
   Generate the root Maven structure, `.gitignore`, and `pom.xml` configured for Java 17, Spring Boot 3.x, WAR packaging, `provided` Tomcat dependencies, Thymeleaf, Spring Data JPA, Spring Security, Validation, and H2.
4. **B-04: Domain Entities, Enums & JPA Repositories**  
   Implement `CaseStatus` enum (`FILED`, `UNDER_REVIEW`, `HEARING_SCHEDULED`, `CLOSED`), `CaseType` enum (`CIVIL`, `CRIMINAL`, `CONSTITUTIONAL`, `CORPORATE`, `FAMILY`), `LegalCase` JPA entity with validation annotations, and `CaseRepository` with custom search queries.
5. **B-05: Business Logic Service & Strict RBAC State Transition Rules**  
   Implement `CaseService` interface and `CaseServiceImpl` enforcing allowed status transitions based on user roles (`CLERK`, `LAWYER`, `ADMIN`), status count aggregations, and search/filter operations.
6. **B-06: Spring Security Configuration & In-Memory RBAC**  
   Implement `SecurityConfig` with BCrypt password encoder, in-memory users (`clerk`, `lawyer`, `admin`), CSRF protection, custom login page, and URL authorization rules.
7. **B-07: MVC Web Controllers & Dynamic Query Handler**  
   Implement `DashboardController` (metric counters), `CaseController` (listing, search, view detail, creation, status transitions), and `AuthController` (custom login/logout view mapping).
8. **B-08: Bespoke Legal Handcrafted CSS Design System**  
   Author `src/main/resources/static/css/styles.css` embodying the editorial legal palette (`#F7F4EE`, `#14213D`, `#B08D3C`, `#7A2E2E`, `#4F6F52`), Google Fonts integration, responsive left sidebar, status pill badges, timeline component, and subtle borders.
9. **B-09: Responsive Thymeleaf View Templates**  
   Build `login.html`, `layout.html` (header, sidebar, user badge, CSRF token), `dashboard.html` (stat cards, recent cases), `cases.html` (data table, search bar, filter), `detail.html` (metadata table, status progression timeline, transition action buttons), and `create.html` (case filing form).
10. **B-10: Automated Testing: JUnit 5 unit tests, plus Selenium WebDriver suite of 8 test cases**  
    Create JUnit 5 tests and Selenium WebDriver suite covering: login valid/invalid, create case, case in list, update, search, role rule clerk cannot close/admin can, and dashboard counts.
11. **B-11: Local Build, WAR Generation & Standalone Tomcat 10.1 Verification**  
    Package the application as `legal-diary.war` using `mvn clean package`. Deploy to `C:\tomcat10\webapps`, verify exploded directory, test HTTP endpoints on `http://localhost:8080/legal-diary`.
12. **B-12: Git Version Control Setup & Branching Strategy**  
    Initialize local Git repository, configure `.gitignore`, commit all source artifacts, and establish branching model (`main`, `develop`, `feature/pipeline`).
13. **B-13: Jenkins CI/CD Pipeline as Code**  
    Create `Jenkinsfile` with Windows batch (`bat`) stages: Checkout, Clean & Compile, Test Execution, WAR Packaging, and Automated Copy to `C:\tomcat10\webapps`. Document exact Jenkins GUI setup steps for port 9090.
14. **B-14: Docker Containerization for Tomcat 10**  
    Create multi-stage `Dockerfile` (or Tomcat 10.1-jdk17 base with WAR overlay), `.dockerignore`, and `docker-compose.yml`. Build image and verify standalone container execution on mapped port `8082`.
15. **B-15: Ansible Automation via WSL2 & Final Comprehensive Audit**  
    Create Ansible playbook (`playbook.yml`) and inventory file executed in WSL2 Ubuntu (`asees` user) for automated deployment to Tomcat/Docker. Conduct final end-to-end verification and compile complete Viva Voce preparation package.

