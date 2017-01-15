package com.edasaki.misakachan.persistence.options;

public enum Option {
    TEST("test default for test option");
    private String def;

    Option(String def) {
        this.def = def;
    }

    public String getDefault() {
        return def;
    }
}
