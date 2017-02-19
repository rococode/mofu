package com.edasaki.misakachan.manga;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

    public static List<MangaPage> convertURLs(Collection<String> urls) {
        List<MangaPage> pages = new ArrayList<MangaPage>();
        int pageNumber = 1;
        Iterator<String> iter = urls.iterator();
        while (iter.hasNext()) {
            pages.add(new MangaPage(pageNumber++, iter.next()));
        }
        return pages;
    }
}
