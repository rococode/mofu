package com.edasaki.misakachan.web.spark;

import org.json.JSONObject;

import com.edasaki.misakachan.utils.TriConsumer;

import spark.Request;
import spark.Response;

public interface TriRouteBase extends TriConsumer<Request, Response, JSONObject> {

    @Override
    void apply(Request request, Response response, JSONObject jo);
}
