# Jenkins-Docker CD Setup Guide
**Student:** Asees Kaur Dham (Roll No. 23102C0050)

### Manual Configuration Steps for Jenkins-Docker Automation:

1. **System PATH & Docker CLI Access:**
   Ensure `C:\Program Files\Docker\Docker\resources\bin` is added to Windows System (Machine) PATH so the Jenkins service can execute `docker`.
   Restart the Jenkins service from an Administrator PowerShell prompt:
   ```powershell
   Restart-Service Jenkins
   ```

2. **Docker Desktop Pre-requisite:**
   Ensure Docker Desktop is running with the Linux containers engine active.

3. **Jenkins Docker Hub Credentials:**
   Verify that global credential `dockerhub-creds` exists in Jenkins (Kind: Username with password):
   - Username: `aseeskaurd`
   - Password: `<Docker Hub Personal Access Token>`

4. **Service Permission Fallback (if Docker CLI fails as LocalSystem):**
   If Jenkins reports permission issues accessing the Docker pipe/daemon, open `services.msc`, locate the **Jenkins** service, open Properties -> **Log On**, and switch from *Local System account* to your Windows user account (`lenovo`), then restart the service.
