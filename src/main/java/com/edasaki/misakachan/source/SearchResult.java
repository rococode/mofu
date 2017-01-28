package com.edasaki.misakachan.source;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    public final String title;
    private final List<String> altNames;
    public final String url;

    public SearchResult(String title, List<String> altNames, String url) {
        this.title = title;
        this.altNames = new ArrayList<String>();
        if (altNames != null)
            this.altNames.addAll(altNames);
        this.url = url;
    }

    public String getAltNames() {
        StringBuilder sb = new StringBuilder();
        for (String s : altNames) {
            sb.append(s);
            sb.append(',');
            sb.append(' ');
        }
        String built = sb.toString().trim();
        if (built.endsWith(","))
            built = built.substring(0, built.length() - 1);
        return built;
    }

    @Override
    public String toString() {
        return title + "::" + url;
    }

}
