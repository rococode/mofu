package com.edasaki.misakachan.utils;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
    void apply(A a, B b, C c);
}