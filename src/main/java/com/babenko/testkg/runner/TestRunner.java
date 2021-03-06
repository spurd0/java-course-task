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
import java.util.Queue;
import java.util.stream.Collectors;

public class TestRunner implements Runnable {
    private static final boolean DISPLAY_StACK_TRACE = false;

    private final Report reporter;
    private final RunnerCallback callback;
    @NotNull
    private Queue<Class<?>> testClasses;

    public TestRunner(Report reporter, @NotNull Queue<Class<?>> testClasses, RunnerCallback callback) {
        this.reporter = reporter;
        this.testClasses = testClasses;
        this.callback = callback;
    }

    @Override
    public void run() {
        waitForTestClass();
    }

    private void waitForTestClass() {
        while (testClasses.peek() != null) {
            try {
                Class<?> testClass = testClasses.poll();
                executeTests(testClass);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
                callback.onTestingFailed(e);
            }
        }
        callback.onTestingFinished();
    }

    private void executeTests(Class testClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<Method> testMethods = getMethods(testClass, Test.class);
        @Nullable Method beforeMethod = getMethod(testClass, Before.class);
        @Nullable Method afterMethod = getMethod(testClass, After.class);

        String testGroup = testClass.getName();

        for (Method method : testMethods) {
            Object instance = testClass.newInstance();
            if (beforeMethod != null) runAnnotateMethod(instance, beforeMethod);
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
            if (afterMethod != null) runAnnotateMethod(instance, afterMethod);
        }
    }

    @Nullable
    private Method getMethod(Class testClass, Class<? extends Annotation> annotation) {
        List<Method> methods = getMethods(testClass, annotation);
        if (!methods.isEmpty()) {
            return methods.stream()
                    .findFirst()
                    .get();
        }
        return null;
    }

    private List<Method> getMethods(Class testClass, Class<? extends Annotation> annotation) {
        return Arrays.stream(testClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
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

    private void runAnnotateMethod(Object instance, Method method)
            throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        method.invoke(instance);
    }
}
