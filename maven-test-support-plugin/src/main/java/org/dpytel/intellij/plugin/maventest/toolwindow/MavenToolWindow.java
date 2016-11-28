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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.JUnitConfigurationType;
import com.intellij.execution.process.NopProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

/**
 *
 */
public class MavenToolWindow {

    private static final String TOOL_WINDOW_ID = "Maven test results";
    private static final String TOOL_WINDOW_ICON = "/icons/showResultsToolWindow.png";
    private final Project project;
    private final MavenProject mavenProject;


    public MavenToolWindow(Project project, MavenProject mavenProject) {
        this.project = project;
        this.mavenProject = mavenProject;
    }

    public void showMavenToolWindow() {
        ConfigurationType configurationType = new JUnitConfigurationType();
        ConfigurationFactory configurationFactory = new ConfigurationFactoryEx<JUnitConfiguration>(configurationType) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new JUnitConfiguration(project.getName(), project, this);
            }
        };
        JUnitConfiguration configuration = new JUnitConfiguration(project.getName(), project, configurationFactory);
        Executor executor = new DefaultRunExecutor();
        ProcessHandler processHandler = new NopProcessHandler();
        TestConsoleProperties consoleProperties = new MavenTestConsoleProperties(mavenProject, project, executor, configuration, processHandler);
        BaseTestsOutputConsoleView consoleView;
        try {
            consoleView = SMTestRunnerConnectionUtil.createAndAttachConsole(TOOL_WINDOW_ID, processHandler, consoleProperties);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        showInToolWindow(consoleView, mavenProject.getFinalName());
        processHandler.startNotify();
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
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
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
