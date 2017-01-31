package com.edasaki.misakachan.scanlator;

import java.util.List;

public class BakaUpdateManager {

    private BakaUpdateSearcher searcher;

    public BakaUpdateManager() {
        searcher = new BakaUpdateSearcher();
    }

    public String getURL(String title) {
        return searcher.getURL(title);
    }

    public List<ScanGroup> getScanlator(String mangaName) {
//        searcher.getGroups(mangaName);
        return null;
    }

}
