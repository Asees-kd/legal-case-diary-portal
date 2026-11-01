package com.legal.diary.selenium.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JUnit 5 extension that automatically captures a screenshot to
 * target/screenshots/<testName>.png whenever a test fails.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public class ScreenshotOnFailureExtension implements TestWatcher {

    private static WebDriver currentDriver;

    public static void setDriver(WebDriver driver) {
        currentDriver = driver;
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = context.getRequiredTestMethod().getName();
        System.err.println("[ScreenshotOnFailureExtension] Test failure detected: " + testName + " - Capturing screenshot...");

        WebDriver driver = currentDriver;
        if (driver != null) {
            System.err.println("[ScreenshotOnFailureExtension] Current URL at failure: " + driver.getCurrentUrl());
            System.err.println("[ScreenshotOnFailureExtension] Page title: " + driver.getTitle());
        }

        if (driver instanceof TakesScreenshot takesScreenshot) {
            try {
                byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
                Path screenshotDir = Paths.get("target", "screenshots");
                Files.createDirectories(screenshotDir);
                Path destination = screenshotDir.resolve(testName + ".png");
                Files.write(destination, screenshotBytes);
                System.out.println("[ScreenshotOnFailureExtension] Screenshot successfully saved to " + destination.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("[ScreenshotOnFailureExtension] Failed to save screenshot: " + e.getMessage());
            }
        }
    }
}

