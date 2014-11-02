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

import com.intellij.mock.MockVirtualFile;
import org.jetbrains.idea.maven.project.MavenProject;

/**
 * Creates module for unit tests
 */
public class MockMavenModuleBuilder {

    private final MockVirtualFile moduleDir;

    private MockMavenModuleBuilder(String moduleName) {
        this.moduleDir = new MockVirtualFile(true, moduleName);
    }

    public static MockMavenModuleBuilder newModule() {
        return new MockMavenModuleBuilder("SomeModule");
    }

    public MockVirtualFile addDir(String dirName) {
        MockVirtualFile dir = new MockVirtualFile(true, dirName);
        moduleDir.addChild(dir);
        return dir;
    }

    public MavenProject build() {
        MockVirtualFile pomFile = new MockVirtualFile("pom.xml");
        pomFile.setParent(moduleDir);
        MavenProject mavenProject = new MavenProject(pomFile);
        return mavenProject;
    }

}
