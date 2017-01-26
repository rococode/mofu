package com.edasaki.misakachan.source;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    public final String title;
    public final List<String> altNames;
    public final String url;

    public SearchResult(String title, List<String> altNames, String url) {
        this.title = title;
        this.altNames = new ArrayList<String>();
        if (altNames != null)
            this.altNames.addAll(altNames);
        this.url = url;
    }

    @Override
    public String toString() {
        return title + "::" + url;
    }

}
