package com.edasaki.misakachan.source.french;

import com.edasaki.misakachan.manga.Chapter;
import com.edasaki.misakachan.manga.MangaPage;
import com.edasaki.misakachan.manga.SerieBuilder;
import com.edasaki.misakachan.manga.Series;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.source.SearchResult;
import com.edasaki.misakachan.utils.MCache;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.edasaki.misakachan.source.french.FrenchSourcesCommons.*;

/**
 * Created by FARICH on 26/02/2017.
 */
public class BleachMx extends AbstractSource {

    private static final String WEB_SITE_URL = "bleach";
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

        Elements chapters = serieDocument.select("select[name=\"chapter\"] > option");

        List<Pair<String, String>> chaptersList = chapters.stream()
                .map(chapter -> new ImmutablePair<>(fetchChapterName(chapter), fetchChapterURL(serieURL, chapter)))
                .collect(Collectors.toList());

        return new SerieBuilder()
                .withTitle(serieTitle)
                .withImageUrl(serieImageUrl)
                .withChaptes(chaptersList)
                .buildSeries();
    }

    private String fetchChapterName(Element chapter) {
        return chapter.text();
    }

    private String fetchChapterURL(String serieURL, Element chapter) {
        String chapterURL = new StringBuilder().append(serieURL).append("/").append(chapter.attr("value")).toString();
        return chapterURL;
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

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public SearchAction getSearch() {
        return buildSearchAction(this::fetchMangaListFromWebSite, this);
    }

    public List<SearchResult> fetchMangaListFromWebSite(String searchSring) {
        Document htmlDocument = MCache.getDocument(FULL_WEB_SITE_URL);
        Elements options = htmlDocument.select("select[name=\"manga\"] > option");
        return options.stream().filter(option -> stringMatch(option.text(), searchSring)).map(option -> new SearchResult(option
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
