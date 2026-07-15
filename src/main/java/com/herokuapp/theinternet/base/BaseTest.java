package com.herokuapp.theinternet.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;
import utilities.ConfigReader;
import utilities.DriverFactory;

import java.lang.reflect.Method;

@Listeners({com.herokuapp.theinternet.base.TestListener2.class})
public class BaseTest {
    protected Logger log;
    protected WebDriver driverInstance;
    protected String testSuiteName;
    protected String testName;
    protected String testMethodName;

    @Parameters({"browser", "chromeProfile", "deviceName"})
    //@Parameters({"chromeProfile", "deviceName"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method, @Optional String browser, @Optional String chromeProfile, @Optional String deviceName, ITestContext ctx) {
        // Logging
        this.testName = ctx.getCurrentXmlTest().getName();
        this.testSuiteName = ctx.getSuite().getName();
        this.testMethodName = method.getName();

        log = LogManager.getLogger(testName);
        log.info("Starting test: " + testMethodName);

        DriverFactory.setDriver(DriverFactory.createDriver(browser, chromeProfile, deviceName));
        driverInstance = DriverFactory.getDriver();
        log.info("Driver is: " + driverInstance);
        driverInstance.get(ConfigReader.get("baseUrl"));

        // Utilities for listeners
        ctx.setAttribute("base", this);
        ctx.setAttribute("screenUtils", new ScreenUtils(driverInstance, log));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Closing driver");
        DriverFactory.quitDriver();
    }
}