package org.dpytel.intellij.plugin.maventest.model;

import com.intellij.execution.junit2.info.ClassBasedInfo;
import com.intellij.execution.junit2.info.DisplayTestInfoExtractor;
import com.intellij.execution.junit2.segments.ObjectReader;

/**
 *
 */
public class TestSuiteInfo extends ClassBasedInfo {

    public TestSuiteInfo(String className) {
        super(DisplayTestInfoExtractor.FOR_CLASS);
        setClassName(className);
    }

    @Override
    public void readFrom(ObjectReader reader) {
        // do nothing
    }
}
