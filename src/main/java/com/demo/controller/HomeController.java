package com.demo.controller;

import com.demo.config.MediaType;
import com.demo.util.ResponseUtils;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * Created by Tu Pham Phuong - phamptu@gmail.com on 2/25/20.
 */
public class HomeController implements Handler<RoutingContext> {
    private static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Override
    public void handle(RoutingContext context) {
        final String runtimeMXBeanName = ManagementFactory.getRuntimeMXBean().getName();
        final String threadName = Thread.currentThread().getName();

        EventBus eb = context.vertx().eventBus();
        eb.publish("page.visit", "home");

        context.response().setStatusCode(200).putHeader("Content-Type", MediaType.TEXT_HTML)
                .end("Hello World from Vert.x-Web from " + Thread.currentThread().getName());
    }
}
