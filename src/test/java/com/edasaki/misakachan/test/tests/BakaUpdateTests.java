package com.edasaki.misakachan.test.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.edasaki.misakachan.scanlator.BakaUpdateSearcher;
import com.edasaki.misakachan.test.Test;
import com.google.common.truth.Truth;

public class BakaUpdateTests {

    private static final String[][] ID_TESTS_MIN = {
            { "Toaru Kagaku no Choudenjihou", "15668" },
            { "cbozjbodivjdsoi", null },
            { "Toaru Kagaku no Railgun", "15668" },
            { "ika musume", "24656" },
            { "Ichaicha Railgun", "38436" },
    };

    private static final String[][] ID_TESTS = {
            { "one piece", "33" },
            { "naruto", "15" },
            { "nozaki-kun", "69388" },
            { "yotsuba", "5" },
            { "toaru index", "13800" },
            { "toaru accelerator", "103281" },
            { "tomochan onnanoko", "120945" },
            { "black haze", "102511" },
            { "alice borderland", "59540" },
    };

    @Test
    public void testSearchFull() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        BakaUpdateSearcher searcher = new BakaUpdateSearcher();
        Method m = searcher.getClass().getDeclaredMethod("getURL", String.class);
        m.setAccessible(true);
        for (String[] s : ID_TESTS) {
            System.out.print("Testing " + Arrays.toString(s) + "... ");
            String res = (String) m.invoke(searcher, s[0]);
            if (s[1] == null) {
                Truth.assertThat(res).isNull();
            } else {
                Truth.assertThat(res).endsWith("?id=" + s[1]);
            }
        }
    }

    @Test
    public void testSearch() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        BakaUpdateSearcher searcher = new BakaUpdateSearcher();
        Method m = searcher.getClass().getDeclaredMethod("getURL", String.class);
        m.setAccessible(true);
        for (String[] s : ID_TESTS_MIN) {
            System.out.print("Testing " + Arrays.toString(s) + "... ");
            String res = (String) m.invoke(searcher, s[0]);
            if (s[1] == null) {
                Truth.assertThat(res).isNull();
            } else {
                Truth.assertThat(res).endsWith("?id=" + s[1]);
            }
        }
    }

}
