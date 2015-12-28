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
import com.intellij.mock.MockVirtualFile;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.rt.execution.junit.states.PoolOfTestStates;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

public class ModelCreatorTest {

    private final JUnitConsoleProperties consoleProperties = mock(JUnitConsoleProperties.class);
    private MockMavenModuleBuilder builder;
    private JUnitRunningModel model;
    private Project project;

    @Before
    public void setUp() throws Exception {
        ApplicationManager.setApplication(mock(Application.class, RETURNS_DEEP_STUBS),
                mock(Disposable.class));
        builder = MockMavenModuleBuilder.newModule();
        project = mock(Project.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void noTargetDir() throws Exception {
        model = createModel();

        assertNoTestsRun(model);
    }

    @Test
    public void emptyTargetDir() throws Exception {
        builder.addDir("target");
        model = createModel();

        assertNoTestsRun(model);
    }

    @Test
    public void emptySurefireDir() throws Exception {
        MockVirtualFile target = builder.addDir("target");
        target.addChild(new MockVirtualFile(true, "surefire-reports"));
        model = createModel();

        assertNoTestsRun(model);
    }

    private JUnitRunningModel createModel() {
        ModelCreator modelCreator = new ModelCreator(builder.build(), consoleProperties, project);
        return modelCreator.createModel();
    }

    private void assertNoTestsRun(JUnitRunningModel model) {
        TestProxy root = model.getRoot();
        assertThat(root.getChildCount(), is(0));
        assertThat(root.getMagnitude(), is(PoolOfTestStates.SKIPPED_INDEX));
    }

}