package com.neotys.qtest.webhook;


import com.neotys.qtest.webhook.httpserver.WebHookReceiver;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.concurrent.TimeUnit;

public class NeoloadTestResultSync {

    private static final int MAX=24;
    public static void main(String[] args) {

        VertxOptions options=new VertxOptions().setMaxWorkerExecuteTime(MAX).setMaxWorkerExecuteTimeUnit(TimeUnit.HOURS).setWarningExceptionTimeUnit(TimeUnit.HOURS).setWarningExceptionTime(1);
        Vertx.vertx(options).deployVerticle(new WebHookReceiver());


    }
}
