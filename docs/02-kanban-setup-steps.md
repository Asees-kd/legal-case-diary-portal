# Exact Setup Steps: GitHub Projects Kanban Board
**Project:** CI/CD Pipeline for a Legal Case Diary Portal  
**Student:** Asees Kaur Dham (Roll No. 23102C0050)  
**Deliverable:** Official Task 2 (Agile Planning & DevOps Workflow)  

---

## 1. Overview
This guide provides click-by-click instructions to create a GitHub Projects Kanban board matching the 15-item MVP backlog (`B-01` to `B-15`). You will use this board to track task status through **To Do**, **In Progress**, and **Done** throughout the DevOps lifecycle.

---

## 2. Step-by-Step Board Creation

### Step 1: Navigate to GitHub Projects
1. Open your web browser and navigate to [https://github.com](https://github.com).
2. Log in with your GitHub account.
3. In the top navigation bar, click on your profile picture in the top-right corner and select **Your projects** (or open `https://github.com/users/<your-username>/projects`).
   *(Alternatively, if you prefer repository-level projects, you can do this from the repo page once created in Task 4).*

### Step 2: Create a New Project
1. Click the green **"New project"** button (top right).
2. A template modal titled *"Select a template"* appears.
3. Select the **"Board"** template (the Kanban column layout).
4. In the Project name input field, enter:  
   `Legal Case Diary Portal - DevOps Kanban`
5. Click the green **"Create"** button.

### Step 3: Verify & Standardize the Columns
1. GitHub creates three default columns: **Todo**, **In Progress**, and **Done**.
2. To rename or customize columns, edit the **Status** field options:
   - Click the view dropdown or the three dots (`...`) next to the project title and select **Project settings** (or click the column header menu `...` and choose **Edit limit / details**).
   - In the **Status** field configuration, edit the option values to ensure they read:
     - `To Do`
     - `In Progress`
     - `Done`
   - Save the changes to return to the board view.

---

## 3. Adding the 15 MVP Backlog Items (B-01 to B-15)

### Step 4: Add Completed Task 1 Item to "Done"
At the bottom of the **Done** column:
1. Click **"+ Add item"**.
2. Type:  
   `B-01: Problem Definition & Requirements Specification`
3. Press **Enter**.

### Step 5: Add Remaining Items to "To Do"
At the bottom of the **To Do** column, click **"+ Add item"** and enter each card sequentially:
1. `B-02: Architecture & System Design Document`
2. `B-03: Maven Scaffolding (Java 17, Spring Boot 3, WAR, Tomcat 10)`
3. `B-04: Domain Entities, Enums & JPA Repositories`
4. `B-05: Business Logic Service & Strict RBAC State Transition Rules`
5. `B-06: Spring Security Configuration & In-Memory RBAC`
6. `B-07: MVC Web Controllers & Dynamic Query Handler`
7. `B-08: Bespoke Legal Handcrafted CSS Design System`
8. `B-09: Responsive Thymeleaf View Templates & Status Timeline`
9. `B-10: Automated Testing: JUnit 5 unit tests, plus Selenium WebDriver suite of 8 test cases (login valid/invalid, create case, case in list, update, search, role rule clerk cannot close/admin can, dashboard counts)`
10. `B-11: Local Build, WAR Generation & Tomcat 10.1 Verification`
11. `B-12: Git Version Control Setup & Branching Strategy`
12. `B-13: Jenkins CI/CD Pipeline as Code (bat for port 9090)`
13. `B-14: Docker Containerization for Tomcat 10 (port 8082)`
14. `B-15: Ansible Automation via WSL2 & Final Comprehensive Audit`

*(Note: In Task 4, when our remote GitHub repository is linked, you can right-click any card or click `...` -> "Convert to issue" to link them directly to repository commits and pull requests).*

### Step 6: Configure Sprint Field & Grouping
1. Click the `+` icon on the table/board field headers or go to **Project settings** -> **Fields** -> **New field**.
2. Choose **Single select** or **Iteration**, name the field: `Sprint`.
3. Add options: `Sprint 1` and `Sprint 2`.
4. Assign items `B-01` through `B-07` to `Sprint 1`.
5. Assign items `B-08` through `B-15` to `Sprint 2`.
6. On the board view, click the view settings / filter icon, choose **Group by**, and select **Sprint**.

---

## 4. Required Screenshots to Capture for College Submission & Viva

Save these screenshots in a folder or document for your exam dossier:

1. **Screenshot 1: Kanban Board Full View (`01_kanban_board_full_view.png`)**
   - **What to show:** The entire board displaying all three columns (`To Do`, `In Progress`, `Done`).
   - **Verification detail:** Ensure `B-01` is visible under `Done`, cards `B-02` to `B-15` are visible under `To Do`, and your GitHub profile avatar/username and the board title `Legal Case Diary Portal - DevOps Kanban` are clearly visible in the browser header.
2. **Screenshot 2: Card Description & Details Drawer (`02_kanban_card_detail.png`)**
   - **What to show:** Click on card `B-01` so the right-side detail drawer slides open, showing the description or link to `docs/01-problem-definition.md`.
3. **Screenshot 3: Board Grouped by Sprint (`03_kanban_grouped_by_sprint.png`)**
   - **What to show:** The board view with grouping enabled by the `Sprint` field, visibly showing `Sprint 1` (`B-01` to `B-07`) and `Sprint 2` (`B-08` to `B-15`) swimlanes/sections.

