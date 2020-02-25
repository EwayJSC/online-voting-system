package com.demo.controller;

import com.demo.config.MediaType;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tu Pham Phuong - phamptu@gmail.com on 2/25/20.
 */
public class BlockingController {
    private static Logger logger = LoggerFactory.getLogger(BlockingController.class);

    public static void createBlockingScenarios(RoutingContext context) {
        context.vertx().setTimer(1,id->{
            //Blocking the Vert.xe vent loop
            try{
                Thread.sleep(10000);
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        context.response().setStatusCode(200).putHeader("Content-Type", MediaType.TEXT_HTML)
                .end("Welcome to blocking world");
    }

    public static void createNonBlockingScenarios(RoutingContext context) {
        context.vertx().executeBlocking(promise -> {
            // Call some blocking API that takes a significant amount of time to return
            context.vertx().setTimer(1,id->{
                //Blocking the Vert.xe vent loop
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            });

            String result = "Non Blocking";
            promise.complete(result);
        }, res -> {
            logger.info("The result is: " + res.result());
        });

        context.response().setStatusCode(200).putHeader("Content-Type", MediaType.TEXT_HTML)
                .end("Welcome to non blocking world");
    }
}
