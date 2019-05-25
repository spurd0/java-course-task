package com.babenko;

import com.babenko.testkg.report.HtmlReport;
import com.babenko.testkg.runner.TestRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final int ARGS_POSITION_THREADS_COUNT = 0;
    private static final int ARGS_POSITION_CLASS_NAME = 1;
    private static final int ARGS_POSITION_CLASS_NAME_START = 2;
    private static final String ARG_NAME_CLASS_NAME = "class-name";
    private static final String TEXT_TO_EXECUTE = "N class-name [class-name]*";

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException {
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

        System.out.printf("Running program with threads count:%d%n", threadsCount);
        System.out.printf("Classes to test:%s \n", classesToTest.toArray());

//        Создание класса по имени класса
        Class cl;
        try {
            cl = Class.forName(classesToTest.get(0));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        TestRunner runner = new TestRunner(new HtmlReport("report.html"));

        runner.runTests(cl);
        runner.doReport();
    }
}
