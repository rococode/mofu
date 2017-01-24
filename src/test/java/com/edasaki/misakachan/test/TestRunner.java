package com.edasaki.misakachan.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.edasaki.misakachan.test.tests.BakaUpdateTests;
import com.edasaki.misakachan.test.tests.BasicTests;
import com.edasaki.misakachan.test.tests.StringUtilTests;
import com.edasaki.misakachan.test.tests.UpdateTests;

public class TestRunner {

    private static final Class<?>[] TEST_CLASSES = {
            BasicTests.class,
            BakaUpdateTests.class,
            StringUtilTests.class,
            UpdateTests.class
    };

    public static void main(String[] args) {
        System.setProperty("http.agent", "");
        int passed = 0, failed = 0, count = 0, ignore = 0;
        for (Class<?> obj : TEST_CLASSES) {
            for (Method method : obj.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    Annotation annotation = method.getAnnotation(Test.class);
                    Test test = (Test) annotation;
                    if (test.enabled()) {
                        try {
                            method.invoke(obj.newInstance());
                            System.out.printf("%s - Test '%s.%s' - passed %n", ++count, obj.getName(), method.getName());
                            passed++;
                        } catch (Throwable ex) {
                            System.out.printf("%s - Test '%s.%s' - failed: %s%n", ++count, obj.getName(), method.getName(), ex.getCause());
                            ex.printStackTrace();
                            failed++;
                        }
                    } else {
                        System.out.printf("%s - Test '%s.%s' - ignored%n", ++count, obj.getName(), method.getName());
                        ignore++;
                    }
                }
            }
        }
        System.out.printf("%nResult: Total: %d, Passed: %d, Failed: %d, Ignored: %d%n", count, passed, failed, ignore);
        if (failed > 0) {
            System.out.println("Warning: Failed more than one test.");
        }
    }

}