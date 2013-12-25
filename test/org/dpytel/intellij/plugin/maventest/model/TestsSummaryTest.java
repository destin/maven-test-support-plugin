package org.dpytel.intellij.plugin.maventest.model;

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.states.SuiteState;
import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.execution.junit2.ui.model.RootTestInfo;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.testFramework.PlatformUltraLiteTestFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class TestsSummaryTest {

    @Before
    public void setUp() throws Exception {
        PlatformUltraLiteTestFixture.getFixture().setUp();
    }

    @After
    public void tearDown() throws Exception {
        PlatformUltraLiteTestFixture.getFixture().tearDown();
    }

    @Ignore("No mocking library")
    @Test
    public void testNoTests() throws Exception {
        RootTestInfo rootInfo = new RootTestInfo();
        rootInfo.setName("Root");
        TestProxy root = new TestProxy(rootInfo);
        SuiteState suiteState = new SuiteState(root);
        root.setState(suiteState);
        JUnitConsoleProperties properties = null; // TODO need to mock it but no mocking lib is available
        JUnitRunningModel model = new JUnitRunningModel(root, properties);

        TestsSummary summary = TestsSummary.createSummary(model);

        assertThat(summary.getTotal(), is(0));
        assertThat(summary.getErrors(), is(0));
        assertThat(summary.getFailed(), is(0));
        assertThat(summary.getSkipped(), is(0));
    }
}