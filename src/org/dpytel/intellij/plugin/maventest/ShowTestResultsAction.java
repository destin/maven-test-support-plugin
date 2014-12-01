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

package org.dpytel.intellij.plugin.maventest;

import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.dpytel.intellij.plugin.maventest.view.MavenTreeConsoleView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

/**
 *
 */
public class ShowTestResultsAction extends AnAction {

    public static final String TOOL_WINDOW_ID = "Maven test results";
    private static final String TOOL_WINDOW_ICON = "/icons/showResultsToolWindow.png";

    public ShowTestResultsAction() {
        super("ShowTestResultsAction");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        MavenProject mavenProject = MavenActionUtil.getMavenProject(event.getDataContext());
        final JUnitConsoleProperties consoleProperties = JUnitApiUtils.createConsoleProperties(project);
        if (consoleProperties == null) {
            return;
        }
        ExecutionEnvironment environment = new ExecutionEnvironment();
        final MavenTreeConsoleView consoleView = new MavenTreeConsoleView(consoleProperties, environment, null);
        consoleView.initUI();
        ModelCreator modelCreator = new ModelCreator(mavenProject, consoleProperties);
        JUnitRunningModel model = modelCreator.createModel();
        consoleView.attachToModel(model);

        showInToolWindow(project, mavenProject, consoleView);
    }

    private void showInToolWindow(Project project, MavenProject mavenProject, ComponentContainer consoleView) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
        if (toolWindow == null) {
            toolWindow = toolWindowManager
                .registerToolWindow(TOOL_WINDOW_ID, true, ToolWindowAnchor.BOTTOM);
            toolWindow.setIcon(IconLoader.findIcon(TOOL_WINDOW_ICON));
        }
        toolWindow.activate(null);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory()
            .createContent(consoleView.getComponent(), mavenProject.getFinalName(), false);
        Disposer.register(content, consoleView);
        contentManager.addContent(content);
        contentManager.setSelectedContent(content);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Presentation p = e.getPresentation();
        p.setEnabled(isAvailable(e));
        p.setVisible(isVisible(e));
    }

    private boolean isAvailable(AnActionEvent e) {
        return isMavenModuleSelected(e);
    }

    private boolean isVisible(AnActionEvent e) {
        return isMavenModuleSelected(e);
    }

    private boolean isMavenModuleSelected(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        if (!MavenActionUtil.hasProject(dataContext)) {
            return false;
        }
        MavenProject mavenProject = MavenActionUtil.getMavenProject(dataContext);
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        return mavenProject != null && selectedFile != null
            && (selectedFile.equals(mavenProject.getDirectoryFile()) || selectedFile.equals(mavenProject.getFile()));
    }
}
