package org.dpytel.intellij.plugin.maventest.model;

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.states.NotFailedState;
import com.intellij.execution.junit2.ui.model.RootTestInfo;
import com.intellij.rt.execution.junit.states.PoolOfTestStates;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class AggregatedStateTest {

    private AggregatedState state;
    private TestProxy rootTest;

    @Before
    public void setUp() throws Exception {
        rootTest = new TestProxy(new RootTestInfo());
        state = new AggregatedState(rootTest);
        rootTest.setState(state);
    }

    @Test
    public void noChildren() throws Exception {
        assertMagnitudeIs(PoolOfTestStates.NOT_RUN_INDEX);
    }

    @Test
    public void oneTestPassed() throws Exception {
        addPassedTest();

        assertMagnitudeIs(PoolOfTestStates.PASSED_INDEX);
    }

    @Test
    public void oneTestPassedAndOneFailed() throws Exception {
        addPassedTest();
        addFailedTest();

        assertMagnitudeIs(PoolOfTestStates.FAILED_INDEX);
    }

    @Test
    public void oneTestFailedAndOnePassed() throws Exception {
        addPassedTest();
        addFailedTest();

        assertMagnitudeIs(PoolOfTestStates.FAILED_INDEX);
    }

    private void assertMagnitudeIs(int magnitude) {
        assertThat(rootTest.getState().getMagnitude(), is(magnitude));
    }

    private void addFailedTest() {
        TestProxy passedChild = new TestProxy(new TestMethodInfo("org.dpytel.Test", "failed"));
        passedChild.setState(FailOrErrorState.createFailedState("stacktrace"));
        rootTest.addChild(passedChild);
    }

    private void addPassedTest() {
        TestProxy passedChild = new TestProxy(new TestMethodInfo("org.dpytel.Test", "passed"));
        passedChild.setState(NotFailedState.createPassed());
        rootTest.addChild(passedChild);
    }
}
