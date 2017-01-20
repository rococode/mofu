package com.edasaki.misakachan.utils;

public class MStringUtils {

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
