package com.edasaki.misakachan.chapter;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private final String chapterName, mangaTitle;
    private final int pageCount, chapterNumber;
    private final List<Page> pages;

    public Chapter(String mangaTitle, String chapterTitle, int chapterNumber, List<Page> pages) {
        this.mangaTitle = mangaTitle;
        this.chapterName = chapterTitle;
        this.chapterNumber = chapterNumber;
        this.pageCount = pages.size();
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

    public String getMangaTitle() {
        return this.mangaTitle;
    }

    public int getChapterNumber() {
        return this.chapterNumber;
    }

}
