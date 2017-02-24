package com.edasaki.misakachan.multithread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jsoup.nodes.Document;

import com.edasaki.misakachan.utils.MCache;

public class MultiThreadTaskManager {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final ExecutorService ORDERED_EXECUTOR = Executors.newFixedThreadPool(4);

    public static <T> Future<T> queueTask(Callable<T> callable) {
        return EXECUTOR.submit(callable);
    }

    public static <T> List<Future<T>> queueTasks(Collection<Callable<T>> callables) {
        List<Future<T>> futures = new ArrayList<Future<T>>();
        for (Callable<T> c : callables)
            futures.add(EXECUTOR.submit(c));
        return futures;
    }

    public static <T> boolean allReady(Collection<Future<T>> futures) {
        for (Future<T> f : futures)
            if (!f.isDone())
                return false;
        return true;
    }

    public static List<Document> getDocuments(Collection<String> urls) {
        List<Future<Document>> futures = new ArrayList<Future<Document>>();
        for (String pageURL : urls) {
            Callable<Document> c = () -> {
                Document doc = MCache.getDocument(pageURL);
                return doc;
            };
            futures.add(MultiThreadTaskManager.queueTask(c));
        }
        wait(futures);
        List<Document> docs = new ArrayList<Document>();
        for (Future<Document> f : futures) {
            try {
                docs.add(f.get(3, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        return docs;
    }

    public static <T> void wait(Collection<Future<T>> futures) {
        while (!allReady(futures)) {
            try {
                Thread.sleep(20L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> Future<T> queueTaskOrdered(Callable<T> callable) {
        return ORDERED_EXECUTOR.submit(callable);
    }

    public static <T> List<Future<T>> queueTasksOrdered(Collection<Callable<T>> callables) {
        List<Future<T>> futures = new ArrayList<Future<T>>();
        for (Callable<T> c : callables)
            futures.add(ORDERED_EXECUTOR.submit(c));
        return futures;
    }
}
