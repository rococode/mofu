package com.edasaki.misakachan.utils.logging;

public class MTimer {
    private long start;

    public MTimer() {
        start = System.currentTimeMillis();
    }

    public void reset() {
        start = System.currentTimeMillis();
    }

    public void output() {
        M.debug("Time elapsed since start: " + (System.currentTimeMillis() - start) + " ms");
    }

    public void output(String s) {
        M.debug(s + ": " + (System.currentTimeMillis() - start) + " ms");
    }

    public void outputAndReset() {
        output();
        reset();
    }

    public void outputAndReset(String s) {
        output(s);
        reset();
    }
}
