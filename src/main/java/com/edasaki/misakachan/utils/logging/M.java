package com.edasaki.misakachan.utils.logging;

public class M {

    private final static CustomSecurityManager mySecurityManager = new CustomSecurityManager();
    private static final boolean CHECK_CLASSES = true;

    public static void debug(String s) {
        if (CHECK_CLASSES) {
            Class<?> clazz = getCallerClass(3);
            if (clazz == MTimer.class) {
                clazz = getCallerClass(4);
            }
            if (clazz.isAnnotationPresent(NoDebug.class)) {
                return;
            }
        }
        System.out.println(s);
    }

    public static void debug(Object o) {
        debug(o.toString());
    }

    public static void edb(String s) {
        if (CHECK_CLASSES) {
            Class<?> clazz = getCallerClass(3);
            if (clazz == MTimer.class) {
                clazz = getCallerClass(4);
            }
            if (clazz.isAnnotationPresent(NoDebug.class)) {
                return;
            }
        }
        System.err.println(s);
    }

    public static void edb(Object o) {
        edb(o.toString());
    }

    private static Class<?> getCallerClass(int depth) {
        return mySecurityManager.getCallerClass(depth);
    }

    /** 
     * A custom security manager that exposes the getClassContext() information
     * Source: http://stackoverflow.com/questions/421280
     */
    private static class CustomSecurityManager extends SecurityManager {
        public Class<?> getCallerClass(int callStackDepth) {
            return getClassContext()[callStackDepth];
        }
    }

}
