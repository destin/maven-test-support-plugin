package org.dpytel.intellij.plugin.maventest.model;

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.rt.execution.junit.states.PoolOfTestStates;

import java.util.ArrayList;

/**
 *
 */
public class TestsSummary {

    private final JUnitRunningModel myModel;
    private int failed;
    private int errors;
    private int skipped;
    private int total;

    public static TestsSummary createSummary(JUnitRunningModel model) {
        return new TestsSummary(model);
    }

    private TestsSummary(JUnitRunningModel model) {
        myModel = model;
        calculateStatistics();
    }

    private void calculateStatistics() {
        TestProxy modelRoot = myModel.getRoot();
        ArrayList<TestProxy> allTests = new ArrayList<TestProxy>();
        modelRoot.collectAllTestsTo(allTests);
        int[] states = new int[PoolOfTestStates.ERROR_INDEX + 1];
        total = 0;
        for (TestProxy test : allTests) {
            if (test.isLeaf() && test != modelRoot) {
                ++total;
                states[test.getState().getMagnitude()]++;
            }
        }
        failed = states[PoolOfTestStates.FAILED_INDEX];
        errors = states[PoolOfTestStates.ERROR_INDEX];
        skipped = states[PoolOfTestStates.IGNORED_INDEX];
    }

    public int getFailed() {
        return failed;
    }

    public int getErrors() {
        return errors;
    }

    public int getSkipped() {
        return skipped;
    }

    public int getTotal() {
        return total;
    }
}
