package com.edasaki.misakachan.utils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.edasaki.misakachan.utils.logging.M;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class MCacheUtils {

    private static boolean initialized = false;

    private static LoadingCache<String, Document> documentCache;

    private static void initialize() {
        initialized = true;
        documentCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, Document>() {
            public Document load(String url) {
                try {
                    M.debug("Connecting to " + url + "...");
                    return Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public static Document getDocument(String url) {
        if (!initialized) {
            initialize();
        }
        try {
            return documentCache.get(url);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
