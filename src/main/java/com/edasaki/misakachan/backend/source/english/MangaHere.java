package com.edasaki.misakachan.backend.source.english;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edasaki.misakachan.backend.source.AbstractSource;

public class MangaHere extends AbstractSource {

    @Override
    public boolean match(String url) {
        return url.contains("mangahere.co");
    }

    @Override
    protected int getPageCount(List<String> urls) {
        return urls.size();
    }

    @Override
    protected List<String> getPageUrls(Document doc) {
        Elements options = doc.select("select.wid60 > option");
        List<String> pages = new ArrayList<String>();
        for (Element element : options) {
            String url = element.attr("value");
            if (!pages.contains(url))
                pages.add(url);
        }
        List<String> srcs = new ArrayList<String>();
        for (String url : pages) {
            logger.debug("Loading page " + url);
            try {
                Connection conn = Jsoup.connect(url);
                Document page = conn.get();
                String src = page.getElementById("image").attr("src");
                srcs.add(src);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return srcs;
    }

    @Override
    protected String getName(Document doc) {
        return null;
    }

    @Override
    public String getSourceName() {
        return "MangaHere";
    }

}
