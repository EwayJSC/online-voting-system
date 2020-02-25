package com.demo.util;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tu Pham Phuong - phamptu@gmail.com on 2/25/20.
 */
public class JsonUtil {
    public static List<JsonObject> rowToJson(RowSet<Row> rows) {
        List<JsonObject> list = new ArrayList<>();

        for (Row row : rows) {
            int size = row.size();
            JsonObject jsonObject = new JsonObject();
            for (int i = 0; i < size; i++) {
                String columnName = row.getColumnName(i);
                Object value = row.getValue(i);
                if(value != null){
                    if(value instanceof LocalDateTime){
                        jsonObject.put(columnName, ((LocalDateTime)value).atZone(ZoneId.systemDefault()).toInstant());
                    } else jsonObject.put(columnName, row.getValue(i));
                }
            }
            list.add(jsonObject);
        }

        return list;
    }
}
