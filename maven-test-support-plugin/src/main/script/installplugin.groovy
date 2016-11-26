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
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.configuration.PropertiesConfigurationLayout

def installDir = new File(properties.getProperty('intellij.install.dir') as String)

def intellijPropertiesFile = new File(installDir, "bin/idea.properties")

println "Installing plugin to: $intellijPropertiesFile"

PropertiesConfiguration config = new PropertiesConfiguration()
PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(config)
layout.load(new InputStreamReader(new FileInputStream(intellijPropertiesFile)))

config.setProperty("plugin.path", properties.getProperty('plugin.jar'))
layout.save(intellijPropertiesFile.newWriter())
