package com.edasaki.misakachan.manga;

import java.util.ArrayList;
import java.util.List;

public class MangaPage {
    private int num; // page number, one-indexed
    private String url; // url of the page

    public MangaPage(int num, String url) {
        this.num = num;
        this.url = url;
    }

    public int getPageNumber() {
        return this.num;
    }

    public String getURL() {
        return this.url;
    }

    public static List<MangaPage> convertURLs(List<String> urls) {
        List<MangaPage> pages = new ArrayList<MangaPage>();
        for (int k = 0; k < urls.size(); k++)
            pages.add(new MangaPage(k + 1, urls.get(k)));
        return pages;
    }
}
