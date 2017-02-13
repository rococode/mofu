package com.edasaki.misakachan.persistence;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.manga.Chapter;
import com.edasaki.misakachan.manga.MangaPage;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.utils.logging.M;

public class ChapterDownloader {

    public static Chapter getChapterFromURL(String url) {
        for (AbstractSource source : Misaka.instance().spark.sources) {
            if (source.match(url)) {
                Chapter chapter = source.getChapter(url);
                return chapter;
            }
        }
        return null;
    }

    public static JSONObject getChapterFromURLAsJSON(String url) {
        Chapter chapter = getChapterFromURL(url);
        if (chapter == null) {
            return null;
        }
        JSONObject jo = new JSONObject();
        jo.put("type", "url");
        jo.put("source", chapter.source.getSourceName());
        jo.put("mangaName", chapter.getMangaTitle());
        jo.put("chapterName", chapter.getChapterName());
        jo.put("chapterNumber", chapter.getChapterNumber());
        jo.put("pagecount", chapter.getPageCount());
        JSONArray urls = new JSONArray();
        for (MangaPage p : chapter.getPages())
            urls.put(p.getURL());
        jo.put("urls", urls);
        return jo;

    }

    public static boolean downloadChapterFromURL(String url) {
        M.debug("Beginning download of " + url);
        Chapter chapter = getChapterFromURL(url);
        if (chapter == null) {
            return false;
        }
        Misaka.instance().persist.saveChapter(chapter.getMangaTitle(), chapter.source.getSourceName(), chapter.getChapterNumber(), chapter.getPages());
        return true;
    }
}
