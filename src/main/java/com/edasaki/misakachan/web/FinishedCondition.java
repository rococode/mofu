package com.edasaki.misakachan.web;

import org.jsoup.Connection.Response;

public abstract class FinishedCondition<E> extends AbstractExtra {
    public abstract boolean finished(E res);

    private FinishedCondition() {
    }

    public abstract static class FinishedStringCondition extends FinishedCondition<String> {
        public abstract boolean finished(String res);
    }

    public abstract static class FinishedResponseCondition extends FinishedCondition<Response> {
        public abstract boolean finished(Response res);
    }
}
