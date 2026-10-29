# Legal Case Diary Portal
**A Modern, Secure, Role-Governed Digital Case Registry for Legal Practice**

---

### Academic & Project Metadata
- **Student Name:** Asees Kaur Dham
- **Roll Number:** 23102C0050
- **Course:** DevOps Engineering Lab / Project
- **Workspace:** `C:\Users\ssing\Desktop\devops`
- **Current Status:** MVP Release Candidate v1.0.0 (Stabilized Baseline)

---

## 1. Project Overview

The **Legal Case Diary Portal** is a production-grade enterprise web application designed to transition legal practices, advocates, and judicial clerks away from physical paper diaries and fragmented spreadsheets into an auditable, role-governed digital workflow.

Physical case tracking suffers from severe structural vulnerabilities: missed court hearing dates, unmonitored statute of limitations deadlines, lack of role segregation, and absence of an immutable audit trail. This portal solves these systemic issues by providing centralized case intake, automated hearing schedules, role-enforced state transitions, and an integrated CI/CD delivery lifecycle.

---

## 2. Core Features & Role-Based Workflow

### 2.1 Role-Based Access Control (RBAC)
The application enforces strict separation of duties across three primary organizational roles:

| Role | Permitted Actions | Transition Authority |
| :--- | :--- | :--- |
| **CLERK** | Case intake, filing, metadata entry, search & case registry viewing | Can initiate case in `FILED` state |
| **LAWYER** | Case review, hearing scheduling, timeline inspection, notes logging | `FILED` &rarr; `UNDER_REVIEW`<br>`UNDER_REVIEW` &rarr; `HEARING_SCHEDULED` |
| **ADMIN** | Full administrative governance, user audit, case closure | All transitions + exclusively permitted to close cases (`HEARING_SCHEDULED` &rarr; `CLOSED`) |

### 2.2 Case Lifecycle State Machine

```
[ FILED ]
   │
   │  (Advanced by LAWYER or ADMIN)
   ▼
[ UNDER_REVIEW ]
   │
   │  (Advanced by LAWYER or ADMIN)
   ▼
[ HEARING_SCHEDULED ]
   │
   │  (Exclusively closed by ADMIN)
   ▼
[ CLOSED ]
```

- **FILED:** Newly registered legal matter with initial petition details, court name, and case number.
- **UNDER_REVIEW:** Advocate assigned; pleadings, documents, and evidentiary materials being audited.
- **HEARING_SCHEDULED:** Court listing date, bench notes, and hearing schedule committed.
- **CLOSED:** Final judgment rendered or matter disposed; case archived into permanent record.

---

## 3. Technology Stack

### Application & Runtime
- **Language / Runtime:** Java 17 (LTS) Source & Target compilation (OpenJDK 17)
- **Framework:** Spring Boot 3.3.5
- **Servlet Specification:** Jakarta EE 10 (`jakarta.*` namespace throughout; no legacy `javax.*`)
- **Web & Templating:** Spring MVC + Thymeleaf 3 with Spring Security 6 dialect integration
- **Persistence:** Spring Data JPA with Hibernate 6 ORM
- **Database Engine:** Embedded H2 Database in file-persistence mode (`./data/casediary`)
- **Packaging:** Web Application Archive (WAR) for Apache Tomcat 10.1 deployment
- **Build Automation:** Apache Maven 3.9.16

### Visual Design System
- **Philosophy:** Bespoke editorial aesthetic tailored for high-end legal chambers. Zero CSS frameworks (no Bootstrap, Tailwind, or Bulma).
- **Typography:** *Source Serif 4* (Headings & legal emblems), *Inter* (Data grids and interactive controls)
- **Color Palette:**
  - Background: Warm Legal Parchment (`#F7F4EE`)
  - Typography & Sidebar: Deep Ink Navy (`#14213D`)
  - Primary Accents: Muted Antique Brass (`#B08D3C`)
  - Status Indicators: Oxblood Red (`#7A2E2E` for critical/closed), Sage Green (`#4F6F52` for completed/filed)

---

## 4. Repository Structure

```
devops/
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md
│   │   └── feature_request.md
│   └── pull_request_template.md
├── docs/
│   ├── 01-problem-definition.md      # Official Task 1: Problem statement & SLA metrics
│   ├── 02-agile-plan.md              # Official Task 2: User stories, MoSCoW backlog & Sprints
│   ├── 02-kanban-setup-steps.md      # Official Task 2: Step-by-step GitHub Projects guide
│   ├── 03-srs-architecture.md       # Official Task 3: Full SRS, endpoints & security
│   ├── architecture.mmd              # System architecture diagram (Mermaid)
│   ├── devops-lifecycle.mmd          # DevOps infinity loop toolchain diagram (Mermaid)
│   ├── er-diagram.mmd                # Entity relationship diagram (Mermaid)
│   └── use-case.mmd                  # Role use case diagram (Mermaid)
├── src/
│   ├── main/
│   │   ├── java/com/legal/diary/
│   │   │   ├── LegalDiaryApplication.java
│   │   │   ├── config/SecurityConfig.java
│   │   │   └── controller/HomeController.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/css/styles.css
│   │       └── templates/
│   │           ├── index.html
│   │           └── login.html
│   └── test/java/com/legal/diary/
│       └── LegalDiaryApplicationTests.java
├── .gitignore                        # Strict rules excluding target/, data/, logs/, secrets
├── CONTRIBUTING.md                   # Branching policy, commit conventions, PR rules
├── pom.xml                           # Maven POM configuration (WAR packaging, Spring Boot 3.3.5)
├── PROGRESS.md                       # Comprehensive course progress and verification audit
└── README.md                         # Project documentation and developer guide
```

---

## 5. Local Setup & Execution

### 5.1 Prerequisites
- Java Development Kit: JDK 17 installed and configured on PATH
- Apache Maven: Maven 3.9.16 (`mvn.cmd`)
- Git Version Control: Git 2.40+

### 5.2 Running the Application Locally (Port 8081)
To start the application in embedded development mode:

```powershell
# Using the installed Maven command
C:\Tools\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run
```

Once started, navigate to:
```
http://localhost:8081/login
```

### 5.3 Building the Deployable WAR
To compile, execute tests, and generate the standalone Web Application Archive:

```powershell
C:\Tools\apache-maven-3.9.16\bin\mvn.cmd clean package
```

The verified WAR artifact is generated at:
```
target/legal-diary.war
```

---

## 6. Demo User Credentials

For demonstration, testing, and assessment evaluation, the system provides pre-configured in-memory accounts:

| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `clerk` | `clerk123` | `ROLE_CLERK` | Registry Clerk: Creates filings, browses register |
| `lawyer` | `lawyer123` | `ROLE_LAWYER` | Advocate / Counsel: Reviews cases, schedules hearings |
| `admin` | `admin123` | `ROLE_ADMIN` | Managing Partner: Full control, exclusive closure rights |

*(Note: In accordance with academic guidelines, live production secrets are never committed. Authentication configurations use standard BCrypt hashing.)*

---

## 7. Git Branching Strategy & Contribution Policy

The project follows a disciplined Git branching model:

- `main` — Production-ready releases only. Every commit on `main` is stable, tested, and deployable.
- `develop` — Central integration branch for completed feature branches.
- `feature/<name>` — Isolated feature development (e.g., `feature/domain-model`, `feature/case-search`).
- `bugfix/<name>` — Defect resolutions branched from `develop`.
- `release/<version>` — Staging and stabilization prior to merging into `main`.
- `hotfix/<name>` — Critical production fixes branched directly from `main`.

Detailed collaboration instructions, pull request workflows, and Conventional Commit specifications are documented in [`CONTRIBUTING.md`](file:///c:/Users/ssing/Desktop/devops/CONTRIBUTING.md).

---

## 8. Planned CI/CD Pipeline Architecture

The complete DevOps lifecycle integrates continuous integration, automated continuous testing, container lifecycle management, and automated provisioning:

```
┌────────────────┐      ┌─────────────────────────┐      ┌─────────────────────────┐
│  Developer     │ ───> │  GitHub Repository      │ ───> │  Jenkins CI (Port 9090) │
│  Git Commit/PR │      │  (main / develop)       │      │  (Windows Service / bat)│
└────────────────┘      └─────────────────────────┘      └────────────┬────────────┘
                                                                      │
                                   ┌──────────────────────────────────┴──────────────────────────────────┐
                                   ▼                                                                     ▼
                        ┌─────────────────────┐                                               ┌─────────────────────┐
                        │  Maven Build & Test │                                               │  Selenium Suite     │
                        │  (mvn clean package)│                                               │  (8 Test Cases E2E) │
                        └──────────┬──────────┘                                               └──────────┬──────────┘
                                   │                                                                     │
                                   ▼                                                                     ▼
                        ┌────────────────────────────────────────────────────────────────────────────────────────┐
                        │                              Continuous Deployment Pipeline                            │
                        └──────────┬──────────────────────────────────┬──────────────────────────────────┬───────┘
                                   │                                  │                                  │
                                   ▼                                  ▼                                  ▼
                        ┌─────────────────────┐            ┌─────────────────────┐            ┌─────────────────────┐
                        │ Apache Tomcat 10.1  │            │ Docker Hub Registry │            │ Ansible Automation  │
                        │ Standalone (8080)   │            │ aseeskaurd/legal-   │            │ WSL2 Ubuntu (8083)  │
                        │ (C:\tomcat10)       │            │ diary (8082)        │            │ Reliability Check   │
                        └─────────────────────┘            └─────────────────────┘            └─────────────────────┘
```

1. **Source Control Management:** Code committed following Conventional Commits, merged to `develop` via audited Pull Requests on GitHub.
2. **Continuous Integration (Jenkins):** Triggered automatically on commit; compiles Java 17 bytecode, executes unit tests, validates packaging.
3. **Automated End-to-End Testing:** Headless Selenium WebDriver test suite covering 8 end-to-end user journeys (authentication, creation, RBAC transitions, search, boundary conditions).
4. **Tomcat Deployment:** Automated WAR deployment to local Apache Tomcat 10.1 (`C:\tomcat10`) at port `8080`.
5. **Container Lifecycle Management:** Docker container build, health inspection, port `8082` verification, and image publishing to Docker Hub (`aseeskaurd/legal-diary`).
6. **Configuration Management:** Ansible provisioning via WSL2 Ubuntu (`asees` user) for automated multi-environment deployment validation on port `8083`.

---

## 9. Academic Integrity & Verification Audit

Complete milestone history, configuration logs, verification commands, manual review procedures, and viva voce preparation notes are continuously maintained in [`PROGRESS.md`](file:///c:/Users/ssing/Desktop/devops/PROGRESS.md).

