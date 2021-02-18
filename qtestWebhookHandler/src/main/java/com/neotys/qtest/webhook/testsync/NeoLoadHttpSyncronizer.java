package com.neotys.qtest.webhook.testsync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.neotys.ascode.api.v3.client.ApiClient;
import com.neotys.ascode.api.v3.client.ApiException;
import com.neotys.ascode.api.v3.client.api.ResultsApi;
import com.neotys.ascode.api.v3.client.model.*;
import com.neotys.qtest.api.client.QtestApiClient;
import com.neotys.qtest.api.client.QtestApiException;
import com.neotys.qtest.api.client.api.*;
import com.neotys.qtest.api.client.model.*;
import com.neotys.qtest.webhook.Logger.NeoLoadLogger;
import com.neotys.qtest.webhook.common.NeoLoadException;
import com.neotys.qtest.webhook.datamodel.*;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.json.Json;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.stream.Collectors;

import static com.neotys.qtest.webhook.common.Constants.*;
import static javax.management.remote.JMXConnectionNotification.FAILED;


public class NeoLoadHttpSyncronizer {
    private String testid;
    private String workspaceid;
    private Optional<String> neoload_Web_Url;
    private Optional<String> neoload_API_Url;
    private Optional<String> neoload_API_PORT;
    private String neoload_API_key;
    private NeoLoadLogger logger;
    private ApiClient apiClient;
    private Optional<String> qtestApiHost;
    private Optional<String> qtestApiport;
    private Optional<String> qtestApipath;

    private ResultsApi resultsApi;
    private String projectName;
    private String scenarioName;
    private String relasename;
    private long testEnd;
    private String status;

    private boolean ssl;
    private UUID uipathprojectid;
    private UUID uipathcaseid;
    private String qTestProjectName;

    private String qtesttestCycle;
    private QtestApiClient qTestApiClient;
    private String qtestAPIurl;
    private String neoloadUIPathTestCaseName;
    private Optional<String> qtestApiToken;
    private Vertx vertx;
    private List<String> uiPathCookies;

    private UUID testsetid;

    public NeoLoadHttpSyncronizer(String testid, String workspaceid, Vertx vertx) throws NeoLoadException {
        this.testid=testid;
        this.workspaceid=workspaceid;

        logger=new NeoLoadLogger(this.getClass().getName());

        apiClient=new ApiClient();
        getEnvVariables();
        generateApiUrl();


        apiClient.setBasePath(HTTPS+neoload_API_Url.get());
        apiClient.setApiKey(neoload_API_key);

        qTestApiClient =new QtestApiClient();
        qTestApiClient.setBasePath(HTTPS+ qtestAPIurl);
        qTestApiClient.setApiKey(qtestApiToken.get());


        this.vertx=vertx;


    }

    private String generateNlTestResultUrl()
    {
        return HTTPS+neoload_Web_Url.get()+NEOLAOD_WEB_URL+testid+NEOLAOD_WEB_LASTPART_URL;
    }


    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
                  try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


    private Future<QtestContext> getQtestProject()  {
        Future<QtestContext> future=Future.future();

        resultsApi =new ResultsApi(apiClient);
        try {
            TestResultDefinition testResultDefinition=resultsApi.getTestResult(workspaceid,testid);
            String description = testResultDefinition.getDescription();
            projectName=testResultDefinition.getProject();
            scenarioName=testResultDefinition.getScenario();
            neoloadUIPathTestCaseName=NEOLOAD+ " "+ testResultDefinition.getProject()+"_"+testResultDefinition.getScenario();
            if(description!=null)
            {


                if(!description.isEmpty()||!description.trim().isEmpty()) {
                    if(isJSONValid(description)) {
                        logger.debug("Converting Description into java Object + " + description);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory()).create();
                        QtestContext uipathContext = gson.fromJson(description, QtestContext.class);
                        future.complete(uipathContext);
                    }
                    else
                        future.fail("Not a json content");
                }
                else
                    future.fail("No qtestContext");
            }
            else {
                qTestProjectName = null;
                future.fail("No project found");
            }

        } catch (ApiException e) {
           logger.error("APIExeption to retrieve test results "+e.getResponseBody(),e);
           future.fail( new NeoLoadException("Api Exeption - impossible to retrieve test "+e.getResponseBody()));
        }
        catch (JsonSyntaxException e)
        {
            logger.error("JsonSyntaxExeption - impossible to  deserialize the Qtest context",e);
            future.fail(new NeoLoadException("JsonSyntaxExeption impossible to deserialize the uiPath context"));
        }
        return future;
    }



    private void generateApiUrl()
    {
        if(neoload_API_Url.isPresent()&&neoload_API_PORT.isPresent())
        {
            if(neoload_API_PORT.get().equalsIgnoreCase(DEFAULT_NL_API_PORT))
                if (!neoload_API_Url.get().contains(API_URL_VERSION))
                    neoload_API_Url = Optional.of(neoload_API_Url.get() );

                else
                {
                    if (!neoload_API_Url.get().contains(API_URL_VERSION))
                        neoload_API_Url = Optional.of(neoload_API_Url.get() + ":" + neoload_API_PORT.get() );
                }
        }

        if(qtestApiHost.isPresent())
        {
            if(qtestApiport.isPresent())
                qtestAPIurl = qtestApiHost.get()+":"+ qtestApiport.get();
            else
                qtestAPIurl = qtestApiHost.get()+":"+DEFAULT_NL_API_PORT;



        }

        logger.debug("URL used to interact with apis : "+neoload_API_Url.get()+" and "+ qtestAPIurl);
    }
    private void getEnvVariables() throws NeoLoadException {

        logger.debug("retrieve the environement variables for neoload  neoload service ");
        neoload_API_key = System.getenv(SECRET_API_TOKEN);
        if (neoload_API_key == null) {
            logger.error("No API key defined");
            throw new NeoLoadException("No API key is defined");
        }
        neoload_API_PORT = Optional.ofNullable(System.getenv(SECRET_NL_API_PORT)).filter(o -> !o.isEmpty());
        if (!neoload_API_PORT.isPresent())
            neoload_API_PORT = Optional.of(DEFAULT_NL_API_PORT);

        neoload_API_Url = Optional.ofNullable(System.getenv(SECRET_NL_API_HOST)).filter(o -> !o.isEmpty());
        if (!neoload_API_Url.isPresent())
            neoload_API_Url = Optional.of(DEFAULT_NL_SAAS_API_URL);

        neoload_Web_Url = Optional.ofNullable(System.getenv(SECRET_NL_WEB_HOST)).filter(o -> !o.isEmpty());
        if (!neoload_Web_Url.isPresent())
            neoload_Web_Url = Optional.of(SECRET_NL_WEB_HOST);

        if (System.getenv(SECRET_SSL) != null && !System.getenv(SECRET_SSL).isEmpty()) {
            ssl = Boolean.parseBoolean(System.getenv(SECRET_SSL));
        } else
            ssl = false;

        qtestApipath =Optional.ofNullable(System.getenv(SECRET_QTEST_APIPATH)).filter(o->!o.isEmpty());

        qtestApiHost = Optional.ofNullable(System.getenv(SECRET_QTEST_HOST)).filter(o -> !o.isEmpty());
        if (qtestApiHost.isPresent())
        {
            logger.debug("A Managed hostname is defined");
            qtestApiport = Optional.ofNullable(System.getenv(SECRET_QTEST_PORT)).filter(o -> !o.isEmpty());
            if (!qtestApiport.isPresent())
                qtestApiport = Optional.of(DEFAULT_MANAGED_PORT);



            qtestApipath =Optional.ofNullable(System.getenv(SECRET_QTEST_APIPATH)).filter(o->!o.isEmpty());



            if(!qtestApipath.isPresent())
                throw new NeoLoadException(SECRET_QTEST_APIPATH+"environment varaible is missing");

            qtestApiToken=Optional.ofNullable(System.getenv(SECRET_QTEST_APITOKEN)).filter(o->!o.isEmpty());

            if(!qtestApiToken.isPresent())
                throw new NeoLoadException(SECRET_QTEST_APITOKEN+"environment varaible is missing");



        } else
            throw new NeoLoadException("The APi host name of your Qtest needs to be defined");
    }

    public QtestUser getQtestUser() throws NeoLoadException {
        UserApi userApi= new UserApi(qTestApiClient);

        try {


        LoggedUser loggedUser= userApi.reevaluateToken(false);
        QtestUser qtestUser=new QtestUser( loggedUser.getId(),loggedUser.getClientId());
        return qtestUser;

        } catch (QtestApiException e) {
            logger.error("QtestApiException to retrieve user details "+e.getResponseBody(),e);
            throw new NeoLoadException("QtestApiException to retrieve user details "+e.getResponseBody());

        }


    }

    public Future<Boolean> endQTestExecution() {
        Future<Boolean> future=Future.future();
        Future<QtestContext> futureproject= getQtestProject();
        AtomicInteger atomicInteger=new AtomicInteger(0);
        List<NeoLoadRequirement> neoLoadRequirementList=new ArrayList<>();
        futureproject.setHandler(stringAsyncResult -> {
            if(stringAsyncResult.succeeded())
            {
                QtestContext qtestContext=stringAsyncResult.result();
                if(qtestContext!=null)
                {
                    qTestProjectName=qtestContext.getProjectName();
                    qtesttestCycle=qtestContext.getTestcycle();
                    relasename=qtestContext.getReleasename();
                }
                else
                    future.fail("TestProject and TestCycle are missing");

                try {

                    Long projectid=getProjectID();
                    TestCycleResource  testcylce=getTestCycle(projectid);
                    //get the sla rules
                    resultsApi = new ResultsApi(apiClient);
                    TestResultDefinition testResultDefinition=resultsApi.getTestResult(workspaceid,testid);
                    ArrayOfSLAGlobalIndicatorDefinition arrayOfSLAGlobalIndicatorDefinition = resultsApi.getTestResultSLAGlobalIndicators(workspaceid, testid, null);

                    ArrayOfSLAPerTestResultDefinition arrayOfSLAPerTestResultDefinition = resultsApi.getTestResultSLAPerTest(workspaceid, testid, null, null);

                    ArrayOfSLAPerIntervalDefinition arrayOfSLAPerIntervalDefinition = resultsApi.getTestResultSLAPerInterval(workspaceid, testid, null, null);

                    if (arrayOfSLAGlobalIndicatorDefinition.size() == 0 && arrayOfSLAPerIntervalDefinition.size() == 0 && arrayOfSLAPerTestResultDefinition.size() == 0) {
                           future.fail(new NeoLoadException("No SLA in the NeoLoad test"));
                    }
                    else
                    {
                        // creating the NeoLoad requirements based on the SLA
                        arrayOfSLAGlobalIndicatorDefinition.stream().forEach(slaGlobalIndicatorDefinition -> {
                            try {

                                neoLoadRequirementList.add(new NeoLoadRequirement(slaGlobalIndicatorDefinition, atomicInteger));
                            }
                            catch (NeoLoadException e) {
                                logger.error("Unable to convert the sal into a neolaod requiremt", e);
                            }

                        });
                        arrayOfSLAPerIntervalDefinition.stream().forEach(slaGlobalIndicatorDefinition -> {
                            try {
                                neoLoadRequirementList.add(new NeoLoadRequirement(slaGlobalIndicatorDefinition, atomicInteger));
                            } catch (NeoLoadException e) {
                                logger.error("Unable to convert the sal into a neolaod requiremt", e);
                            }


                        });
                        arrayOfSLAPerTestResultDefinition.stream().forEach(slaGlobalIndicatorDefinition -> {
                            try {
                                neoLoadRequirementList.add(new NeoLoadRequirement(slaGlobalIndicatorDefinition, atomicInteger));
                            } catch (NeoLoadException e) {
                                logger.error("Unable to convert the sal into a neolaod requiremt", e);
                            }


                        });

                        QtestUser qtestUser=getQtestUser();
                        if(qtestUser==null)
                        {
                            logger.error("Impossible to retrieve the QtestUser");
                            future.fail(new NeoLoadException("Impossilble to retrieve the QtestUser"));
                        }



                        AutomationRequest automationRequest =new AutomationRequest();
                        automationRequest.setExecutionDate(OffsetDateTime.ofInstant(Instant.ofEpochMilli(testResultDefinition.getStartDate()), ZoneId.systemDefault()));
                        List<AutomationTestLogResource> automationTestLogResourceList=new ArrayList<>();
                        AutomationTestLogResource testLogResource=new AutomationTestLogResource();
                        testLogResource.setExeEndDate( OffsetDateTime.ofInstant(Instant.ofEpochMilli(testResultDefinition.getEndDate()), ZoneId.systemDefault()));
                        testLogResource.setExeStartDate( OffsetDateTime.ofInstant(Instant.ofEpochMilli(testResultDefinition.getStartDate()), ZoneId.systemDefault()));
                        if(testResultDefinition.getQualityStatus().getValue().equalsIgnoreCase("FAILED"))
                             testLogResource.setStatus("FAIL");
                        else
                             testLogResource.setStatus("PASS");
                        testLogResource.setSystemName(NEOLOAD);
                        testLogResource.setName(NEOLOAD+" Project:"+testResultDefinition.getProject()+ " Scenario:"+testResultDefinition.getScenario());
                        testLogResource.setNote(generateTestNote(resultsApi));
                        testLogResource.setOrder(new Long(0));
                        testLogResource.setBuildUrl(generateNlTestResultUrl());
                        testLogResource.setActualExeTime(testResultDefinition.getDuration());

                        ///---get the required fields to create a defect
                        List<FieldResource> fieldResourceList=getDefectProperties(projectid);

                        testLogResource.setTestStepLogs(neoLoadRequirementList.stream().map(neoLoadRequirement -> {
                            if(!neoLoadRequirement.getResult().equalsIgnoreCase("FAILED"))
                                return  neoLoadRequirement.toTestLogResult(null);
                            else
                                return neoLoadRequirement.toTestLogResult(getSLaGraph(neoLoadRequirement.getElementID(),neoLoadRequirement.getSlakpiDefinition()));

                        //#TODO handle the attachments ..waitng for tricentisi return  neoLoadRequirement.toTestLogResult(getSLaGraph(neoLoadRequirement.getElementID(),neoLoadRequirement.getSlakpiDefinition()))
                            }).map(testStepLogResult -> {
                                logger.debug("Status of the step "+ testStepLogResult.getResult());
                                   if(!testStepLogResult.getResult().equalsIgnoreCase("FAILED"))
                                         return testStepLogResult.toAutomationTestStepLog();
                                   else
                                   {
                                       if(qtestContext.isEnableDefectCreation())
                                           return testStepLogResult.toAutomationTestStepLogWithDefect(projectid, qTestApiClient,logger,fieldResourceList,scenarioName,relasename);
                                        else return testStepLogResult.toAutomationTestStepLog();

                                   }
                        }).collect(Collectors.toList()));
                        testLogResource.setAutomationContent(NEOLOAD);
                        testLogResource.setModuleNames(Arrays.asList(NEOLOAD,projectName,scenarioName));

                      //  testLogResource.setAttachments(getGraph().stream().map(neoLoadAttachment -> neoLoadAttachment.toAttachment()).collect(Collectors.toList()));
                        automationTestLogResourceList.add(testLogResource);
                        automationRequest.setTestLogs(automationTestLogResourceList);
                        automationRequest.setTestCycle(testcylce.getPid());
                        logger.debug("Going to send the results to qtest : " +automationRequest.toString());






                        WorkerExecutor workerExecutor=vertx.createSharedWorkerExecutor("Get TestCase");
                        workerExecutor.executeBlocking(promise -> {
                                    try {
                                        String status = getSyncStatus(projectid, automationRequest, String.valueOf(qtestUser.getUserid()));
                                        if (status.equalsIgnoreCase("FAILED"))
                                            promise.fail("FAILED");
                                        else
                                        {
                                            //---get the run id
                                            TestObject testObject=getPrevisouTestLogIds(projectid, testcylce.getId(),NEOLOAD+" Project:"+testResultDefinition.getProject()+ " Scenario:"+testResultDefinition.getScenario(),OffsetDateTime.now());
                                            if(testObject!=null)
                                            {
                                                String url="https://"+qtestApiHost.get()+":"+qtestApiport.get()+"/p/"+ testObject.getProjectid()+"/portal/project#tab=testexecution&object=3&id="+testObject.getTestrunid();
                                                if(qtestContext.isEnableDefectCreation())
                                                    attachDefectsToTestLogs(testObject,automationRequest);
                                                promise.complete(url);
                                            }
                                        }

                                    } catch (NeoLoadException e) {
                                        promise.fail(e);
                                    }
                                }

                                , asyncResult ->
                                {
                                    try {
                                        if (asyncResult.succeeded()) {
                                            String url = asyncResult.result().toString();
                                            logger.debug("Updating the neoload project ");
                                            TestResultUpdateRequest testResultUpdateRequest = new TestResultUpdateRequest();
                                            testResultUpdateRequest.setDescription("link to the Qtest execution : " + url);
                                            resultsApi.updateTestResult(testResultUpdateRequest, workspaceid, testid);
                                            logger.debug("The qtest Context has been removed from NeoLoad test result");
                                            future.complete(true);
                                        } else
                                            future.fail(asyncResult.cause());
                                    } catch (ApiException e) {
                                        logger.error("Api exeption when updating project "+e.getResponseBody(),e);
                                        future.fail(e);
                                    }

                                });


                    }
                }
                catch (ApiException e) {
                    logger.error("Unable to retrieve NeoloAD results "+e.getResponseBody(),e);
                    future.fail(e);
                } /*catch (QtestApiException e) {
                    logger.error("Unable to retrieve Qtest data "+e.getResponseBody(),e);
                    future.fail(e);
                }*/ catch (NeoLoadException e) {
                    logger.error("Neoload Exeption "+e.getMessage(),e);
                    future.fail(e);
                }/* catch (IOException e) {
                    logger.error("Issue when generating attachment ",e);
                    future.fail(e);
                }*/
            }
            else
            {
                logger.error("Impossible to retrieve qtest context",stringAsyncResult.cause());
                future.fail(stringAsyncResult.cause());
            }

        });



        return future;

    }

    private void attachDefectsToTestLogs(TestObject testObject,AutomationRequest automationRequest)
    {
        TestLogApi testLogApi=new TestLogApi(qTestApiClient);
        ObjectLinkApi objectLinkApi=new ObjectLinkApi(qTestApiClient);
        try
        {
            TestLogResource testLogResource=testLogApi.getLastRunLog(testObject.getProjectid(),testObject.getTestrunid(),"test-steps");
            testLogResource.getTestStepLogs().stream().filter(testStepLogResource -> testStepLogResource.getStatus().getName().equalsIgnoreCase(NEOLOAD_FAIL_STATUS)).forEach(testStepLogResource -> {
                automationRequest.getTestLogs().stream().forEach(automationTestLogResource -> automationTestLogResource.getTestStepLogs().stream().filter(automationTestStepLog -> automationTestStepLog.getStatus().equalsIgnoreCase(JIRA_FAIL_STATUS)).filter(automationTestStepLog -> automationTestStepLog.getDescription().toUpperCase().equalsIgnoreCase(testStepLogResource.getDescription().toUpperCase()) && (automationTestStepLog.getOrder()+1)==testStepLogResource.getOrder()).forEach(automationTestStepLog -> {
                    ///Get defects and attache them
                    automationTestStepLog.getDefects().forEach(linkedDefectResource -> {
                        try {
                            objectLinkApi.linkArtifacts(testObject.getProjectid(),"test-steps","defects",Arrays.asList(linkedDefectResource.getId()),testStepLogResource.getTestStepLogId());
                        } catch (QtestApiException e) {
                            logger.error("Unable to attach defect to test step id "+e.getResponseBody(),e);
                        }
                    });
                }));
            });

        } catch (QtestApiException e) {
            logger.error("Unable to get the TestLogRessource of the last test "+e.getResponseBody(),e);
        }
    }

    private List<FieldResource> getDefectProperties(Long projectid)
    {
        FieldApi fieldApi=new FieldApi(qTestApiClient);
        try
        {
            List<FieldResource> fieldResourceList= fieldApi.getFields(projectid,"defects",false);
            return fieldResourceList;
        } catch (QtestApiException e) {
            logger.error("Unable to retrieve the defects fields for the project "+e.getResponseBody(),e);
        }
        return null;
    }

    private TestObject getPrevisouTestLogIds(long projectid, long testcycleId, String executionName, OffsetDateTime rundate) throws NeoLoadException {
        try
        {
            TestRunApi testRunApi=new TestRunApi(qTestApiClient);

            TestRunListResource testRunListResource=testRunApi.getOf(projectid,testcycleId,"test-cycle","descendants",Long.valueOf(1),Long.valueOf(999));
            List<TestRunWithCustomFieldResource> testRunWithCustomFieldResources=testRunListResource.getItems().stream().filter(testRunWithCustomFieldResource -> testRunWithCustomFieldResource.getName().equalsIgnoreCase(executionName)).sorted((o1, o2) -> o1.getCreatedDate().compareTo(o2.getCreatedDate())).collect(Collectors.toList());
            testRunWithCustomFieldResources.sort((o1, o2) -> - o1.getCreatedDate().compareTo(o2.getCreatedDate()));
            Optional<TestObject> testlogids=testRunWithCustomFieldResources.stream().filter(testRunWithCustomFieldResource -> testRunWithCustomFieldResource.getCreatedDate().isBefore(rundate)).map(testrun -> {return new TestObject(projectid,testcycleId,testrun.getLatestTestLog().getId(),testrun.getId());}).findFirst();
            if(testlogids.isPresent())
                 return testlogids.get();
            else
                return null;
        } catch (QtestApiException e) {
            logger.error("QTest api exception "+e.getResponseBody());
            throw new NeoLoadException("QTest api exception "+e.getResponseBody());
        }
    }

    private String getSyncStatus(long projectid,AutomationRequest automationRequest,String  userid) throws NeoLoadException {
        try {

            TestLogApi testLogApi = new TestLogApi(qTestApiClient);
            QueueProcessingResponse queueProcessingResponse = testLogApi.submitAutomationTestLogs_0(projectid, automationRequest, "automation", null, userid);

            while(!Arrays.asList("SUCCESS","FAILED").contains(queueProcessingResponse.getState())) {
                logger.debug("Received the queueProcessingResponse still not finalized  "+queueProcessingResponse.getContent() +" and the state "+queueProcessingResponse.getState());

                Thread.sleep(1000);

                queueProcessingResponse=testLogApi.track(queueProcessingResponse.getId());



            }
            logger.debug("Status :"+queueProcessingResponse.getState());


            return queueProcessingResponse.getState();




        } catch (QtestApiException e) {
            logger.error("Unable to interact with qtet api "+e.getResponseBody(),e);
            throw new NeoLoadException("Unable to interact with qtet api "+e.getResponseBody());
        } catch (InterruptedException e) {
            logger.error("Technical InterruptedException "+e.getMessage(),e);
            throw new NeoLoadException("Technical InterruptedException "+e.getMessage());
        }

    }

    private ElementIdDefinition.StatisticsEnum getStatiscticsEnumfromKPI(SLAKPIDefinition slakpiDefinition)
    {
        if(slakpiDefinition.getValue().contains("transaction"))
        {
            if(slakpiDefinition.equals(SLAKPIDefinition.AVG_TRANSACTION_RESP_TIME))
            {
                return ElementIdDefinition.StatisticsEnum.AVG_DURATION;
            }
            else
                return ElementIdDefinition.StatisticsEnum.PERCENTILES_DURATION;
        }

        if(slakpiDefinition.getValue().contains("request"))
        {
            if(slakpiDefinition.equals(SLAKPIDefinition.AVG_REQUEST_PER_SEC))
                return ElementIdDefinition.StatisticsEnum.ELEMENTS_PER_SECOND;

            if(slakpiDefinition.equals(SLAKPIDefinition.AVG_REQUEST_RESP_TIME))
                return ElementIdDefinition.StatisticsEnum.ELEMENTS_PER_SECOND;
        }

        if(slakpiDefinition.getValue().contains("page"))
            return ElementIdDefinition.StatisticsEnum.AVG_DURATION;

        if(slakpiDefinition.equals(SLAKPIDefinition.AVG_RESP_TIME))
            return ElementIdDefinition.StatisticsEnum.AVG_DURATION;

        if(slakpiDefinition.equals(SLAKPIDefinition.ERRORS_COUNT))
            return ElementIdDefinition.StatisticsEnum.ERRORS;

        return ElementIdDefinition.StatisticsEnum.valueOf(slakpiDefinition.getValue().toUpperCase());
    }
    private NeoLoadAttachment getSLaGraph(String elementid,SLAKPIDefinition slakpiDefinition)  {
        try {
            TestResultRasterConfiguration testResultRasterConfiguration = new TestResultRasterConfiguration();
            ElementIdDefinition elementDefinition = new ElementIdDefinition();

            if (elementid != null) {
                testResultRasterConfiguration.setRasterType(TestResultRasterConfiguration.RasterTypeEnum.PNG);
                testResultRasterConfiguration.setTheme(TestResultRasterConfiguration.ThemeEnum.LIGHT);
                testResultRasterConfiguration.setWidth(800);
                testResultRasterConfiguration.setHeight(600);
                testResultRasterConfiguration.setLegend(true);
                testResultRasterConfiguration.setMultiYAxis(false);
                testResultRasterConfiguration.setYAxisLabel("");
                testResultRasterConfiguration.setXAxisLabel("time");
                testResultRasterConfiguration.setTitle(slakpiDefinition.getValue());
                elementDefinition.setId(elementid);
                elementDefinition.setStatistics(Arrays.asList(getStatiscticsEnumfromKPI(slakpiDefinition)));
                testResultRasterConfiguration.setElementIds(Arrays.asList(elementDefinition));
            } else {
                testResultRasterConfiguration.setRasterType(TestResultRasterConfiguration.RasterTypeEnum.PNG);
                testResultRasterConfiguration.setTheme(TestResultRasterConfiguration.ThemeEnum.LIGHT);
                testResultRasterConfiguration.setWidth(800);
                testResultRasterConfiguration.setHeight(600);
                testResultRasterConfiguration.setLegend(true);
                testResultRasterConfiguration.setMultiYAxis(false);
                testResultRasterConfiguration.setYAxisLabel("");
                testResultRasterConfiguration.setXAxisLabel("time");
                elementDefinition.setId(ELEMENTID_ALL_REQUTEST);
                elementDefinition.setStatistics(Arrays.asList(getStatiscticsEnumfromKPI(slakpiDefinition)));
                testResultRasterConfiguration.setElementIds(Arrays.asList(elementDefinition));
            }

            File userload = resultsApi.getTestResultGraph(workspaceid, testid, testResultRasterConfiguration);

           return  new NeoLoadAttachment(userload, "Sla Graph", "image/png");
        } catch (ApiException e) {
            logger.error("APi execption when generating image "+e.getResponseBody(),e);
            return null;
        } catch (IOException e) {
            logger.error("IOexeption when generating image ",e);
            return null;

        }
    }

    private Long getProjectID() throws NeoLoadException {
        ProjectApi projectApi=new ProjectApi(qTestApiClient);
        try {
            List<ProjectResource> projectResourceList=projectApi.getProjects("userprofile",false,Long.valueOf(1),Long.valueOf(999));
            Optional<ProjectResource> projectResource=projectResourceList.stream().filter(resource -> {
                return qTestProjectName.equalsIgnoreCase(resource.getName());
            }).findFirst();

            if(projectResource.isPresent())
            {
                return projectResource.get().getId();

            }
            else
                throw new NeoLoadException("No Project found with the name "+ qTestProjectName);

        } catch (QtestApiException e) {
            logger.error("Unable to retrieve the Qtest Project "+e.getResponseBody(),e);
            throw new NeoLoadException("Unable to retrieve the Qtest Project "+e.getResponseBody());
        }
    }

    private List<NeoLoadAttachment> getGraph() throws NeoLoadException, ApiException, IOException {
        List<NeoLoadAttachment> neoLoadAttachments=new ArrayList<>();

        TestResultRasterConfiguration testResultRasterConfiguration=new TestResultRasterConfiguration();
        testResultRasterConfiguration.setRasterType(TestResultRasterConfiguration.RasterTypeEnum.PNG);
        testResultRasterConfiguration.setTheme(TestResultRasterConfiguration.ThemeEnum.LIGHT);
        testResultRasterConfiguration.setWidth(800);
        testResultRasterConfiguration.setHeight(600);
        testResultRasterConfiguration.setLegend(true);
        testResultRasterConfiguration.setMultiYAxis(true);
        testResultRasterConfiguration.setXAxisLabel("Time");
        testResultRasterConfiguration.setYAxisLabel("");
        List<ElementIdDefinition> elementDefinitions=new ArrayList<>();
        ElementIdDefinition elementDefinition= new ElementIdDefinition();
        elementDefinition.setId(ELEMENTID_ALL_REQUTEST);
        elementDefinition.setStatistics(Arrays.asList(ElementIdDefinition.StatisticsEnum.ELEMENTS_PER_SECOND, ElementIdDefinition.StatisticsEnum.THROUGHPUT, ElementIdDefinition.StatisticsEnum.AVG_DURATION));
        elementDefinitions.add(elementDefinition);
        testResultRasterConfiguration.setElementIds(elementDefinitions);
        testResultRasterConfiguration.setTitle("Hit/s - Error/s - Request average duration");
        testResultRasterConfiguration.setCounterIds(Arrays.asList(getUserLoadCounterId()));

        File userload =resultsApi.getTestResultGraph(workspaceid,testid,testResultRasterConfiguration);

        neoLoadAttachments.add(new NeoLoadAttachment( userload,"Hit/s - Error/s - Request average duration","image/png"));


        elementDefinition = new ElementIdDefinition();
        elementDefinition.setId(ELEMENTID_ALL_TRANSACTION);
        elementDefinition.setStatistics(Arrays.asList(ElementIdDefinition.StatisticsEnum.AVG_DURATION));
        testResultRasterConfiguration=new TestResultRasterConfiguration();
        testResultRasterConfiguration.setRasterType(TestResultRasterConfiguration.RasterTypeEnum.PNG);
        testResultRasterConfiguration.setTheme(TestResultRasterConfiguration.ThemeEnum.LIGHT);
        testResultRasterConfiguration.setWidth(600);
        testResultRasterConfiguration.setHeight(600);
        testResultRasterConfiguration.setLegend(true);
        testResultRasterConfiguration.setMultiYAxis(false);
        testResultRasterConfiguration.setXAxisLabel("Time");
        testResultRasterConfiguration.setYAxisLabel("response time ms");
        testResultRasterConfiguration.setElementIds(Arrays.asList(elementDefinition));
        testResultRasterConfiguration.setTitle("Average response time of All Transactions");

        File transaction=resultsApi.getTestResultGraph(workspaceid,testid,testResultRasterConfiguration);
        neoLoadAttachments.add(new NeoLoadAttachment(transaction,"Average response time of All Transactions","image/png"));

        return neoLoadAttachments;
    }

    private String getUserLoadCounterId() throws NeoLoadException {
        try {
            CounterDefinitionArray counterDefinitions =resultsApi.getTestResultMonitors(workspaceid,testid);
            Optional<CounterDefinition> userloadcoutner = counterDefinitions.stream().filter(counterDefinition -> counterDefinition.getName().equalsIgnoreCase(COUNTER_USER_LOAD)).findFirst();
            if(userloadcoutner.isPresent())
                return userloadcoutner.get().getId();
            else
                throw new NeoLoadException("Unable to get the counter id of User load");
        } catch (ApiException e) {
            logger.error("unable to retrieve the id of the counter user load "+e.getResponseBody());
            throw new NeoLoadException("unable to retrieve the id of the counter user load "+e.getResponseBody());
        }
    }

    private TestCycleResource getTestCycle(Long projectid) throws NeoLoadException {
        TestCycleApi testCycleApi=new TestCycleApi(qTestApiClient);
        ReleaseApi releaseApi=new ReleaseApi(qTestApiClient);
        try {

            List<ReleaseWithCustomFieldResource> releaseWithCustomFieldResourceList=releaseApi.getAll(projectid,false);

            Optional<ReleaseWithCustomFieldResource> optionalReleaseWithCustomFieldResource=releaseWithCustomFieldResourceList.stream().filter(releaseWithCustomFieldResource -> releaseWithCustomFieldResource.getName().equalsIgnoreCase(relasename)).findFirst();

            if(optionalReleaseWithCustomFieldResource.isPresent()) {
                Long releaseId=optionalReleaseWithCustomFieldResource.get().getId();

                List<TestCycleResource> testCycleResourceList = testCycleApi.getTestCycles(projectid, releaseId, "release", null);

                Optional<TestCycleResource> testCycleResource = testCycleResourceList.stream().filter(testCycle -> testCycle.getName().equalsIgnoreCase(qtesttestCycle)).findFirst();
                if (testCycleResource.isPresent()) {
                    return testCycleResource.get();
                } else
                    throw new NeoLoadException("No Test Cycle found with the name " + qtesttestCycle);
            }
            else
                throw new NeoLoadException("No Release found with the name " + relasename);

        } catch (QtestApiException e) {
            logger.error("Unable to retrieve the Qtest test cycle "+e.getResponseBody(),e);
            throw new NeoLoadException("Unable to retrieve the Qtest test cycle "+e.getResponseBody());
        }
    }

    private String generateTestNote(ResultsApi resultsApi) throws NeoLoadException {
        try {
            TestResultStatistics statistics=resultsApi.getTestResultStatistics(workspaceid,testid);
            StringBuilder node=new StringBuilder();
            node.append("Test results : "+ generateNlTestResultUrl()+" \n");
            node.append("Statistics of the Test :\n");
            node.append("Average transaction duration :"+String.valueOf(statistics.getTotalTransactionDurationAverage())+"\n");
            node.append("Average request duration :"+String.valueOf(statistics.getTotalRequestDurationAverage())+"\n");
            node.append("Number of errors :"+String.valueOf(statistics.getTotalGlobalCountFailure())+"\n");
            node.append("Number of Transaction in Success:"+String.valueOf(statistics.getTotalTransactionCountSuccess())+"\n");
            node.append("Number of Transaction in Error:"+String.valueOf(statistics.getTotalTransactionCountFailure())+"\n");
            node.append("Total Bytes downloaded/s:"+String.valueOf(statistics.getTotalGlobalDownloadedBytesPerSecond())+"\n");
            node.append("Total requests/s:"+String.valueOf(statistics.getTotalRequestCountPerSecond())+"\n");

            return node.toString();
        }

        catch (ApiException e)
        {
            logger.error("Unable to retrieve NeoloAD results "+e.getResponseBody(),e);
            throw new NeoLoadException("Unable to retrieve NeoloAD results "+e.getResponseBody());
        }
    }


}
