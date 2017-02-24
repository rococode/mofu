package com.edasaki.misakachan.web.spark.routes;

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
import com.edasaki.misakachan.web.spark.TriRouteBase;

import spark.Request;
import spark.Response;
import spark.Spark;

public class LoadRequestedURLRoute implements TriRouteBase {
    private List<SearchResultSet> lastSearchResults;

    public LoadRequestedURLRoute() {
        this.lastSearchResults = new ArrayList<SearchResultSet>();
    }

    @Override
    public void apply(Request request, Response response, JSONObject jo) {
        String url = request.body();
        if (ChapterDownloader.getChapterFromURLAsJSON(url, jo) == null) {
            String searchPhrase = request.body();
            if (searchPhrase == null || searchPhrase.length() == 0)
                return;
            jo.put("type", "search");
            jo.put("searchPhrase", searchPhrase);
            List<Callable<SearchResultSet>> searches = new ArrayList<Callable<SearchResultSet>>();
            for (AbstractSource source : Misaka.SOURCES) {
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
    }

    private void prefetchImages() {
        for (SearchResultSet set : this.lastSearchResults) {
            MultiThreadTaskManager.queueTask(() -> {
                Map<String, Future<File>> individualFutures = new HashMap<String, Future<File>>();
                AbstractSource src = set.getAbstractSource();
                for (SearchResult s : set.getResults()) {
                    if (!FetchResultImagesRoute.cachedURLToLocalImage.containsKey(s.url)) {
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
                        FetchResultImagesRoute.cachedURLToLocalImage.put(entry.getKey(), name);
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
