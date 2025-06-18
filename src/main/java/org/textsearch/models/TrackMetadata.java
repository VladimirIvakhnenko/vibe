package org.textsearch.models;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Метаданные трека для текстового поиска
 */
public class TrackMetadata {
    private final String trackId;
    private final String title;
    private final String artist;
    private final String album;
    private final String lyrics;
    private final Set<String> genres;
    private final int year;

    public TrackMetadata(String trackId, String title, String artist, String album, 
                        String lyrics, Set<String> genres, int year) {
        this.trackId = Objects.requireNonNull(trackId, "Track ID cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.artist = Objects.requireNonNull(artist, "Artist cannot be null");
        this.album = album; // может быть null
        this.lyrics = lyrics; // может быть null
        this.genres = genres != null ? new HashSet<>(genres) : new HashSet<>();
        this.year = year;
    }

    // Геттеры
    public String getTrackId() { return trackId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getLyrics() { return lyrics; }
    public Set<String> getGenres() { return new HashSet<>(genres); }
    public int getYear() { return year; }

    // Методы для поиска
    public String getSearchableText() {
        StringBuilder sb = new StringBuilder();
        sb.append(title.toLowerCase()).append(" ");
        sb.append(artist.toLowerCase()).append(" ");
        if (album != null) {
            sb.append(album.toLowerCase()).append(" ");
        }
        if (lyrics != null) {
            sb.append(lyrics.toLowerCase()).append(" ");
        }
        for (String genre : genres) {
            sb.append(genre.toLowerCase()).append(" ");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackMetadata that = (TrackMetadata) o;
        return year == that.year &&
                trackId.equals(that.trackId) &&
                title.equals(that.title) &&
                artist.equals(that.artist) &&
                Objects.equals(album, that.album) &&
                Objects.equals(lyrics, that.lyrics) &&
                genres.equals(that.genres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, title, artist, album, lyrics, genres, year);
    }

    @Override
    public String toString() {
        return String.format("TrackMetadata{id='%s', title='%s', artist='%s', album='%s', year=%d}",
                trackId, title, artist, album, year);
    }
} 