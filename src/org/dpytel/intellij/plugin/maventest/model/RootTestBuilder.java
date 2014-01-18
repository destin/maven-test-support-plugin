package org.dpytel.intellij.plugin.maventest.model;

import com.intellij.execution.junit2.TestProxy;
import com.intellij.execution.junit2.ui.model.RootTestInfo;

/**
 *
 */
public class RootTestBuilder {

    private final String name;

    public RootTestBuilder(String name) {
        this.name = name;
    }

    public static RootTestBuilder named(String name) {
        return new RootTestBuilder(name);
    }

    public TestProxy build() {
        RootTestInfo rootInfo = new RootTestInfo();
        rootInfo.setName(name);
        TestProxy root = new TestProxy(rootInfo);
        AggregatedState suiteState = new AggregatedState(root);
        root.setState(suiteState);
        return root;
    }

}
