package com.babenko.testkg.report;

public class TestResult {

    private String testName;
    private ResultType result;
    private String message;


    public TestResult(String testName,
                      ResultType result,
                      String message) {
        this.testName = testName;
        this.result = result;
        this.message = message;
    }

    public enum ResultType {
        SUCCESS {
            @Override
            public String toString() {
                return "SUCCESS";
            }
        },
        FAIL {
            @Override
            public String toString() {
                return "FAIL";
            }
        }
    }

    public String getTestName() {
        return testName;
    }

    public ResultType getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
