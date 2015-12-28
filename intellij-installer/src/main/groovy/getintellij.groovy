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


import org.apache.commons.io.FileUtils
import org.codehaus.plexus.archiver.UnArchiver

def intellijVersion = properties.getProperty('intellij.version')
def installDir = new File(properties.getProperty('intellij.install.dir'))
if (installDir.exists()) {
    println("IntelliJ $intellijVersion already exists. Skipping downloading")
    return
}
def intellijPackageFilePath = properties.getProperty('intellij.install.packageFile')
def intellijDownloadUrl = properties.getProperty('intellij.install.downloadUrl')

File intellijPackageFile
if (intellijPackageFilePath != null) {
    println("Using existing IntelliJ package: $intellijPackageFilePath")
    intellijPackageFile = new File(intellijPackageFilePath)
} else {
    intellijPackageFile = downloadIntelliJ(intellijVersion, intellijDownloadUrl)
}

println("Extracting IntelliJ")
def unarchiver = container.lookup(UnArchiver.ROLE, "tgz")
unarchiver.setSourceFile(intellijPackageFile);
installDir.mkdirs();
unarchiver.setDestDirectory(installDir);
unarchiver.extract();

File downloadIntelliJ(intellijVersion, intellijDownloadUrl) {
    println("Downloading IntelliJ version: $intellijVersion")

    URLConnection connection = new URL(intellijDownloadUrl).openConnection()
    def intellijPackageFile = File.createTempFile("$intellijVersion-", ".tar.gz")
    intellijPackageFile.with {
        append(connection.inputStream)
    }
    return intellijPackageFile
}

File[] files = installDir.listFiles()
if (files.length == 1 && files[0].directory) {
    File intellijDirInInstallDir = files[0]
    intellijDirInInstallDir.eachFile {
        FileUtils.moveToDirectory(it, installDir, false)
    }
    intellijDirInInstallDir.deleteDir()
}
