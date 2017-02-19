package com.edasaki.misakachan.manga;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.source.AbstractSource;

public class Series {
    public String imageURL = AbstractSource.DEFAULT_IMAGE;
    public String title = AbstractSource.DEFAULT_TITLE;
    public String authors = AbstractSource.DEFAULT_AUTHOR;
    public String artists = AbstractSource.DEFAULT_ARTIST;
    public String genres = AbstractSource.DEFAULT_GENRE;
    public String altNames = AbstractSource.DEFAULT_ALTNAMES;
    public String description = AbstractSource.DEFAULT_DESCRIPTION;
    public String source = AbstractSource.DEFAULT_SOURCE;
    private JSONArray scanlatorArr;
    private List<ChapterListing> chapters = new ArrayList<ChapterListing>();

    private boolean processed = false;

    public void addChapter(String name, String url) {
        chapters.add(new ChapterListing(name, url));
    }

    private void postProcess() {
        processed = true;
        for (Field f : Series.class.getFields()) {
            if (f.getType() == String.class) {
                try {
                    String curr = ((String) f.get(this));
                    if (curr != null) {
                        f.set(this, curr.trim());
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //        String url = Misaka.getInstance().baka.getURL(this.title);
        //        JSONArray jo = new JSONArray();
        //        if (url != null) {
        //            List<ScanGroup> groups = Misaka.instance().baka.getScanlator(url);
        //            if (groups != null) {
        //                for (ScanGroup sg : groups) {
        //                    jo.put(sg.getJSONObject());
        //                }
        //            }
        //        }
        //        this.scanlatorArr = jo;
    }

    public JSONObject getSeriesObject() {
        if (!processed) {
            postProcess();
        }
        JSONObject jo = new JSONObject();
        jo.put("imageURL", imageURL);
        jo.put("title", title);
        jo.put("authors", authors);
        jo.put("artists", artists);
        jo.put("genres", genres);
        jo.put("altNames", altNames);
        jo.put("description", description);
        jo.put("source", source);
        jo.put("chapters", getChaptersJSON());
        if (this.scanlatorArr != null) {
            jo.put("scanlator", this.scanlatorArr);
        }
        return jo;
    }

    public JSONArray getChaptersJSON() {
        JSONArray jo = new JSONArray();
        for (ChapterListing cl : chapters) {
            JSONObject clo = new JSONObject();
            clo.put("name", cl.name);
            clo.put("url", cl.url);
            jo.put(clo);
        }
        return jo;
    }

    private static class ChapterListing {

        private String name, url;

        public ChapterListing(String name, String url) {
            this.name = name;
            this.url = url;
        }

    }
}