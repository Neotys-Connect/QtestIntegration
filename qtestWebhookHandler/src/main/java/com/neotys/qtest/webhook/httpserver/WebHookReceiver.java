package com.neotys.qtest.webhook.httpserver;

import com.google.gson.JsonSyntaxException;

import com.neotys.qtest.webhook.Logger.NeoLoadLogger;
import com.neotys.qtest.webhook.common.NeoLoadException;
import com.neotys.qtest.webhook.testsync.NeoLoadHttpSyncronizer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;

import static com.neotys.qtest.webhook.common.Constants.*;


public class WebHookReceiver extends AbstractVerticle {

    private HttpServer server;
    private NeoLoadLogger loadLogger;
    int httpPort;
    private Vertx rxvertx;
    private HashMap<String,String> testidStringStringHashMap;

    public void start() {
        testidStringStringHashMap=new HashMap<>();
        loadLogger=new NeoLoadLogger(this.getClass().getName());
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post(WEBHOOKPATHEND).handler(this::endwebhook);
        router.get(HEALTH_PATH).handler(this::getHealth);
        String port=System.getenv(SECRET_PORT);
        if(port==null)
        {
            httpPort=HTTP_PORT;
        }
        else
        {
            httpPort=Integer.parseInt(port);
        }
        server = vertx.createHttpServer();

        server.requestHandler(router::accept);
        server.listen(httpPort);
    }

    private void endwebhook(RoutingContext routingContext) {
        if(routingContext.getBody()==null) {
            loadLogger.error("Technical error - there is no payload" );
            routingContext.response().setStatusCode(500).end("Technical error - there is no payload");
            return;
        }
        JsonObject body=routingContext.getBodyAsJson();
        if(body.containsKey(TESTID_KEY)) {
            String testid = body.getString(TESTID_KEY);
            String workspaceid = body.getString(WORKSPACEID_KEY);

            loadLogger.setTestid(testid);
            loadLogger.debug("Received following payload "+ body.toString());

            loadLogger.debug("Received Webhook with testid  " + testid + "worspaceid  "+workspaceid);

            //----is test id currently processed?-----
            if (testidStringStringHashMap.containsKey(testid)) {
                loadLogger.info("The test execution is currently in process");
                routingContext.response().setStatusCode(200).end("This test result is currently in process of beein synchronized in Qtest");
            } else {

                try {
                    testidStringStringHashMap.put(testid,testid);
                    NeoLoadHttpSyncronizer httpSyncronizert = new NeoLoadHttpSyncronizer(testid, workspaceid,  vertx);

                    Future<Boolean> booleanFuture = httpSyncronizert.endQTestExecution();
                    booleanFuture.setHandler(booleanAsyncResult -> {
                        if (booleanAsyncResult.succeeded()) {
                            routingContext.response().setStatusCode(200)
                                    .end("Test execution completely synchronise with Qtest");
                            testidStringStringHashMap.remove(testid);
                        } else {
                            routingContext.response().setStatusCode(500)
                                    .end("Issue to synchronize the test with Qtest");
                        }
                    });


                } catch (NeoLoadException e) {
                    loadLogger.error("NeoLoadExecption : " + e.getMessage(), e);
                    routingContext.response().setStatusCode(500).end(e.getMessage());
                } catch (JsonSyntaxException e) {
                    loadLogger.error("JsonSyntaxException error " + e.getMessage());
                    e.printStackTrace();
                    routingContext.response().setStatusCode(500).end(e.getMessage());
                } catch (Exception e) {
                    loadLogger.error("Technical error " + e.getMessage());
                    e.printStackTrace();
                    routingContext.response().setStatusCode(500).end(e.getMessage());
                }
            }
        }
        else
        {
            if(routingContext.getBody()==null) {
                loadLogger.error("Technical error - there is no testid in the payload" );
                routingContext.response().setStatusCode(420).end("Technical error - there is no testid in the payload");
                return;
            }
        }
    }

    private void getHealth(RoutingContext routingContext) {
        routingContext.response().setStatusCode(200).end("Health Check status OK");
    }


}
