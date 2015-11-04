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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.ModuleTestCase;
import org.jetbrains.idea.maven.project.MavenProject;

import java.io.File;

/**
 *
 */
public abstract class ITBase extends ModuleTestCase {
    protected MavenProject loadMavenModule(String moduleName) {
        String modulePath = "./out/test/maven-test-support-plugin/test_projects/" + moduleName + "/" + moduleName + ".iml";
        Module module = loadModule(
            new File(modulePath).getAbsolutePath());
        final VirtualFile moduleFile = module.getModuleFile();
        if (moduleFile == null) {
            throw new IllegalArgumentException("No module file for module" + moduleName);
        }
        VirtualFile pomFile = moduleFile.getParent().findChild("pom.xml");
        if (!pomFile.exists()) {
            throw new IllegalStateException("File " + pomFile.getCanonicalPath() + " does not exist");
        }
        return new MavenProject(pomFile);
    }
}
