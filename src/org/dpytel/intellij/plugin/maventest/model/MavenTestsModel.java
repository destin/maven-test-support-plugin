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

package org.dpytel.intellij.plugin.maventest.model;

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.info.TestInfo;
import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.dpytel.intellij.plugin.maventest.JUnitApiUtils;
import org.dpytel.intellij.plugin.maventest.ModelCreator;
import org.dpytel.intellij.plugin.maventest.actions.AutoRefreshTestResultChangedListener;
import org.jetbrains.idea.maven.project.MavenProject;

/**
 *
 */
public class MavenTestsModel implements Disposable {

    private final Project project;
    private final MavenProject mavenProject;
    private final JUnitConsoleProperties junitConsoleProperties;
    private JUnitRunningModel jUnitRunningModel;
    private ModelCreator modelCreator;
    private boolean autorefreshEnabled;

    public MavenTestsModel(Project project, MavenProject mavenProject) {
        this.project = project;
        this.mavenProject = mavenProject;
        this.junitConsoleProperties = JUnitApiUtils.createConsoleProperties(project);
    }

    private static Project getProjectFrom(JUnitRunningModel jUnitRunningModel) {
        JUnitConsoleProperties jUnitConsoleProperties = jUnitRunningModel.getProperties();
        return jUnitConsoleProperties.getProject();
    }

    private static MavenProject getMavenProjectFrom(JUnitRunningModel jUnitRunningModel) {
        TestProxy root = jUnitRunningModel.getRoot();
        TestInfo info = root.getInfo();
        MavenRootTestInfo rootTestInfo = (MavenRootTestInfo) info;
        return rootTestInfo.getMavenProject();
    }

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public Project getProject() {
        return project;
    }

    public JUnitRunningModel getJUnitRunningModel() {
        return jUnitRunningModel;
    }

    public void refreshModel() {
        modelCreator = new ModelCreator(getMavenProject(), junitConsoleProperties);
        jUnitRunningModel = modelCreator.createModel();
    }

    public void addTestResultChangedListener(TestResultChangedListener listener) {
        modelCreator.addListener(listener);
    }

    public void removeTestResultChangedListener(AutoRefreshTestResultChangedListener listener) {
        modelCreator.removeListener(listener);
    }

    @Override
    public void dispose() {
        if (jUnitRunningModel != null) {
            Disposer.dispose(jUnitRunningModel);
        }
    }

    public boolean isAutorefreshEnabled() {
        return autorefreshEnabled;
    }

    public void setAutorefreshEnabled(boolean autorefreshEnabled) {
        this.autorefreshEnabled = autorefreshEnabled;
    }
}
