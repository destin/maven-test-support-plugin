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

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.MapDataContext;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShowTestResultsActionTest {

    private ShowTestResultsAction action = new ShowTestResultsAction();
    private MapDataContext dataContext = new MapDataContext();
    private Presentation presentation = new Presentation();
    private ActionManager actionManager = mock(ActionManager.class);
    private AnActionEvent actionEvent;
    private Project project;
    private MavenProjectsManager mavenProjectManager;
    private VirtualFile selectedFile;
    private MavenProject mavenProject;

    @Before
    public void setUp() throws Exception {
        actionEvent = new AnActionEvent(null, dataContext, "Some place", presentation, actionManager, 0);

        project = mock(Project.class);
        mavenProjectManager = mock(MavenProjectsManager.class);
        when(project.getComponent(MavenProjectsManager.class)).thenReturn(mavenProjectManager);
        dataContext.put(CommonDataKeys.PROJECT, project);
        selectedFile = mock(VirtualFile.class);
        dataContext.put(CommonDataKeys.VIRTUAL_FILE, selectedFile);
        mavenProject = mock(MavenProject.class);
        when(mavenProjectManager.findProject(selectedFile)).thenReturn(mavenProject);
    }

    @Test
    public void unknownEvent() throws Exception {
        action.update(actionEvent);

        assertFalse(presentation.isVisible());
        assertFalse(presentation.isEnabled());
    }

    @Test
    public void pomFileSelectedInProjectWindow() throws Exception {
        when(mavenProject.getFile()).thenReturn(selectedFile);

        action.update(actionEvent);

        assertTrue(presentation.isVisible());
        assertTrue(presentation.isEnabled());
    }

    @Test
    public void moduleSelectedInProjectWindow() throws Exception {
        when(mavenProject.getDirectoryFile()).thenReturn(selectedFile);

        action.update(actionEvent);

        assertTrue(presentation.isVisible());
        assertTrue(presentation.isEnabled());
    }
}