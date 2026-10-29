# Contribution & Collaboration Guidelines
**Legal Case Diary Portal — DevOps Engineering Standard**

---

### Academic & Project Metadata
- **Project:** Legal Case Diary Portal
- **Author / Maintainer:** Asees Kaur Dham (Roll No. 23102C0050)
- **Repository:** `legal-case-diary-portal`

---

## 1. Purpose & Scope

This document specifies the branching model, commit conventions, code review criteria, and pull request procedures governing development of the Legal Case Diary Portal. Adherence to these guidelines ensures traceability, reproducible builds, and seamless integration with our automated CI/CD pipeline.

---

## 2. Branching Strategy

Our branching taxonomy enforces disciplined separation between active development, feature experimentation, stabilization, and production releases.

```
(main)       ●───────────────────────────────────────────────● (v1.0.0 Tag)
             │                                               ▲
(release)    │                          ┌─ release/v1.0.0 ───┤
             │                          │                    │
(develop)    ●─────────●────────────────●────────────────────●
                       ▲                ▲
(feature)              │                │
                       └── feature/x ───┘
```

### 2.1 Branch Classification

| Branch Pattern | Base Branch | Target Branch | Purpose & Lifecycle |
| :--- | :--- | :--- | :--- |
| `main` | N/A | N/A | **Production releases only.** Contains solely stable, verified, production-ready code. Direct commits are strictly prohibited. |
| `develop` | `main` | `main` | **Integration branch.** The default working baseline where feature branches integrate once reviewed and tested. |
| `feature/<short-name>` | `develop` | `develop` | **New capabilities or modules.** Isolated branch for feature work (e.g., `feature/domain-model`, `feature/case-search`). Deleted post-merge. |
| `bugfix/<short-name>` | `develop` | `develop` | **Non-critical defect remediation.** Used to resolve bugs discovered during sprint development. |
| `release/<version>` | `develop` | `main` & `develop` | **Release preparation.** Final stabilization, documentation updates, and version bumping before production tagging (e.g., `release/v1.0.0`). |
| `hotfix/<short-name>` | `main` | `main` & `develop` | **Emergency production fixes.** Direct critical remediations for production defects. |

---

## 3. Commit Message Conventions (Conventional Commits)

All commit messages must adhere to the **Conventional Commits 1.0.0** specification. This format guarantees readable history and facilitates automated release notes.

### 3.1 Structure
```
<type>(<scope>): <short imperative summary>

[optional detailed description explaining WHY and WHAT changed]

[optional issue reference or footer, e.g., Refs: #12, Closes: #5]
```

### 3.2 Approved Types

| Type | Definition | Example |
| :--- | :--- | :--- |
| `feat` | Introduces a new application capability or business logic | `feat(case): implement hearing schedule date picker and validation` |
| `fix` | Resolves a defect or incorrect behavior | `fix(auth): correct CSRF exemption pattern for H2 web console` |
| `docs` | Documentation-only alterations (Markdown, diagrams) | `docs(agile): document Sprint 1 backlog and MoSCoW prioritization` |
| `chore` | Build configuration, dependency bumps, or tool adjustments | `chore(maven): configure WAR packaging and finalName legal-diary` |
| `test` | Adding or updating unit tests, integration tests, or Selenium scripts | `test(e2e): implement Selenium test case for unauthorized case closure` |
| `ci` | Changes to CI/CD pipelines, Jenkinsfiles, or deployment scripts | `ci(jenkins): add bat pipeline stage for automated Tomcat WAR deploy` |

---

## 4. Pull Request (PR) & Code Review Rules

To maintain code quality and prevent regressions from entering the integration branch, every pull request must satisfy the following checklist before merge:

### 4.1 PR Preparation
1. **Target Branch:** All feature and bugfix PRs must target `develop` (never directly into `main`).
2. **Descriptive Title:** Follow conventional commit phrasing (e.g., `feat(domain): add LegalCase and CaseStatusHistory entities`).
3. **Template Completion:** Complete every section of `.github/pull_request_template.md`.

### 4.2 Quality Gate & Definition of Done
Prior to merging, the following conditions must be met:
- [ ] Code compiles without warnings (`mvn.cmd clean compile`).
- [ ] Local build and packaging succeeds (`mvn.cmd clean package`).
- [ ] All automated unit tests (JUnit 5) and Selenium E2E tests pass with 0 failures.
- [ ] No hardcoded passwords, tokens, API keys, or machine-specific absolute paths.
- [ ] Handwritten CSS conforms strictly to design guidelines (no unauthorized frameworks or purple palettes).
- [ ] At least one code review comment / sign-off recorded on GitHub.
- [ ] Branch is rebased or up to date with `develop` (fast-forward or clean merge).

---

## 5. Security & Academic Integrity

- **Zero Credentials Policy:** Never commit plain-text passwords or private certificates. Reference credential identifiers (e.g., `dockerhub-creds`, `tomcat-creds`) rather than secret payloads.
- **Traceability:** Every contribution must correlate with a defined user story or Kanban backlog item (`B-01` through `B-15`).

