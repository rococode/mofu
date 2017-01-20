package com.edasaki.misakachan.scanlator;

public class BakaUpdateManager {

    private BakaUpdateSearcher searcher;

    public BakaUpdateManager() {
        searcher = new BakaUpdateSearcher();
    }

    public Scanlator getScanlator(String mangaName) {
        return searcher.search(mangaName);
    }

}
