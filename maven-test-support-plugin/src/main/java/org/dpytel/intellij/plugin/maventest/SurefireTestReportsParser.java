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

import com.intellij.execution.testframework.sm.runner.GeneralTestEventsProcessor;
import com.intellij.execution.testframework.sm.runner.events.TestSuiteFinishedEvent;
import com.intellij.execution.testframework.sm.runner.events.TestSuiteStartedEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.JDOMException;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.IOException;

/**
 *
 */
public class SurefireTestReportsParser {

    public static final String RESULTS_FILE_PATTERN = "TEST-.*\\.xml";
    public static final String SUREFIRE_REPORTS_DIR = "surefire-reports";
    public static final String FAILSAFE_REPORTS_DIR = "failsafe-reports";
    private final MavenProject mavenProject;

    private final static Logger LOGGER = Logger.getInstance(SurefireTestReportsParser.class);
    private final MavenProjectsManager mavenProjectsManager;
    private final GeneralTestEventsProcessor testEventsProcessor;

    public SurefireTestReportsParser(MavenProject mavenProject, Project project, GeneralTestEventsProcessor testEventsProcessor) {
        this.mavenProject = mavenProject;
        mavenProjectsManager = MavenProjectsManager.getInstance(project);
        this.testEventsProcessor = testEventsProcessor;
    }

    public void parseReports() {
        MavenId mavenId = mavenProject.getMavenId();
        testEventsProcessor.onRootPresentationAdded(mavenId.getArtifactId(), mavenId.getDisplayString(), null);
        parseReportFiles(mavenProject.getDirectoryFile());
    }

    private boolean isTestResultFile(VirtualFile file) {
        String path = file.getCanonicalPath();
        return path != null && (path.contains(SUREFIRE_REPORTS_DIR) || path.contains(FAILSAFE_REPORTS_DIR))
                && "xml".equalsIgnoreCase(file.getExtension());
    }

    private void parseReportFiles(VirtualFile baseDir) {
        if (baseDir.exists()) {
            baseDir.refresh(false, false);
            VirtualFile target = baseDir.findChild("target");
            if (target != null && target.exists()) {
                target.refresh(false, false);
                processReportsDir(target, SUREFIRE_REPORTS_DIR);
                processReportsDir(target, FAILSAFE_REPORTS_DIR);
            }
            processSubProjects(baseDir);
        }
    }

    private void processReportsDir(VirtualFile baseDir, String reportDir) {
        VirtualFile reportsDir = baseDir.findChild(reportDir);
        if (reportsDir != null && reportsDir.exists()) {
            addReports(reportsDir);
        }
    }

    private void addReports(VirtualFile reportsDir) {
        VirtualFile[] children = reportsDir.getChildren();
        for (VirtualFile child : children) {
            if (isValidResultsFile(child)) {
                parseAndAddToRoot(child);
            }
        }
    }

    private boolean isValidResultsFile(VirtualFile child) {
        return child.getName().matches(RESULTS_FILE_PATTERN) && child.exists() && child.getCanonicalPath() != null;
    }

    private void parseAndAddToRoot(VirtualFile child) {
        try {
            ReportParser reportParser = new ReportParser(testEventsProcessor);
            reportParser.parseTestSuite(child);
        } catch (IOException e) {
            LOGGER.error("Cannot open file: " + child.getCanonicalPath(), e);
        } catch (JDOMException e) {
            LOGGER.error("Cannot parse file: " + child.getCanonicalPath(), e);
        }
    }

    private void processSubProjects(VirtualFile baseDir) {
        for (VirtualFile child : baseDir.getChildren()) {
            final MavenProject childMavenProject = getMavenProject(child);
            if (childMavenProject != null) {
                String suiteName = childMavenProject.getMavenId().getArtifactId();
                testEventsProcessor.onSuiteStarted(new TestSuiteStartedEvent(suiteName, null));
                parseReportFiles(childMavenProject.getDirectoryFile());
                testEventsProcessor.onSuiteFinished(new TestSuiteFinishedEvent(suiteName));
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
}
