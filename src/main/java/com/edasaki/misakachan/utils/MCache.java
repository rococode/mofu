package com.edasaki.misakachan.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;

import com.edasaki.misakachan.utils.logging.M;
import com.edasaki.misakachan.web.WebAccessor;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class MCache {

    private static boolean initialized = false;

    private static LoadingCache<String, Document> documentCache;

    private synchronized static void initialize() {
        if (initialized) {
            return;
        }
        documentCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<String, Document>() {
            public Document load(String url) {
                return WebAccessor.getURL(url);
            }
        });
        initialized = true;
    }

    static {
        initialize();
    }

    public static Document getDocument(String url) {
        if (!initialized) {
            initialize();
        }
        if (url == null) {
            return null;
        }
        try {
            return documentCache.get(url);
        } catch (ExecutionException e) {
            M.edb("Failed on " + url);
            e.printStackTrace();
        }
        return null;
    }
}
