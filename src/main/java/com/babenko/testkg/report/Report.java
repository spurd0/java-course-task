package com.babenko.testkg.report;

public interface Report {
    void addTestResult(String testGroup, TestResult result);

    void doReport();
}
