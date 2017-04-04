/*
 * Copyright 2014 Dawid Pytel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dpytel.intellij.plugin.maventest;

import com.intellij.execution.testframework.sm.runner.GeneralTestEventsProcessor;
import com.intellij.execution.testframework.sm.runner.events.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import jetbrains.buildServer.messages.serviceMessages.TestIgnored;
import jetbrains.buildServer.messages.serviceMessages.TestStarted;
import jetbrains.buildServer.messages.serviceMessages.TestSuiteStarted;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ReportParser {
    private final GeneralTestEventsProcessor testEventsProcessor;

    public ReportParser(GeneralTestEventsProcessor testEventsProcessor) {
        this.testEventsProcessor = testEventsProcessor;
    }

    public void parseTestSuite(VirtualFile child) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        File file = new File(child.getCanonicalPath());
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            Document document = builder.build(in);
            Element rootElement = document.getRootElement();
            Attribute nameAttribute = rootElement.getAttribute("name");
            String name = nameAttribute.getValue();
            TestSuiteStarted suiteStarted = new TestSuiteStarted(name);
            testEventsProcessor.onSuiteStarted(new TestSuiteStartedEvent(suiteStarted, null));
            @SuppressWarnings("unchecked")
            List<Element> testcases = rootElement.getChildren("testcase");

            for (Element testcase : testcases) {
                parseTestCase(testcase);
            }

            testEventsProcessor.onSuiteFinished(new TestSuiteFinishedEvent(name));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private void parseTestCase(Element testcase) {
        String methodName = testcase.getAttributeValue("name");
        String className = testcase.getAttributeValue("classname");

        TestStarted testStarted = new TestStarted(methodName, true, null);
        TestStartedEvent testStartedEvent = new TestStartedEvent(testStarted, null);
        testEventsProcessor.onTestStarted(testStartedEvent);

        List children = testcase.getChildren();
        Long duration = getDuration(testcase);
        if (children.size() == 0) {
            reportTestFinished(methodName, duration);
        } else {
            Element errorOrFailure = (Element) children.get(0);
            String stateName = errorOrFailure.getName();
            if ("skipped".equals(stateName)) {
                String message = errorOrFailure.getAttributeValue("message", "");
                testEventsProcessor.onTestIgnored(new TestIgnoredEvent(new TestIgnored(methodName, message), null));
            } else {
                StringBuilder sysout = new StringBuilder();
                StringBuilder syserr = new StringBuilder();
                for (int i = 1; i < children.size(); ++i) {
                    Element child = (Element) children.get(i);
                    String name = child.getName();
                    String text = child.getText();
                    if ("system-out".equals(name)) {
                        sysout.append(text);
                    } else if ("system-err".equals(name)) {
                        syserr.append(text);
                    }
                }
                if (sysout.length() > 0) {
                    testEventsProcessor.onTestOutput(new TestOutputEvent(methodName, sysout.toString(), true));
                }
                if (syserr.length() > 0) {
                    testEventsProcessor.onTestOutput(new TestOutputEvent(methodName, syserr.toString(), false));
                }
                boolean testError;
                if ("failure".equals(stateName)) {
                    testError = false;
                } else if ("error".equals(stateName)) {
                    testError = true;
                } else {
                    throw new IllegalStateException("Unknown state: " + stateName);
                }
                String failureMessage = errorOrFailure.getAttributeValue("message", "");
                reportTestFailure(methodName, testError, failureMessage);
                reportTestFinished(methodName, duration);
            }
        }
    }

    private void reportTestFinished(String methodName, Long duration) {
        testEventsProcessor.onTestFinished(new TestFinishedEvent(methodName, duration));
    }

    private void reportTestFailure(String methodName, boolean testError, String failureMessage) {
        testEventsProcessor.onTestFailure(new TestFailedEvent(methodName, failureMessage, null, testError, null, null));
    }

    private Long getDuration(Element testcase) {
        String timeValue = testcase.getAttributeValue("time", (String) null);
        if (StringUtil.isEmpty(timeValue)) {
            return null;
        }
        return (long)(Double.parseDouble(timeValue) * 1000);
    }
}