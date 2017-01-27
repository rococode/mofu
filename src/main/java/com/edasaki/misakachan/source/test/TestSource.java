package com.edasaki.misakachan.source.test;

import java.util.ArrayList;
import java.util.List;

import com.edasaki.misakachan.chapter.Chapter;
import com.edasaki.misakachan.chapter.Page;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.source.SearchResult;

public class TestSource extends AbstractSource {

    @Override
    public boolean match(String url) {
        return url.contains("test");
    }

    @Override
    public Chapter getChapter(String url) {
        //        return new MangaHere().getChapter("http://www.mangahere.co/manga/gekkan_shoujo_nozaki_kun/v01/c070/");
        List<String> pages = new ArrayList<String>();
        for (int k = 0; k < 5; k++) {
            try {
                pages.add("http://edasaki.com/i/test-page.png");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Chapter chapter = new Chapter("Test Source M Name", pages.size(), Page.convertURLs(pages));
        return chapter;
    }

    @Override
    public String getSourceName() {
        return "TestSource.com";
    }

    @Override
    public SearchAction getSearch() {
        return (s) -> {
            List<SearchResult> res = new ArrayList<SearchResult>();
            res.add(new SearchResult("test-search-result-" + s + "-1", null, "http://blah.com"));
            res.add(new SearchResult("test-search-result-" + s + "-2", null, "http://blah.com"));
            res.add(new SearchResult("test-search-result-" + s + "-3", null, "http://blah.com"));
            return createResultSet(res);
        };
    }

}
