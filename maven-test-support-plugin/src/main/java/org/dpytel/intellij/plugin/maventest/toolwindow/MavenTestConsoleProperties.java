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

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.config.Storage;
import org.dpytel.intellij.plugin.maventest.SurefireTestReportsParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

/**
 *
 */
public class MavenTestConsoleProperties extends TestConsoleProperties implements SMCustomMessagesParsing {
    private final MavenProject mavenProject;
    private final RunProfile configuration;
    private final ProcessHandler processHandler;

    public MavenTestConsoleProperties(MavenProject mavenProject, Project project, Executor executor, RunProfile configuration, ProcessHandler processHandler) {
        super(new Storage.PropertiesComponentStorage("MavenTestSupport.", PropertiesComponent.getInstance()), project, executor);
        this.mavenProject = mavenProject;
        this.configuration = configuration;
        this.processHandler = processHandler;
    }

    @Override
    public RunProfile getConfiguration() {
        return configuration;
    }

    @Override
    public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        return new OutputToGeneralTestEventsConverter("Maven test results", this) {
            @Override
            public void onStartTesting() {
                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    SurefireTestReportsParser parser = new SurefireTestReportsParser(mavenProject, getProject(), getProcessor());
                    parser.parseReports();
                    processHandler.detachProcess();
                });
            }
        };
    }
}
