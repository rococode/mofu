package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.manga.Series;
import com.edasaki.misakachan.source.english.MangaHere;
import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;
import com.google.common.truth.Truth;

@TestClass(enabled = false)
public class MangaHereTests {

    @TestMethod
    public void testGetSeries() {
        MangaHere mh = new MangaHere();
        Series series = mh.getSeries("http://www.mangahere.co/manga/gekkan_shoujo_nozaki_kun/");
        Truth.assertThat(series.title).containsMatch("Gekkan Shoujo");
    }

}
