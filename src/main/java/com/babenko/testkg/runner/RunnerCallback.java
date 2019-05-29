package com.babenko.testkg.runner;

public interface RunnerCallback {
    void onTestingFinished();

    void onTestingFailed(Exception e);
}
