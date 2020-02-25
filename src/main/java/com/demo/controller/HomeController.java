package com.demo.controller;

import com.demo.config.MediaType;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tu Pham Phuong - phamptu@gmail.com on 2/25/20.
 */
public class HomeController {
    private static Logger logger = LoggerFactory.getLogger(HomeController.class);

    public static void getHomePage(RoutingContext context) {
        logger.info("Handler homepage with thread: " + Thread.currentThread().getName());

        EventBus eb = context.vertx().eventBus();
        eb.publish("page.visit", "home");

        context.response().setStatusCode(200).putHeader("Content-Type", MediaType.TEXT_HTML)
                .end("Hello World from Vert.x-Web!");
    }
}
