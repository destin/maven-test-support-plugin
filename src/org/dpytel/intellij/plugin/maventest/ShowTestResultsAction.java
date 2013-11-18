package org.dpytel.intellij.plugin.maventest;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.JUnitConfigurationType;
import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.info.TestInfo;
import com.intellij.execution.junit2.states.SuiteState;
import com.intellij.execution.junit2.ui.JUnitTreeConsoleView;
import com.intellij.execution.junit2.ui.model.JUnitRunningModel;
import com.intellij.execution.junit2.ui.properties.JUnitConsoleProperties;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.dpytel.intellij.plugin.maventest.model.NamedTestInfo;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

/**
 *
 */
public class ShowTestResultsAction extends AnAction {

    public static final String TOOL_WINDOW_ID = "Maven test results";
    private final ReportParser reportParser = new ReportParser();

    public ShowTestResultsAction() {
        super("ShowTestResultsAction");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        MavenProject mavenProject = MavenActionUtil.getMavenProject(event.getDataContext());
        ConfigurationFactory configurationFactory = JUnitConfigurationType.getInstance().getConfigurationFactories()[0];
        JUnitConfiguration myConfiguration = new JUnitConfiguration("maven", project, configurationFactory);
        Executor executor = new DefaultRunExecutor();
        final JUnitConsoleProperties consoleProperties = new JUnitConsoleProperties(myConfiguration, executor);
        final JUnitTreeConsoleView consoleView = new JUnitTreeConsoleView(consoleProperties, null, null, null);
        consoleView.initUI();
        VirtualFile selectedFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        JUnitRunningModel model = createModel(selectedFile, consoleProperties);
        consoleView.attachToModel(model);


        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
        if (toolWindow == null) {
            toolWindow = toolWindowManager
                .registerToolWindow(TOOL_WINDOW_ID, true, ToolWindowAnchor.BOTTOM);
        }
        toolWindow.activate(null);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory()
            .createContent(consoleView.getComponent(), mavenProject.getFinalName(), false);
        contentManager.addContent(content);
        contentManager.setSelectedContent(content);
    }

    private JUnitRunningModel createModel(VirtualFile baseDir, JUnitConsoleProperties consoleProperties) {
        TestInfo info = new NamedTestInfo("Root");
        TestProxy root = new TestProxy(info);
        SuiteState suiteState = new SuiteState(root);
        root.setState(suiteState);
        if (baseDir.exists()) {
            processReportsDir(baseDir, root, suiteState, "target/surefire-reports");
            processReportsDir(baseDir, root, suiteState, "target/failsafe-reports");
        }
        JUnitRunningModel model = new JUnitRunningModel(root, consoleProperties);
        return model;
    }

    private void processReportsDir(VirtualFile baseDir, TestProxy root, SuiteState suiteState, String reportDir) {
        VirtualFile reportsDir = baseDir.findFileByRelativePath(reportDir);
        if (reportsDir != null && reportsDir.exists()) {
            addReports(root, suiteState, reportsDir);
        }
    }

    private void addReports(TestProxy root, SuiteState suiteState, VirtualFile reportsDir) {
        VirtualFile[] children = reportsDir.getChildren();
        for (VirtualFile child : children) {
            if (child.getName().matches("TEST-.*\\.xml")) {
                TestProxy childTestProxy = reportParser.parseTestSuite(child);
                root.addChild(childTestProxy);
                suiteState.updateMagnitude(childTestProxy.getMagnitude());
            }
        }

    }

    @Override
    public boolean isInInjectedContext() {
        return super.isInInjectedContext();
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation p = e.getPresentation();
        p.setEnabled(isAvailable(e));
        p.setVisible(isVisible(e));
    }

    private boolean isAvailable(AnActionEvent e) {
        return MavenActionUtil.hasProject(e.getDataContext());
    }

    private boolean isVisible(AnActionEvent e) {
        MavenProject mavenProject = MavenActionUtil.getMavenProject(e.getDataContext());
        return mavenProject != null;
    }
}
