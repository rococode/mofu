package com.edasaki.misakachan.manga;

public class SeriesBuilder {
private Series curr;
    
    
    public SeriesBuilder() {
        curr = new Series();
    }
    
    public SeriesBuilder withTitle(String title) {
        curr.title = title;
        return this;
    }
    
    public SeriesBuilder withImageURL(String imageURL) {
        curr.imageURL = imageURL;
        return this;
    }
    
    public SeriesBuilder withAuthors(String authors) {
        curr.authors = authors;
        return this;
    }
    
    public SeriesBuilder withArtists(String artists) {
        curr.artists = artists;
        return this;
    }
    
    public SeriesBuilder withGenres(String genres) {
        curr.genres= genres;
        return this;
    }
    
    public SeriesBuilder withAltNames(String altNames) {
        curr.altNames = altNames;
        return this;
    }
    
    public SeriesBuilder withDescription(String description) {
        curr.description = description;
        return this;
    }
    
    public SeriesBuilder withSource(String source) {
        curr.source = source;
        return this;
    }
    
    public Series build() {
        Series tmp = curr;
        curr = new Series();
        return tmp;
    }
    
}
