package com.edasaki.misakachan.web.spark.routes;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.web.spark.TriRouteBase;

import spark.Request;
import spark.Response;

public class FetchResultImagesRoute implements TriRouteBase {

    public static final Map<String, String> cachedURLToLocalImage = new HashMap<String, String>();
    private int lastCachedSize = 0;
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
            while (!cachedURLToLocalImage.containsKey(url)) {
                if (cachedURLToLocalImage.size() != lastCachedSize && counter++ > 5 * 10) { //5 is one second, max 10 sec hang per image
                    break;
                }
                System.out.println("Waiting for " + url + " in " + cachedURLToLocalImage);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("id", id);
            jsonobject.put("imgUrl", cachedURLToLocalImage.getOrDefault(url, "http://edasaki.com/i/test-page.png"));
            ja.put(jsonobject);
        }
        lastCachedSize = cachedURLToLocalImage.size();
    }

}
