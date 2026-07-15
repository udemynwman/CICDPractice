package com.herokuapp.theinternet.base;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
    private int attempts = 0;
    private static final int maxRetry = 1;

    public boolean retry(ITestResult result) {
        if (attempts < maxRetry) {
            attempts++;
            return true;
        }
        return false;
    }
}
