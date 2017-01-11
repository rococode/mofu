package com.edasaki.misakachan.source.test;

import java.util.ArrayList;
import java.util.List;

import com.edasaki.misakachan.chapter.Chapter;
import com.edasaki.misakachan.chapter.Page;
import com.edasaki.misakachan.source.AbstractSource;

public class TestSource extends AbstractSource {

    @Override
    public boolean match(String url) {
        return true;
    }

    @Override
    public Chapter getChapter(String url) {
        List<String> pages = new ArrayList<String>();
        for (int k = 0; k < 5; k++) {
            try {
                pages.add("http://edasaki.com/i/test-page.png");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Chapter chapter = new Chapter("Test Source M Name", pages.size(), Page.convertURLs(pages));
        return chapter;
    }

    @Override
    public String getSourceName() {
        return "TestSource.com";
    }

}
