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
import com.edasaki.misakachan.utils.MCacheUtils;
import com.edasaki.misakachan.utils.logging.M;
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
        Document doc = MCacheUtils.getDocument(url);
        if (doc == null)
            return null;
        Series series = new Series();
        //        series.source = this.getSourceName();
        //        Element detail = doc.select(".detail_topText").first();
        //        series.imageURL = doc.select(".manga_detail_top").first().select("img.img").first().absUrl("src");
        //        series.title = doc.select("meta[property=og:title").first().attr("content");
        //        series.description = detail.select("#show").first().ownText();
        //        series.authors = selectFirst(detail, "a[href^=http://www.mangahere.co/author/]");
        //        series.artists = selectFirst(detail, "a[href^=http://www.mangahere.co/artist/]");
        //        Elements labels = detail.select("label");
        //        for (Element e : labels) {
        //            if (e.text().contains("Genre")) {
        //                series.genres = e.parent().ownText();
        //            } else if (e.text().contains("Alternative")) {
        //                series.altNames = e.parent().ownText();
        //            }
        //        }
        //        Elements chapters = doc.select(".detail_list > ul:not([class]) > li");
        //        for (Element chapter : chapters) {
        //            String cURL = chapter.select("a").first().absUrl("href");
        //            String cName = chapter.select("a").first().text();
        //            series.addChapter(cName, cURL);
        //        }
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
            M.debug(doc);
            Elements entries = doc.select(".listing tr > td:first-child");
            Map<String, List<String>> linkMap = new HashMap<String, List<String>>();
            for (Element td : entries) {
                Element title = Jsoup.parseBodyFragment(td.attr("title"), doc.baseUri()).body();
                Element link = title.select("a").first();
                link.setBaseUri("http://kissmanga.com");
                String name = link.ownText();
                List<String> ls = new ArrayList<String>();
                ls.add(name);
                String href = link.absUrl("href");
                M.debug("href of : " + href + " for " + link);
                linkMap.put(href, ls);
            }
            System.out.println("LINKS: " + linkMap);

            return createResultSet(linkMap);
        };
    }

    @Override
    public String getImageURL(Document doc) {
        return doc.select("#rightside > .rightBox > .barContent img").first().absUrl("src");
    }

}
