package com.edasaki.misakachan.backend.source;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edasaki.misakachan.backend.chapter.Chapter;
import com.edasaki.misakachan.backend.chapter.Page;

public abstract class AbstractSource {

    protected final Logger logger = LoggerFactory.getLogger(AbstractSource.class);

    /**
     * @param url the url to match
     * @return <code>true</code> if the given URL matches this source's URL pattern
     */
    public abstract boolean match(String url);

    public Chapter getChapter(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        List<String> urls = getPageUrls(doc);
        logger.debug("urls {}", urls);
        int pageCount = getPageCount(urls);
        logger.debug("pageCount {}", pageCount);
        List<Page> pages = new ArrayList<Page>();
        for (int k = 0; k < urls.size(); k++) {
            pages.add(new Page(k + 1, urls.get(k)));
        }
        String name = getName(doc);
        Chapter chapter = new Chapter(name, pageCount, pages);
        return chapter;
    }

    protected abstract int getPageCount(List<String> urls);

    protected abstract List<String> getPageUrls(Document doc);

    protected abstract String getName(Document doc);
}
