package com.herokuapp.theinternet.base;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenUtils {

    private final WebDriver driver;
    private final Logger log;

    public ScreenUtils(WebDriver driver, Logger log) {
        this.driver = driver;
        this.log = log;
    }

    public String takeScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        String screenshotName = testName + "_" + timestamp + ".png";
        String screenshotPath = "test-output/screenshots/" + screenshotName;

        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File(screenshotPath);

        try {
            Files.createDirectories(destFile.getParentFile().toPath());
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("Screenshot saved: " + screenshotPath);
        } catch (IOException e) {
            log.error("Failed to save screenshot: " + e.getMessage());
        }

        return screenshotPath;
    }
}
