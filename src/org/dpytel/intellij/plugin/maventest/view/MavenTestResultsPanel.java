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

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.ui.JUnitTestTreeView;
import com.intellij.execution.junit2.ui.actions.JUnitToolbarPanel;
import com.intellij.execution.junit2.ui.model.JUnitAdapter;
import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestTreeView;
import com.intellij.execution.testframework.ToolbarPanel;
import com.intellij.execution.testframework.ui.TestResultsPanel;
import com.intellij.execution.testframework.ui.TestStatusLine;
import com.intellij.execution.testframework.ui.TestsOutputConsolePrinter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.progress.util.ColorProgressBar;
import org.dpytel.intellij.plugin.maventest.model.TestsSummary;
import org.dpytel.intellij.plugin.maventest.text.TextBundle;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;

/**
 *
 */
public class MavenTestResultsPanel extends TestResultsPanel {
    @NonNls
    private static final String PROPORTION_PROPERTY = "test_tree_console_proprtion";
    private static final float DEFAULT_PROPORTION = 0.2f;

    //private StatisticsPanel myStatisticsPanel;
    private TestTreeView myTreeView;
    private TestsOutputConsolePrinter myPrinter;

    public MavenTestResultsPanel(final JComponent console,
                                 final TestsOutputConsolePrinter printer,
                                 final JUnitConsoleProperties properties,
                                 final ExecutionEnvironment environment,
                                 AnAction[] consoleActions) {
        super(console, consoleActions, properties, environment, PROPORTION_PROPERTY, DEFAULT_PROPORTION);
        myPrinter = printer;
    }

    public void initUI() {
        super.initUI();
    }

    protected JComponent createStatisticsPanel() {
        //myStatisticsPanel = new StatisticsPanel();
        //myStatisticsPanel = new StatisticsPanel();
        //return myStatisticsPanel;
        return new JLabel("Statistics");
    }

    protected ToolbarPanel createToolbarPanel() {
        return new JUnitToolbarPanel(myProperties, myEnvironment, this);
    }

    protected TestStatusLine createStatusLine() {
        return new TestStatusLine();
    }

    protected JComponent createTestTreeView() {
        myTreeView = new JUnitTestTreeView();
        return myTreeView;
    }

    public void setModel(final JUnitRunningModel model) {
        final TestTreeView treeView = model.getTreeView();
        treeView.setLargeModel(true);
        setLeftComponent(treeView);
        myToolbarPanel.setModel(model);
        updateStatusLine(model);
        model.addListener(new JUnitAdapter() {
            @Override
            public void onTestSelected(final TestProxy test) {
                if (myPrinter != null) {
                    myPrinter.updateOnTestSelected(test);
                }
            }
        });
        //myStatisticsPanel.attachTo(model);
    }

    private void updateStatusLine(JUnitRunningModel model) {
        myStatusLine.setFraction(1);
        TestProxy modelRoot = model.getRoot();
        myStatusLine.setStatusColor(modelRoot.isPassed() ? ColorProgressBar.GREEN : ColorProgressBar.RED);
        TestsSummary summary = TestsSummary.createSummary(model);
        myStatusLine.setText(TextBundle
            .getText("maventestsupport.statusline.summary", summary.getTotal(), summary.getFailed(),
                summary.getErrors(), summary.getSkipped()));
    }

    public TestTreeView getTreeView() {
        return myTreeView;
    }

    public void dispose() {
        myPrinter = null;
    }

}
