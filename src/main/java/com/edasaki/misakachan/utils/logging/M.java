package com.edasaki.misakachan.utils.logging;

public class M {
    
    private static final boolean CHECK_CLASSES = true;
    
    public static void debug(String s) {
        System.out.println(s);
    }
    
    public static void debug(Object o) {
        debug(o.toString());
    }
}
