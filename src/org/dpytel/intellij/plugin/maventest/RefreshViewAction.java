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

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.info.TestInfo;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.execution.testframework.TestFrameworkRunningModel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.dpytel.intellij.plugin.maventest.model.MavenRootTestInfo;
import org.dpytel.intellij.plugin.maventest.text.TextBundle;
import org.dpytel.intellij.plugin.maventest.toolwindow.MavenToolWindow;
import org.dpytel.intellij.plugin.maventest.view.MavenTreeConsoleView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

/**
 *
 */
public class RefreshViewAction extends DumbAwareAction {

    private final MavenTreeConsoleView consoleView;
    private TestFrameworkRunningModel model;

    public RefreshViewAction(MavenTreeConsoleView consoleView) {
        super(TextBundle
            .getText("maventestsupport.toolbar.actions.refresh.name"), TextBundle
            .getText("maventestsupport.toolbar.actions.refresh.description"), AllIcons.Actions.Refresh);
        this.consoleView = consoleView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TestProxy root = (TestProxy) model.getRoot();
        TestInfo info = root.getInfo();
        MavenRootTestInfo rootTestInfo = (MavenRootTestInfo) info;
        MavenProject mavenProject = rootTestInfo.getMavenProject();
        JUnitConsoleProperties jUnitConsoleProperties = (JUnitConsoleProperties) model.getProperties();
        Project project = jUnitConsoleProperties.getProject();
        MavenToolWindow window = new MavenToolWindow(project);
        window.refreshTab(consoleView, mavenProject, jUnitConsoleProperties);
    }

    public void setModel(TestFrameworkRunningModel model) {
        this.model = model;
    }
}
