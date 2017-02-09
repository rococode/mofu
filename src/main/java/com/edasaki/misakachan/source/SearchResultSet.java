package com.edasaki.misakachan.source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.edasaki.misakachan.utils.MStringUtils;
import com.edasaki.misakachan.utils.logging.M;
import com.edasaki.misakachan.utils.logging.NoDebug;

@NoDebug
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

    public void sort(String searchTerm) {
        M.debug("Before sort by " + searchTerm + ": " + results);
        Collections.sort(results, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult res1, SearchResult res2) {
                String s1 = res1.title;
                String s2 = res2.title;
                double d1 = Math.max(MStringUtils.similarityMaxContains(s1, searchTerm), MStringUtils.similarityMaxContainsAlphanumeric(s1, searchTerm));
                double d2 = Math.max(MStringUtils.similarityMaxContains(s2, searchTerm), MStringUtils.similarityMaxContainsAlphanumeric(s2, searchTerm));
                if (d1 == 1.0 && d2 == 1.0) {
                    d1 = MStringUtils.similarity(s1, searchTerm);
                    d2 = MStringUtils.similarity(s2, searchTerm);
                }
                return d1 > d2 ? -1 : d1 < d2 ? 1 : 0;
            }
        });
        M.debug("Sorted: " + results);
    }

    @Override
    public String toString() {
        return source + ": " + results.toString();
    }
}
