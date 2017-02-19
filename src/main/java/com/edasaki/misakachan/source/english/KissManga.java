package com.edasaki.misakachan.source.english;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import com.edasaki.misakachan.manga.Chapter;
import com.edasaki.misakachan.manga.Series;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.utils.MCache;
import com.edasaki.misakachan.web.ExtraModifiers;
import com.edasaki.misakachan.web.FinishedCondition;
import com.edasaki.misakachan.web.WebAccessor;

public class KissManga extends AbstractSource {

    @Override
    public boolean match(String url) {
        return url.contains("kissmanga.com");
    }

    @Override
    public boolean matchInfo(String url) {
        return url.contains("kissmanga.com/Manga/");
    }

    @Override
    public Series getSeries(String url) {
        Document doc = MCache.getDocument(url);
        if (doc == null)
            return null;
        Series series = new Series();
        series.source = this.getSourceName();
        series.imageURL = getImageURL(doc);
        series.title = attempt(DEFAULT_TITLE, () -> {
            return doc.select(".bigChar").first().ownText();
        });
        series.description = attempt(DEFAULT_DESCRIPTION, () -> {
            return doc.select(".info:contains(Summary)").first().parent().nextElementSibling().ownText();
        });
        series.authors = attempt(DEFAULT_AUTHOR, () -> {
            return doc.select(".info:contains(Author) + a").first().ownText();
        });
        series.genres = attempt(DEFAULT_GENRE, () -> {
            String s;
            return (s = doc.select(".info:contains(Genre) + a").first().parent().text()).substring(s.indexOf(':') + 1);
        });
        Elements chapters = attempt(DEFAULT_ELEMENTS, () -> {
            return doc.select(".listing tr > td:first-child > a");
        });
        for (Element chapter : chapters) {
            attempt(() -> {
                String cURL = chapter.absUrl("href");
                String cName = chapter.ownText();
                series.addChapter(cName, cURL);
            });
        }
        return series;
    }

    @Override
    public Chapter getChapterFromSite(String url) {
        return null;
    }

    @Override
    public String getSourceName() {
        return "KissManga";
    }

    @Override
    public SearchAction getSearch() {
        return (searchTerm) -> {
            String url = "http://kissmanga.com/Search/Manga?keyword=" + searchTerm;
            Document doc = WebAccessor.postURL(url, new ExtraModifiers() {
                @Override
                public Connection modify(Connection conn) {
                    return conn.cookie("vns_doujinshi", "1");
                }

                @Override
                public <E extends WebDriver> E modify(E driver) {
                    driver.manage().addCookie(new Cookie("vns_doujinshi", "1"));
                    return driver;
                }

            }, new FinishedCondition() {
                @Override
                public boolean finished(String src) {
                    boolean res = src.contains("class=\"listing\"");
                    return res;
                }

            });
            if (doc == null)
                return null;
            // update last search time after site has been accessed
            Elements entries = doc.select(".listing tr > td:first-child");
            Map<String, List<String>> linkMap = new HashMap<String, List<String>>();
            for (Element td : entries) {
                attempt(() -> {
                    Element title = Jsoup.parseBodyFragment(td.attr("title"), doc.baseUri()).body();
                    Element link = title.select("a").first();
                    link.setBaseUri("http://kissmanga.com");
                    String name = link.ownText();
                    List<String> ls = new ArrayList<String>();
                    ls.add(name);
                    String href = link.absUrl("href");
                    linkMap.put(href, ls);
                });
            }
            System.out.println("LINKS: " + linkMap);

            return createResultSet(linkMap);
        };
    }

    @Override
    public String getImageURL(Document doc) {
        return attempt(DEFAULT_IMAGE, () -> {
            return doc.select("#rightside > .rightBox > .barContent img").first().absUrl("src");
        });
    }

}
