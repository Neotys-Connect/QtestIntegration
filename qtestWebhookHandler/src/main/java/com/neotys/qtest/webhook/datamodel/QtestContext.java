package com.neotys.qtest.webhook.datamodel;

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
