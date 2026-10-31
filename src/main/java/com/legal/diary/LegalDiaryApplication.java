package com.legal.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;

/**
 * Main application entrypoint for Legal Case Diary Portal.
 * Extends SpringBootServletInitializer to support WAR deployment onto Apache Tomcat 10.1.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@SpringBootApplication
public class LegalDiaryApplication extends SpringBootServletInitializer {

    static {
        // Ensure H2 database directory exists before DataSource initialization
        String h2Path = System.getenv("H2_DB_PATH");
        if (h2Path == null || h2Path.isBlank()) {
            h2Path = System.getProperty("h2.db.path");
        }
        if (h2Path == null || h2Path.isBlank()) {
            h2Path = System.getProperty("user.home") + File.separator + ".legal-diary" + File.separator + "data" + File.separator + "casediary";
        }
        try {
            File dbFile = new File(h2Path);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LegalDiaryApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(LegalDiaryApplication.class, args);
    }
}
