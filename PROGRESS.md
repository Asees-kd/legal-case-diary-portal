# CI/CD Pipeline for a Legal Case Diary Portal
**Student:** Asees Kaur Dham  
**Roll Number:** 23102C0050  
**Project Workspace:** `C:\Users\ssing\Desktop\devops`  

---

## Numbering Convention
Official assignment tasks are 14 (Task 1 to Task 14, defined by my college: 1 Problem Definition, 2 Agile Planning and DevOps Workflow, 3 Requirements/Architecture/Tech Setup, 4 GitHub Repo Initialization, 5 Feature Development with Branching, 6 MVP Completion and Git Collaboration, 7 Jenkins CI Job, 8 Pipeline as Code and Tomcat Deployment, 9 Selenium Tests Local (8 test cases), 10 Continuous Testing in Jenkins, 11 Docker Image and Container Lifecycle, 12 Jenkins-Docker CD, 13 Configuration Management Script, 14 Provisioning and Reliability Validation). The existing TASK-01 to TASK-15 roadmap in section 3 is NOT the official numbering; rename it to 'MVP Backlog (15 items, B-01 to B-15)' and use it as the 15-task Kanban plan. Always refer to official tasks as 'Task N' and backlog items as 'B-NN'.

---

## 1. System & Environment Specifications

| Component | Specification / Version | Configuration & Location | Port |
| :--- | :--- | :--- | :--- |
| **Operating System** | Windows 11 (64-bit) | Local Host | - |
| **Java Development Kit** | JDK 17 (Project Code) / JDK 21 (Jenkins) | Java 17 Source & Target compilation | - |
| **Build Tool** | Apache Maven 3.9.16 | Local CLI / Jenkins pipeline runner | - |
| **Servlet Container** | Apache Tomcat 10.1 | `C:\tomcat10` (Jakarta EE 10 / `jakarta.*`), Tomcat Manager enabled with `manager-script` role | `8080` |
| **Container Engine** | Docker Desktop, Linux containers (WSL2 backend) | Windows Docker Desktop host | `8082` |
| **Jenkins Server** | Jenkins version 2.580.1 | Windows Service, pipeline execution using Windows batch (`bat`) | `9090` |
| **Jenkins Tools** | Configured in Global Tool Configuration | JDK21, Maven3, Git (Default) | - |
| **Jenkins Credentials** | Stored in Jenkins Credentials Manager | `dockerhub-creds` and `tomcat-creds` (actual passwords never stored in repo) | - |
| **Jenkins Plugins** | Core automation plugins installed | Maven Integration, Docker Pipeline, Ansible, Deploy to container | - |
| **Docker Hub Registry** | Docker Hub Account | Username: `aseeskaurd` (Target image: `aseeskaurd/legal-diary`) | - |
| **Source Control (Remote)** | GitHub Repository URL | `[I will fill in at Task 4]` | - |
| **Config Management** | Ansible on WSL2 Ubuntu | User: `asees` | `8083` (Target) |
| **Database** | Embedded H2 Database (File mode) | Persistent local file storage (`./data/casediary`) | Web Console / App |
| **Port Matrix Overview** | **Summary of Network Ports** | Tomcat: `8080`, Local Dev: `8081`, Docker: `8082`, Ansible Target: `8083`, Jenkins: `9090` | Multi-Port |

---

## 2. Application Architecture & Rules

### Core Technologies
- **Framework:** Spring Boot 3.x
- **Packaging:** WAR (Web Application Archive) for standalone deployment on Apache Tomcat 10.1
- **Jakarta EE:** Full compliance with `jakarta.*` namespace (`jakarta.servlet`, `jakarta.persistence`, `jakarta.validation`)
- **Templating:** Thymeleaf 3
- **ORM / Persistence:** Spring Data JPA with Hibernate 6
- **Database:** Embedded H2 in file-persisted mode (`./data/casediary`)
- **Security:** Spring Security 6 with in-memory users and role-based access control (RBAC)

### User Roles & Credentials
| Username | Password | Role | Permitted Workflow Actions |
| :--- | :--- | :--- | :--- |
| `clerk` | `clerk123` | `CLERK` | Create new cases (`FILED`), search and view all cases. |
| `lawyer` | `lawyer123` | `LAWYER` | View cases, advance `FILED` &rarr; `UNDER_REVIEW`, advance `UNDER_REVIEW` &rarr; `HEARING_SCHEDULED`. |
| `admin` | `admin123` | `ADMIN` | Full access: all transitions + exclusively permitted to close cases (`HEARING_SCHEDULED` &rarr; `CLOSED`). |

### Status Lifecycle
`FILED -> UNDER_REVIEW -> HEARING_SCHEDULED -> CLOSED` (clerk files, lawyer advances to review/hearing, admin exclusively closes)

### Bespoke Visual Design System
- **Look & Feel:** High-end boutique legal firm case register; tactile, authoritative, editorial.
- **Palette:**
  - Parchment / Off-white background: `#F7F4EE`
  - Deep Ink Navy (text, sidebar, headers): `#14213D`
  - Muted Antique Brass / Gold (accents, highlights): `#B08D3C`
  - Oxblood Red (urgent notices, hearing alerts, closed archive): `#7A2E2E`
  - Sage Green (filed / completed status badges): `#4F6F52`
- **Typography:**
  - Headings & Editorial Display: `"Source Serif 4"`, serif
  - Interface & Body Text: `"Inter"`, sans-serif
- **Styling Rules:**
  - Custom handwritten CSS (zero CSS frameworks, no Bootstrap, no Tailwind).
  - No purple/violet gradients, no glassmorphism, no emoji icons.
  - Lightweight inline SVGs for iconography.
  - Crisp subtle borders (`1px solid #E2DCD2`) instead of heavy dropshadows.
  - Left navigation sidebar, status timelines, clear tabular case registry.

---

## 3. Official Assignment Tasks (14 Tasks)

| Task ID | Official Task Name | Status | Deliverable / Tracking Artifacts |
| :--- | :--- | :--- | :--- |
| **Task 1** | **Problem Definition** | **DONE** | [`docs/01-problem-definition.md`](file:///c:/Users/ssing/Desktop/devops/docs/01-problem-definition.md) |
| **Task 2** | **Agile Planning and DevOps Workflow** | **DONE** | [`docs/02-agile-plan.md`](file:///c:/Users/ssing/Desktop/devops/docs/02-agile-plan.md), [`docs/devops-lifecycle.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/devops-lifecycle.mmd), [`docs/02-kanban-setup-steps.md`](file:///c:/Users/ssing/Desktop/devops/docs/02-kanban-setup-steps.md) |
| **Task 3** | **Requirements/Architecture/Tech Setup** | **DONE** | [`docs/03-srs-architecture.md`](file:///c:/Users/ssing/Desktop/devops/docs/03-srs-architecture.md), [`docs/use-case.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/use-case.mmd), [`docs/architecture.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/architecture.mmd), [`docs/er-diagram.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/er-diagram.mmd), `pom.xml`, Spring Boot 3 skeleton (`BUILD SUCCESS`, `target/legal-diary.war` confirmed) |
| **Task 4** | **GitHub Repo Initialization** | PENDING | Local & remote repository initialization, `.gitignore`, initial commit |
| **Task 5** | **Feature Development with Branching** | PENDING | Domain entities, service layer, security, feature branches |
| **Task 6** | **MVP Completion and Git Collaboration** | PENDING | Controllers, handcrafted UI, Thymeleaf views, PR merges |
| **Task 7** | **Jenkins CI Job** | PENDING | Freestyle / Maven CI job on port 9090, automated build & test |
| **Task 8** | **Pipeline as Code and Tomcat Deployment** | PENDING | `Jenkinsfile` with Windows `bat` stages, deployment to `C:\tomcat10` (8080) |
| **Task 9** | **Selenium Tests Local (8 test cases)** | PENDING | Automated end-to-end browser test suite (8 distinct test cases) |
| **Task 10** | **Continuous Testing in Jenkins** | PENDING | Automated test stage integration in Jenkins with test reporting |
| **Task 11** | **Docker Image and Container Lifecycle** | PENDING | `Dockerfile`, container build, port 8082 run, Docker Hub push (`aseeskaurd`) |
| **Task 12** | **Jenkins-Docker CD** | PENDING | Automated Docker build, tag, push, and run pipeline stage |
| **Task 13** | **Configuration Management Script** | PENDING | Ansible playbook & inventory via WSL2 Ubuntu (`asees` user) for port 8083 |
| **Task 14** | **Provisioning and Reliability Validation** | PENDING | Health checks, rollback/recovery testing, final viva documentation |

---

## 4. MVP Backlog (15 items, B-01 to B-15 - Kanban Plan)

| Backlog ID | Kanban Item Description | Status | Target Deliverables |
| :--- | :--- | :--- | :--- |
| **B-01** | Problem Definition & Requirements Specification | **DONE** | [`docs/01-problem-definition.md`](file:///c:/Users/ssing/Desktop/devops/docs/01-problem-definition.md) |
| **B-02** | Architecture & System Design Document | **DONE** | [`docs/03-srs-architecture.md`](file:///c:/Users/ssing/Desktop/devops/docs/03-srs-architecture.md), [`docs/use-case.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/use-case.mmd), [`docs/architecture.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/architecture.mmd), [`docs/er-diagram.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/er-diagram.mmd) |
| **B-03** | Maven Project Scaffolding & Configuration (WAR, Java 17, Tomcat 10) | **DONE** | `pom.xml`, `.gitignore`, `src/` skeleton, `target/legal-diary.war` (BUILD SUCCESS) |
| **B-04** | Domain Entities, Enums & JPA Repositories | PENDING | `LegalCase.java`, `CaseStatus.java`, `CaseType.java`, `CaseRepository.java` |
| **B-05** | Business Logic Service & Strict RBAC State Transition Rules | PENDING | `CaseService.java`, `CaseServiceImpl.java` |
| **B-06** | Spring Security Configuration & In-Memory RBAC Setup | PENDING | `SecurityConfig.java` |
| **B-07** | MVC Web Controllers & Dynamic Query Handler | PENDING | `CaseController.java`, `DashboardController.java`, `AuthController.java` |
| **B-08** | Bespoke Legal Handcrafted CSS Design System | PENDING | `src/main/resources/static/css/styles.css` |
| **B-09** | Responsive Thymeleaf View Templates | PENDING | `layout.html`, `dashboard.html`, `cases.html`, `detail.html`, `create.html`, `login.html` |
| **B-10** | Automated Testing: JUnit 5 + Selenium Suite (8 test cases) | PENDING | `CaseServiceTest.java`, `LegalCaseSeleniumTest.java` |
| **B-11** | Local Build, WAR Generation & Tomcat 10.1 (`C:\tomcat10`) Verification | PENDING | `target/legal-diary.war`, `C:\tomcat10\webapps\` |
| **B-12** | Git Version Control Setup & Branching Strategy | PENDING | Git repository initialization & branching |
| **B-13** | Jenkins CI/CD Pipeline as Code (`bat` pipeline for port 9090) | PENDING | `Jenkinsfile` |
| **B-14** | Docker Containerization for Tomcat 10 | PENDING | `Dockerfile`, `docker-compose.yml`, `.dockerignore` |
| **B-15** | Ansible Automation via WSL2 (`asees` user) & Final Comprehensive Audit | PENDING | `ansible/playbook.yml`, `ansible/inventory` |

---

## 5. Detailed Task Execution Log

### Task 1: Problem Definition
- **Status:** Completed
- **Date:** 2026-10-29
- **Files Created / Modified:**
  - [`docs/01-problem-definition.md`](file:///c:/Users/ssing/Desktop/devops/docs/01-problem-definition.md)
  - [`PROGRESS.md`](file:///c:/Users/ssing/Desktop/devops/PROGRESS.md)
- **Branch:** `main` (pre-git init documentation phase)
- **Commands Executed:** None (file generation)
- **Ports Used:** None
- **Manual Actions Required by Student:**
  - Review [`docs/01-problem-definition.md`](file:///c:/Users/ssing/Desktop/devops/docs/01-problem-definition.md) to ensure all problem scope and SLA metrics meet course expectations.
- **Viva Voce Knowledge Point (3-4 lines):**
  > *Task 1 defines the operational foundation for the Legal Case Diary Portal by analyzing the vulnerabilities of physical legal diaries, such as missed court hearings, unmonitored status progression, and lack of role segregation. It establishes clear functional requirements, technical constraints (Tomcat 10.1, Jakarta EE 10, Java 17), and concrete SLA metrics (<60s case creation, <2s search, <10min pipeline). The project roadmap is frozen into 14 official curriculum tasks alongside a 15-item technical MVP backlog.*

### Task 2: Agile Planning and DevOps Workflow
- **Status:** Completed
- **Date:** 2026-10-29
- **Files Created / Modified:**
  - [`docs/02-agile-plan.md`](file:///c:/Users/ssing/Desktop/devops/docs/02-agile-plan.md)
  - [`docs/devops-lifecycle.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/devops-lifecycle.mmd)
  - [`docs/02-kanban-setup-steps.md`](file:///c:/Users/ssing/Desktop/devops/docs/02-kanban-setup-steps.md)
  - [`PROGRESS.md`](file:///c:/Users/ssing/Desktop/devops/PROGRESS.md)
- **Branch:** `main`
- **Commands Executed:** None (markdown & mermaid artifact generation)
- **Ports Used:** None (planning phase)
- **Manual Actions Required by Student:**
  1. Open GitHub in your web browser and create the GitHub Projects board named `Legal Case Diary Portal - DevOps Kanban` with columns `To Do`, `In Progress`, and `Done` as documented in [`docs/02-kanban-setup-steps.md`](file:///c:/Users/ssing/Desktop/devops/docs/02-kanban-setup-steps.md).
  2. Populate cards `B-01` to `B-15` as detailed (placing `B-01` in `Done` and `B-02` to `B-15` in `To Do`).
  3. Capture the 3 screenshots outlined in [`docs/02-kanban-setup-steps.md`](file:///c:/Users/ssing/Desktop/devops/docs/02-kanban-setup-steps.md) (`01_kanban_board_full_view.png`, `02_kanban_card_detail.png`, `03_kanban_grouped_by_sprint.png`) for your course submission.
- **Viva Voce Knowledge Point (3-4 lines):**
  > *Task 2 operationalizes Agile and DevOps methodologies by translating legal domain requirements into 10 formal user stories with strict acceptance criteria and MoSCoW prioritization (50 Story Points total). We structured two distinct 1-week sprints separating core architectural implementation from automated delivery. The DevOps infinity loop diagram maps each stage to our concrete toolchain: GitHub Projects (Plan), Git/GitHub (Code), Maven (Build), JUnit/Selenium (Test), Jenkins/Docker Hub (Release), Tomcat/Docker (Deploy), Ansible (Operate), and health-checks/logs (Monitor).*

### Task 3: Requirements / Architecture / Tech Setup
- **Status:** Completed
- **Date:** 2026-10-29
- **Backlog Items Covered:** B-02 (Architecture & Design), B-03 (Maven Scaffolding)
- **Files Created / Modified:**
  - [`docs/03-srs-architecture.md`](file:///c:/Users/ssing/Desktop/devops/docs/03-srs-architecture.md) — SRS (FR-01..FR-09, NFR-01..NFR-06), 3-tier architecture, endpoint table, state machine, security model, CI/CD topology
  - [`docs/use-case.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/use-case.mmd) — Mermaid use-case diagram (CLERK / LAWYER / ADMIN)
  - [`docs/architecture.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/architecture.mmd) — Mermaid system architecture (Browser → Spring MVC → Service → JPA → H2 + DevOps pipeline)
  - [`docs/er-diagram.mmd`](file:///c:/Users/ssing/Desktop/devops/docs/er-diagram.mmd) — Mermaid ER diagram (`LEGAL_CASES` and `CASE_STATUS_HISTORY`)
  - [`pom.xml`](file:///c:/Users/ssing/Desktop/devops/pom.xml) — WAR packaging, Java 17, Spring Boot 3.3.5, all required dependencies
  - [`.gitignore`](file:///c:/Users/ssing/Desktop/devops/.gitignore) — excludes `target/`, `data/`, `*.log`, `.idea/`, `.vscode/`, password/secret files
  - `src/main/java/com/legal/diary/LegalDiaryApplication.java` — extends `SpringBootServletInitializer`
  - `src/main/java/com/legal/diary/config/SecurityConfig.java` — in-memory users, BCrypt, RBAC URL rules
  - `src/main/java/com/legal/diary/controller/HomeController.java` — `/` and `/login` mappings
  - `src/main/resources/application.properties` — port 8081, H2 file mode, Thymeleaf cache off
  - `src/main/resources/static/css/styles.css` — full bespoke design system CSS
  - `src/main/resources/templates/login.html` — bespoke login page with SVG emblem
  - `src/main/resources/templates/index.html` — dashboard home with left sidebar
  - `src/test/java/com/legal/diary/LegalDiaryApplicationTests.java` — context loads test
  - `mvn.ps1` — **DELETED** (removed from project root)
- **Branch:** `main` (pre-git-init phase)
- **Commands Executed:**
  ```
  C:\Tools\apache-maven-3.9.16\bin\mvn.cmd clean package
  ```
  Result: **BUILD SUCCESS** — `target/legal-diary.war` generated (BypassSandbox required on this machine)
- **Ports Used:** `8081` (local dev, Spring Boot embedded Tomcat for `spring-boot:run`)
- **Evidence Screenshots Required (take these before your viva):**
  1. `03_pom_xml.png` — Open `pom.xml` in your editor; screenshot showing `<packaging>war</packaging>`, `spring-boot-starter-parent 3.3.5`, and `<java.version>17</java.version>` all visible.
  2. `03_build_success.png` — Open a terminal in `C:\Users\ssing\Desktop\devops`, run `C:\Tools\apache-maven-3.9.16\bin\mvn.cmd clean package`, and screenshot the terminal output ending with `BUILD SUCCESS` and the line showing `legal-diary.war`.
  3. `03_war_file.png` — Open File Explorer to `C:\Users\ssing\Desktop\devops\target\` and screenshot `legal-diary.war` visible in the folder.
  4. `03_app_running_8081.png` — Run `C:\Tools\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run`, open `http://localhost:8081/login` in your browser, and screenshot the bespoke legal login page.
  5. `03_architecture_diagram.png` — Open `docs/architecture.mmd` in VS Code with the Mermaid Preview extension (or paste into [mermaid.live](https://mermaid.live)) and screenshot the rendered diagram.
  6. `03_er_diagram.png` — Same as above for `docs/er-diagram.mmd`.
  7. `03_srs_doc.png` — Open `docs/03-srs-architecture.md` in your editor and screenshot the FR / NFR requirements table visible.
- **Manual Actions Required by Student:**
  1. Run `C:\Tools\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run` in a terminal window.
  2. Open `http://localhost:8081/login` in your browser to see the login page.
  3. Log in with `clerk/clerk123`, `lawyer/lawyer123`, or `admin/admin123` to verify home page loads.
  4. Stop the server (Ctrl+C in the terminal).
  5. Take the 7 evidence screenshots listed above.
- **Viva Voce Knowledge Point (3-4 lines):**
  > *Task 3 establishes the full technical foundation for the Legal Case Diary Portal. We authored a formal SRS document with 9 functional requirements (FR-01 to FR-09) and 6 non-functional requirements (NFR-01 to NFR-06), then designed a 3-tier architecture (Browser → Spring MVC Controller → Service → JPA Repository → H2 file DB). The Maven project skeleton uses WAR packaging for deployment on Tomcat 10.1 (Jakarta EE 10 / `jakarta.*` namespace), Spring Boot 3.3.5 with Java 17 source compilation, and a bespoke handcrafted CSS design system — the `mvn clean package` command confirmed BUILD SUCCESS producing `target/legal-diary.war`.*

---

## 6. Next Immediate Step
Official Task 4 — GitHub Repo Initialization: run `git init`, make the initial commit, create the remote GitHub repository, push to `main`, and fill in the GitHub URL in `PROGRESS.md`.
