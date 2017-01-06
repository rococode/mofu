package com.edasaki.misakachan.backend.chapter;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private String chapterName;
    private int pageCount;
    private List<Page> pages;

    public Chapter(String name, int pageCount, List<Page> pages) {
        this.chapterName = name;
        this.pageCount = pageCount;
        this.pages = new ArrayList<Page>();
        this.pages.addAll(pages);
    }

}
