package org.dpytel.intellij.plugin.maventest;

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.states.NotFailedState;
import com.intellij.execution.junit2.states.SuiteState;
import com.intellij.openapi.vfs.VirtualFile;
import org.dpytel.intellij.plugin.maventest.model.FailOrErrorState;
import org.dpytel.intellij.plugin.maventest.model.NamedTestInfo;
import org.dpytel.intellij.plugin.maventest.model.SkippedState;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.util.List;

public class ReportParser {
    public ReportParser() {
    }

    public TestProxy parseTestSuite(VirtualFile child) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(child.getInputStream());
            Element rootElement = document.getRootElement();
            Attribute name = rootElement.getAttribute("name");
            TestProxy testSuite = new TestProxy(new NamedTestInfo(name.getValue()));
            List<Element> testcases = rootElement.getChildren("testcase");
            SuiteState suiteState = new SuiteState(testSuite);
            testSuite.setState(suiteState);

            for (Element testcase : testcases) {
                TestProxy childTestProxy = parseTestCase(testcase);
                testSuite.addChild(childTestProxy);
                suiteState.updateMagnitude(childTestProxy.getMagnitude());
            }
            return testSuite;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    TestProxy parseTestCase(Element testcase) {
        Attribute nameAttr = testcase.getAttribute("name");
        TestProxy testCase = new TestProxy(new NamedTestInfo(nameAttr.getValue()));
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