package com.edasaki.misakachan.source;

@FunctionalInterface
public interface SearchAction {

    public SearchResultSet search(String s);

}
