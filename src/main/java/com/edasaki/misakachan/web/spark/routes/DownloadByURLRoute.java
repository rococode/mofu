package com.edasaki.misakachan.web.spark.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.persistence.ChapterDownloader;
import com.edasaki.misakachan.utils.logging.M;
import com.edasaki.misakachan.web.spark.TriRouteBase;

import spark.Request;
import spark.Response;

public class DownloadByURLRoute implements TriRouteBase {

    @Override
    public void apply(Request request, Response response, JSONObject jo) {
        M.debug("Received batch download request");
        M.debug(request.body());
        try {
            JSONArray arr = new JSONArray(request.body());
            List<Callable<Boolean>> downloads = new ArrayList<Callable<Boolean>>();
            for (int k = 0; k < arr.length(); k++) {
                String url = arr.getString(k);
                downloads.add(() -> {
                    ChapterDownloader.downloadChapterFromURL(url);
                    return true;
                });
            }
            jo.put("count", downloads.size());
            Callable<Void> wrapper = () -> {
                MultiThreadTaskManager.queueTasks(downloads);
                return null;
            };
            MultiThreadTaskManager.queueTask(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
