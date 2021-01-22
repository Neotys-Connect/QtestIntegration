package com.neotys.qtest.webhook.datamodel;

import com.neotys.qtest.api.client.model.AutomationTestStepLog;

import java.util.UUID;

public class TestStepLogResult {

    //"id": "{testStep_1_Id}",
    // "testCaseId": "{testCaseId}",
    // "orderNo": 0,
    // "actionType": null,
    // "description": "SLA",
    // "expectedResult": "pass",
    // "clipboardData": null,
    // "hasError": false,
    // "result": "Passed",
    // "comment": "95%"


    Integer orderNo;
    String actionType;
    String description;
    String expectedResult;
    String clipboardData;
    boolean hasError;
    String result;
    String comment;

    public TestStepLogResult( Integer orderNo, String actionType, String description, String expectedResult, String clipboardData, boolean hasError, String result, String comment) {

        this.orderNo = orderNo;
        this.actionType = actionType;
        this.description = description;
        this.expectedResult = expectedResult;
        this.clipboardData = clipboardData;
        this.hasError = hasError;
        this.result = result;
        this.comment = comment;
    }



    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getClipboardData() {
        return clipboardData;
    }

    public void setClipboardData(String clipboardData) {
        this.clipboardData = clipboardData;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public AutomationTestStepLog toAutomationTestStepLog()
    {
        AutomationTestStepLog automationTestStepLog=new AutomationTestStepLog();
        automationTestStepLog.setExpectedResult(this.getExpectedResult());
        automationTestStepLog.setDescription(this.description);
        automationTestStepLog.setActualResult(this.comment);
        automationTestStepLog.setOrder(this.orderNo);
        automationTestStepLog.setStatus(this.result);

        return automationTestStepLog;

    }
}
