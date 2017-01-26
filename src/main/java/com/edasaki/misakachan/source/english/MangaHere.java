package com.edasaki.misakachan.source.english;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edasaki.misakachan.chapter.Chapter;
import com.edasaki.misakachan.chapter.Page;
import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.utils.logging.M;

public class MangaHere extends AbstractSource {

    @Override
    public boolean match(String url) {
        return url.contains("mangahere.co");
    }

    @Override
    public Chapter getChapter(String url) {
        Document doc;
        try {
            M.debug("connecting... ");
            doc = Jsoup.connect(url).get();
            M.debug("connected!");
            Elements dropdownSelector = doc.select("select.wid60 > option");
            List<String> pageURLs = new ArrayList<String>();
            for (Element option : dropdownSelector) {
                String pageURL = option.attr("value");
                if (!pageURLs.contains(pageURL))
                    pageURLs.add(pageURL);
            }
            String title = "Unknown Title";
            for (Element e : doc.select("div.title > h1 > a")) {
                String txt = e.text().trim();
                if (!txt.matches(".* [0-9]+"))
                    continue;
                title = txt.substring(0, txt.lastIndexOf(' '));
                break;
            }
            List<String> srcs = new ArrayList<String>();
            long start = System.currentTimeMillis();
            for (Document d : MultiThreadTaskManager.getDocuments(pageURLs)) {
                String src = d.getElementById("image").attr("src");
                srcs.add(src);
            }
            System.out.println("finished parsing pages in " + (System.currentTimeMillis() - start) + "ms");
            List<Page> pages = Page.convertURLs(srcs);
            Chapter chapter = new Chapter(title, pages.size(), pages);
            return chapter;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getSourceName() {
        return "MangaHere";
    }

    private static long lastSearch = 0;

    @Override
    public SearchAction getSearch() {
        return (searchTerm) -> {
            if (System.currentTimeMillis() - lastSearch < 5000) {
                try {
                    long sleep = 5000 - (System.currentTimeMillis() - lastSearch) + 100;
                    M.debug("sleeping " + sleep);
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String prefix = "http://www.mangahere.co/search.php?name_method=cw&author_method=cw&artist_method=cw&advopts=1&name=";
            String url = prefix + searchTerm;
            try {
                Document doc = Jsoup.connect(url).get();
                // update last search time after site has been accessed
                lastSearch = System.currentTimeMillis();
                Elements entries = doc.select(".result_search > dl");
                Elements mainLinks = entries.select("dl > dt > a.name_one");
                Map<Element, List<String>> linkMap = new HashMap<Element, List<String>>();
                for (Element link : mainLinks) {
                    Elements alt = link.parent().parent().select("dd");
                    String altText = alt.text();
                    altText = altText.substring(altText.indexOf(':') + 1).trim();
                    List<String> associatedNames = new ArrayList<String>();
                    associatedNames.add(link.attr("rel"));
                    if (altText.contains("...")) {
                        M.debug(altText + " requires extra parsing");
                        Document detailed = Jsoup.connect(link.absUrl("href")).get();
                        Elements altNameLabel = detailed.select("label:contains(Alternative Name)");
                        for (Element altNameLabelEle : altNameLabel) {
                            try {
                                altText = altNameLabelEle.parent().text();
                                altText = altText.substring(altText.indexOf(':') + 1);
                                for (String s : altText.split(";")) {
                                    s = s.trim();
                                    if (s.length() > 0)
                                        associatedNames.add(s);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        for (String s : altText.split(";")) {
                            s = s.trim();
                            if (s.length() > 0)
                                associatedNames.add(s);
                        }
                    }
                    M.debug(associatedNames);
                    linkMap.put(link, associatedNames);
                }
                return createResultSet(linkMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return createResultSet();
        };
    }

}
