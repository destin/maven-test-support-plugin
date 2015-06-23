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

import com.intellij.execution.junit2.states.TestState;
import com.intellij.execution.testframework.CompositePrintable;
import com.intellij.execution.testframework.Printer;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.rt.execution.junit.states.PoolOfTestStates;

/**
 *
 */
public class FailOrErrorState extends TestState {

    private final String stacktrace;
    private int typeIndex;

    private String systemout;

    private String systemerr;

    public static FailOrErrorState createFailedState(String stacktrace) {
        return new FailOrErrorState(stacktrace, PoolOfTestStates.FAILED_INDEX);
    }

    public static FailOrErrorState createErrorState(String stacktrace) {
        return new FailOrErrorState(stacktrace, PoolOfTestStates.ERROR_INDEX);
    }

    private FailOrErrorState(String stacktrace, int typeIndex) {
        this.stacktrace = stacktrace;
        this.typeIndex = typeIndex;
    }

    @Override
    public int getMagnitude() {
        return typeIndex;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public void printOn(Printer printer) {
        printer.print(CompositePrintable.NEW_LINE, ConsoleViewContentType.ERROR_OUTPUT);
        printer.mark();
        if (systemout != null) {
            printer.print(systemout, ConsoleViewContentType.NORMAL_OUTPUT);
        }
        if (systemerr != null) {
            printer.print(systemerr, ConsoleViewContentType.ERROR_OUTPUT);
        }
        printer.print(stacktrace, ConsoleViewContentType.ERROR_OUTPUT);
    }

    public String getSystemout() {
        return systemout;
    }

    public void setSystemout(String systemout) {
        this.systemout = systemout;
    }

    public String getSystemerr() {
        return systemerr;
    }

    public void setSystemerr(String systemerr) {
        this.systemerr = systemerr;
    }
}
