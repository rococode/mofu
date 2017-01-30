package com.edasaki.misakachan.spark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.chapter.Chapter;
import com.edasaki.misakachan.chapter.Page;
import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.source.SearchResult;
import com.edasaki.misakachan.source.SearchResultSet;
import com.edasaki.misakachan.utils.logging.M;

import spark.Request;
import spark.Response;
import spark.Spark;

public class SparkManager {

    private AbstractSource[] sources;

    public SparkManager(AbstractSource[] sources) {
        this.sources = sources;
    }

    public void startWebserver() {
        Spark.port(10032);
        Spark.staticFileLocation("/public");
        Spark.post("/load", this::loadRequestedURL);
        Spark.post("/search", this::search);
        Spark.get("/changelog", this::loadChangelog);
        Spark.post("/lookup", this::lookup);
    }

    public int getPort() {
        return Spark.port();
    }

    private String lookup(Request req, Response res) {
        String url = req.body();
        JSONObject jo = new JSONObject();
        for (AbstractSource source : sources) {
            if (source.matchInfo(url)) {
                jo.put("status", "success");
                jo.put("series", source.getSeries(url).getSeriesObject());
                return jo.toString();
            }
        }
        jo.put("status", "failure");
        jo.put("reason", "Invalid URL.");
        return jo.toString();
    }

    private String loadChangelog(Request req, Response res) {
        JSONObject jo = new JSONObject();
        List<String> ls = Misaka.instance().changelog.getChangelog();
        JSONArray lines = new JSONArray();
        if (ls == null || ls.size() == 0) {
            lines.put("Failed to fetch latest changelog - probably not a big deal.");
            jo.put("lines", lines);
        } else {
            for (String s : ls)
                lines.put(s);
            jo.put("lines", lines);
        }
        return jo.toString();
    }

    private String loadRequestedURL(Request req, Response res) {
        String url = req.body();
        JSONObject jo = new JSONObject();
        for (AbstractSource source : sources) {
            if (source.match(url)) {
                M.debug("Matched " + source);
                jo.put("type", "url");
                jo.put("site", source.getSourceName());
                Chapter chapter = source.getChapter(url);
                jo.put("name", chapter.getChapterName());
                jo.put("pagecount", chapter.getPageCount());
                JSONArray urls = new JSONArray();
                for (Page p : chapter.getPages())
                    urls.put(p.getURL());
                jo.put("urls", urls);
                //                M.debug("Generated JSON " + jo.toString());
                return jo.toString();
            }
        }
        return search(req, res);
    }

    private String search(Request req, Response res) {
        String searchPhrase = req.body();
        JSONObject jo = new JSONObject();
        jo.put("type", "search");
        jo.put("searchPhrase", searchPhrase);
        List<Callable<SearchResultSet>> searches = new ArrayList<Callable<SearchResultSet>>();
        for (AbstractSource source : sources) {
            SearchAction sa = source.getSearch();
            searches.add(() -> {
                return sa.search(searchPhrase);
            });
        }
        List<Future<SearchResultSet>> futures = MultiThreadTaskManager.queueTasks(searches);
        MultiThreadTaskManager.wait(futures);
        JSONArray results = new JSONArray();
        for (Future<SearchResultSet> future : futures) {
            try {
                SearchResultSet set = future.get();
                JSONObject src = new JSONObject();
                src.put("sourceName", set.getSource());
                JSONArray linkArr = new JSONArray();
                for (SearchResult sr : set.getResults()) {
                    JSONObject sro = new JSONObject();
                    sro.put("title", sr.title);
                    sro.put("alt", sr.getAltNames());
                    sro.put("url", sr.url);
                    linkArr.put(sro);
                }
                src.put("links", linkArr);
                results.put(src);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        jo.put("results", results);
        M.debug(jo.toString(4));
        return jo.toString();
    }

}
