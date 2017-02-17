package com.edasaki.misakachan.web;

@FunctionalInterface
public interface FinishedCondition {
    public boolean finished(String src);
}
