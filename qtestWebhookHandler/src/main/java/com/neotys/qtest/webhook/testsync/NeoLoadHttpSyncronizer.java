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
import com.neotys.qtest.api.client.api.TestLogApi;
import com.neotys.qtest.api.client.api.UserApi;
import com.neotys.qtest.api.client.model.AutomationRequest;
import com.neotys.qtest.api.client.model.AutomationTestLogResource;
import com.neotys.qtest.api.client.model.LoggedUser;
import com.neotys.qtest.api.client.model.TestSuite;
import com.neotys.qtest.webhook.Logger.NeoLoadLogger;
import com.neotys.qtest.webhook.common.NeoLoadException;
import com.neotys.qtest.webhook.datamodel.NeoLoadRequirement;
import com.neotys.qtest.webhook.datamodel.QtestContext;
import com.neotys.qtest.webhook.datamodel.QtestUser;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;


import java.util.*;
import java.util.stream.Collectors;

import static com.neotys.qtest.webhook.common.Constants.*;


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

    private long testEnd;
    private String status;

    private boolean ssl;
    private UUID uipathprojectid;
    private UUID uipathcaseid;
    private String qTestProjectName;
    private QtestApiClient qTestApiClient;
    private String qtestAPIurl;
    private String neoloadUIPathTestCaseName;
    private Optional<String> qtestApiToken;

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




    }

    private String generateNlTestResultUrl()
    {
        return HTTPS+neoload_Web_Url.get()+NEOLAOD_WEB_URL+testid+NEOLAOD_WEB_LASTPART_URL;
    }





    private Future<String> getQtestProject()  {
        Future<String> future=Future.future();

        resultsApi =new ResultsApi(apiClient);
        try {
            TestResultDefinition testResultDefinition=resultsApi.getTestResult(workspaceid,testid);
            String description = testResultDefinition.getDescription();
            projectName=testResultDefinition.getProject();
            scenarioName=testResultDefinition.getScenario();
            neoloadUIPathTestCaseName=NEOLOAD+ " "+ testResultDefinition.getProject()+"_"+testResultDefinition.getScenario();
            if(description!=null)
            {
                if(description.isEmpty()||description.trim().isEmpty())
                {

                    logger.debug("Description is currently empty--let's wait");
                    Thread.sleep(2000);
                    logger.debug("The context has not been sent yet...");
                    testResultDefinition=resultsApi.getTestResult(workspaceid,testid);
                    description = testResultDefinition.getDescription();
                }

                logger.debug("Converting Description into java Object + "+description);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory()).create();
                QtestContext uipathContext = gson.fromJson(description, QtestContext.class);
                qTestProjectName =uipathContext.getProjectName();
                future.complete(qTestProjectName);
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
        } catch (InterruptedException e) {
            logger.error("Forced to wait to get the description  "+e.getMessage(),e);
            future.fail( new NeoLoadException("Forced to wait to get the description  "+e.getMessage()));
        }
        return future;
    }



    private void generateApiUrl()
    {
        if(neoload_API_Url.isPresent()&&neoload_API_PORT.isPresent())
        {
            if(neoload_API_PORT.get().equalsIgnoreCase(DEFAULT_NL_API_PORT))
                if (!neoload_API_Url.get().contains(API_URL_VERSION))
                    neoload_API_Url = Optional.of(neoload_API_Url.get() + API_URL_VERSION);

                else
                {
                    if (!neoload_API_Url.get().contains(API_URL_VERSION))
                        neoload_API_Url = Optional.of(neoload_API_Url.get() + ":" + neoload_API_PORT.get() + API_URL_VERSION);
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
                throw new NeoLoadException(SECRET_QTEST_APIPATH+"environment varaible is missing");



        } else
            throw new NeoLoadException("The APi host name of your UiPath needs to be defined");
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
        Future<String> futureproject= getQtestProject();
        List<NeoLoadRequirement> neoLoadRequirementList=new ArrayList<>();
        futureproject.setHandler(stringAsyncResult -> {
            if(stringAsyncResult.succeeded())
            {
                qTestProjectName=stringAsyncResult.result();
                try {
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
                                neoLoadRequirementList.add(new NeoLoadRequirement(slaGlobalIndicatorDefinition, projectName));
                            }
                            catch (NeoLoadException e) {
                                logger.error("Unable to convert the sal into a neolaod requiremt", e);
                            }

                        });
                        arrayOfSLAPerIntervalDefinition.stream().forEach(slaGlobalIndicatorDefinition -> {
                            try {
                                neoLoadRequirementList.add(new NeoLoadRequirement(slaGlobalIndicatorDefinition, projectName));
                            } catch (NeoLoadException e) {
                                logger.error("Unable to convert the sal into a neolaod requiremt", e);
                            }


                        });
                        arrayOfSLAPerTestResultDefinition.stream().forEach(slaGlobalIndicatorDefinition -> {
                            try {
                                neoLoadRequirementList.add(new NeoLoadRequirement(slaGlobalIndicatorDefinition, projectName));
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

                        TestLogApi testLogApi= new TestLogApi(qTestApiClient);




                        TestSuite testSuite =new TestSuite();

                        AutomationTestLogResource testLogResource=new AutomationTestLogResource();
                        testLogResource.setExeEndDate( OffsetDateTime.ofInstant(Instant.ofEpochMilli(testResultDefinition.getEndDate()), ZoneId.systemDefault()));
                        testLogResource.setExeStartDate( OffsetDateTime.ofInstant(Instant.ofEpochMilli(testResultDefinition.getStartDate()), ZoneId.systemDefault()));
                        testLogResource.setStatus(testResultDefinition.getQualityStatus().getValue());
                        testLogResource.setSystemName(NEOLOAD);
                        testLogResource.setName(NEOLOAD+" Project:"+testResultDefinition.getProject()+ " Scenario:"+testResultDefinition.getScenario());
                        testLogResource.setNote(generateTestNote(resultsApi));
                        testLogResource.setOrder(new Long(0));
                        testLogResource.setBuildUrl(generateNlTestResultUrl());

                        testLogResource.setTestStepLogs(neoLoadRequirementList.stream().map(neoLoadRequirement -> { return  neoLoadRequirement.toTestLogResult();}).map(testStepLogResult -> {
                            return testStepLogResult.toAutomationTestStepLog();
                        }).collect(Collectors.toList()));




                        AutomationTestLogResource automationTestLogResource = testLogApi.submitAutomationLog(Long.parseLong(projectName),testLogResource,null,null,null,false,false,null,String.valueOf(qtestUser.getUserid()));
                        automationTestLogResource.getLinks().stream().forEach(link ->
                        {
                            logger.debug("Link found "+link.toString());
                        });
                        future.complete(true);
                    }
                }
                catch (ApiException e) {
                    logger.error("Unable to retrieve NeoloAD results "+e.getResponseBody(),e);
                    future.fail(e);
                } catch (QtestApiException e) {
                    logger.error("Unable to retrieve Uipath data "+e.getResponseBody(),e);
                    future.fail(e);
                } catch (NeoLoadException e) {
                    logger.error("Neoload Exeption "+e.getMessage(),e);
                    future.fail(e);
                }
            }
            else
            {
                logger.error("Impossible to retrieve qtest context",stringAsyncResult.cause());
                future.fail(stringAsyncResult.cause());
            }

        });



        return future;

    }


    private String generateTestNote(ResultsApi resultsApi) throws NeoLoadException {
        try {
            TestResultStatistics statistics=resultsApi.getTestResultStatistics(workspaceid,testid);
            StringBuilder node=new StringBuilder();

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
