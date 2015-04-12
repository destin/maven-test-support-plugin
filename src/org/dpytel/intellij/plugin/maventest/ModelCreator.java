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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import org.dpytel.intellij.plugin.maventest.actions.AutoRefreshTestResultChangedListener;
import org.dpytel.intellij.plugin.maventest.model.RootTestBuilder;
import org.dpytel.intellij.plugin.maventest.model.TestResultChangedListener;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ModelCreator {

    public static final String RESULTS_FILE_PATTERN = "TEST-.*\\.xml";
    public static final String SUREFIRE_REPORTS_DIR = "surefire-reports";
    public static final String FAILSAFE_REPORTS_DIR = "failsafe-reports";
    private final ReportParser reportParser = new ReportParser();
    private final MavenProject mavenProject;
    private final JUnitConsoleProperties consoleProperties;

    private final static Logger LOGGER = Logger.getInstance(ModelCreator.class);
    private final MavenProjectsManager mavenProjectsManager;
    private TestResultsFileListener testResultsFileListener;
    private Set<TestResultChangedListener> listeners = new HashSet<TestResultChangedListener>();

    public ModelCreator(MavenProject mavenProject,
                        JUnitConsoleProperties consoleProperties, Project project) {
        this.mavenProject = mavenProject;
        this.consoleProperties = consoleProperties;
        mavenProjectsManager = MavenProjectsManager.getInstance(project);
    }

    public JUnitRunningModel createModel() {
        TestProxy root = createRootTestProxy(this.mavenProject);
        addChildResults(mavenProject.getDirectoryFile(), root);
        return new JUnitRunningModel(root, consoleProperties);
    }

    private TestProxy createRootTestProxy(MavenProject mavenProject) {
        return RootTestBuilder.fromMavenProject(mavenProject).build();
    }

    public void addListener(final TestResultChangedListener listener) {
        listeners.add(listener);
        if (testResultsFileListener == null) {
            testResultsFileListener = new TestResultsFileListener();
        }
        VirtualFileManager.getInstance().addVirtualFileListener(testResultsFileListener);
    }

    public void removeListener(AutoRefreshTestResultChangedListener listener) {
        listeners.remove(listener);
        if (listeners.isEmpty() && testResultsFileListener != null) {
            VirtualFileManager.getInstance().removeVirtualFileListener(testResultsFileListener);
        }
    }

    private boolean isTestResultFile(VirtualFile file) {
        String path = file.getCanonicalPath();
        return path != null && (path.contains(SUREFIRE_REPORTS_DIR) || path.contains(FAILSAFE_REPORTS_DIR))
                && "xml".equalsIgnoreCase(file.getExtension());
    }

    private void addChildResults(VirtualFile baseDir, TestProxy root) {
        if (baseDir.exists()) {
            baseDir.refresh(false, false);
            VirtualFile target = baseDir.findChild("target");
            if (target != null && target.exists()) {
                target.refresh(false, false);
                processReportsDir(target, root, SUREFIRE_REPORTS_DIR);
                processReportsDir(target, root, FAILSAFE_REPORTS_DIR);
            }
            processSubProjects(baseDir, root);
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
            if (isValidResultsFile(child)) {
                parseAndAddToRoot(root, child);
            }
        }
    }

    private boolean isValidResultsFile(VirtualFile child) {
        return child.getName().matches(RESULTS_FILE_PATTERN) && child.exists() && child.getCanonicalPath() != null;
    }

    private void parseAndAddToRoot(TestProxy root, VirtualFile child) {

        try {
            TestProxy childTestProxy = reportParser.parseTestSuite(child);
            root.addChild(childTestProxy);
        } catch (IOException e) {
            LOGGER.error("Cannot open file: " + child.getCanonicalPath(), e);
        } catch (JDOMException e) {
            LOGGER.error("Cannot parse file: " + child.getCanonicalPath(), e);
        }
    }

    private void processSubProjects(VirtualFile baseDir, TestProxy root) {
        for (VirtualFile child : baseDir.getChildren()) {
            final MavenProject childMavenProject = getMavenProject(child);
            if (childMavenProject != null) {
                final TestProxy childTestProxy = createRootTestProxy(childMavenProject);
                addChildResults(child, childTestProxy);
                root.addChild(childTestProxy);
            }
        }
    }

    private MavenProject getMavenProject(VirtualFile file) {
        if (file.isDirectory() && file.exists()) {
            final VirtualFile pom = file.findChild(MavenConstants.POM_XML);
            if (pom != null && pom.exists()) {
                return mavenProjectsManager.findProject(pom);
            }
        }
        return null;
    }

    private class TestResultsFileListener extends VirtualFileAdapter {

        @Override
        public void contentsChanged(@NotNull VirtualFileEvent event) {
            process(event);
        }

        @Override
        public void fileCreated(@NotNull VirtualFileEvent event) {
            process(event);
        }

        @Override
        public void fileDeleted(@NotNull VirtualFileEvent event) {
            process(event);
        }

        @Override
        public void fileMoved(@NotNull VirtualFileMoveEvent event) {
            process(event);
        }

        private void process(@NotNull VirtualFileEvent event) {
            final VirtualFile file = event.getFile();
            if (isTestResultFile(file)) {
                for (TestResultChangedListener listener : listeners) {
                    final VirtualFile root = listener.getRoot();
                    if (isAncestor(root, file)) {
                        listener.testChanged();
                    }
                }
            }
        }

        private boolean isAncestor(VirtualFile root, VirtualFile child) {
            while (child != null) {
                if (root.equals(child)) {
                    return true;
                }
                child = child.getParent();
            }
            return false;
        }
    }
}
