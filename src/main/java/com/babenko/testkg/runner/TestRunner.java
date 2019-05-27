package com.babenko.testkg.runner;

import com.babenko.testkg.annotation.After;
import com.babenko.testkg.annotation.Before;
import com.babenko.testkg.annotation.Test;
import com.babenko.testkg.asserts.exception.AssertException;
import com.babenko.testkg.asserts.exception.UnexpectedExceptionError;
import com.babenko.testkg.report.Report;
import com.babenko.testkg.report.TestResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TestRunner {
    private static final boolean DISPLAY_StACK_TRACE = false;

    private Report reporter;

    @Nullable
    private Class<?> testClass;

    private AtomicBoolean testIsRunning = new AtomicBoolean(false);

    public TestRunner(Report reporter) {
        this.reporter = reporter;
        new Thread(this::waitForTestClass).start();
    }

    private void waitForTestClass() {
        while (testClass == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            testIsRunning.set(true);
            executeTests(testClass);
            doReport();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace(); // todo add callback for failed test
        }
        testIsRunning.set(false); // todo add callback for succeed test
        testClass = null;
        waitForTestClass();
    }

    private void executeTests(Class testClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<Method> testMethods = Arrays.stream(testClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Test.class))
                .collect(Collectors.toList());

        String testGroup = testClass.getName();

        for (Method method : testMethods) {
            Object instance = testClass.newInstance();
            runAnnotateMethod(instance, Before.class);
            try {
                runTestMethod(instance, method);
                reporter.addTestResult(testGroup, new TestResult(
                        method.getName(),
                        TestResult.ResultType.SUCCESS,
                        ""));
            } catch (InvocationTargetException exp) {
                reporter.addTestResult(testGroup, new TestResult(
                        method.getName(),
                        TestResult.ResultType.FAIL,
                        exp.getCause().getMessage()));
                if (DISPLAY_StACK_TRACE) exp.printStackTrace();
            }
            runAnnotateMethod(instance, After.class);
        }
    }

    private void doReport() {
        reporter.doReport();
    }

    private void runTestMethod(Object instance, Method method) throws InvocationTargetException {
        try {
            method.setAccessible(true);
            method.invoke(instance);
        } catch (IllegalAccessException e) {
            if (DISPLAY_StACK_TRACE) e.printStackTrace();
        } catch (InvocationTargetException exp) {
            if (!(exp.getCause() instanceof AssertException)
                    && !method.getAnnotation(Test.class).expected().isInstance(exp.getCause())) {

                String errorMessage = String.format("expected exception - %s, but actual exception - %s ",
                        method.getAnnotation(Test.class).expected().toString(),
                        exp.getCause().toString());

                throw new InvocationTargetException(new UnexpectedExceptionError(errorMessage, exp.getCause()));
            } else {
                throw exp;
            }

        }
    }

    private void runAnnotateMethod(Object instance, Class<? extends Annotation> annotation)
            throws InvocationTargetException, IllegalAccessException {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                method.setAccessible(true);
                method.invoke(instance);
                return;
            }
        }
    }

    public void setTestClass(@NotNull Class<?> testClass) {
        this.testClass = testClass;
    }

    public boolean isTestRunning() {
        return testIsRunning.get();
    }
}
