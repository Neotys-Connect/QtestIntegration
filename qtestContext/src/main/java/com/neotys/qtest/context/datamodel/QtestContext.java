package com.neotys.qtest.context.datamodel;

public class QtestContext {
    String projectName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public QtestContext(String projectName) {
        this.projectName = projectName;
    }
}
