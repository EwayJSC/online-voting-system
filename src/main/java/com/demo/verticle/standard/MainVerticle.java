package com.demo.verticle.standard;

import com.demo.Global;
import com.demo.controller.BlockingController;
import com.demo.controller.CandidateController;
import com.demo.controller.HomeController;
import com.demo.controller.VoteController;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private HttpServer server;
    private static MySQLPool mySQLPool;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    public static MySQLPool getMySQLPool() {
        return mySQLPool;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        ConfigStoreOptions yamlStore = new ConfigStoreOptions().setType("file").setFormat("yaml")
                .setConfig(new JsonObject().put("path", "conf/config.yaml"));
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(yamlStore));

        retriever.getConfig(json -> {
            JsonObject appConfig = json.result();
            int port = appConfig.getInteger("application.main_service.port");

            String mysqlHost = appConfig.getString("mysql.host");
            int mysqlPort = appConfig.getInteger("mysql.port");
            String mysqlDb = appConfig.getString("mysql.db");
            String mysqlUser = appConfig.getString("mysql.user");
            String mysqlPass = appConfig.getString("mysql.password");
            int mysqlPoolSize = appConfig.getInteger("mysql.pool_size");

            // Init MySQLPool
            MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(mysqlPort).setHost(mysqlHost)
                    .setDatabase(mysqlDb).setUser(mysqlUser).setPassword(mysqlPass);
            PoolOptions poolOptions = new PoolOptions().setMaxSize(mysqlPoolSize);
            mySQLPool = MySQLPool.pool(vertx, connectOptions, poolOptions);

            // Get a connection from the pool
            mySQLPool.getConnection(ar1 -> {
                if (ar1.succeeded()) {
                    logger.info("Connected to MySQL instance host: {}", mysqlHost);
                    Global.setMySQLPool(mySQLPool);
                } else logger.info("Could not connect to MySQL instance host: {} cause: {}" + ar1.cause().getMessage());
            });

            Router router = Router.router(vertx);
            // Register a body handler to be able to easily get the body from the request
            router.route().handler(BodyHandler.create());

            // Home route
            router.route(HttpMethod.GET, "/").handler(HomeController::getHomePage);

            // Blocking route
            router.route(HttpMethod.GET, "/bocking").handler(BlockingController::createBlockingScenarios);
            router.route(HttpMethod.GET, "/non_blocking").handler(BlockingController::createNonBlockingScenarios);

            // Candidate route
            router.route(HttpMethod.GET, "/candidates/:candidate_id").handler(CandidateController::getCandidate);
            router.route(HttpMethod.GET, "/candidates").handler(CandidateController::listCandidates);
            router.route(HttpMethod.POST, "/candidates").handler(CandidateController::createCandidate);

            // Vote route
            router.route(HttpMethod.GET, "/votes").handler(VoteController::listVotes);
            router.route(HttpMethod.POST, "/votes").handler(VoteController::createVote);

            // Admin route
            server = vertx.createHttpServer().requestHandler(router).listen(port);
            logger.info("{} port: {} started with current deployment id: {}",
                    this.getClass().getName(), port, Vertx.currentContext().deploymentID());
        });
    }
}
