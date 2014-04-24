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

import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.states.TestState;
import com.intellij.execution.testframework.CompositePrintable;
import com.intellij.execution.testframework.Printer;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.rt.execution.junit.states.PoolOfTestStates;

/**
 *
 */
public class SkippedState extends TestState {
    private TestProxy myPeformedTest;
    private String myIgnoredMessage;

    public SkippedState(TestProxy myPeformedTest, String myIgnoredMessage) {
        this.myPeformedTest = myPeformedTest;
        if (myIgnoredMessage == null) {
            myIgnoredMessage = "";
        }
        this.myIgnoredMessage = myIgnoredMessage;
    }

    @Override
    public int getMagnitude() {
        return PoolOfTestStates.IGNORED_INDEX;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public void printOn(Printer printer) {
        String parentName = myPeformedTest.getParent() == null ? myPeformedTest.getInfo().getComment() : myPeformedTest
            .getParent().toString();
        String message = ExecutionBundle
            .message("junit.runing.info.ignored.console.message", parentName, myPeformedTest.getInfo().getName());
        printer.print(message + (myIgnoredMessage
            .length() > 0 ? " (" + myIgnoredMessage + ")" : "") + CompositePrintable.NEW_LINE,
            ConsoleViewContentType.ERROR_OUTPUT);
    }
}
