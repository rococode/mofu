package com.edasaki.misakachan.scanlator;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.utils.MCache;
import com.edasaki.misakachan.utils.MStringUtils;
import com.edasaki.misakachan.utils.logging.MTimer;
import com.edasaki.misakachan.utils.logging.NoDebug;

@NoDebug
public class BakaUpdateSearcher {
    private static final String PREFIX = "https://www.mangaupdates.com/series.html?stype=title&orderby=rating&perpage=100&page=";
    private static final String SEARCH = "&search=";

    private static final String SELECTOR_REGEX = ".*\\Qmangaupdates.com/series.html?id=\\E.*";
    private static final String SELECTOR = "a[href~=" + SELECTOR_REGEX + "]";

    private static final String LINK_REGEX = "\\Qhttp\\Es?\\Q://\\E(www)?.?\\Qmangaupdates.com\\E.*";

    private static final String CHARSET = "UTF-8";

    private static final String GROUP_SELECTOR_REGEX = ".*\\Qmangaupdates.com/groups.html?id=\\E.*";
    private static final String GROUP_SELECTOR = "a[href~=" + GROUP_SELECTOR_REGEX + "]";

    private static final Map<Element, List<String>> emptyMap = new HashMap<>();

    private static final class ThreadState {
        private int endPage = -1;
    }

    protected String getURL(final String title) {
        MTimer t = new MTimer();
        int page = 1;
        int counter = 30;
        final ThreadState ts = new ThreadState();
        List<Future<Map<Element, List<String>>>> futures = new ArrayList<Future<Map<Element, List<String>>>>();
        while (counter-- >= 0) {
            if (ts.endPage >= 0 && page > ts.endPage) {
                break;
            }
            for (int k = 0; k < 2; k++) {
                final int currPage = page++;
                if (ts.endPage >= 0 && currPage > ts.endPage) {
                    continue;
                }
                Callable<Map<Element, List<String>>> fetchSearchResultsPageTask = () -> {
                    if (ts.endPage >= 0 && currPage > ts.endPage) {
                        // don't go past the last valid page
                        return emptyMap;
                    }
                    Map<Element, List<String>> urls = new HashMap<Element, List<String>>();
                    try {
                        final String connectionURL = PREFIX + currPage + SEARCH + URLEncoder.encode(title.trim(), CHARSET);
                        Document doc = MCache.getDocument(connectionURL);
                        String txt = doc.text();
                        if (txt.contains("There are no") || txt.contains("make your query more restrictive.")) {
                            // search complete, mark page as last valid page
                            if (ts.endPage == -1 || currPage <= ts.endPage) {
                                ts.endPage = currPage;
                            }
                            return emptyMap;
                        }
                        Elements links = doc.select(SELECTOR);
                        for (Element link : links) {
                            String linkHref = link.absUrl("href");
                            if (!linkHref.matches(LINK_REGEX))
                                continue;
                            List<String> arrList = new ArrayList<String>();
                            String linkName = link.text().trim();
                            arrList.add(linkName);
                            urls.put(link, arrList);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Misaka.update("Error: Failed to fetch baka search results.");
                    }
                    return urls;
                };
                Future<Map<Element, List<String>>> future = MultiThreadTaskManager.queueTaskOrdered(fetchSearchResultsPageTask);
                futures.add(future);
            }
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MultiThreadTaskManager.wait(futures);
        HashMap<Element, List<String>> resultMap = new HashMap<Element, List<String>>();
        for (Future<Map<Element, List<String>>> f : futures) {
            try {
                resultMap.putAll(f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        Map<Element, String> topResults = MStringUtils.getTopResults(resultMap, title, 3);
        t.output("BakaBT search");
        if (topResults.size() == 0) {
            return null;
        }
        String res = topResults.keySet().iterator().next().absUrl("href");
        return res;
    }

    protected Map<ScanGroup, List<String>> getGroups(String bestURL) {
        Document detailPage = MCache.getDocument(bestURL);
        if (detailPage == null)
            return new HashMap<>();
        Elements sCats = detailPage.select(GROUP_SELECTOR);
        HashMap<Integer, ScanGroup> groups = new HashMap<Integer, ScanGroup>();
        for (Element e : sCats) {
            try {
                String href = e.absUrl("href");
                int id = Integer.parseInt(href.substring(href.indexOf("?id=") + "?id=".length()).trim());
                if (groups.containsKey(id))
                    continue;
                String name = e.text();
                ScanGroup sg = new ScanGroup(id, name);
                groups.put(id, sg);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        Map<ScanGroup, Future<List<String>>> chapters = new HashMap<ScanGroup, Future<List<String>>>();
        for (ScanGroup sg : groups.values()) {
            Future<List<String>> f = MultiThreadTaskManager.queueTask(() -> {
                //                Document doc = MCacheUtils.getDocument(sg.url);
                //                    return conn.get();
                return null;
            });
            chapters.put(sg, f);
        }
        MultiThreadTaskManager.wait(chapters.values());
        //        for (Entry<ScanGroup, Future<List<String>>> e : chapters.entrySet()) {
        //
        //        }
        return new HashMap<>();
    }

}
