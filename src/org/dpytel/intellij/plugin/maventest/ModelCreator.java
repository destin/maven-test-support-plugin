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
import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.openapi.vfs.VirtualFile;
import org.dpytel.intellij.plugin.maventest.model.RootTestBuilder;
import org.jetbrains.idea.maven.project.MavenProject;

/**
 *
 */
public class ModelCreator {

    private final ReportParser reportParser = new ReportParser();
    private final MavenProject mavenProject;
    private final JUnitConsoleProperties consoleProperties;

    public ModelCreator(MavenProject mavenProject,
                        JUnitConsoleProperties consoleProperties) {
        this.mavenProject = mavenProject;
        this.consoleProperties = consoleProperties;
    }

    public JUnitRunningModel createModel() {
        TestProxy root = RootTestBuilder.fromMavenProject(this.mavenProject).build();
        addChildResults(mavenProject.getDirectoryFile(), root);
        return new JUnitRunningModel(root, consoleProperties);
    }

    private void addChildResults(VirtualFile baseDir, TestProxy root) {
        if (baseDir.exists()) {
            baseDir.refresh(false, false);
            VirtualFile target = baseDir.findChild("target");
            if (target != null && target.exists()) {
                target.refresh(false, false);
                processReportsDir(target, root, "surefire-reports");
                processReportsDir(target, root, "failsafe-reports");
            }
        }
    }

    private void processReportsDir(VirtualFile baseDir, TestProxy root, String reportDir) {
        VirtualFile reportsDir = baseDir.findChild(reportDir);
        if (reportsDir != null && reportsDir.exists()) {
            addReports(root, reportsDir);
        }
    }

    private void addReports(TestProxy root, VirtualFile reportsDir) {
        VirtualFile[] children = reportsDir.getChildren();
        for (VirtualFile child : children) {
            if (child.getName().matches("TEST-.*\\.xml")) {
                TestProxy childTestProxy = reportParser.parseTestSuite(child);
                root.addChild(childTestProxy);
            }
        }

    }
}
