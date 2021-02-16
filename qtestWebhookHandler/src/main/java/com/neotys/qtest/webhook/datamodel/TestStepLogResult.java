package com.neotys.qtest.webhook.datamodel;

import com.neotys.qtest.api.client.QtestApiClient;
import com.neotys.qtest.api.client.QtestApiException;
import com.neotys.qtest.api.client.api.DefectApi;
import com.neotys.qtest.api.client.model.*;
import com.neotys.qtest.webhook.Logger.NeoLoadLogger;
import org.threeten.bp.OffsetDateTime;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    NeoLoadAttachment neoLoadAttachment;

    public TestStepLogResult(Integer orderNo, String actionType, String description, String expectedResult, String clipboardData, boolean hasError, String result, String comment, NeoLoadAttachment neoLoadAttachment) {

        this.orderNo = orderNo;
        this.actionType = actionType;
        this.description = description;
        this.expectedResult = expectedResult;
        this.clipboardData = clipboardData;
        this.hasError = hasError;
        this.result = result;
        this.comment = comment;
        this.neoLoadAttachment=neoLoadAttachment;
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
        automationTestStepLog.setStatus(toQtestStatus());



        if(neoLoadAttachment!=null) {
            AttachmentResource attachmentResource = new AttachmentResource();
            attachmentResource.setName(neoLoadAttachment.getTile());
            attachmentResource.setCreatedDate(OffsetDateTime.now());
            attachmentResource.setContentType(neoLoadAttachment.getContent_type());
            attachmentResource.setData(neoLoadAttachment.getData());
            automationTestStepLog.setAttachments(Arrays.asList(attachmentResource));
        }
        return automationTestStepLog;

    }

    public AutomationTestStepLog toAutomationTestStepLogWithDefect(Long projectid, QtestApiClient qtestApiClient, NeoLoadLogger logger, List<FieldResource> fieldResourceList,String moduleName ,String releaseName)
    {

            AutomationTestStepLog automationTestStepLog = new AutomationTestStepLog();
            automationTestStepLog.setExpectedResult(this.getExpectedResult());
            automationTestStepLog.setDescription(this.description);
            automationTestStepLog.setActualResult(this.comment);
            automationTestStepLog.setOrder(this.orderNo);
            automationTestStepLog.setStatus(toQtestStatus());

        try {
            if(fieldResourceList!=null && fieldResourceList.size()>0) {
                logger.debug("Creating defect for this step ");
                //---Create the defect---
                DefectApi defectApi = new DefectApi(qtestApiClient);
                DefectResource defectResource = new DefectResource();
                defectResource.setSubmittedDate(OffsetDateTime.now());
                defectResource.setProperties(toProperties(fieldResourceList,logger,moduleName,releaseName));
                if (neoLoadAttachment != null)
                    defectResource.setAttachments(Arrays.asList(neoLoadAttachment.toAttachment()));

                DefectResource defectcrated = defectApi.submitDefect(projectid, defectResource);
                if (defectcrated != null) {
                    logger.debug("Attaching the defect to the result");
                    LinkedDefectResource linkedDefectResource = new LinkedDefectResource();
                    linkedDefectResource.setDescription(this.comment);
                    linkedDefectResource.setStatus("New");
                    linkedDefectResource.setSummary(this.description);
                    linkedDefectResource.setPid(defectcrated.getPid());
                    linkedDefectResource.setId(defectcrated.getId());
                    automationTestStepLog.setDefects(Arrays.asList(linkedDefectResource));


                }
            }
            else
                logger.info("The fields list is empty. impossible to create defects");

        } catch (QtestApiException e) {
            logger.error("Unable to create the defect "+ e.getResponseBody(),e);
        }






        return automationTestStepLog;

    }

    private List<PropertyResource> toProperties(List<FieldResource> fieldResourceList, NeoLoadLogger logger,String moduleName,String releasename) {
        //---handle the fileds of type string
        List<PropertyResource> propertyResourceList= fieldResourceList.stream().filter(fieldResource -> fieldResource.isRequired()).map(fieldResource ->
        {
            return toPropertyRessource(fieldResource,logger);
        }).collect(Collectors.toList());

        PropertyResource propertyResourcemodule=getPropertyModuleName(fieldResourceList,logger,moduleName);
        if (propertyResourcemodule!=null)
            propertyResourceList.add(propertyResourcemodule);

        PropertyResource propertyRelease=getReleaseProperty(fieldResourceList,logger,releasename,"Fixed Release/Build");
        if(propertyRelease!=null)
            propertyResourceList.add(propertyRelease);

        PropertyResource propertyReleaseTarget=getReleaseProperty(fieldResourceList,logger,releasename,"Target Release/Build");
        if(propertyReleaseTarget!=null)
            propertyResourceList.add(propertyReleaseTarget);


        return propertyResourceList;
    }


    private PropertyResource getPropertyModuleName(List<FieldResource> fieldResourceList, NeoLoadLogger logger,String modulename)
    {
        logger.debug("Trying to retrieve the property for Module "+modulename);
        Optional<FieldResource> optionalFieldResource=fieldResourceList.stream().filter(fieldResource -> fieldResource.getLabel().equalsIgnoreCase("Module")).findFirst();
        if(optionalFieldResource.isPresent())
        {
            logger.debug("Found field Module searching for value: "+modulename);

            PropertyResource propertyResource=new PropertyResource();
            propertyResource.setFieldId(optionalFieldResource.get().getId());
            propertyResource.setFieldName(optionalFieldResource.get().getOriginalName());

            Optional<AllowedValueResource> allowedValueResource=optionalFieldResource.get().getAllowedValues().stream().filter(allowedValueResource1 -> allowedValueResource1.getLabel().toUpperCase().contains(modulename.toUpperCase())).findFirst();
            if( allowedValueResource.isPresent())
            {
                propertyResource.setFieldValue(String.valueOf(allowedValueResource.get().getValue()));
                propertyResource.setFieldValueName(allowedValueResource.get().getLabel());
                logger.debug("Found the module property "+ allowedValueResource.get().getLabel());
                return propertyResource;
            }
            else {
                optionalFieldResource.get().getAllowedValues().stream().forEach(allowedValueResource1 -> {logger.debug("Label : "+ allowedValueResource1.getLabel());});
                logger.debug("Unable to find the module property");
                return null;
            }

        }
        else
            return null;
    }

    private PropertyResource getReleaseProperty(List<FieldResource> fieldResourceList, NeoLoadLogger logger, String releasename,String propertyName)
    {
        Optional<FieldResource> optionalFieldResource=fieldResourceList.stream().filter(fieldResource -> fieldResource.getLabel().equalsIgnoreCase(propertyName)).findFirst();
        if(optionalFieldResource.isPresent())
        {
            PropertyResource propertyResource=new PropertyResource();
            propertyResource.setFieldId(optionalFieldResource.get().getId());
            propertyResource.setFieldName(optionalFieldResource.get().getOriginalName());
            propertyResource=setPropertieValue(propertyResource,optionalFieldResource.get().getAllowedValues(),releasename,logger);

            return propertyResource;
        }
        else
            return null;
    }

    private PropertyResource toPropertyRessource(FieldResource fieldResource, NeoLoadLogger logger)
    {
        PropertyResource propertyResource=new PropertyResource();
        propertyResource.setFieldId(fieldResource.getId());
        propertyResource.setFieldName(fieldResource.getOriginalName());

        if(fieldResource.getDataType()<3)
        {
            if(fieldResource.getOriginalName().equalsIgnoreCase("Summary"))
            {
                logger.debug("Found field Summmary");
                propertyResource.setFieldValue(this.description);
                propertyResource.setFieldValueName(this.description);
            }
            if(fieldResource.getOriginalName().equalsIgnoreCase("Description"))
            {
                logger.debug("Found field Description");
                propertyResource.setFieldValue(this.comment);
                propertyResource.setFieldValueName(this.comment);

            }

        }

        if(fieldResource.getDataType()==3)
        {
            // case of a number
            if(fieldResource.getOriginalName().equalsIgnoreCase("Severity"))
            {

                propertyResource=setPropertieValue(propertyResource,fieldResource.getAllowedValues(),"Average",logger);
            }
            if(fieldResource.getOriginalName().equalsIgnoreCase("Priority"))
            {
                propertyResource=setPropertieValue(propertyResource,fieldResource.getAllowedValues(),"Undecided", logger);
            }

            if(fieldResource.getOriginalName().equalsIgnoreCase("Type"))
            {
                propertyResource=setPropertieValue(propertyResource,fieldResource.getAllowedValues(),"Other", logger);
            }


        }


        if(fieldResource.getDataType()==15)
        {
            if(fieldResource.getOriginalName().equalsIgnoreCase("Status"))
            {
                propertyResource=setPropertieValue(propertyResource,fieldResource.getAllowedValues(),"New", logger);
            }
        }
        logger.debug("Field "+fieldResource.getOriginalName()+" type "+fieldResource.getInstanceType());

        return propertyResource;

    }

    private PropertyResource setPropertieValue(PropertyResource propertieResource, List<AllowedValueResource> allowedValueResourceList, String defaultValue, NeoLoadLogger logger)
    {
        Optional<AllowedValueResource> optionalAllowedValueResource= allowedValueResourceList.stream().filter(allowedValueResource -> allowedValueResource.getLabel().equalsIgnoreCase(defaultValue)).findFirst();
        if(optionalAllowedValueResource.isPresent())
        {
            logger.debug("Found  allowed valuew with name"+ defaultValue+ " Value "+String.valueOf(optionalAllowedValueResource.get().getValue())+ " label "+optionalAllowedValueResource.get().getLabel());
            propertieResource.setFieldValue(String.valueOf(optionalAllowedValueResource.get().getValue()));
            propertieResource.setFieldValueName(String.valueOf(optionalAllowedValueResource.get().getLabel()));
        }
        else
            logger.debug("Cannot find any allowed valuew with name"+ defaultValue);

        return propertieResource;
    }
    private String toQtestStatus()
    {
        if(this.result.equalsIgnoreCase("WARNING"))
                return "PASS";
        else {
            if (!this.result.equalsIgnoreCase("PASSED"))
                return "FAIL";
            else
                return "PASS";
        }
    }
}
