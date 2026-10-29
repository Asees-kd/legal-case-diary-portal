# 07. Jenkins CI Job Setup Guide

**Project:** CI/CD Pipeline for a Legal Case Diary Portal  
**Student:** Asees Kaur Dham (Roll No. 23102C0050)  
**Jenkins URL:** http://localhost:9090  
**Repository:** https://github.com/Asees-kd/legal-case-diary-portal  

---

## Step 1: Verify Required Plugins Are Installed

1. Open http://localhost:9090 in your browser and log in.
2. Click **Manage Jenkins** → **Plugins** → **Installed plugins** tab.
3. In the search box, type each plugin name and confirm it appears in the list:
   - **Git** (or "Git plugin") — provides SCM polling and Git clone.
   - **Maven Integration** — provides "Invoke top-level Maven targets" build step.
   - **Pipeline** — provides Pipeline job type (needed for later Task 8 Jenkinsfile).
4. If any are missing: click the **Available plugins** tab, search by name, tick the checkbox, click **Install**.

---

## Step 2: Create the Freestyle Job "legalcase-ci"

1. From the Jenkins dashboard, click **New Item**.
2. In the **Item name** field type exactly: `legalcase-ci`
3. Select **Freestyle project** (the first option).
4. Click **OK**.

---

## Step 3: Configure Source Code Management (SCM)

1. In the job configuration page, scroll to **Source Code Management**.
2. Select **Git**.
3. In **Repository URL** paste:
   ```
   https://github.com/Asees-kd/legal-case-diary-portal.git
   ```
4. Leave **Credentials** as `- none -` (the repository is public).
5. Under **Branches to build**, change `*/master` to:
   ```
   */develop
   ```
6. Leave all other SCM options at their defaults.

---

## Step 4: Configure Build Trigger (Poll SCM)

1. Scroll to **Build Triggers**.
2. Tick the checkbox **Poll SCM**.
3. In the **Schedule** box that appears, enter:
   ```
   H/2 * * * *
   ```
   This polls GitHub every 2 minutes. Jenkins will start a build automatically when it detects a new commit on `*/develop`.

> **Note:** The `H` symbol distributes load across nodes. For testing you can also click **Build Now** from the job page manually.

---

## Step 5: Configure the Build Step

1. Scroll to **Build Steps**.
2. Click **Add build step** → **Invoke top-level Maven targets**.
3. In the **Maven Version** dropdown, select **Maven3**  
   *(this is the tool name configured in Manage Jenkins → Tools → Maven installations).*
4. In the **Goals** field enter:
   ```
   clean package
   ```
5. Leave all other fields at defaults.

> **Why not use `-DskipTests`?** We want Jenkins to run the 6 JUnit tests on every build to catch regressions early.

---

## Step 6: Configure Post-Build Action (Archive Artifacts)

1. Scroll to **Post-build Actions**.
2. Click **Add post-build action** → **Archive the artifacts**.
3. In the **Files to archive** field enter:
   ```
   target/*.war
   ```
4. Leave the other archiving options (e.g., "Allow empty archive") unchecked.

---

## Step 7: Save the Job

1. Click **Save** at the bottom of the page.
2. You will be taken to the `legalcase-ci` job overview page.

---

## Step 8: Trigger and Verify the First Build

### Trigger manually (immediate test):
1. From the `legalcase-ci` job page, click **Build Now** in the left sidebar.
2. A build entry appears under **Build History** in the left panel (e.g., `#1`).
3. Click the build number → **Console Output**.
4. Scroll to the bottom. You should see:
   ```
   [INFO] BUILD SUCCESS
   [INFO] Total time: ...
   [INFO] Finished at: ...
   ```
5. After the build finishes, click **Workspace** (or go to the build → **Archived Artifacts**) and confirm `legal-diary.war` is listed.

### Verify SCM polling triggers automatically:
1. After pushing a new commit to `develop` on GitHub, wait up to 2 minutes.
2. On the `legalcase-ci` job page, look at **Build History** — a new build should appear with the description `Started by an SCM change`.
3. Click the build → **Console Output** to see the Maven output ending in `BUILD SUCCESS`.
4. In the left sidebar, click **Git Polling Log** to see the SCM change detection message:
   ```
   Changes found
   Scheduling build
   ```

---

## Step 9: Evidence Screenshots to Capture

| # | What to Screenshot | Where to Find It |
|:--|:---|:---|
| 1 | Plugins installed page showing Git, Maven Integration, Pipeline | Manage Jenkins → Plugins → Installed |
| 2 | Job configuration page showing SCM = Git, branch `*/develop`, Poll SCM `H/2 * * * *` | legalcase-ci → Configure |
| 3 | Console output of a successful build ending in `BUILD SUCCESS` | legalcase-ci → Build #N → Console Output |
| 4 | Archived artifact `legal-diary.war` listed | legalcase-ci → Build #N → Archived Artifacts |
| 5 | Git Polling Log showing "Changes found / Scheduling build" | legalcase-ci → Git Polling Log |

---

## Viva Voce Notes (Task 7)

Task 7 establishes continuous integration by configuring a Jenkins Freestyle job **legalcase-ci** on port 9090. The job polls the public GitHub repository (`*/develop` branch) every 2 minutes via the `H/2` SCM cron schedule, executes `mvn clean package` using the pre-configured `Maven3` tool (Maven 3.9.16), and archives `target/*.war` as a build artifact. When a new commit is pushed to `develop`, Jenkins detects the SCM change via polling, triggers the build automatically, runs all 6 JUnit tests, packages the WAR, and provides a console log confirming **BUILD SUCCESS** with the compiled `legal-diary.war`. This proves the CI pipeline reacts to developer commits without any manual intervention.

---

*Generated: 2026-10-29 | Student: Asees Kaur Dham | Roll No. 23102C0050*

