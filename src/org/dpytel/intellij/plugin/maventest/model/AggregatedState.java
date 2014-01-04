package org.dpytel.intellij.plugin.maventest.model;

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.TestProxyListener;
import com.intellij.execution.junit2.states.TestState;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.Printer;
import com.intellij.rt.execution.junit.states.PoolOfTestStates;

/**
 *
 */
public class AggregatedState extends TestState {

    private final TestProxy myTest;

    private int myMagnitude = PoolOfTestStates.NOT_RUN_INDEX;

    public AggregatedState(TestProxy test) {
        this.myTest = test;
        this.myTest.addListener(new TestProxyListener() {
            @Override
            public void onChildAdded(AbstractTestProxy parent, AbstractTestProxy newChild) {
                if (newChild.getParent() == myTest) {
                    updateMagnitude(newChild.getMagnitude());
                }
            }

            @Override
            public void onChanged(AbstractTestProxy proxy) {
                // do nothing
            }

            @Override
            public void onStatisticsChanged(AbstractTestProxy testProxy) {
                // do nothing
            }
        });
    }

    @Override
    public int getMagnitude() {
        return myMagnitude;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public void printOn(Printer printer) {
        // do nothing
    }

    @Override
    public void changeStateAfterAddingChildTo(TestProxy test, TestProxy child) {
        // do nothing
    }

    private void updateMagnitude(int magnitude) {
        if (myMagnitude == PoolOfTestStates.NOT_RUN_INDEX) {
            myMagnitude = magnitude;
        } else if (myMagnitude < magnitude) {
            myMagnitude = magnitude;
        }
    }
}
