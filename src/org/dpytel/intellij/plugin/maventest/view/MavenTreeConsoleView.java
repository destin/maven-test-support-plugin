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

package org.dpytel.intellij.plugin.maventest.view;

import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.execution.junit2.ui.model.TreeCollapser;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.testframework.ui.TestResultsPanel;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Represents view (i.e. single tab in Tool Window) that displays test results, console output, all the buttons.
 */
public class MavenTreeConsoleView extends BaseTestsOutputConsoleView {
    private MavenTestResultsPanel myConsolePanel;
    private final JUnitConsoleProperties myProperties;
    private final ExecutionEnvironment myEnvironment;
    private JUnitRunningModel myModel;

    public MavenTreeConsoleView(final JUnitConsoleProperties properties,
                                final ExecutionEnvironment environment,
                                final AbstractTestProxy unboundOutputRoot) {
        super(properties, unboundOutputRoot);
        myProperties = properties;
        myEnvironment = environment;
    }

    protected TestResultsPanel createTestResultsPanel() {
        myConsolePanel = new MavenTestResultsPanel(getConsole().getComponent(), getPrinter(), myProperties, myEnvironment,
            getConsole().createConsoleActions(), this);
        return myConsolePanel;
    }

    @Override
    public void dispose() {
        super.dispose();
        myConsolePanel = null;
    }

    @Override
    public JComponent getPreferredFocusableComponent() {
        return myConsolePanel.getTreeView();
    }

    public void attachToModel(@NotNull JUnitRunningModel model) {
        if (myConsolePanel != null) {
            setMyModel(model);
            myConsolePanel.getTreeView().attachToModel(model);
            model.attachToTree(myConsolePanel.getTreeView());
            myConsolePanel.setModel(model);
            model.onUIBuilt();
            new TreeCollapser().setModel(model);
        }
    }

    private void setMyModel(JUnitRunningModel model) {
        if (myModel != null) {
            Disposer.dispose(myModel);
        }
        myModel = model;
        Disposer.register(this, model);
    }
}
