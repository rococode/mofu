package com.edasaki.misakachan.source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchResultSet {
    private final String source;
    private List<SearchResult> results;

    public SearchResultSet(String source) {
        this.source = source;
        this.results = new ArrayList<SearchResult>();
    }

    public void addResults(Collection<SearchResult> results) {
        this.results.addAll(results);
    }

    @Override
    public String toString() {
        return source + ": " + results.toString();
    }
}
