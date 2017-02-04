package com.edasaki.misakachan.spark;

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
import com.edasaki.misakachan.chapter.Chapter;
import com.edasaki.misakachan.chapter.Page;
import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.source.SearchResult;
import com.edasaki.misakachan.source.SearchResultSet;
import com.edasaki.misakachan.utils.MCacheUtils;

import spark.Request;
import spark.Response;
import spark.Spark;

public class SparkManager {

    private AbstractSource[] sources;

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
    }

    public int getPort() {
        return Spark.port();
    }

    private String downloadSingle(Request req, Response res) {
        JSONObject result = new JSONObject();
        try {
            JSONObject o = new JSONObject(req.body());
            String title = o.getString("mangaName").trim();
            String source = o.getString("source");
            int chapterNumber = o.getInt("chapterNumber");
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
                if (counter > 5 * 10) { //5 is one second, max 5 sec hang
                    break;
                }
                try {
                    counter++;
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
        JSONObject jo = new JSONObject();
        for (AbstractSource source : sources) {
            if (source.match(url)) {
                jo.put("type", "url");
                jo.put("source", source.getSourceName());
                Chapter chapter = source.getChapter(url);
                jo.put("mangaName", chapter.getMangaTitle());
                jo.put("chapterName", chapter.getChapterName());
                jo.put("chapterNumber", chapter.getChapterNumber());
                jo.put("pagecount", chapter.getPageCount());
                JSONArray urls = new JSONArray();
                for (Page p : chapter.getPages())
                    urls.put(p.getURL());
                jo.put("urls", urls);
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
                        Document doc = MCacheUtils.getDocument(url);
                        String img = src.getImageURL(doc);
                        return img;
                    });
                    individualFutures.put(s.url, f);
                }
                MultiThreadTaskManager.wait(individualFutures.values());
                for (Entry<String, Future<String>> e : individualFutures.entrySet()) {
                    cachedURLToImage.put(e.getKey(), e.getValue().get());
                }
                this.lastSearchResults.remove(set);
                return true;
            });
        }
    }

}
