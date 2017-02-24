package com.edasaki.misakachan.web.spark.routes;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.utils.logging.M;
import com.edasaki.misakachan.web.spark.TriRouteBase;

import spark.Request;
import spark.Response;

public class DownloadSingleRoute implements TriRouteBase {

    @Override
    public void apply(Request request, Response response, JSONObject jo) {
        try {
            JSONObject o = new JSONObject(request.body());
            M.debug(o.toString(4));
            String title = o.getString("mangaName").trim();
            String source = o.getString("source");
            String chapterNumber = o.getString("chapterNumber");
            JSONArray arr = o.getJSONArray("arr");
            Object[][] pages = new Object[arr.length()][2];
            for (int k = 0; k < arr.length(); k++) {
                JSONObject obj = arr.getJSONObject(k);
                int pageNumber = obj.getInt("number");
                String src = obj.getString("src");
                pages[k] = new Object[] { pageNumber, src };
            }
            Misaka.instance().persist.saveChapter(title, source, chapterNumber, pages);
            jo.put("status", "success");
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        jo.put("status", "failure");
    }

}
