package com.edasaki.misakachan.web.spark.routes;

import org.json.JSONObject;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.web.spark.TriRouteBase;

import spark.Request;
import spark.Response;

public class LookupRoute implements TriRouteBase {

    @Override
    public void apply(Request request, Response response, JSONObject jo) {
        String url = request.body();
        for (AbstractSource source : Misaka.SOURCES) {
            if (source.matchInfo(url)) {
                jo.put("status", "success");
                try {
                    jo.put("series", source.getSeries(url).getSeriesObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        jo.put("status", "failure");
        jo.put("reason", "Invalid URL.");
    }

}
