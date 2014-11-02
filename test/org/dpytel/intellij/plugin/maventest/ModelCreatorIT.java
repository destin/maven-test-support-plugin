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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.rt.execution.junit.states.PoolOfTestStates;
import com.intellij.testFramework.ModuleTestCase;
import org.jetbrains.idea.maven.project.MavenProject;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class ModelCreatorIT extends ModuleTestCase {

    private JUnitConsoleProperties consoleProperties;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        consoleProperties = JUnitApiUtils.createConsoleProperties(getProject());
    }

    @Test
    public void testAllKindsOfResults() throws Exception {
        MavenProject mavenProject = loadMavenModule("submodule");
        ModelCreator modelCreator = new ModelCreator(mavenProject, consoleProperties);

        JUnitRunningModel model = modelCreator.createModel();

        TestProxy root = model.getRoot();
        assertIsError(root);
        List<TestProxy> firstLevel = root.getChildren();
        assertThat(firstLevel.size(), is(6));
        TestProxy testTest = findChildByName(root, "test.Test");
        assertIsError(testTest);
        TestProxy testFailure = findChildByName(testTest, ".testFailure");
        assertIsFailed(testFailure);
        TestProxy testError = findChildByName(testTest, ".testError");
        assertIsError(testError);
        TestProxy testSuccess = findChildByName(testTest, ".testSuccess");
        assertIsSuccess(testSuccess);
        TestProxy testIgnored = findChildByName(testTest, ".testIgnored");
        assertIsIgnored(testIgnored);
    }

    private void assertIsSuccess(TestProxy proxy) {
        assertThat(proxy.getMagnitude(), is(PoolOfTestStates.PASSED_INDEX));
    }

    private void assertIsIgnored(TestProxy proxy) {
        assertThat(proxy.getMagnitude(), is(PoolOfTestStates.IGNORED_INDEX));
    }

    private void assertIsError(TestProxy proxy) {
        assertThat(proxy.getMagnitude(), is(PoolOfTestStates.ERROR_INDEX));
    }

    private void assertIsFailed(TestProxy proxy) {
        assertThat(proxy.getMagnitude(), is(PoolOfTestStates.FAILED_INDEX));
    }

    private MavenProject loadMavenModule(String moduleName) {
        Module module = loadModule(
            new File("./out/test/maven-test-support-plugin/test_projects/" + moduleName + "/" + moduleName + ".iml"));
        VirtualFile pomFile = module.getModuleFile().getParent().findChild("pom.xml");
        if (!pomFile.exists()) {
            throw new IllegalStateException("File " + pomFile.getCanonicalPath() + " does not exist");
        }
        return new MavenProject(pomFile);
    }

    private TestProxy findChildByName(TestProxy parent, String proxyName) {
        List<TestProxy> children = parent.getChildren();
        for (TestProxy proxy : children) {
            String name = proxy.toString();
            if (name != null && name.equals(proxyName)) {
                return proxy;
            }
        }
        throw new IllegalStateException(
            String.format("No proxy named %s on list of children: %s", proxyName, children));
    }
}
