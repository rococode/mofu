package com.edasaki.misakachan.web.spark.routes;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.utils.MCache;
import com.edasaki.misakachan.web.spark.TriRouteBase;

import spark.Request;
import spark.Response;

public class FetchResultImagesRoute implements TriRouteBase {

    @Override
    public void apply(Request request, Response response, JSONObject jo) {
        JSONArray ja = new JSONArray();
        jo.put("arr", ja);
        JSONArray urlArray = new JSONArray(request.body());
        for (int k = 0; k < urlArray.length(); k++) {
            int counter = 0;
            JSONObject obj = urlArray.getJSONObject(k);
            String id = obj.getString("id");
            String url = obj.getString("url");
            while (!MCache.isCached(url)) {
                if (counter++ > 5 * 10) { //5 is one second, max 10 sec hang per image
                    break;
                }
                System.out.println("Waiting for MCache to cache " + url);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("id", id);
            jsonobject.put("imgUrl", MCache.getCachedImage(url));
            ja.put(jsonobject);
        }
    }

}
