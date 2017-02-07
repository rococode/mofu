package com.edasaki.misakachan.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import com.edasaki.misakachan.utils.logging.M;

public class MStringUtils {

    /**
     * @deprecated buggy / not fully functional
     */
    @Deprecated
    public static <T> Map<T, String> getTopResults2(Map<T, List<String>> map, String toMatch, int count) {
        Map<T, String> bestNames = new HashMap<T, String>();
        Map<T, Double> bestSims = new HashMap<T, Double>();
        for (Entry<T, List<String>> e : map.entrySet()) {
            double bestSimilarity = 0;
            String bestName = "null";
            List<String> contained = new ArrayList<String>();
            for (String s : e.getValue()) {
                M.debug("checking " + s);
                double sim = MStringUtils.similarityMaxContains(s, toMatch);
                M.debug("sim of " + s + " and " + toMatch + ": " + sim);
                if (sim > bestSimilarity) {
                    bestName = s;
                    bestSimilarity = sim;
                }
                if (sim == 1.0) {
                    contained.add(s);
                }
                sim = MStringUtils.similarityMaxContainsAlphanumeric(s, toMatch);
                M.debug("asim of " + s + " and " + toMatch + ": " + sim);
                if (sim > bestSimilarity) {
                    bestName = s;
                    bestSimilarity = sim;
                }
                if (sim == 1.0) {
                    contained.add(s);
                }
            }
            if (contained.size() > 1) {
                bestSimilarity = 0;
                for (String s : contained) {
                    double sim = MStringUtils.similarity(s, toMatch);
                    if (sim > bestSimilarity) {
                        bestName = s;
                        bestSimilarity = sim;
                    }
                }
            }
            bestNames.put(e.getKey(), bestName);
            bestSims.put(e.getKey(), bestSimilarity);
        }
        M.debug("best NAmes: " + bestNames);
        M.debug("best Sims: " + bestSims);
        List<T> topKeys = topNKeysByValue(bestSims, count);
        Map<T, String> resultMap = new LinkedHashMap<T, String>();
        for (T link : topKeys) {
            resultMap.put(link, bestNames.get(link));
        }
        return resultMap;
    }

    public static <T> Map<T, String> getTopResults(Map<T, List<String>> map, String toMatch, int count) {
        Map<T, String> bestNames = new HashMap<T, String>();
        for (Entry<T, List<String>> e : map.entrySet()) {
            double bestSimilarity = 0;
            String bestName = "null";
            List<String> contained = new ArrayList<String>();
            for (String s : e.getValue()) {
                double sim = MStringUtils.similarityMaxContains(s, toMatch);
                if (sim > bestSimilarity) {
                    bestName = s;
                    bestSimilarity = sim;
                }
                sim = MStringUtils.similarityMaxContainsAlphanumeric(s, toMatch);
                if (sim > bestSimilarity) {
                    bestName = s;
                    bestSimilarity = sim;
                }
                if (sim == 1.0) {
                    contained.add(s);
                }
            }
            if (contained.size() > 1) {
                bestSimilarity = 0;
                for (String s : contained) {
                    double sim = MStringUtils.similarity(s, toMatch);
                    if (sim > bestSimilarity) {
                        bestName = s;
                        bestSimilarity = sim;
                    }
                }
            }
            bestNames.put(e.getKey(), bestName);
        }
        //        M.debug("best Names: " + bestNames);

        PriorityQueue<T> topN = new PriorityQueue<T>(count, new Comparator<T>() {
            public int compare(T t1, T t2) {
                String t1Val = bestNames.get(t1);
                String t2Val = bestNames.get(t2);
                double sim1_1 = MStringUtils.similarityMaxContains(t1Val, toMatch);
                double sim1_2 = MStringUtils.similarityMaxContainsAlphanumeric(t1Val, toMatch);
                double sim2_1 = MStringUtils.similarityMaxContains(t2Val, toMatch);
                double sim2_2 = MStringUtils.similarityMaxContainsAlphanumeric(t2Val, toMatch);
                double sim1 = Math.min(sim1_1, sim1_2);
                double sim2 = Math.min(sim2_1, sim2_2);
                if (sim1 == 1.0 && sim2 == 1.0) {
                    sim1 = MStringUtils.similarity(t1Val, toMatch);
                    sim2 = MStringUtils.similarity(t2Val, toMatch);
                }
                int res = Double.compare(sim2, sim1);
                //                M.debug("res of compare " + t1Val + " and " + t2Val + ": " + res);
                return res;
            }
        });
        topN.addAll(bestNames.keySet());
        //        M.debug("DEBUG: " + topN);
        Map<T, String> resultMap = new LinkedHashMap<T, String>();
        for (int k = 0; k < count; k++) {
            T next = topN.poll();
            //            M.debug("polled " + next);
            if (next != null && bestNames.containsKey(next)) {
                resultMap.put(next, bestNames.get(next));
            }
        }
        return resultMap;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> topNKeysByValue(final Map<T, Double> map, int n) {
        PriorityQueue<T> topN = new PriorityQueue<T>(n, new Comparator<T>() {
            public int compare(T t1, T t2) {
                return Double.compare(map.get(t2), map.get(t1));
            }
        });
        for (T key : map.keySet()) {
            if (topN.size() < n)
                topN.add(key);
            else if (map.get(topN.peek()) < map.get(key)) {
                topN.poll();
                topN.add(key);
            }
        }
        return (List<T>) Arrays.asList(topN.toArray());
    }

    public static double similarityMaxContains(String parent, String substring) {
        parent = parent.toLowerCase();
        substring = substring.toLowerCase();
        if (parent.contains(substring))
            return 1.0;
        return similarity(parent, substring);

    }

    public static double similarityMaxContainsAlphanumeric(String parent, String substring) {
        parent = parent.toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
        substring = substring.toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
        if (parent.contains(substring))
            return 1.0;
        return similarity(parent, substring);

    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Levenshtein Edit Distance implementation
    private static int editDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] costs = new int[b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            int last = i;
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newVal = costs[j - 1];
                        if (a.charAt(i - 1) != b.charAt(j - 1))
                            newVal = Math.min(Math.min(newVal, last), costs[j]) + 1;
                        costs[j - 1] = last;
                        last = newVal;
                    }
                }
            }
            if (i > 0)
                costs[b.length()] = last;
        }
        return costs[b.length()];
    }

}
