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
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.dpytel.intellij.plugin.maventest.model.MavenTestsModel;

import javax.swing.*;

/**
 *
 */
public class MavenToolbarPanel extends JUnitToolbarPanel {

    public MavenToolbarPanel(final MavenTestResultsConsoleProperties properties,
                             JComponent parent, MavenTestsModel model) {
        super(properties, parent);
    }

    /**
     * Method needed for pre-IntelliJ 15 EAP. Starting from IJ 15 properties.appendAdditionalActions is
     * invoked directly.
     */
    protected void appendAdditionalActions(DefaultActionGroup actionGroup, TestConsoleProperties properties,
                                           ExecutionEnvironment environment, JComponent parent) {
        MavenTestResultsConsoleProperties myProperties = (MavenTestResultsConsoleProperties) properties;
        myProperties.appendAdditionalActions(actionGroup, environment, parent);
    }
}
