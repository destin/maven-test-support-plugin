package org.dpytel.intellij.plugin.maventest.view;

import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.execution.junit2.ui.model.TreeCollapser;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.testframework.ui.TestResultsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 *
 */
public class MavenTreeConsoleView extends BaseTestsOutputConsoleView {
    private MavenTestResultsPanel myConsolePanel;
    private final JUnitConsoleProperties myProperties;
    private final ExecutionEnvironment myEnvironment;

    public MavenTreeConsoleView(final JUnitConsoleProperties properties,
                                final ExecutionEnvironment environment,
                                final AbstractTestProxy unboundOutputRoot) {
        super(properties, unboundOutputRoot);
        myProperties = properties;
        myEnvironment = environment;
    }

    protected TestResultsPanel createTestResultsPanel() {
        myConsolePanel = new MavenTestResultsPanel(getConsole().getComponent(), getPrinter(), myProperties, myEnvironment,
            getConsole().createConsoleActions());
        return myConsolePanel;
    }

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
            myConsolePanel.getTreeView().attachToModel(model);
            model.attachToTree(myConsolePanel.getTreeView());
            myConsolePanel.setModel(model);
            model.onUIBuilt();
            new TreeCollapser().setModel(model);
        }
    }
}
