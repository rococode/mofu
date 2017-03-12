package com.edasaki.misakachan.source.french;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edasaki.misakachan.manga.Chapter;
import com.edasaki.misakachan.manga.MangaPage;
import com.edasaki.misakachan.manga.Series;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.source.SearchResult;
import com.edasaki.misakachan.source.SearchResultSet;
import com.edasaki.misakachan.utils.MCache;

/**
 * Created by FARICH on 26/02/2017.
 */
public class BleachMx extends AbstractSource {

    private static final String WEB_SITE_URL = "bleach-mx.net";
    private static final String FULL_WEB_SITE_URL = "http://www.bleach-mx.net/lecture-en-ligne/";
    private static final String SOURCE_NAME = "Bleach-mx (French)";


    @Override
    public boolean match(String url) {
        return null != url && url.contains(WEB_SITE_URL);
    }

    @Override
    public boolean matchInfo(String url) {
        return null != url && url.contains(WEB_SITE_URL);
    }

    @Override
    public Series getSeries(String serieURL) {
        Document serieDocument = MCache.getDocument(serieURL);

        String serieTitle = serieDocument.select("select[name=\"manga\"] > option[selected=\"selected\"]").first().text();
        String serieImageUrl = getImageURL(serieDocument);

        /**
         * TODO: INCOMPLETE
         */

        /*Series serie = new SerieBuilder()
                .withTitle(serieTitle)
                .withImageUrl(serieImageUrl)
                .buildSeries();*/


        Series serie = new Series();
        Elements chapters = serieDocument.select("select[name=\"chapter\"] > option");
        chapters.stream()
                .forEach(chapter -> serie.addChapter(chapter.text(), new StringBuilder().append(serieURL).append("/").append(chapter.attr("value")).toString()));

        return serie;
    }

    @Override
    protected Chapter getChapterFromSite(String chapterUrl) {

        Document doc = MCache.getDocument(chapterUrl);
        String chapterTitle = doc.select("select[name=\"chapter\"] > option[selected=\"selected\"]").first().text();
        String mangaTitle = doc.select("select[name=\"manga\"] > option[selected=\"selected\"]").first().text();
        String chapterNum = new StringBuilder().append(tryParseInt(chapterTitle)).toString();
        List<MangaPage> pages = fetchMangaPagesFromChapter(doc, chapterUrl);
        Chapter chapter = new Chapter(mangaTitle, chapterTitle, chapterNum, pages);
        return chapter;
    }

    private List<MangaPage> fetchMangaPagesFromChapter(Document chapterDocument, String chapterUrl) {

        Element pagesSelect = chapterDocument.select("select[name=\"page\"]").first();
        return pagesSelect.select("option").stream()
                .map(aElement -> new MangaPage(tryParseInt(aElement.text()),
                        getImageURL(new StringBuilder().append(chapterUrl).append("/").append(aElement.attr("value")).toString())))
                .collect(Collectors.toList());
    }

    private Integer tryParseInt(String stringNumber) {
        Integer retVal;
        try {
            retVal = Integer.parseInt(stringNumber);
            return retVal;
        } catch (NumberFormatException nfe) {

            try {
                String[] numbers = stringNumber.replaceAll("[^0-9]+", " ").trim().split(" ");
                return numbers.length > 0 ? Integer.parseInt(numbers[0]) : 0;
            } catch (Exception e) {
                return 0;
            }
        }
    }

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public SearchAction getSearch() {
        return new SearchAction() {
            private AbstractSource myAbstractSource;

            public SearchAction withAbstractSource(AbstractSource abstractSource) {
                myAbstractSource = abstractSource;
                return this;
            }

            @Override
            public SearchResultSet search(String searchString) {
                Collection<SearchResult> results = fetchMangaListFromWebSite(searchString);
                SearchResultSet searchResultSet = new SearchResultSet(myAbstractSource);
                searchResultSet.addResults(results);
                return searchResultSet;
            }

        }.withAbstractSource(this);
    }

    private List<SearchResult> fetchMangaListFromWebSite(String searchSring) {
        Document htmlDocument = MCache.getDocument(FULL_WEB_SITE_URL);
        Elements options = htmlDocument.select("select[name=\"manga\"] > option");
        // Elements options = mangasSelectTag.select("option");
        return options.stream().filter(option -> option.text().contains(searchSring)).map(option -> new SearchResult(option
                .text(), Arrays.asList(option.text()), option.absUrl("value")))
                .collect(Collectors.toList());
    }

    @Override
    public String getImageURL(Document doc) {
        String imageUrl = doc.select("img[class=\"picture\"]").first().absUrl("src");
        return imageUrl;
    }

    public String getImageURL(String pageURL) {
        Document htmlDocument = MCache.getDocument(pageURL);
        return getImageURL(htmlDocument);
    }
}
