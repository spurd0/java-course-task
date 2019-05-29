package com.babenko.testkg.report;

import j2html.TagCreator;
import j2html.tags.ContainerTag;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;

public class HtmlReport implements Report {
    private final ConcurrentHashMap<String, List<TestResult>> results = new ConcurrentHashMap<>();
    private final String reportName;


    public HtmlReport(String reportName) {
        this.reportName = reportName;
    }

    @Override
    public synchronized void addTestResult(String testGroup, TestResult result) {
        if (results.get(testGroup) != null) {
            results.get(testGroup).add(result);
        } else {
            List<TestResult> testResults = new ArrayList<>();
            testResults.add(result);
            results.put(testGroup, testResults);
        }
    }

    @Override
    public void doReport() {
        List<ContainerTag> tags = new ArrayList<>();

        ContainerTag body = body(div(results.keySet().stream().map(
                testGroup ->
                        div(attrs("#employees"),
                                h1(testGroup),
                                each(results.get(testGroup), result ->
                                        div(attrs("#result"),
                                                h2(result.getTestName()),
                                                h4(result.getResult().toString()),
                                                h4(result.getMessage())
                                        )
                                )
                        )
                ).toArray(ContainerTag[]::new))
        );

        try (FileWriter writer = new FileWriter(reportName)) {
            TagCreator.html(body).render(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
