package com.edasaki.misakachan.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;

public class TestRunner {

    private static final Set<Class<?>> specifics = new HashSet<Class<?>>();

    public static void main(String[] args) {
        // is this super hacky code? yeah.. does it matter that much? ehhhh not really hehe
        // only run once at the beginning of testing anyways
        Reflections reflections = new Reflections("com.edasaki.misakachan.test");
        Set<Class<?>> annotatedSet = reflections.getTypesAnnotatedWith(TestClass.class);
        List<Class<?>> annotatedList = new ArrayList<Class<?>>();
        annotatedList.addAll(annotatedSet);
        annotatedList.sort(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (Class<?> clazz : annotatedList) {
            if (clazz.getAnnotation(TestClass.class).solo()) {
                specifics.add(clazz);
            }
        }
        int passed = 0, failed = 0, count = 0, ignore = 0;
        Annotation annotation;
        for (Class<?> clazz : annotatedList) {
            boolean autoskip = false;
            if (clazz.isAnnotationPresent(TestClass.class) && !((TestClass) clazz.getAnnotation(TestClass.class)).enabled()) {
                autoskip = true;
            }
            if (specifics.size() > 0 && !specifics.contains(clazz)) {
                autoskip = true;
            }
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(TestMethod.class)) {
                    annotation = method.getAnnotation(TestMethod.class);
                    TestMethod test = (TestMethod) annotation;
                    if (!autoskip && test.enabled()) {
                        try {
                            method.invoke(clazz.newInstance());
                            System.out.printf("%s - Test '%s.%s' - passed %n", ++count, chop(clazz.getName()), method.getName());
                            passed++;
                        } catch (Throwable ex) {
                            System.out.printf("%s - Test '%s.%s' - FAILED%n", ++count, chop(clazz.getName()), method.getName());
                            ex.printStackTrace();
                            failed++;
                        }
                    } else {
                        System.out.printf("%s - Test '%s.%s' - ^ignored^%n", ++count, chop(clazz.getName()), method.getName());
                        ignore++;
                    }
                }
            }
        }
        System.out.printf("%nResult: Total: %d, Passed: %d, Failed: %d, Ignored: %d%n", count, passed, failed, ignore);
        if (failed > 0) {
            System.err.println("Warning: Failed more than one test.");
        }
        System.exit(0);
    }

    // because I don't want to import an entire stringutils library for one thing lol
    private static final String chop(String s) {
        int n = 2;
        int pos = s.lastIndexOf('.');
        while (--n > 0 && pos != -1)
            pos = s.lastIndexOf('.', pos + 1);
        return s.substring(pos + 1);
    }

}