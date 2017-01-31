package com.edasaki.misakachan.scanlator;

import org.json.JSONObject;

public class ScanGroup {
    protected int id;
    protected String name;
    protected String url;

    public ScanGroup(int id, String name) {
        this.id = id;
        this.name = name;
        this.url = "https://www.mangaupdates.com/groups.html?id=" + id;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ScanGroup))
            return false;
        if (other == this)
            return true;
        return this.id == ((ScanGroup) other).id;
    }

    @Override
    public String toString() {
        return "GROUP:" + name;
    }

    public JSONObject getJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("name", name);
        jo.put("url", url);
        return jo;
    }

}
