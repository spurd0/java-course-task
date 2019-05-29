package com.babenko;

import com.babenko.testkg.report.HtmlReport;
import com.babenko.testkg.report.Report;
import com.babenko.testkg.runner.RunnerCallback;
import com.babenko.testkg.runner.TestRunner;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    private static final int ARGS_POSITION_THREADS_COUNT = 0;
    private static final int ARGS_POSITION_CLASS_NAME = 1;
    private static final int ARGS_POSITION_CLASS_NAME_START = 2;
    private static final String ARG_NAME_CLASS_NAME = "class-name";
    private static final String TEXT_TO_EXECUTE = "N class-name [class-name]*";
    private volatile static Report reporter = new HtmlReport("report.html");
    private static Integer activeThreads;
    private static RunnerCallback callback = new RunnerCallback() {
        @Override
        public void onTestingFinished() {
            Main.onTestingFinished();
        }

        @Override
        public void onTestingFailed(Exception e) {
            System.out.println(String.format("Oops, got system error while testing:%s", e.toString()));
        }
    };

    public static void main(String[] args) {
        System.out.println("Hello! Parsing arguments");
        parseArgumentsAndRunTests(args);
    }

    private static void parseArgumentsAndRunTests(String[] args) {
        int threadsCount = 0;
        List<String> classesToTest = new ArrayList<>();
        // Парсинг входящей строки
        try {
            threadsCount = Integer.parseInt(args[ARGS_POSITION_THREADS_COUNT]);
            if (!args[ARGS_POSITION_CLASS_NAME].equals(ARG_NAME_CLASS_NAME)) {
                throw new IllegalStateException(TEXT_TO_EXECUTE);
            }
            if (args.length <= ARGS_POSITION_CLASS_NAME_START) return;
            classesToTest.addAll((Arrays.asList(args).subList(ARGS_POSITION_CLASS_NAME_START, args.length)));
            // Pattern p = Pattern.compile("(\\d+) class-name ([\\w.]*)"); // todo use regexp?
        } catch (Exception ex) {
            System.out.println(TEXT_TO_EXECUTE);
            return;
        }

        runTesting(threadsCount, classesToTest);
    }

    private static void runTesting(int threadsCount, List<String> classesNamesForTest) {
        System.out.printf("Running testing with threads count:%d%n", threadsCount);
        System.out.printf("Classes to test:%s \n", classesNamesForTest.toString());

        Queue<Class<?>> classesForTest = getClassesForTestQueue(classesNamesForTest);
        Runnable testRunnable = new TestRunner(reporter, classesForTest, callback);
        List<Thread> testThreads = getTestingThreads(threadsCount, testRunnable);
        activeThreads = threadsCount;
        for (Thread thread : testThreads) thread.start();
    }

    private static Queue<Class<?>> getClassesForTestQueue(List<String> classesNamesForTest) {
        System.out.println("Lets look what did you give us for test");
        Queue<Class<?>> classesList = new LinkedList<>();
        classesNamesForTest
                .forEach(className -> {
                    try {
                        Class<?> cl = Class.forName(className);
                        classesList.add(cl);
                    } catch (ClassNotFoundException e) {
                        System.out.printf("Error! Wrong class name %s \n", className);
                    }
                });

        Queue<Class<?>> classesForTest = new ArrayBlockingQueue<>(classesList.size());
        classesForTest.addAll(classesList);

        System.out.printf("Successfully created %d classes for test \n", classesForTest.size());
        return classesForTest;
    }

    private static List<Thread> getTestingThreads(int threadsCount, Runnable testRunnable) {
        List<Thread> testThreads = new ArrayList<>(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            testThreads.add(new Thread(testRunnable));
        }
        return testThreads;
    }

    private static void onTestingFinished() {
        synchronized (activeThreads) {
            activeThreads--;
            if (activeThreads != 0) return;
        }
        System.out.println("Testing finished. Have a nice day!");
        reporter.doReport();
        System.exit(0);
    }
}
