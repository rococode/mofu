package com.edasaki.misakachan.backend.utils;

import java.util.Collections;
import java.util.List;

public class MathUtils {

    public static <E extends Comparable<? super E>> E getMax(List<E> values) {
        return Collections.max(values);
    }

}
