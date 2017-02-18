package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.source.english.KissManga;
import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;
import com.google.common.truth.Truth;

@TestClass(enabled = true, solo = true)
public class KissMangaTests {

    private static final KissManga km = new KissManga();

    @TestMethod
    public void testMatches() {
        Truth.assertThat(km.match("http://kissmanga.com/Manga/Onepunch-Man")).isTrue();
        Truth.assertThat(km.match("http://kissmango.com/Manga/Onepunch-Man")).isFalse();
        Truth.assertThat(km.matchInfo("http://kissmanga.com/Manga/Onepunch-Man")).isTrue();
        Truth.assertThat(km.matchInfo("kissmanga/blah")).isFalse();
        Truth.assertThat(km.matchInfo("http://kissmanga.com/")).isFalse();
    }

    @TestMethod
    public void testFetchURL() {
        KissManga km = new KissManga();
        SearchAction sa = km.getSearch();
        sa.search("ika musume");
    }

}
