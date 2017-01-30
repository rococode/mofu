package com.edasaki.misakachan.chapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Series {
    public String imageURL;
    public String title;
    public String authors;
    public String artists;
    public String genres;
    public String altNames;
    public String description;
    private List<ChapterListing> chapters = new ArrayList<ChapterListing>();

    public void addChapter(String name, String url) {
        chapters.add(new ChapterListing(name, url));
    }

    public JSONObject getSeriesObject() {
        JSONObject jo = new JSONObject();
        jo.put("imageURL", imageURL);
        jo.put("title", title);
        jo.put("authors", authors);
        jo.put("artists", artists);
        jo.put("genres", genres);
        jo.put("altNames", altNames);
        jo.put("description", description);
        jo.put("chapters", getChaptersJSON());
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