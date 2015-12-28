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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.dpytel.intellij.plugin.maventest.Icons;
import org.dpytel.intellij.plugin.maventest.model.MavenTestsModel;
import org.dpytel.intellij.plugin.maventest.text.TextBundle;
import org.dpytel.intellij.plugin.maventest.view.MavenTreeConsoleView;

/**
 *
 */
public class AutoRefreshAction extends ToggleAction {

    private final MavenTreeConsoleView consoleView;
    private MavenTestsModel myModel;
    private AutoRefreshTestResultChangedListener testResultChangedListener;

    public AutoRefreshAction(MavenTreeConsoleView consoleView, MavenTestsModel model) {
        super(TextBundle.getText("maventestsupport.toolbar.actions.autorefresh.name"),
            TextBundle.getText("maventestsupport.toolbar.actions.autorefresh.description"),
            Icons.AUTOREFRESH);
        this.consoleView = consoleView;
        this.myModel = model;
    }

    @Override
    public boolean isSelected(AnActionEvent e) {
        return myModel != null && myModel.isAutorefreshEnabled();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        myModel.setAutorefreshEnabled(state);
        if (state) {
            enableAutoRefresh();
        } else {
            disableAutoRefresh();
        }
    }

    private void enableAutoRefresh() {
        if (testResultChangedListener == null) {
            testResultChangedListener = new AutoRefreshTestResultChangedListener(myModel, consoleView);
        }
        myModel.addTestResultChangedListener(testResultChangedListener);
    }

    private void disableAutoRefresh() {
        myModel.removeTestResultChangedListener(testResultChangedListener);
    }

}
