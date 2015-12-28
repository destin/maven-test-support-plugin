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

package org.dpytel.intellij.plugin.maventest.actions;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.log4j.Logger;
import org.dpytel.intellij.plugin.maventest.model.MavenTestsModel;
import org.dpytel.intellij.plugin.maventest.model.TestResultChangedListener;
import org.dpytel.intellij.plugin.maventest.toolwindow.MavenToolWindow;
import org.dpytel.intellij.plugin.maventest.view.MavenTreeConsoleView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class AutoRefreshTestResultChangedListener implements TestResultChangedListener {

    private static final Logger LOG = Logger.getLogger(AutoRefreshTestResultChangedListener.class);

    private MavenTestsModel model;
    private MavenTreeConsoleView consoleView;
    private final AtomicBoolean waitingForExecution = new AtomicBoolean(false);

    public AutoRefreshTestResultChangedListener(MavenTestsModel model, MavenTreeConsoleView consoleView) {
        this.model = model;
        this.consoleView = consoleView;
    }

    @Override
    public void testChanged() {
        final boolean wasWaitingForExecution = waitingForExecution.getAndSet(true);
        if (wasWaitingForExecution) {
            return; // it is going to be refreshed anyway
        }
        final Application application = ApplicationManager.getApplication();
        application.executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                waitAndQueueRefresh(application);
            }

        });
    }

    private void waitAndQueueRefresh(Application application) {
        waitForAWhile();
        LOG.debug("Queuing refresh of " + model.getProject().getName());
        application.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LOG.debug("Initiating refresh of " + model.getProject().getName());
                    MavenToolWindow window = new MavenToolWindow(model);
                    waitingForExecution.set(false);
                    window.refreshTab(consoleView);
                } finally {
                    waitingForExecution.set(false);
                }
            }
        });
    }

    private void waitForAWhile() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOG.warn("Waiting interrupted", e);
        }
    }

    @Override
    public VirtualFile getRoot() {
        return model.getMavenProject().getDirectoryFile();
    }
}
