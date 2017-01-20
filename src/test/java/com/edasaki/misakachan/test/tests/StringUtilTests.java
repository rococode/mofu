package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.test.Test;
import com.edasaki.misakachan.utils.MStringUtils;
import com.google.common.truth.Truth;

public class StringUtilTests {

    // {Manga Name, Better Match, Worse Match}
    private static final String[][] URL_COMPARISONS = {
            { "Toaru Kagaku no Choudenjihou", "Toaru Kagaku no Choudenjihou", "Toaru Kagaku no Choudenjihou SS (Novel)" },
            { "Toaru Kagaku no Choudenjihou", "Toaru Kagaku no Choudenjihou", "Toaru Kagaku no Choudenjihou dj - Toaru ..." },
            { "Toaru Kagaku no Choudenjihou", "Toaru Kagaku no Choudenjihou", "To Aru Kagaku no Choudenjihou dj - Blackout" },
            { "Ika Musume", "Shinryaku! Ika Musume", "Shinryaku! Ika Musume dj - Ika Musumeshi" },
    };

    @Test
    public void testMangaURLComparisons() {
        for (String[] s : URL_COMPARISONS) {
            Truth.assertThat(MStringUtils.similarity(s[0], s[2]) < MStringUtils.similarity(s[0], s[1])).isTrue();
        }
    }
}
