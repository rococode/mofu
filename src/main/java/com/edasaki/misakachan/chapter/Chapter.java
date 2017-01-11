package com.edasaki.misakachan.chapter;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private final String chapterName;
    private final int pageCount;
    private final List<Page> pages;

    public Chapter(String name, int pageCount, List<Page> pages) {
        this.chapterName = name;
        this.pageCount = pageCount;
        this.pages = new ArrayList<Page>();
        this.pages.addAll(pages);
    }

    public String getChapterName() {
        return this.chapterName;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public List<Page> getPages() {
        return pages;
    }

}
