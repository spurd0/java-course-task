package com.babenko.testkg.asserts.exception;


public class AssertException extends RuntimeException {

    public AssertException() {
    }

    public AssertException(String s) {
        super(s);
    }

    public AssertException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
