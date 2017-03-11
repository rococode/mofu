package com.edasaki.misakachan.manga;

import com.edasaki.misakachan.source.AbstractSource;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

/**
 * Created by Farich on 26/02/2017.
 */
public class SerieBuilder {

    private String imageURL = AbstractSource.DEFAULT_IMAGE;
    private String title = AbstractSource.DEFAULT_TITLE;
    private String authors = AbstractSource.DEFAULT_AUTHOR;
    private String artists = AbstractSource.DEFAULT_ARTIST;
    private String genres = AbstractSource.DEFAULT_GENRE;
    private String altNames = AbstractSource.DEFAULT_ALTNAMES;
    private String description = AbstractSource.DEFAULT_DESCRIPTION;
    private String source = AbstractSource.DEFAULT_SOURCE;
    private List<Pair<String, String>> chapters = Collections.emptyList();

    public SerieBuilder withImageUrl(String imageUrl) {
        this.imageURL = imageUrl;
        return this;
    }

    public SerieBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public SerieBuilder withAuthors(String authors) {
        this.authors = authors;
        return this;
    }

    public SerieBuilder withArtists(String artists) {
        this.artists = artists;
        return this;
    }

    public SerieBuilder withGenres(String genres) {
        this.genres = genres;
        return this;
    }

    public SerieBuilder withAltNames(String altNames) {
        this.altNames = altNames;
        return this;
    }

    public SerieBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public SerieBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    public SerieBuilder withChaptes(List<Pair<String, String>> chapters) {
        this.chapters = chapters;
        return this;

    }

    public Series buildSeries() {
        Series mySerie = new Series();
        mySerie.imageURL = imageURL;
        mySerie.title = title;
        mySerie.authors = authors;
        mySerie.artists = artists;
        mySerie.genres = genres;
        mySerie.altNames = altNames;
        mySerie.description = description;
        mySerie.source = source;

        this.chapters.stream()
                .forEach(myChapter -> mySerie.addChapter(myChapter.getKey(),myChapter.getValue()));

        return mySerie;

    }



}
