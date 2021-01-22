package com.neotys.qtest.webhook.datamodel;

import com.neotys.ascode.api.v3.client.model.*;
import com.neotys.qtest.api.client.model.AutomationTestLog;
import com.neotys.qtest.webhook.common.NeoLoadException;

import org.eclipse.jgit.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.neotys.qtest.webhook.common.Constants.NEOLOAD;
import static com.neotys.qtest.webhook.common.Constants.SLA_TYPE_GLOBAL;
import static com.neotys.qtest.webhook.common.Constants.SLA_TYPE_PERTEST;


public class NeoLoadRequirement {
    String name;
    String description;
    String projectid;
    Integer ordno;

    boolean haserror;
    String comment;
    String result;


    public NeoLoadRequirement(String name, String description, String projectid) {
        this.name = name;
        this.description = description;
        this.projectid = projectid;
    }

    public TestStepLogResult toTestLogResult()
    {
        TestStepLogResult testStepLogResult=new TestStepLogResult(ordno,null,description,"PASS",null,haserror,result,comment);
        return  testStepLogResult;
    }

    public Integer getOrdno() {
        return ordno;
    }

    public void setOrdno(Integer ordno) {
        this.ordno = ordno;
    }



    public boolean isHaserror() {
        return haserror;
    }


    public void setHaserror(boolean haserror) {
        this.haserror = haserror;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public NeoLoadRequirement(SLAPerIntervalDefinition slaPerIntervalDefinition, String projectid) throws NeoLoadException {
        this.projectid = projectid;
        if(slaPerIntervalDefinition!=null)
        {
            name=NEOLOAD+"_"+SLA_TYPE_PERTEST+"_"+slaPerIntervalDefinition.getKpi().getValue()+":"+slaPerIntervalDefinition.getElement().getCategory().getValue()+"_"+slaPerIntervalDefinition.getElement().getName();
            description=slaPerIntervalDefinition.getKpi().getValue() +  " on "+slaPerIntervalDefinition.getElement().getCategory().getValue()+" named "+slaPerIntervalDefinition.getElement().getName()+" Failed Threshold is " +  getThresholdString(slaPerIntervalDefinition.getFailedThreshold());

            if(slaPerIntervalDefinition.getStatus().equals(SLAStatusDefinition.FAILED))
                haserror=true;
            else
                haserror=false;

            if(slaPerIntervalDefinition.getStatus().getValue().equalsIgnoreCase("FAILED"))
                comment=generateIntervalMessage("FAILED",slaPerIntervalDefinition.getFailedThreshold(),slaPerIntervalDefinition.getElement(),slaPerIntervalDefinition.getKpi(),slaPerIntervalDefinition.getWarning(),slaPerIntervalDefinition.getFailed());
            else
            {
                if(slaPerIntervalDefinition.getStatus().getValue().equalsIgnoreCase("WARNING")) {
                    if(slaPerIntervalDefinition.getWarningThreshold()!=null)
                        comment=generateIntervalMessage("WARNING", slaPerIntervalDefinition.getWarningThreshold(),  slaPerIntervalDefinition.getElement(),slaPerIntervalDefinition.getKpi(), slaPerIntervalDefinition.getWarning(),slaPerIntervalDefinition.getFailed());
                    else
                    comment=generateIntervalMessage("WARNING", slaPerIntervalDefinition.getFailedThreshold(),  slaPerIntervalDefinition.getElement(), slaPerIntervalDefinition.getKpi(),slaPerIntervalDefinition.getWarning(),slaPerIntervalDefinition.getFailed());
                }
                else
                    comment=generateIntervalMessage("FAILED",slaPerIntervalDefinition.getFailedThreshold(),slaPerIntervalDefinition.getElement(),slaPerIntervalDefinition.getKpi(),slaPerIntervalDefinition.getWarning(),slaPerIntervalDefinition.getFailed());

            }

            result=slaPerIntervalDefinition.getStatus().getValue();

        }
        else
            throw new NeoLoadException("No SLA per time interval");
    }



    private String generateMessage(String failed, ThresholdDefinition definition, Float value, SLAElementDefinition elementDefinition, SLAKPIDefinition slakpiDefinition, @Nullable Long maxvalue)
    {
        if(maxvalue!=null) {
            if (slakpiDefinition != null)
                return  elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() + " the kpi :" + slakpiDefinition.getValue() + " equal to " + String.valueOf(maxvalue.doubleValue()) + " - the " + failed + " Threshold is " + getThresholdString(definition);
            else
                return  elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() + " SLA equal to " + String.valueOf(maxvalue.doubleValue()) + " - the " + failed + " Threshold is " + getThresholdString(definition);
        }
        else
        {
            if (slakpiDefinition != null)
                return  elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() + " the kpi :" + slakpiDefinition.getValue() + " equal to " + String.valueOf(value) + " - the " + failed + " Threshold is " + getThresholdString(definition);
            else
                return elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() + " SLA equal to " + String.valueOf(value) + " - the " + failed + " Threshold is " + getThresholdString(definition);

        }
    }

    private String generateIntervalMessage(String failed, ThresholdDefinition definition,  SLAElementDefinition elementDefinition, SLAKPIDefinition slakpiDefinition, Float warningValue,Float failedValue)
    {

            if (slakpiDefinition != null)
                return  elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() + " the kpi :" + slakpiDefinition.getValue() + "  \n \t"+ warningValue.toString()+ " % reach the  Warning threshold \n\t  "+failedValue.toString() +" % reach the Failed Threshold \n Threshold is " + getThresholdString(definition);
            else
                return elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() +  "  \n \t"+ warningValue.toString()+ " % reach the  Warning threshold \n\t  "+failedValue.toString() +" % reach the Failed Threshold \n  The " + failed + " is " + getThresholdString(definition);


    }

    private String getThresholdString(ThresholdDefinition definition)
    {
        String result;
        result= "value "+definition.getOperator().getValue()+" to " + definition.getValues().stream().map(fload->{return fload.toString();}).collect(Collectors.joining("and"));

        return result;
    }

    public NeoLoadRequirement(SLAGlobalIndicatorDefinition slaGlobalIndicatorDefinition, String projectid) throws NeoLoadException {
        this.projectid = projectid;


        if(slaGlobalIndicatorDefinition!=null)
        {
            name=NEOLOAD+"_"+SLA_TYPE_GLOBAL+slaGlobalIndicatorDefinition.getKpi().getValue();
            description=slaGlobalIndicatorDefinition.getKpi().getValue() +  " Failed Threshold is " +  getThresholdString(slaGlobalIndicatorDefinition.getFailedThreshold());


            if(slaGlobalIndicatorDefinition.getStatus().getValue().equalsIgnoreCase("FAILED"))
            {
                if (slaGlobalIndicatorDefinition.getKpi() != null)
                    comment= slaGlobalIndicatorDefinition.getKpi().getValue() + " equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Failed Threshold is " +  getThresholdString(slaGlobalIndicatorDefinition.getFailedThreshold());
                else
                    comment= "global SLA equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Failed Threshold is " +  getThresholdString(slaGlobalIndicatorDefinition.getFailedThreshold());

            }
            else
            {

                if(slaGlobalIndicatorDefinition.getStatus().getValue().equalsIgnoreCase("WARNING"))
                {
                    if (slaGlobalIndicatorDefinition.getKpi() != null)
                        comment= slaGlobalIndicatorDefinition.getKpi().getValue() + " equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Warning Threshold is " +  getThresholdString(slaGlobalIndicatorDefinition.getWarningThreshold());
                    else
                        comment= "global SLA equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Warning Threshold is " +  getThresholdString(slaGlobalIndicatorDefinition.getWarningThreshold());

                }
                else
                {
                    if (slaGlobalIndicatorDefinition.getKpi() != null)
                        comment= slaGlobalIndicatorDefinition.getKpi().getValue() + " equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Failed Threshold is " + getThresholdString(slaGlobalIndicatorDefinition.getFailedThreshold());
                    else
                        comment= "global SLA equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Failed Threshold is " +  getThresholdString(slaGlobalIndicatorDefinition.getFailedThreshold());

                }
            }

            result=slaGlobalIndicatorDefinition.getStatus().getValue();

        }
        else
            throw new NeoLoadException("No GLobal SLA defined");
    }
    public NeoLoadRequirement(SLAPerTestResultDefinition slaPerTestResultDefinition, String projectid) throws NeoLoadException {
        this.projectid = projectid;


        if(slaPerTestResultDefinition !=null) {
            name=NEOLOAD+"_"+SLA_TYPE_PERTEST+"_"+slaPerTestResultDefinition.getKpi().getValue()+":"+slaPerTestResultDefinition.getElement().getCategory().getValue()+"_"+slaPerTestResultDefinition.getElement().getName();
            description=slaPerTestResultDefinition.getKpi().getValue() +  " on "+slaPerTestResultDefinition.getElement().getCategory().getValue()+" named "+slaPerTestResultDefinition.getElement().getName()+" Failed Threshold is " +  getThresholdString(slaPerTestResultDefinition.getFailedThreshold());


            if(slaPerTestResultDefinition.getStatus().getValue().equalsIgnoreCase("FAILED"))
                comment=generateMessage("FAILED",slaPerTestResultDefinition.getFailedThreshold(),slaPerTestResultDefinition.getValue(),slaPerTestResultDefinition.getElement(),slaPerTestResultDefinition.getKpi(),null);
            else
            {
                if(slaPerTestResultDefinition.getStatus().getValue().equalsIgnoreCase("WARNING")) {
                    if(slaPerTestResultDefinition.getWarningThreshold()!=null)
                        comment=generateMessage("WARNING", slaPerTestResultDefinition.getWarningThreshold(),slaPerTestResultDefinition.getValue(),  slaPerTestResultDefinition.getElement(),slaPerTestResultDefinition.getKpi(),null);
                    else
                        comment=generateMessage("WARNING", slaPerTestResultDefinition.getFailedThreshold(),slaPerTestResultDefinition.getValue(),  slaPerTestResultDefinition.getElement(), slaPerTestResultDefinition.getKpi(),null);
                }
                else
                    comment=generateMessage("FAILED",slaPerTestResultDefinition.getFailedThreshold(),slaPerTestResultDefinition.getValue(),slaPerTestResultDefinition.getElement(),slaPerTestResultDefinition.getKpi(),null);

            }

            result=slaPerTestResultDefinition.getStatus().getValue();
        }
        else
            throw new NeoLoadException("No sla per test");


    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }



}
