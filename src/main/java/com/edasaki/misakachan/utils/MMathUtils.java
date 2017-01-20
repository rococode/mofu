package com.edasaki.misakachan.utils;

import java.util.Collections;
import java.util.List;

public class MMathUtils {

    public static <E extends Comparable<? super E>> E getMax(List<E> values) {
        return Collections.max(values);
    }

}
