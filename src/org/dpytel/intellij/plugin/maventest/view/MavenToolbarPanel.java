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

import com.intellij.execution.junit2.ui.actions.JUnitToolbarPanel;
import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.TestFrameworkRunningModel;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.dpytel.intellij.plugin.maventest.actions.AutoRefreshAction;
import org.dpytel.intellij.plugin.maventest.actions.RefreshViewAction;
import org.dpytel.intellij.plugin.maventest.model.MavenTestsModel;

import javax.swing.*;

/**
 *
 */
public class MavenToolbarPanel extends JUnitToolbarPanel {

    private RefreshViewAction myRefreshViewAction;
    private AutoRefreshAction autoRefreshAction;

    public MavenToolbarPanel(final TestConsoleProperties properties,
                             ExecutionEnvironment environment, JComponent parent) {
        super(properties, environment, parent);
    }

    @Override
    protected void appendAdditionalActions(DefaultActionGroup actionGroup, TestConsoleProperties properties,
                                           ExecutionEnvironment environment, JComponent parent) {
        super.appendAdditionalActions(actionGroup, properties, environment, parent);
        MavenTreeConsoleView consoleView = ((MavenTestResultsPanel) parent).getConsoleView();
        myRefreshViewAction = new RefreshViewAction(consoleView);
        actionGroup.addAction(myRefreshViewAction);
        autoRefreshAction = new AutoRefreshAction(consoleView);
        actionGroup.addAction(autoRefreshAction);
    }

    @Override
    public void setModel(TestFrameworkRunningModel model) {
        super.setModel(model);
        MavenTestsModel mavenTestsModel = new MavenTestsModel((JUnitRunningModel) model);
        myRefreshViewAction.setModel(mavenTestsModel);
        autoRefreshAction.setModel(mavenTestsModel);
    }
}
