package com.demo;

import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;

/**
 * Created by Tu Pham Phuong - phamptu@gmail.com on 2/23/20.
 */
public class Global {
    private static JsonObject appConfig;

    private static MySQLPool mySQLPool;

    public static MySQLPool getMySQLPool() {
        return mySQLPool;
    }

    public static void setMySQLPool(MySQLPool mySQLPool) {
        Global.mySQLPool = mySQLPool;
    }

    public static JsonObject getAppConfig() {
        return appConfig;
    }

    public static void setAppConfig(JsonObject appConfig) {
        Global.appConfig = appConfig;
    }
}
