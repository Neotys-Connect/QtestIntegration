package com.neotys.qtest.context.customActions;

import com.google.common.base.Optional;

import com.google.gson.Gson;
import com.neotys.action.result.ResultFactory;
import com.neotys.ascode.api.v3.client.ApiClient;
import com.neotys.ascode.api.v3.client.ApiException;
import com.neotys.ascode.api.v3.client.api.ResultsApi;
import com.neotys.ascode.api.v3.client.model.TestResultUpdateRequest;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.qtest.context.datamodel.QtestContext;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;

public class SendQtestContextActionEngine implements ActionEngine {


    private static final String STATUS_CODE_INVALID_PARAMETER = "NL-QTEST-SENDCONTEXT_ACTION-01";
    private static final String STATUS_CODE_TECHNICAL_ERROR = "NL-QTEST-SENDCONTEXT_ACTION-02";
    private static final String STATUS_CODE_BAD_CONTEXT = "NL-QTEST-SENDCONTEXT_ACTION-03";
    private static final String NLWEB_VERSION ="v3" ;

    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();

        final Map<String, Optional<String>> parsedArgs;
        try {
            parsedArgs = parseArguments(parameters, QtestContextOption.values());
        } catch (final IllegalArgumentException iae) {
            return ResultFactory.newErrorResult(context, STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
        }


        final String project = parsedArgs.get(QtestContextOption.ProjectName.getName()).get();
        final String testcycle=parsedArgs.get(QtestContextOption.TestCycle.getName()).get();
        final String releasename=parsedArgs.get(QtestContextOption.ReleaseName.getName()).get();
        final Optional<String> enableDefectCreation = parsedArgs.get(QtestContextOption.EnableDefectCreation.getName());


        boolean defectcreation;
        if(!enableDefectCreation.isPresent())
            defectcreation=false;
        else
        {
            defectcreation=Boolean.valueOf(enableDefectCreation.get());
        }
        final Logger logger = context.getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug("Executing " + this.getClass().getName() + " with parameters: "
                    + getArgumentLogString(parsedArgs, QtestContextOption.values()));
        }


        try {
            Optional<HashMap<String,String>> customFields;
            Optional<List<String>> optinalListofTags;
            ApiClient client=new ApiClient();
            ResultsApi resultsApi=new ResultsApi(client);
            client.setBasePath(getBasePath(context));
            client.setApiKey(context.getAccountToken());
            Gson gson=new Gson();


            TestResultUpdateRequest testUpdateRequest =new TestResultUpdateRequest();



            QtestContext uiPathContext=new QtestContext(project,testcycle,releasename,defectcreation);
            String description=gson.toJson(uiPathContext);

            testUpdateRequest.setDescription(description);
            resultsApi.updateTestResult(testUpdateRequest,context.getWorkspaceId(),context.getTestId());
            appendLineToStringBuilder(responseBuilder, description);

        }catch (ApiException e) {
            return ResultFactory.newErrorResult(context, STATUS_CODE_TECHNICAL_ERROR, "Qtest Send context Api Error - API Exception "+e.getResponseBody(), e);
        }
        catch (Exception e)
        {
            return ResultFactory.newErrorResult(context, STATUS_CODE_TECHNICAL_ERROR, "Qtest Send context technical Error  ", e);

        }


        sampleResult.setRequestContent(requestBuilder.toString());
        sampleResult.setResponseContent(responseBuilder.toString());


        return sampleResult;
    }

    private String getBasePath(final Context context) {
        final String webPlatformApiUrl = context.getWebPlatformApiUrl();
        final StringBuilder basePathBuilder = new StringBuilder(webPlatformApiUrl);
        if(!webPlatformApiUrl.endsWith("/")) {
            basePathBuilder.append("/");
        }
      //  basePathBuilder.append(NLWEB_VERSION + "/");
        return basePathBuilder.toString();
    }

    private void appendLineToStringBuilder(final StringBuilder sb, final String line) {
        sb.append(line).append("\n");
    }

    /**
     * This method allows to easily create an error result and log exception.
     */
    private static SampleResult getErrorResult(final Context context, final SampleResult result, final String errorMessage, final Exception exception) {
        result.setError(true);
        result.setStatusCode("NL-QTEST_ERROR");
        result.setResponseContent(errorMessage);
        if (exception != null) {
            context.getLogger().error(errorMessage, exception);
        } else {
            context.getLogger().error(errorMessage);
        }
        return result;
    }

    @Override
    public void stopExecute() {
        // TODO add code executed when the test have to stop.

    }
}