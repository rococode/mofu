package com.edasaki.misakachan.source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edasaki.misakachan.manga.Chapter;
import com.edasaki.misakachan.manga.Series;

public abstract class AbstractSource {

    public static final String DEFAULT_IMAGE = "http://edasaki.com/i/test-page.png";
    public static final String DEFAULT_TITLE = "Unknown Title";
    public static final String DEFAULT_CHAPTER = "Unknown Chapter";
    public static final String DEFAULT_CHAPTER_NUMBER = "000";
    public static final String DEFAULT_DESCRIPTION = "No description available.";
    public static final String DEFAULT_AUTHOR = "Unknown Author";
    public static final String DEFAULT_ARTIST = "";
    public static final String DEFAULT_GENRE = "";
    public static final String DEFAULT_ALTNAMES = "";
    public static final String DEFAULT_SOURCE = "Unknown Source";
    public static final Elements DEFAULT_ELEMENTS = new Elements();

    /**
     * @param url the url to match
     * @return <code>true</code> if the given URL matches this source's URL pattern
     */
    public abstract boolean match(String url);

    /**
     * @param url the url to match
     * @return <code>true</code> if the given URL matches this source's URL pattern for main pages of manga
     */
    public abstract boolean matchInfo(String url);

    public abstract Series getSeries(String url);

    public Chapter getChapter(String url) {
        Chapter chap = getChapterFromSite(url);
        if (chap == null) {
            return null;
        }
        chap.source = this;
        return chap;
    }

    protected abstract Chapter getChapterFromSite(String url);

    public abstract String getSourceName();

    public abstract SearchAction getSearch();

    public abstract String getImageURL(Document doc);

    @Override
    public String toString() {
        return this.getSourceName();
    }

    protected SearchResultSet createResultSet() {
        return new SearchResultSet(this);
    }

    protected SearchResultSet createResultSet(Collection<SearchResult> results) {
        SearchResultSet set = createResultSet();
        set.addResults(results);
        return set;
    }

    protected SearchResultSet createResultSet(Map<String, List<String>> results) {
        List<SearchResult> res = new ArrayList<SearchResult>();
        for (Entry<String, List<String>> e : results.entrySet()) {
            List<String> ls = e.getValue();
            SearchResult sr = new SearchResult(ls.get(0), ls.size() > 1 ? ls.subList(1, e.getValue().size()) : null, e.getKey());
            res.add(sr);
            System.out.println("ADded sr " + sr);
        }
        return createResultSet(res);
    }

    protected String selectFirst(Element ele, String selector) {
        Elements e = ele.select(selector);
        if (e.isEmpty())
            return "";
        return e.first().text();
    }

    @FunctionalInterface
    protected interface ExecutorWithDefault<E> {
        public E execute();
    }

    @FunctionalInterface
    protected interface VoidExecutor {
        public void execute();
    }

    protected static <E> E attempt(E defaultValue, ExecutorWithDefault<E> exec) {
        try {
            return exec.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    protected static void attempt(VoidExecutor exec) {
        try {
            exec.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
