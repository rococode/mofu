package com.edasaki.misakachan.source;

import com.edasaki.misakachan.chapter.Chapter;

public abstract class AbstractSource {

    /**
     * @param url the url to match
     * @return <code>true</code> if the given URL matches this source's URL pattern
     */
    public abstract boolean match(String url);
    
    public abstract Chapter getChapter(String url);

    public abstract String getSourceName();

    @Override
    public String toString() {
        return this.getSourceName();
    }
}
