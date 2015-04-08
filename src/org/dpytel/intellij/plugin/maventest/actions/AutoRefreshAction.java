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

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.dpytel.intellij.plugin.maventest.model.MavenTestsModel;
import org.dpytel.intellij.plugin.maventest.view.MavenTreeConsoleView;

/**
 *
 */
public class AutoRefreshAction extends ToggleAction {

    private final MavenTreeConsoleView consoleView;
    private MavenTestsModel model;
    private AutoRefreshTestResultChangedListener testResultChangedListener;

    public AutoRefreshAction(MavenTreeConsoleView consoleView) {
        super("Auto refresh", "Automatically refreshes results when changed", AllIcons.Actions.Refresh);
        this.consoleView = consoleView;
    }

    @Override
    public boolean isSelected(AnActionEvent e) {
        return model != null && model.isAutorefreshEnabled();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        model.setAutorefreshEnabled(state);
        if (state) {
            enableAutoRefresh();
        } else {
            disableAutoRefresh();
        }
    }

    private void enableAutoRefresh() {
        if (testResultChangedListener == null) {
            testResultChangedListener = new AutoRefreshTestResultChangedListener(model, consoleView);
        }
        model.addTestResultChangedListener(testResultChangedListener);
    }

    private void disableAutoRefresh() {
        model.removeTestResultChangedListener(testResultChangedListener);
    }

    public void setModel(MavenTestsModel model) {
        this.model = model;
    }
}
