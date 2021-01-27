package com.neotys.qtest.context.datamodel;

public class QtestContext {
    String projectName;
    String testcycle;
    String releasename;


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public QtestContext(String projectName, String testcycle, String releasename) {
        this.projectName = projectName;
        this.testcycle=testcycle;
        this.releasename=releasename;

    }

    public String getReleasename() {
        return releasename;
    }

    public void setReleasename(String releasename) {
        this.releasename = releasename;
    }

    public String getTestcycle() {
        return testcycle;
    }

    public void setTestcycle(String testcycle) {
        this.testcycle = testcycle;
    }
}
