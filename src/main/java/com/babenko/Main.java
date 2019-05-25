package com.babenko;

import com.babenko.testkg.report.HtmlReport;
import com.babenko.testkg.runner.TestRunner;

import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException {
//        Создание класса по имени файла
//        Class cl = Class.forName("org.annotate.testkg.classes.Cookies");
        TestRunner runner = new TestRunner(new HtmlReport("report.html"));

        runner.runTests(ExampleTest.class);

        runner.doReport();
//        try {
//
//        } catch (Exception ex) {
//            System.out.println("java -cp <your-junit-jar>;<tested-classes> <your-main-class> N class-name [class-name]*");
//        }
    }
}
