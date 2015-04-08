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

package org.dpytel.intellij.plugin.maventest.toolwindow;

import com.intellij.execution.junit2.ui.model.CompletionEvent;
import com.intellij.execution.junit2.ui.model.JUnitListenersNotifier;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.dpytel.intellij.plugin.maventest.JUnitApiUtils;
import org.dpytel.intellij.plugin.maventest.model.MavenTestsModel;
import org.dpytel.intellij.plugin.maventest.view.MavenTreeConsoleView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

/**
 *
 */
public class MavenToolWindow {

    public static final String TOOL_WINDOW_ID = "Maven test results";
    private static final String TOOL_WINDOW_ICON = "/icons/showResultsToolWindow.png";

    private final MavenTestsModel model;

    public MavenToolWindow(MavenTestsModel model) {
        this.model = model;
    }

    public void showMavenToolWindow(MavenProject mavenProject) {
        final MavenTreeConsoleView consoleView = createMavenTreeConsoleView();
        if (consoleView == null) {
            return;
        }
        showInToolWindow(consoleView, mavenProject.getFinalName());
    }

    public void refreshTab(@NotNull MavenTreeConsoleView consoleView) {
        model.refreshModel();
        attachModelToView(consoleView);
        refreshCurrentTabWith(consoleView);
    }

    private MavenTreeConsoleView createMavenTreeConsoleView() {
        final JUnitConsoleProperties consoleProperties = JUnitApiUtils.createConsoleProperties(model.getProject());
        if (consoleProperties == null) {
            return null;
        }
        ExecutionEnvironment environment = new ExecutionEnvironment();
        final MavenTreeConsoleView consoleView = new MavenTreeConsoleView(consoleProperties, environment, null, model);
        model.refreshModel();
        attachModelToView(consoleView);
        return consoleView;
    }

    private void attachModelToView(MavenTreeConsoleView consoleView) {
        consoleView.initUI();
        JUnitListenersNotifier notifier = model.getJUnitRunningModel().getNotifier();
        if (notifier != null) {
            notifier.fireRunnerStateChanged(new CompletionEvent(true, 10));
        }
    }

    private void refreshCurrentTabWith(@NotNull MavenTreeConsoleView consoleView) {
        ToolWindow toolWindow = getToolWindow();
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getSelectedContent();
        content.setComponent(consoleView.getComponent());
        contentManager.setSelectedContent(content);
    }

    private void showInToolWindow(ComponentContainer consoleView, String tabName) {
        ToolWindow toolWindow = getToolWindow();
        toolWindow.activate(null);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory()
                .createContent(consoleView.getComponent(), tabName, false);
        Disposer.register(content, consoleView);
        contentManager.addContent(content);
        contentManager.setSelectedContent(content);
    }

    private ToolWindow getToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(model.getProject());
        ToolWindow toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
        if (toolWindow == null) {
            toolWindow = createToolWindow(toolWindowManager);
        }
        return toolWindow;
    }

    private ToolWindow createToolWindow(ToolWindowManager toolWindowManager) {
        ToolWindow toolWindow = toolWindowManager
                .registerToolWindow(TOOL_WINDOW_ID, true, ToolWindowAnchor.BOTTOM);
        toolWindow.setIcon(IconLoader.findIcon(TOOL_WINDOW_ICON));
        return toolWindow;
    }

}
