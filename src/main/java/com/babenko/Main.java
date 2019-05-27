package com.babenko;

import com.babenko.testkg.runner.TestingThread;

import java.util.*;

public class Main {
    private static final int ARGS_POSITION_THREADS_COUNT = 0;
    private static final int ARGS_POSITION_CLASS_NAME = 1;
    private static final int ARGS_POSITION_CLASS_NAME_START = 2;
    private static final String ARG_NAME_CLASS_NAME = "class-name";
    private static final String TEXT_TO_EXECUTE = "N class-name [class-name]*";

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
        List<TestingThread> testThreads = getTestingThreads(threadsCount);

        System.out.println("Starting test manager thread");
        new Thread(() -> {
            startThreads(testThreads);
            while (classesForTest.peek() != null) {
                System.out.printf("Got class for test from queue, %s \n", classesForTest.peek());
                for (int i = 0; i < testThreads.size(); i++) {
                    TestingThread testThread = testThreads.get(i);
                    if (testThread.isTreadBusy()) {
                        if (i == testThreads.size() - 1) {
                            System.out.println("All threads are busy, please wait");
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        continue;
                    }
                    System.out.printf("Running test for class, %s \n", classesForTest.peek());
                    testThread.setTestClass(classesForTest.poll());
                    break;
                }
            }
            System.out.println("Sorry, please wait few seconds for threads"); //todo remove this hack, have no time, sorry
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (TestingThread testThread : testThreads) { //todo implement some better checker for finishing tests, for example map with key-class & value = result (success\failed)
                while (testThread.isTreadBusy()) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Testing finished. Have a nice day!");
            System.exit(0);
        }).start();
    }

    private static void startThreads(List<TestingThread> testThreads) {
        for (TestingThread testThread : testThreads) {
            testThread.start();
            try {
                testThread.waitForStart();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<TestingThread> getTestingThreads(int threadsCount) {
        List<TestingThread> testThreads = new ArrayList<>(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            testThreads.add(new TestingThread(i));
        }
        return testThreads;
    }

    private static Queue<Class<?>> getClassesForTestQueue(List<String> classesNamesForTest) {
        System.out.println("Lets look what did you give us for test");
        Queue<Class<?>> classesForTest = new LinkedList<>();
        classesNamesForTest
                .forEach(className -> {
                    try {
                        Class<?> cl = Class.forName(className);
                        classesForTest.add(cl);
                    } catch (ClassNotFoundException e) {
                        System.out.printf("Error! Wrong class name %s \n", className);
                    }
                });

        System.out.printf("Successfully created %d classes for test \n", classesForTest.size());
        return classesForTest;
    }
}
