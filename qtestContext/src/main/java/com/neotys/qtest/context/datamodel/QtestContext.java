package com.neotys.qtest.context.datamodel;

public class QtestContext {
    String projectName;
    String testcycle;
    String releasename;

    boolean enableDefectCreation;


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public QtestContext(String projectName, String testcycle, String releasename, boolean enableDefectCreation) {
        this.projectName = projectName;
        this.testcycle=testcycle;
        this.releasename=releasename;
        this.enableDefectCreation=enableDefectCreation;

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

    public boolean isEnableDefectCreation() {
        return enableDefectCreation;
    }

    public void setEnableDefectCreation(boolean enableDefectCreation) {
        this.enableDefectCreation = enableDefectCreation;
    }

    public void setTestcycle(String testcycle) {
        this.testcycle = testcycle;
    }


}
