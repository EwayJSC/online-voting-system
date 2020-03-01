package com.demo;

import com.demo.verticle.standard.MainVerticle;
import com.demo.verticle.standard.PrometheusVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tu Pham Phuong - phamptu@gmail.com on 2/21/20.
 */
public class AppRunner {
    private static Logger logger = LoggerFactory.getLogger(AppRunner.class);

    public static void main(String[] args) {
        logger.info("\n\n\n ------ Runner starting up ------");
        VertxOptions vertxOptions = new VertxOptions();
        Vertx vertx = Vertx.vertx(vertxOptions);

        ConfigStoreOptions yamlStore = new ConfigStoreOptions().setType("file").setFormat("yaml")
                .setConfig(new JsonObject().put("path", "conf/config.yaml"));
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(yamlStore));

        retriever.getConfig(json -> {
            // Scale MainVerticle to 100 instances
            vertx.deployVerticle(MainVerticle.class.getName(), new DeploymentOptions().setInstances(10), res -> {
                if (res.succeeded()) logger.info("MainVerticle deployment id is: " + res.result());
                else logger.info("MainVerticle deployment failed!");
            });

            vertx.deployVerticle(PrometheusVerticle.class.getName(), new DeploymentOptions().setInstances(1), res -> {
                if (res.succeeded()) logger.info("PrometheusVerticle deployment id is: " + res.result());
                else logger.info("PrometheusVerticle deployment failed!");
            });

            Context context = vertx.getOrCreateContext();
            logger.info("Vert deploymentIDs: {} isClustered: {}", vertx.deploymentIDs(), vertx.isClustered());
            logger.info("Context instance count: {} deploymentID: {}", context.getInstanceCount(), context.deploymentID());
        });
    }
}
