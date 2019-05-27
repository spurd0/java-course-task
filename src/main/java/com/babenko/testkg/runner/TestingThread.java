package com.babenko.testkg.runner;

import com.babenko.testkg.report.HtmlReport;

import java.util.concurrent.CountDownLatch;

public class TestingThread extends Thread {
    private final int threadNum;
    private CountDownLatch startSignal = new CountDownLatch(1);
    private TestRunner runner;

    public TestingThread(int threadNum) {
        this.threadNum = threadNum;
    }

    @Override
    public void run() {
        Runnable action = () -> {
            runner = new TestRunner(new HtmlReport(String.format("report%d.html", threadNum)));
            startSignal.countDown();
        };
        action.run();
    }

    public void setTestClass(Class testClass) {
        runner.setTestClass(testClass);
    }

    public boolean isTreadBusy() {
        if (runner == null) throw new IllegalStateException("Wait until runner initialized");
        return runner.isTestRunning();
    }

    public void waitForStart() throws InterruptedException {
        startSignal.await();
    }
}