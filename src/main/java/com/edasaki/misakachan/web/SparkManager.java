package com.edasaki.misakachan.web;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

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
import com.edasaki.misakachan.utils.MCache;
import com.edasaki.misakachan.utils.logging.M;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SparkManager {

    public AbstractSource[] sources;

    private static final Map<String, String> cachedURLToLocalImage = new HashMap<String, String>();

    private List<SearchResultSet> lastSearchResults;

    public SparkManager(AbstractSource[] sources) {
        this.sources = sources;
        this.lastSearchResults = new ArrayList<SearchResultSet>();
    }

    public void startWebserver() {
        //        Spark.externalStaticFileLocation(PersistenceManager.getCacheDir().toString());
        Spark.port(10032);
        Spark.staticFileLocation("/public");
        Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });
        Spark.post("/load", processRouteObj(this::loadRequestedURL));
        Spark.post("/search", processRouteObj(this::search));
        Spark.get("/changelog", processRouteObj(this::loadChangelog));
        Spark.post("/lookup", processRouteObj(this::lookup));
        Spark.post("/fetchResultImages", processRouteObj(this::fetchResultImages));
        Spark.post("/download", processRouteObj(this::downloadSingle));
        Spark.post("/downloadbatch", processRouteObj(this::downloadByURL));
    }

    public Route processRouteObj(TriConsumer<Request, Response, JSONObject> consumer) {
        Route r = new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                JSONObject jo = new JSONObject();
                consumer.apply(request, response, jo);
                WebAccessor.appendCookies(jo);
                return jo;
            }
        };
        return r;
    }

    @FunctionalInterface
    private interface TriConsumer<A, B, C> {
        void apply(A a, B b, C c);
    }

    public int getPort() {
        return Spark.port();
    }

    private void downloadByURL(Request req, Response res, JSONObject result) {
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
    }

    private void downloadSingle(Request req, Response res, JSONObject result) {
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
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("status", "failure");
    }

    private void lookup(Request req, Response res, JSONObject jo) {
        String url = req.body();
        for (AbstractSource source : sources) {
            if (source.matchInfo(url)) {
                jo.put("status", "success");
                try {
                    jo.put("series", source.getSeries(url).getSeriesObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        jo.put("status", "failure");
        jo.put("reason", "Invalid URL.");
    }

    private int lastCachedSize = 0;

    private void fetchResultImages(Request req, Response res, JSONObject jo) {
        JSONArray ja = new JSONArray();
        jo.put("arr", ja);
        JSONArray urlArray = new JSONArray(req.body());
        for (int k = 0; k < urlArray.length(); k++) {
            int counter = 0;
            JSONObject obj = urlArray.getJSONObject(k);
            String id = obj.getString("id");
            String url = obj.getString("url");
            while (!cachedURLToLocalImage.containsKey(url)) {
                if (cachedURLToLocalImage.size() != lastCachedSize && counter++ > 5 * 10) { //5 is one second, max 10 sec hang per image
                    break;
                }
                System.out.println("Waiting for " + url + " in " + cachedURLToLocalImage);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("id", id);
            jsonobject.put("imgUrl", cachedURLToLocalImage.getOrDefault(url, "http://edasaki.com/i/test-page.png"));
            ja.put(jsonobject);
        }
        lastCachedSize = cachedURLToLocalImage.size();
    }

    private void loadChangelog(Request req, Response res, JSONObject jo) {
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
    }

    private void loadRequestedURL(Request req, Response res, JSONObject jo) {
        String url = req.body();
        if (ChapterDownloader.getChapterFromURLAsJSON(url, jo) == null) {
            search(req, res, jo);
        }
    }

    private void search(Request req, Response res, JSONObject jo) {
        String searchPhrase = req.body();
        if (searchPhrase == null || searchPhrase.length() == 0)
            return;
        jo.put("type", "search");
        jo.put("searchPhrase", searchPhrase);
        List<Callable<SearchResultSet>> searches = new ArrayList<Callable<SearchResultSet>>();
        for (AbstractSource source : sources) {
            SearchAction sa = source.getSearch();
            searches.add(() -> {
                return sa.search(URLEncoder.encode(searchPhrase, "UTF-8"));
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
    }

    private void prefetchImages() {
        for (SearchResultSet set : this.lastSearchResults) {
            MultiThreadTaskManager.queueTask(() -> {
                Map<String, Future<File>> individualFutures = new HashMap<String, Future<File>>();
                AbstractSource src = set.getAbstractSource();
                for (SearchResult s : set.getResults()) {
                    if (!cachedURLToLocalImage.containsKey(s.url)) {
                        Future<File> f = MultiThreadTaskManager.queueTask(() -> {
                            String url = s.url;
                            Document doc = MCache.getDocument(url);
                            String img = src.getImageURL(doc);
                            return MCache.getFile(img);
                        });
                        individualFutures.put(s.url, f);
                    }
                }
                MultiThreadTaskManager.wait(individualFutures.values());
                for (Entry<String, Future<File>> entry : individualFutures.entrySet()) {
                    try {
                        File f = entry.getValue().get();
                        if (f == null) {
                            M.edb("ERROR: Null file for " + entry.getKey());
                            continue;
                        }
                        //                        M.debug("Downloaded file to " + f + " at " + f.getAbsolutePath());
                        String name = f.getName().replaceAll("[^a-zA-Z0-9]", "");
                        Spark.get("/" + name, (req, res) -> {
                            //                            AbstractFileResolvingResource resource = new ExternalResource(f.getAbsolutePath());
                            //                            String contentType = MimeType.fromResource(resource);
                            //                            res.type(contentType);
                            byte[] bytes = Files.readAllBytes(f.toPath());
                            HttpServletResponse raw = res.raw();
                            raw.getOutputStream().write(bytes);
                            raw.getOutputStream().flush();
                            raw.getOutputStream().close();
                            //                            M.debug("Got content type: " + contentType);
                            return res;
                        });
                        cachedURLToLocalImage.put(entry.getKey(), name);
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
