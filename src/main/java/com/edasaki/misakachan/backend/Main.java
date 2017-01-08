package com.edasaki.misakachan.backend;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edasaki.misakachan.backend.source.AbstractSource;
import com.edasaki.misakachan.backend.source.english.MangaHere;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Main {

    protected static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, URISyntaxException {
        startWeb();
        // testing eclipse git setup
        //        String url = "http://www.mangahere.co/manga/red_storm/c224/2.html";
        //        MangaHere mh = new MangaHere();
        //        if (mh.match(url)) {
        //            mh.getChapter(url);
        //        }
    }

    public static ModelAndView reader(Request req, Response res) {
        Map<String, Object> params = new HashMap<>();
        logger.debug("body: " + req.body());
        logger.debug(req.queryString());
        logger.debug(req.queryParams("url"));
        logger.debug(req.queryParams("name"));
        params.put("name", req.queryParams("name"));
        return new ModelAndView(params, "reader");
    }

    private static final AbstractSource SOURCES[] = {
            new MangaHere()
    };

    public static String loadURL(Request req, Response res) {
        String url = req.body();
        JSONObject jo = new JSONObject();
        for (AbstractSource source : SOURCES) {
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

    public static void startWeb() {
        Spark.port(10032);
        Spark.staticFileLocation("/public");
        Spark.staticFiles.expireTime(5);
        Spark.post("/load", Main::loadURL);
    }

    public static void launchBrowser() {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI("http://127.0.0.1:10032"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

}