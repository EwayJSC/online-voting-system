package com.demo.controller;

import com.demo.config.MediaType;
import com.demo.util.JsonUtil;
import com.demo.verticle.standard.MainVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Created by Tu Pham Phuong - phamptu@gmail.com on 2/25/20.
 */
public class CandidateController {
    private static Logger logger = LoggerFactory.getLogger(CandidateController.class);

    public static void listCandidates(RoutingContext context) {
        MainVerticle.getMySQLPool().preparedQuery("SELECT * FROM candidates", ar -> {
            if (ar.succeeded()) {
                RowSet<Row> rows = ar.result();
                List<JsonObject> jrows = JsonUtil.rowToJson(rows);
                logger.debug("Candidates: {}", jrows);
                context.response().setStatusCode(200).putHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .end(jrows.toString());
            } else {
                logger.warn("Failure: " + ar.cause().getMessage());
                context.response().setStatusCode(500).putHeader("Content-Type", MediaType.TEXT_HTML).end();
            }
        });
    }

    public static void getCandidate(RoutingContext context) {
        String candidateId = context.request().getParam("candidate_id");

        MainVerticle.getMySQLPool().preparedQuery("SELECT * FROM candidates where id = ?", Tuple.of(candidateId), ar -> {
            if (ar.succeeded()) {
                RowSet<Row> rows = ar.result();
                List<JsonObject> jrows = JsonUtil.rowToJson(rows);

                if (jrows.size() > 0) {
                    JsonObject jrow = jrows.get(0);
                    logger.debug("Candidate id: {} name: {}", jrow.getString("id"), jrow.getString("name"));
                    context.response().setStatusCode(200).putHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .end(jrows.get(0).toString());
                } else context.response().setStatusCode(200).putHeader("Content-Type", MediaType.TEXT_HTML)
                        .end("{}");
            } else {
                logger.warn("Failure: " + ar.cause().getMessage());
                context.response().setStatusCode(500).putHeader("Content-Type", MediaType.TEXT_HTML).end();
            }
        });
    }

    public static void createCandidate(RoutingContext context) {
        String id = UUID.randomUUID().toString();
        String name = context.request().getFormAttribute("name");
        logger.debug("Candidate id: {} name: {}", id, name);

        MainVerticle.getMySQLPool().preparedQuery("INSERT INTO candidates(id, name) VALUES (?,?)", Tuple.of(id, name), ar -> {
            if (ar.succeeded()) {
                context.response().setStatusCode(200).putHeader("Content-Type", MediaType.APPLICATION_JSON).end();
            } else {
                logger.warn("Failure: " + ar.cause().getMessage());
                context.response().setStatusCode(500).putHeader("Content-Type", MediaType.TEXT_HTML).end();
            }
        });
    }
}
