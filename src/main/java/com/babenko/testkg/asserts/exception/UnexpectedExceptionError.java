package com.babenko.testkg.asserts.exception;

public class UnexpectedExceptionError extends AssertException {
    public UnexpectedExceptionError(String s, Throwable throwable) {
        super(s, throwable);
    }
}
