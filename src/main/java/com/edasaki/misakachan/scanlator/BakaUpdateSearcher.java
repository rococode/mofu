package com.edasaki.misakachan.scanlator;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.utils.MCacheUtils;
import com.edasaki.misakachan.utils.MStringUtils;
import com.edasaki.misakachan.utils.logging.M;

public class BakaUpdateSearcher {
    private static final String PREFIX = "https://www.mangaupdates.com/series.html?stype=title&orderby=rating&perpage=100&page=";
    private static final String SEARCH = "&search=";

    private static final String SELECTOR_REGEX = ".*\\Qmangaupdates.com/series.html?id=\\E.*";
    private static final String SELECTOR = "a[href~=" + SELECTOR_REGEX + "]";

    private static final String LINK_REGEX = "\\Qhttp\\Es?\\Q://\\E(www)?.?\\Qmangaupdates.com\\E.*";

    private static final String CHARSET = "UTF-8";

    private static final String GROUP_SELECTOR_REGEX = ".*\\Qmangaupdates.com/groups.html?id=\\E.*";
    private static final String GROUP_SELECTOR = "a[href~=" + GROUP_SELECTOR_REGEX + "]";

    protected String getURL(String title) {
        M.debug("searching for " + title);
        title = title.trim();
        Document doc;
        double bestSimilarity = 0;
        String bestTitle = null;
        String bestURL = null;
        int page = 1;
        HashMap<String, String> mapContainedTitleToURL = new HashMap<String, String>();
        long start = System.currentTimeMillis();
        try {
            do {
                final String connectionURL = PREFIX + page + SEARCH + URLEncoder.encode(title, CHARSET);
                doc = MCacheUtils.getDocument(connectionURL);
                if (doc.html().contains("There are no") || doc.html().contains("make your query more restrictive.")) // search complete
                    break;
                Elements links = doc.select(SELECTOR);
                for (Element link : links) {
                    String linkName = link.text().trim();
                    String linkHref = link.absUrl("href");
                    if (!linkHref.matches(LINK_REGEX))
                        continue;
                    double sim = MStringUtils.similarityMaxContains(linkName, title);
                    if (sim >= bestSimilarity) {
                        bestSimilarity = sim;
                        bestURL = linkHref;
                        bestTitle = linkName;
                        if (sim == 1.0) {
                            mapContainedTitleToURL.put(bestTitle, bestURL);
                        }
                    }
                    // check for alphanumeric chars only (mostly to avoid weird
                    // dash issues like tomo-chan vs tomochan)
                    sim = MStringUtils.similarityMaxContainsAlphanumeric(linkName, title);
                    if (sim >= bestSimilarity) {
                        bestSimilarity = sim;
                        bestURL = linkHref;
                        bestTitle = linkName;
                        if (sim == 1.0) {
                            mapContainedTitleToURL.put(bestTitle, bestURL);
                        }
                    }
                }
                page++;
            } while (true);
            if (mapContainedTitleToURL.size() > 1) {
                bestSimilarity = 0;
                for (Entry<String, String> e : mapContainedTitleToURL.entrySet()) {
                    double sim = MStringUtils.similarity(e.getKey(), title);
                    if (sim > bestSimilarity) {
                        bestSimilarity = sim;
                        bestURL = e.getValue();
                        bestTitle = e.getKey();
                    }
                }
            }
            // M.debug("Best result for \"" + title + "\": " + bestTitle + " ["
            // + bestURL + "] with a similarity of " + bestSimilarity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        M.debug("Finished search() in " + (System.currentTimeMillis() - start) + "ms");
        M.debug("best url: " + bestURL);
        return bestURL;
    }

    protected Map<ScanGroup, List<String>> getGroups(String bestURL) {
        Document detailPage = MCacheUtils.getDocument(bestURL);
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
                Document doc = MCacheUtils.getDocument(sg.url);
                //                    return conn.get();
                return null;
            });
            chapters.put(sg, f);
        }
        MultiThreadTaskManager.wait(chapters.values());
        for (Entry<ScanGroup, Future<List<String>>> e : chapters.entrySet()) {

        }
        return new HashMap<>();
    }

}
