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

import com.intellij.execution.Executor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.dpytel.intellij.plugin.maventest.actions.AutoRefreshAction;
import org.dpytel.intellij.plugin.maventest.actions.RefreshViewAction;
import org.dpytel.intellij.plugin.maventest.model.MavenTestsModel;

import javax.swing.*;

/**
 *
 */
public class MavenTestResultsConsoleProperties extends JUnitConsoleProperties {

    private RefreshViewAction myRefreshViewAction;
    private AutoRefreshAction autoRefreshAction;
    private MavenTestsModel myModel;

    public MavenTestResultsConsoleProperties(MavenTestsModel model, JUnitConfiguration configuration, Executor executor) {
        super(configuration, executor);
        myModel = model;
    }

    /**
     * Overrides base method in IntelliJ 15 EAP or just plain method invoked in toolbar panel
     */
    @Override
    public void appendAdditionalActions(DefaultActionGroup actionGroup, JComponent parent, TestConsoleProperties target) {
        MavenTreeConsoleView consoleView = ((MavenTestResultsPanel) parent).getConsoleView();
        myRefreshViewAction = new RefreshViewAction(consoleView, myModel);
        actionGroup.addAction(myRefreshViewAction);
        autoRefreshAction = new AutoRefreshAction(consoleView, myModel);
        actionGroup.addAction(autoRefreshAction);
    }
}
