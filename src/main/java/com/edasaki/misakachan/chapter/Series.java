package com.edasaki.misakachan.chapter;

import java.lang.reflect.Field;
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
    public String source;
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
                    f.set(this, ((String) f.get(this)).trim());
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
        if (this.scanlatorArr != null)
            jo.put("scanlator", this.scanlatorArr);
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