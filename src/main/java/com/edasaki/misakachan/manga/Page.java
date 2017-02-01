package com.edasaki.misakachan.manga;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private int num; // page number, one-indexed
    private String url; // url of the page

    public Page(int num, String url) {
        this.num = num;
        this.url = url;
    }

    public int getPageNumber() {
        return this.num;
    }

    public String getURL() {
        return this.url;
    }

    public static List<Page> convertURLs(List<String> urls) {
        List<Page> pages = new ArrayList<Page>();
        for (int k = 0; k < urls.size(); k++)
            pages.add(new Page(k + 1, urls.get(k)));
        return pages;
    }
}
