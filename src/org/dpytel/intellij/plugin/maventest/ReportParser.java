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

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.states.NotFailedState;
import com.intellij.openapi.vfs.VirtualFile;
import org.dpytel.intellij.plugin.maventest.model.*;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ReportParser {
    public ReportParser() {
    }

    public TestProxy parseTestSuite(VirtualFile child) {
        SAXBuilder builder = new SAXBuilder();
        File file = new File(child.getCanonicalPath());
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            Document document = builder.build(in);
            Element rootElement = document.getRootElement();
            Attribute name = rootElement.getAttribute("name");
            TestProxy testSuite = new TestProxy(new TestSuiteInfo(name.getValue()));
            @SuppressWarnings("unchecked")
            List<Element> testcases = rootElement.getChildren("testcase");
            AggregatedState suiteState = new AggregatedState(testSuite);
            testSuite.setState(suiteState);

            for (Element testcase : testcases) {
                TestProxy childTestProxy = parseTestCase(testcase);
                testSuite.addChild(childTestProxy);
            }
            return testSuite;
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    private TestProxy parseTestCase(Element testcase) {
        String methodName = testcase.getAttributeValue("name");
        String className = testcase.getAttributeValue("classname");
        TestProxy testCase = new TestProxy(new TestMethodInfo(className, methodName));
        List children = testcase.getChildren();
        if (children.size() == 0) {
            testCase.setState(NotFailedState.createPassed());
        } else {
            Element errorOrFailure = (Element) children.get(0);
            String stateName = errorOrFailure.getName();
            if ("skipped".equals(stateName)) {
                String message = errorOrFailure.getAttributeValue("message");
                testCase.setState(new SkippedState(testCase, message));
            } else {
                FailOrErrorState state;
                if ("failure".equals(stateName)) {
                    state = FailOrErrorState.createFailedState(errorOrFailure.getText());
                } else if ("error".equals(stateName)) {
                    state = FailOrErrorState.createErrorState(errorOrFailure.getText());
                } else {
                    throw new IllegalStateException("Unknown state: " + stateName);
                }
                testCase.setState(state);
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
                state.setSystemout(sysout.toString());
                state.setSystemerr(syserr.toString());
            }
        }
        return testCase;
    }
}