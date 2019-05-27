package com.babenko.testkg.runner;

import com.babenko.testkg.report.HtmlReport;

public class TestingThread extends Thread {
    private TestRunner runner;

    public TestingThread(int threadNum) {
        runner = new TestRunner(new HtmlReport(String.format("report%d.html", threadNum)));
    }

    public void setTestClass(Class testClass) {
        runner.setTestClass(testClass);
    }

    public boolean isTreadBusy() {
        return runner.isTestRunning();
    }
}