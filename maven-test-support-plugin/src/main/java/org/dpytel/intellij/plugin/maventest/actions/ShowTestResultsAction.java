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

package org.dpytel.intellij.plugin.maventest.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.dpytel.intellij.plugin.maventest.toolwindow.MavenToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

/**
 *
 */
public class ShowTestResultsAction extends AnAction {

    public ShowTestResultsAction() {
        super("ShowTestResultsAction");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        MavenProject mavenProject = MavenActionUtil.getMavenProject(event.getDataContext());

        MavenToolWindow window = new MavenToolWindow(project, mavenProject);
        window.showMavenToolWindow();
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
