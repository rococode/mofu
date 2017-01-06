package com.edasaki.misakachan.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.edasaki.misakachan.test.tests.TestTests;

public class TestRunner {

    public static void main(String[] args) {
        Class<TestTests> obj = TestTests.class;
        int passed = 0, failed = 0, count = 0, ignore = 0;
        for (Method method : obj.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                Annotation annotation = method.getAnnotation(Test.class);
                Test test = (Test) annotation;
                if (test.enabled()) {
                    try {
                        method.invoke(obj.newInstance());
                        System.out.printf("%s - Test '%s' - passed %n", ++count, method.getName());
                        passed++;
                    } catch (Throwable ex) {
                        System.out.printf("%s - Test '%s' - failed: %s %n", ++count, method.getName(), ex.getCause());
                        failed++;
                    }
                } else {
                    System.out.printf("%s - Test '%s' - ignored%n", ++count, method.getName());
                    ignore++;
                }
            }
        }
        System.out.printf("%nResult: Total: %d, Passed: %d, Failed: %d, Ignored: %d%n", count, passed, failed, ignore);
        if (failed > 0) {
            System.out.println("Warning: Failed more than one test.");
        }
    }

}