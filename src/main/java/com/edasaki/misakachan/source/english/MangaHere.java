package com.edasaki.misakachan.source.english;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edasaki.misakachan.chapter.Chapter;
import com.edasaki.misakachan.chapter.Page;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.utils.logging.ELog;

public class MangaHere extends AbstractSource {

    @Override
    public boolean match(String url) {
        return url.contains("mangahere.co");
    }

    @Override
    public Chapter getChapter(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            Elements dropdownSelector = doc.select("select.wid60 > option");
            List<String> pageURLs = new ArrayList<String>();
            for (Element option : dropdownSelector) {
                String pageURL = option.attr("value");
                if (!pageURLs.contains(pageURL))
                    pageURLs.add(pageURL);
            }
            List<String> srcs = new ArrayList<String>();
            for (String pageURL : pageURLs) {
                ELog.debug("Loading page " + pageURL);
                try {
                    Connection conn = Jsoup.connect(pageURL);
                    Document page = conn.get();
                    String src = page.getElementById("image").attr("src");
                    srcs.add(src);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            List<Page> pages = Page.convertURLs(srcs);
            Chapter chapter = new Chapter("mangahere temp name", pages.size(), pages);
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

}
