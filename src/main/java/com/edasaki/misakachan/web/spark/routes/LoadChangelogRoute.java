package com.edasaki.misakachan.web.spark.routes;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.web.spark.TriRouteBase;

import spark.Request;
import spark.Response;

public class LoadChangelogRoute implements TriRouteBase {

    @Override
    public void apply(Request request, Response response, JSONObject jo) {
        List<String> ls = Misaka.instance().changelog.getChangelog();
        JSONArray lines = new JSONArray();
        if (ls == null || ls.size() == 0) {
            lines.put("Failed to fetch latest changelog - probably not a big deal.");
            jo.put("lines", lines);
        } else {
            for (String s : ls)
                lines.put(s);
            jo.put("lines", lines);
        }
    }

}
