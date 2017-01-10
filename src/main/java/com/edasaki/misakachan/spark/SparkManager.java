package com.edasaki.misakachan.spark;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.source.AbstractSource;

import spark.Request;
import spark.Response;
import spark.Spark;

public class SparkManager {

    private AbstractSource[] sources;

    public SparkManager(AbstractSource[] sources) {
        this.sources = sources;
    }

    public void startWebsever() {
        Spark.port(10032);
        Spark.staticFileLocation("/public");
        Spark.post("/load", this::loadURL);
    }

    public int getPort() {
        return Spark.port();
    }

    private String loadURL(Request req, Response res) {
        String url = req.body();
        JSONObject jo = new JSONObject();
        for (AbstractSource source : sources) {
            if (source.match(url)) {
                //stuff
                jo.put("status", "success");
                jo.put("site", source.getSourceName());
                JSONArray arr = new JSONArray();
                arr.put("http://test.png");
                arr.put("http://test2.png");
                jo.put("urls", arr);
                System.out.println(jo.toString());
                return jo.toString();
            }
        }
        jo.put("status", "failure");
        jo.put("reason", "Invalid URL.");
        return jo.toString();
    }

}
