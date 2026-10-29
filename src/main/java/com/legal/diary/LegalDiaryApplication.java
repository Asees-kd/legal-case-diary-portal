package com.legal.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main application entrypoint for Legal Case Diary Portal.
 * Extends SpringBootServletInitializer to support WAR deployment onto Apache Tomcat 10.1.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@SpringBootApplication
public class LegalDiaryApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LegalDiaryApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(LegalDiaryApplication.class, args);
    }
}

