package com.demo.verticle.standard;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.vertx.MetricsHandler;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrometheusVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(PrometheusVerticle.class);

    private HttpServer server;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new PrometheusVerticle());
    }

    @Override
    public void start() throws Exception {
        ConfigStoreOptions yamlStore = new ConfigStoreOptions().setType("file").setFormat("yaml")
                .setConfig(new JsonObject().put("path", "conf/config.yaml"));
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(yamlStore));

        retriever.getConfig(json -> {
            JsonObject appConfig = json.result();
            int port = appConfig.getInteger("application.monitoring_service.port");

            Router router = Router.router(vertx);
            router.get("/metrics").handler(new MetricsHandler());

            server = vertx.createHttpServer().requestHandler(router).listen(port);
            logger.info("{} port: {} started with current deployment id: {}",
                    this.getClass().getName(), port, Vertx.currentContext().deploymentID());

            MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate("exported");
            CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry));

            Counter uptimeCounter = metricRegistry.counter("uptime");
            Counter downtimeCounter = metricRegistry.counter("downtime");
            Counter homeVisitCount = metricRegistry.counter("home_visit_count");
            vertx.setPeriodic(1_000L, e -> {
                uptimeCounter.inc();
            });

            EventBus eb = vertx.eventBus();
            eb.consumer("page.visit", message -> {
                if (message.body().equals("home")) homeVisitCount.inc();
            });
        });
    }

    @Override
    public void stop(final Future<Void> stopFuture) {
        logger.info("Undeployd verticle {} with status {}", PrometheusVerticle.class.getName(), stopFuture.isComplete());
    }
}
