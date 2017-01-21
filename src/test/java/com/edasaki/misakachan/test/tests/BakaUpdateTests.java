package com.edasaki.misakachan.test.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.edasaki.misakachan.scanlator.BakaUpdateSearcher;
import com.edasaki.misakachan.test.Test;

public class BakaUpdateTests {

    @Test
    public void testSearch() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        BakaUpdateSearcher searcher = new BakaUpdateSearcher();
        Method m = searcher.getClass().getDeclaredMethod("search", String.class);
        m.setAccessible(true);
        m.invoke(searcher, "Toaru Kagaku no Choudenjihou");
        m.invoke(searcher, "asdfoi");
        m.invoke(searcher, "Toaru Kagaku no Railgun");
        m.invoke(searcher, "Ika Musume");
        m.invoke(searcher, "Ichaicha Railgun");
        m.invoke(searcher, "one piece");
        m.invoke(searcher, "naruto");
        m.invoke(searcher, "nozaki-kun");
        m.invoke(searcher, "yotsuba");
        m.invoke(searcher, "toaru index");
        m.invoke(searcher, "toaru accelerator");
        m.invoke(searcher, "tomochan onnanoko");
        m.invoke(searcher, "black haze");
        m.invoke(searcher, "alice borderland");
    }

}
