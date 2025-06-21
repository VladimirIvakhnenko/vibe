package org.textsearch.models;

import java.util.Objects;

/**
 * Результат текстового поиска
 */
public class TextSearchResult implements Comparable<TextSearchResult> {
    private final String trackId;
    private final String title;
    private final String artist;
    private final String album;
    private final float relevanceScore;
    private final String matchedField;
    private final String matchedText;

    public TextSearchResult(String trackId, String title, String artist, String album,
                           float relevanceScore, String matchedField, String matchedText) {
        this.trackId = Objects.requireNonNull(trackId);
        this.title = Objects.requireNonNull(title);
        this.artist = Objects.requireNonNull(artist);
        this.album = album;
        this.relevanceScore = relevanceScore;
        this.matchedField = matchedField;
        this.matchedText = matchedText;
    }

    // Геттеры
    public String getTrackId() { return trackId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public float getRelevanceScore() { return relevanceScore; }
    public String getMatchedField() { return matchedField; }
    public String getMatchedText() { return matchedText; }

    @Override
    public int compareTo(TextSearchResult other) {
        return Float.compare(other.relevanceScore, this.relevanceScore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextSearchResult that = (TextSearchResult) o;
        return Float.compare(that.relevanceScore, relevanceScore) == 0 &&
                trackId.equals(that.trackId) &&
                title.equals(that.title) &&
                artist.equals(that.artist) &&
                Objects.equals(album, that.album) &&
                Objects.equals(matchedField, that.matchedField) &&
                Objects.equals(matchedText, that.matchedText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, title, artist, album, relevanceScore, matchedField, matchedText);
    }

    @Override
    public String toString() {
        return String.format("TextSearchResult{id='%s', title='%s', artist='%s', score=%.2f, field='%s'}",
                trackId, title, artist, relevanceScore, matchedField);
    }
} 