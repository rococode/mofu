package com.edasaki.misakachan.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.persistence.ChapterDownloader;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.source.SearchResult;
import com.edasaki.misakachan.source.SearchResultSet;
import com.edasaki.misakachan.utils.MCacheUtils;
import com.edasaki.misakachan.utils.logging.M;

import spark.Request;
import spark.Response;
import spark.Spark;

public class SparkManager {

    public AbstractSource[] sources;

    private static final Map<String, String> cachedURLToImage = new HashMap<String, String>();

    private List<SearchResultSet> lastSearchResults;

    public SparkManager(AbstractSource[] sources) {
        this.sources = sources;
        this.lastSearchResults = new ArrayList<SearchResultSet>();
    }

    public void startWebserver() {
        Spark.port(10032);
        Spark.staticFileLocation("/public");
        Spark.post("/load", this::loadRequestedURL);
        Spark.post("/search", this::search);
        Spark.get("/changelog", this::loadChangelog);
        Spark.post("/lookup", this::lookup);
        Spark.post("/fetchResultImages", this::fetchResultImages);
        Spark.post("/download", this::downloadSingle);
        Spark.post("/downloadbatch", this::downloadByURL);
    }

    public int getPort() {
        return Spark.port();
    }

    private String downloadByURL(Request req, Response res) {
        JSONObject result = new JSONObject();
        M.debug("Received batch download request");
        M.debug(req.body());
        try {
            JSONArray arr = new JSONArray(req.body());
            List<Callable<Boolean>> downloads = new ArrayList<Callable<Boolean>>();
            for (int k = 0; k < arr.length(); k++) {
                String url = arr.getString(k);
                downloads.add(() -> {
                    ChapterDownloader.downloadChapterFromURL(url);
                    return true;
                });
            }
            result.put("count", downloads.size());
            Callable<Void> wrapper = () -> {
                MultiThreadTaskManager.queueTasks(downloads);
                return null;
            };
            MultiThreadTaskManager.queueTask(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String downloadSingle(Request req, Response res) {
        JSONObject result = new JSONObject();
        try {
            JSONObject o = new JSONObject(req.body());
            M.debug(o.toString(4));
            String title = o.getString("mangaName").trim();
            String source = o.getString("source");
            String chapterNumber = o.getString("chapterNumber");
            JSONArray arr = o.getJSONArray("arr");
            Object[][] pages = new Object[arr.length()][2];
            for (int k = 0; k < arr.length(); k++) {
                JSONObject obj = arr.getJSONObject(k);
                int pageNumber = obj.getInt("number");
                String src = obj.getString("src");
                pages[k] = new Object[] { pageNumber, src };
            }
            Misaka.instance().persist.saveChapter(title, source, chapterNumber, pages);
            result.put("status", "success");
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("status", "failure");
        return result.toString();
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

    private String fetchResultImages(Request req, Response res) {
        JSONArray ja = new JSONArray();
        JSONArray urlArray = new JSONArray(req.body());
        for (int k = 0; k < urlArray.length(); k++) {
            int counter = 0;
            JSONObject obj = urlArray.getJSONObject(k);
            String id = obj.getString("id");
            String url = obj.getString("url");
            while (!cachedURLToImage.containsKey(url)) {
                if (counter++ > 5 * 3) { //5 is one second, max 3 sec hang per image
                    break;
                }
                System.out.println("waiting for " + url + " in " + cachedURLToImage);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jo = new JSONObject();
            jo.put("id", id);
            jo.put("imgUrl", cachedURLToImage.getOrDefault(url, "http://edasaki.com/i/test-page.png"));
            ja.put(jo);
        }
        return ja.toString();

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
        JSONObject jo = ChapterDownloader.getChapterFromURLAsJSON(url);
        if (jo != null) {
            return jo.toString();
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
                set.sort(searchPhrase);
                lastSearchResults.add(set);
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
        prefetchImages();
        return jo.toString();
    }

    private void prefetchImages() {
        for (SearchResultSet set : this.lastSearchResults) {
            MultiThreadTaskManager.queueTask(() -> {
                Map<String, Future<String>> individualFutures = new HashMap<String, Future<String>>();
                AbstractSource src = set.getAbstractSource();
                for (SearchResult s : set.getResults()) {
                    Future<String> f = MultiThreadTaskManager.queueTask(() -> {
                        String url = s.url;
                        M.edb("attempting url " + s.url + " " + s.title);
                        Document doc = MCacheUtils.getDocument(url);
                        String img = src.getImageURL(doc);
                        return img;
                    });
                    individualFutures.put(s.url, f);
                }
                MultiThreadTaskManager.wait(individualFutures.values());
                for (Entry<String, Future<String>> entry : individualFutures.entrySet()) {
                    try {
                        cachedURLToImage.put(entry.getKey(), entry.getValue().get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                this.lastSearchResults.remove(set);
                return true;
            });
        }
    }

}
