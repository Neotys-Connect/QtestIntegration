package com.neotys.qtest.webhook.common;

public class Constants {

    public static final String HEALTH_PATH="/health";
    public static final String WEBHOOKPATHSTART ="/webhookstart";
    public static final String WEBHOOKPATHEND ="/webhookend";
    public static final String DEFAULT_NL_SAAS_API_URL="neoload-api.saas.neotys.com";
    public static final String DEFAULT_NL_API_PORT="443";
    public static final String DEFAULT_NL_WEB_API_URL="neoload.saas.neotys.com";
    public static final String API_URL_VERSION="/v3";
    public static final String UIPATH_DEFAULT_PATH="/api";
    public static final String TESTID_KEY="testid";
    public static final String WORKSPACEID_KEY="workspaceid";
    public static final String OVERVIEW_PICTURE_KEY="url_graph_overview";
    public static final String SECRET_API_TOKEN="NL_API_TOKEN";
    public static final String SECRET_NL_WEB_HOST="NL_WEB_HOST";
    public static final String SECRET_SSL="ssl";
    public static final String SECRET_NL_API_HOST="NL_API_HOST";
    public static final String SECRET_PORT="PORT";
    public static String LOGING_LEVEL_KEY="logging-level";
    public static int HTTP_PORT=8080;
    public static final String SECRET_QTEST_HOST ="QtestApiHost";
    public static final String SECRET_QTEST_PORT ="QtestApiPort";
    public static final String SECRET_QTEST_APIPATH ="QtestAPiPath";

    public static final String SECRET_QTEST_APITOKEN ="QtestAPiToken";

    public static final String SECRET_QTEST_TENANTID="UiPathTenantID";
    public static final String SECRET_QTEST_USERNAME="UiPathUUsername";
    public static final String SECRET_QTEST_PASSWORD="UiPathPassword";



    public static final String SECRET_NL_API_PORT="NL_API_PORT";
    public static final String DEFAULT_CLOUD_PORT="443";

    public static final String NEOLOAD_ACTION_TYPE="NEOLAOD_SLA";
    public static final String SLA_TYPE_PERINTERVAL="SlaPerTimeInterval";
    public static final String SLA_TYPE_PERTEST="SlaPerRun";
    public static final String SLA_TYPE_GLOBAL="SlaGlobal";
    public static final String FIELDNAME_PROJECT="project";
    public static final String PERFORMANCE="Performance";
    public static final String DEFAULT_MANAGED_PORT="80";
    public static final String NEOLOAD="neoload";

    public static final String HTTPS="https://";
    public static final String NEOLAOD_WEB_URL="/#!result/";
    public static final String NEOLAOD_WEB_LASTPART_URL="/overview";


    //-----SLA Status
    public static final String NEOLOAD_PASS_STATUS="PASSED";
    public static final String NEOLOAD_FAIL_STATUS="FAILED";
    public static final String JIRA_PASS_STATUS="PASS";
    public static final String JIRA_FAIL_STATUS="FAIL";

    public static final String ISSUE_TYPE="Test Execution";

    public static final String CLOUD_ROBOT_PARAMETERNAME="results";
    public static final String ONPREM_ROBOT_PARAMETERNAME="file";


    //---neoload web reference
    public static final String COUNTER_USER_LOAD="User Load";
    public static final String ELEMENTID_ALL_REQUTEST="all-requests";
    public static final String ELEMENTID_ALL_TRANSACTION="all-transactions";
}
