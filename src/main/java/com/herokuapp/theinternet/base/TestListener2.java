package com.herokuapp.theinternet.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener2 implements ITestListener {

    // Thread-safe: each thread gets its own ExtentTest
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // Thread-safe: ExtentReports created once per suite, not shared across threads
    private static ExtentReports extent;

    @Override
    public void onStart(ITestContext context) {
        synchronized (TestListener2.class) {
            if (extent == null) {
                String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
                String path = "test-output/ExtentReports/" + timestamp + "/index.html";
                ExtentSparkReporter spark = new ExtentSparkReporter(path);

                extent = new ExtentReports();
                extent.attachReporter(spark);
            }
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        synchronized (TestListener2.class) {
            if (extent != null) {
                extent.flush();
            }
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        Logger log = LogManager.getLogger(result.getMethod().getMethodName());
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);

        log.info("Starting " + result.getMethod().getMethodName());
        extentTest.log(Status.INFO, "Starting test " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Logger log = LogManager.getLogger(result.getMethod().getMethodName());
        log.info("Test passed: " + result.getMethod().getMethodName());

        ScreenUtils screenUtils = (ScreenUtils) result.getTestContext().getAttribute("screenUtils");
        if (screenUtils != null) {
            String screenshotPath = screenUtils.takeScreenshot(result.getMethod().getMethodName());
            test.get().addScreenCaptureFromPath(screenshotPath);
        }
        test.get().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Logger log = LogManager.getLogger(result.getMethod().getMethodName());
        log.info("Test failed: " + result.getMethod().getMethodName());

        IRetryAnalyzer retry = result.getMethod().getRetryAnalyzer(result);

        if (retry != null) {
            int currentRetry = result.getMethod().getCurrentInvocationCount();
            log.info("Retry count is: " + currentRetry);
            int maxRetry = 1; // or read from your Retry class
            log.info("Retry is: " + retry);
            log.info("Retry attempt #" + currentRetry + " for test: " + result.getName());
        }

        ScreenUtils screenUtils = (ScreenUtils) result.getTestContext().getAttribute("screenUtils");
        if (screenUtils != null) {
            String screenshotPath = screenUtils.takeScreenshot(result.getMethod().getMethodName());
            test.get().addScreenCaptureFromPath(screenshotPath);
        }

        test.get().log(Status.FAIL, "Test failed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Logger log = LogManager.getLogger(result.getMethod().getMethodName());
        log.info("Test skipped: " + result.getMethod().getMethodName());

        ScreenUtils screenUtils = (ScreenUtils) result.getTestContext().getAttribute("screenUtils");
        if (screenUtils != null) {
            String screenshotPath = screenUtils.takeScreenshot(result.getMethod().getMethodName());
            test.get().log(Status.SKIP, "Test skipped");
            test.get().addScreenCaptureFromPath(screenshotPath);
        } else {
            test.get().log(Status.SKIP, "Test skipped");
        }
    }
}
