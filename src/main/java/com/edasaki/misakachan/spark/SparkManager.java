package com.edasaki.misakachan.spark;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.chapter.Chapter;
import com.edasaki.misakachan.chapter.Page;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.utils.logging.M;

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
        Spark.post("/load", this::loadRequestedURL);
    }

    public int getPort() {
        return Spark.port();
    }

    private String loadRequestedURL(Request req, Response res) {
        String url = req.body();
        JSONObject jo = new JSONObject();
        for (AbstractSource source : sources) {
            if (source.match(url)) {
                M.debug("Matched " + source);
                jo.put("status", "success");
                jo.put("site", source.getSourceName());
                Chapter chapter = source.getChapter(url);
                jo.put("name", chapter.getChapterName());
                jo.put("pagecount", chapter.getPageCount());
                JSONArray urls = new JSONArray();
                for (Page p : chapter.getPages())
                    urls.put(p.getURL());
                jo.put("urls", urls);
                M.debug("Generated JSON " + jo.toString());
                return jo.toString();
            }
        }
        jo.put("status", "failure");
        jo.put("reason", "Invalid URL.");
        return jo.toString();
    }

}
