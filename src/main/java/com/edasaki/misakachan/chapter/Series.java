package com.edasaki.misakachan.chapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Series {
    public String title;
    public String authors;
    public String artists;
    public String genres;
    public String description;
    private List<ChapterListing> chapters = new ArrayList<ChapterListing>();

    public void addChapter(String name, String url) {
        chapters.add(new ChapterListing(name, url));
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