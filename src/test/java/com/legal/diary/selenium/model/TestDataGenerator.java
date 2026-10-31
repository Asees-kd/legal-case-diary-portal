package com.legal.diary.selenium.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generator producing unique, deterministic test data for end-to-end Selenium execution.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public class TestDataGenerator {

    private static final AtomicInteger COUNTER = new AtomicInteger((int) (System.currentTimeMillis() % 10000));

    public static String generateUniqueCaseNumber() {
        return "DL/HC/2026/SEL-" + COUNTER.incrementAndGet();
    }

    public static String generateTitle() {
        return "Matter of Test Suite #" + COUNTER.get() + " vs Respondent Corp";
    }

    public static String generateCourt() {
        return "High Court of Delhi, Bench " + (COUNTER.get() % 5 + 1);
    }

    public static String generatePetitioner() {
        return "Petitioner " + COUNTER.get();
    }

    public static String generateRespondent() {
        return "Respondent " + COUNTER.get();
    }
}

