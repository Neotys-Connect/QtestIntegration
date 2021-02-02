package com.neotys.qtest.webhook.datamodel;

public class TestObject {
    Long projectid;
    Long testcycleid;
    Long testlogid;
    Long testrunid;

    public TestObject(Long projectid, Long testcycleid, Long testlogid, Long testrunid) {
        this.projectid = projectid;
        this.testcycleid = testcycleid;
        this.testlogid = testlogid;
        this.testrunid = testrunid;
    }

    public Long getProjectid() {
        return projectid;
    }

    public void setProjectid(Long projectid) {
        this.projectid = projectid;
    }

    public Long getTestcycleid() {
        return testcycleid;
    }

    public void setTestcycleid(Long testcycleid) {
        this.testcycleid = testcycleid;
    }

    public Long getTestlogid() {
        return testlogid;
    }

    public void setTestlogid(Long testlogid) {
        this.testlogid = testlogid;
    }

    public Long getTestrunid() {
        return testrunid;
    }

    public void setTestrunid(Long testrunid) {
        this.testrunid = testrunid;
    }
}
