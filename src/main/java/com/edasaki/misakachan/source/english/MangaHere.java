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
import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.source.AbstractSource;
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

}
