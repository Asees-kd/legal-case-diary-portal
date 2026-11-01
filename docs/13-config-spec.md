# Configuration Specification - Legal Case Diary Portal (Task 13)
**Student:** Asees Kaur Dham (Roll No. 23102C0050)

| Category | Specification Item | Configuration Details / Path | Purpose & Access Control |
| :--- | :--- | :--- | :--- |
| **Packages** | `openjdk-17-jre-headless` | APT system package | Java 17 runtime for Spring Boot executable WAR |
| **Users & Groups** | `legaldiary:legaldiary` | System account (`/usr/sbin/nologin`) | Principle of least privilege runtime user |
| **Folders** | `/opt/legal-diary` | Base directory (`0755`, owner `legaldiary`) | Application root directory |
| **Folders** | `/opt/legal-diary/releases` | Release archives (`0755`, owner `legaldiary`) | Versioned immutable release store |
| **Folders** | `/opt/legal-diary/shared` | Shared persistent state (`0755`, owner `legaldiary`) | Embedded H2 persistent database storage |
| **Files** | `legal-diary.war` | `/opt/legal-diary/releases/{{ app_version }}/legal-diary.war` | Packaged Spring Boot web application archive |
| **Files** | Current Symlink | `/opt/legal-diary/current -> releases/{{ app_version }}` | Atomic zero-downtime release switching |
| **Services** | `legal-diary.service` | `/etc/systemd/system/legal-diary.service` (`0644`) | Systemd daemon unit managing lifecycle & auto-restart |
| **Ports** | `8083` | `Environment="SERVER_PORT=8083"` | Dedicated target service HTTP port |
