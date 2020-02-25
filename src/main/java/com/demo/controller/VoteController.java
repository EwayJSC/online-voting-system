package com.demo.controller;

import com.demo.config.MediaType;
import com.demo.util.JsonUtil;
import com.demo.verticle.standard.MainVerticle;
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
public class VoteController {
    private static Logger logger = LoggerFactory.getLogger(CandidateController.class);

    public static void listVotes(RoutingContext context) {
        MainVerticle.getMySQLPool().preparedQuery("SELECT * FROM votes", ar -> {
            if (ar.succeeded()) {
                RowSet<Row> rows = ar.result();
                List<JsonObject> jrows = JsonUtil.rowToJson(rows);
                logger.debug("Votes: {}", jrows);
                context.response().setStatusCode(200).putHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .end(jrows.toString());
            } else {
                logger.warn("Failure: " + ar.cause().getMessage());
                context.response().setStatusCode(500).putHeader("Content-Type", MediaType.TEXT_HTML).end();
            }
        });
    }

    public static void createVote(RoutingContext context) {
        String id = UUID.randomUUID().toString();
        String voterId = UUID.randomUUID().toString();
        String candidateId = context.request().getFormAttribute("candidate_id");
        logger.debug("Vote id: {} voterId: {} candidateId: {}", id, voterId, candidateId);

        MainVerticle.getMySQLPool().preparedQuery("INSERT INTO votes(id, voter_id, candidate_id) VALUES (?,?,?)", Tuple.of(id, voterId, candidateId), ar -> {
            if (ar.succeeded()) {
                context.response().setStatusCode(200).putHeader("Content-Type", MediaType.APPLICATION_JSON).end();
            } else {
                logger.warn("Failure: " + ar.cause().getMessage());
                context.response().setStatusCode(500).putHeader("Content-Type", MediaType.TEXT_HTML).end();
            }
        });
    }
}
