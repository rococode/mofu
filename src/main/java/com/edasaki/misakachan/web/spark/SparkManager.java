package com.edasaki.misakachan.web.spark;

import org.json.JSONObject;

import com.edasaki.misakachan.web.WebAccessor;
import com.edasaki.misakachan.web.spark.routes.DownloadByURLRoute;
import com.edasaki.misakachan.web.spark.routes.DownloadSingleRoute;
import com.edasaki.misakachan.web.spark.routes.FetchResultImagesRoute;
import com.edasaki.misakachan.web.spark.routes.LoadChangelogRoute;
import com.edasaki.misakachan.web.spark.routes.LoadRequestedURLRoute;
import com.edasaki.misakachan.web.spark.routes.LookupRoute;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SparkManager {

    private static final Object[][] POST = {
            { "/load", new LoadRequestedURLRoute() },
            { "/lookup", new LookupRoute() },
            { "/fetchResultImages", new FetchResultImagesRoute() },
            { "/download", new DownloadSingleRoute() },
            { "/downloadbatch", new DownloadByURLRoute() },
    };

    private static final Object[][] GET = {
            { "/changelog", new LoadChangelogRoute() },
    };

    public SparkManager() {
    }

    public void startWebserver() {
        Spark.port(10032);
        Spark.staticFileLocation("/public");
        Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });
        for (Object[] o : POST) {
            Spark.post((String) o[0], processRouteObj((TriRouteBase) o[1]));
        }
        for (Object[] o : GET) {
            Spark.get((String) o[0], processRouteObj((TriRouteBase) o[1]));
        }
    }

    private final Route processRouteObj(TriRouteBase consumer) {
        Route r = new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                JSONObject jo = new JSONObject();
                consumer.apply(request, response, jo);
                WebAccessor.appendCookies(jo);
                return jo;
            }
        };
        return r;
    }

    public int getPort() {
        return Spark.port();
    }

}
