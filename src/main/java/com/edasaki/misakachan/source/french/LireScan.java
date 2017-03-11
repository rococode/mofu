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
public class LireScan extends AbstractSource {

    private static final String WEB_SITE_URL = "lirescan.net";
    private static final String FULL_WEB_SITE_URL = "http://www.lirescan.net";
    private static final String SOURCE_NAME = "Lire Scan (French)";


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

        String serieTitle = serieDocument.select("select#mangas > option[selected=\"selected\"]").first().text();
        String serieImageUrl = getImageURL(serieDocument);

        Elements chapters = serieDocument.select("select#chapitres > option");
        List<Pair<String, String>> chaptersList = chapters.stream()
                .map(chapter -> new ImmutablePair<>(chapter.text(), chapter.absUrl("value")))
                .collect(Collectors.toList());

        return new SerieBuilder()
                .withTitle(serieTitle)
                .withImageUrl(serieImageUrl)
                .withChaptes(chaptersList)
                .buildSeries();
    }


    @Override
    protected Chapter getChapterFromSite(String chapterUrl) {

        Document doc = MCache.getDocument(chapterUrl);
        String chapterTitle = doc.select("h1").first().text();
        String mangaTitle = doc.select("title").first().text();
        String chapterNum = doc.select("select#chapitres > option[selected=\"selected\"]").first().text();
        List<MangaPage> pages = fetchMangaPagesFromChapter(doc);
        Chapter chapter = new Chapter(mangaTitle, chapterTitle, chapterNum, pages);
        return chapter;
    }

    private List<MangaPage> fetchMangaPagesFromChapter(Document chapterDocument) {

        Element pagesSelect = chapterDocument.select("nav#pagination").first();
        return pagesSelect.select("a").stream()
                .filter(aElement -> !(aElement.attr("id").equals("back_chapter") || aElement
                        .attr("id").equals("next_link")))
                .map(aElement -> new MangaPage(tryParseInt(aElement.text()), getImageURL(aElement.absUrl("href"))))
                .collect(Collectors.toList());

    }


    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public SearchAction getSearch() {
        return buildSearchAction(this::fetchMangaListFromWebSite ,this);
    }

    private List<SearchResult> fetchMangaListFromWebSite(String searchSring) {
        Document htmlDocument = MCache.getDocument(FULL_WEB_SITE_URL);
        Element mangasSelectTag = htmlDocument.select("select#mangas").first();
        Elements options = mangasSelectTag.select("option");
        return options.stream().filter(option -> stringMatch(option.text() , searchSring)).map(option -> new SearchResult(option
                .text(), Arrays.asList(option.text()), option.absUrl("value")))
                .collect(Collectors.toList());
    }

    @Override
    public String getImageURL(Document doc) {
        String imageUrl = doc.select("img#image_scan").first().absUrl("src");
        return imageUrl;
    }

    public String getImageURL(String pageURL) {
        Document htmlDocument = MCache.getDocument(pageURL);
        String imageUrl = htmlDocument.select("img#image_scan").first().absUrl("src");
        return imageUrl;
    }
}
