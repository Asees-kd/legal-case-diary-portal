# Task 8: Jenkins Pipeline Setup and Deployment Guide

**Project:** CI/CD Pipeline for a Legal Case Diary Portal  
**Student:** Asees Kaur Dham  
**Roll Number:** 23102C0050  
**Jenkins URL:** `http://localhost:9090`  
**Repository:** `https://github.com/Asees-kd/legal-case-diary-portal`  
**Target Servlet Container:** Apache Tomcat 10.1 (`http://localhost:8080`)  

---

## 1. Disable Legacy Freestyle Job (Avoid Double Builds)
To prevent duplicate builds and port contention between the old Freestyle job and the new Pipeline job:
1. Open Jenkins at `http://localhost:9090` in your web browser.
2. Click on the existing Freestyle job **`legalcase-ci`**.
3. In the left-hand navigation menu, click **Disable Project**.
4. Confirm that the status icon displays as disabled (greyed out).

---

## 2. Create the Declarative Pipeline Job
Follow these click-by-click instructions:

1. On the Jenkins dashboard (`http://localhost:9090`), click **New Item** in the left sidebar.
2. Enter item name: **`legalcase-pipeline`**.
3. Select **Pipeline** as the project type.
4. Click **OK** at the bottom of the page.

---

## 3. Configure Pipeline from SCM
1. On the configuration page, scroll down to the **Pipeline** section.
2. In the **Definition** dropdown, select **Pipeline script from SCM**.
3. In the **SCM** dropdown, select **Git**.
4. Set **Repository URL**: `https://github.com/Asees-kd/legal-case-diary-portal.git`
5. Set **Credentials**: None (public GitHub repository) or your configured GitHub credential if needed.
6. Under **Branches to build**:
   - Change **Branch Specifier (blank for 'any')** to: `*/develop`
7. Set **Script Path**: `Jenkinsfile`
8. Under **Build Triggers** (optional, as the Jenkinsfile defines `pollSCM('H/2 * * * *')`):
   - Check **Poll SCM** and leave schedule empty (pipeline definition takes precedence).
9. Click **Save**.

---

## 4. Initial Run & Parameter Registration
Because declarative parameters are defined directly in the `Jenkinsfile`:
1. Click **Build Now** once. Jenkins will check out the `Jenkinsfile` from branch `develop` and discover the parameter definitions (`DEPLOY_ENV` and `TOMCAT_URL`).
2. Once the first build initializes, refresh the page.
3. Notice that the menu item changes from **Build Now** to **Build with Parameters**.

---

## 5. Build with Parameters Instructions
1. In the left navigation menu, click **Build with Parameters**.
2. Select or verify parameters:
   - **`DEPLOY_ENV`**: Select `dev` (or `staging`).
   - **`TOMCAT_URL`**: Ensure default value is `http://localhost:8080`.
3. Click the **Build** button.
4. Monitor the pipeline stages:
   - **Checkout**: Pulls source code from `develop`.
   - **Build**: Executes `mvn -B clean compile`.
   - **Unit Tests**: Executes `mvn -B test` (runs all unit & service tests).
   - **Package**: Packages `legal-diary.war` and archives the artifact.
   - **Deploy**: Uses Jenkins credential `tomcat-creds` to upload the WAR to Tomcat Manager at `/manager/text/deploy?path=/legal-diary&update=true`.
   - **Verify**: Polls `http://localhost:8080/legal-diary/login` using `curl.exe` until HTTP 200 is confirmed, logging the active `DEPLOY_ENV`.
