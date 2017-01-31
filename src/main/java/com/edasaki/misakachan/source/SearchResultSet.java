package com.edasaki.misakachan.source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchResultSet {
    private final AbstractSource aSource;
    private final String source;
    private List<SearchResult> results;

    public SearchResultSet(AbstractSource source) {
        this.aSource = source;
        this.source = source.getSourceName();
        this.results = new ArrayList<SearchResult>();
    }

    public void addResults(Collection<SearchResult> results) {
        this.results.addAll(results);
    }

    public AbstractSource getAbstractSource() {
        return this.aSource;
    }

    public String getSource() {
        return this.source;
    }

    public List<SearchResult> getResults() {
        return this.results;
    }

    @Override
    public String toString() {
        return source + ": " + results.toString();
    }
}
