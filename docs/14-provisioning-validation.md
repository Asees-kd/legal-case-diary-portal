# Provisioning and Reliability Validation

**Student**: Asees Kaur Dham (Roll No. 23102C0050) | **Project**: Legal Case Diary Portal  
**Target Environment**: Ubuntu systemd (`legal-diary.service`) on port 8083

## 1. Clean-Start Provisioning Run
Executed after wiping `/opt/legal-diary`, systemd unit, and `legaldiary` user.
```
TASK [Verify application endpoint is healthy] **********************************
ok: [localhost]
PLAY RECAP *********************************************************************
localhost                  : ok=11   changed=8    unreachable=0    failed=0    skipped=0
```

## 2. Idempotency Verification Run
Re-executed identical playbook command immediately following clean deployment.
```
TASK [Verify application endpoint is healthy] **********************************
ok: [localhost]
PLAY RECAP *********************************************************************
localhost                  : ok=10   changed=0    unreachable=0    failed=0    skipped=0
```
**Result**: `changed=0`, confirming full convergence and playbook idempotency.

## 3. Standalone Health Check Verification
Executed `ansible-playbook -i ansible/inventory.ini ansible/healthcheck.yml`.
```
TASK [Display health verification status] **************************************
ok: [localhost] => {"msg": "Application health check passed with HTTP 200 on port 8083."}
PLAY RECAP *********************************************************************
localhost                  : ok=3    changed=0    unreachable=0    failed=0    skipped=0
```

## 4. Rollback Validation
1. **Defect Injected**: Corrupted release `2.0.0` deployed; current symlink pointed to `2.0.0`. Service crashed:
   - `Error: Invalid or corrupt jarfile /opt/legal-diary/current/legal-diary.war`
   - `systemd: legal-diary.service: Failed with result 'exit-code'.`
2. **Rollback Playbook Executed**: `ansible-playbook -i ansible/inventory.ini ansible/rollback.yml`.
   - Symlink repointed: `/opt/legal-diary/current -> /opt/legal-diary/releases/1.0.0`
   - Service restarted via systemd handler.
   - Endpoint `/login` polled until HTTP 200 response received.
```
PLAY RECAP *********************************************************************
localhost                  : ok=5    changed=2    unreachable=0    failed=0    skipped=1
```

## 5. Post-rollback verification (live)

service: active
health check after rollback: HTTP 200
current symlink after rollback: /opt/legal-diary/releases/1.0.0
