package org.dpytel.intellij.plugin.maventest.model;

import com.intellij.execution.Location;
import com.intellij.execution.junit2.info.TestInfo;
import com.intellij.execution.junit2.segments.ObjectReader;
import com.intellij.openapi.project.Project;

/**
*
*/
public class NamedTestInfo extends TestInfo {

    private final String name;

    public NamedTestInfo(String name) {
        this.name = name;
    }

    @Override
    public void readFrom(ObjectReader reader) {

    }

    @Override
    public String getComment() {
        return "";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation(Project project) {

        return null;
    }
}
