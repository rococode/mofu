package com.edasaki.misakachan.scanlator;

public class BakaUpdateManager {

    private BakaUpdateSearcher searcher;

    public BakaUpdateManager() {
        searcher = new BakaUpdateSearcher();
    }

    public ScanGroup getScanlator(String mangaName) {
        return searcher.getGroups(mangaName).get(0);
    }

}
