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
