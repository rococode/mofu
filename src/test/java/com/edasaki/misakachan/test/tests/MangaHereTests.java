package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.chapter.Series;
import com.edasaki.misakachan.source.english.MangaHere;
import com.edasaki.misakachan.test.Test;
import com.google.common.truth.Truth;

public class MangaHereTests {

    @Test
    public void testGetSeries() {
        MangaHere mh = new MangaHere();
        Series series = mh.getSeries("http://www.mangahere.co/manga/gekkan_shoujo_nozaki_kun/");
        Truth.assertThat(series.title).containsMatch("Gekkan Shoujo");
    }

}
