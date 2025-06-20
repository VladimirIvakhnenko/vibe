package org.recomendation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * Модель, представляющая музыкальный трек.
 * Поля соответствуют структуре данных из Spotify.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("artists")
    private List<String> artists;

    @JsonProperty("genres")
    private List<String> genres;

    @JsonProperty("album")
    private String album;

    @JsonProperty("popularity")
    private int popularity;

    // Пустой конструктор для Jackson
    public Track() {
    }

    public Track(String id, String name, List<String> artists, List<String> genres) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.genres = genres;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getArtists() {
        return artists;
    }
    
    public List<String> getGenres() {
        return genres;
    }

    public String getAlbum() {
        return album;
    }

    public int getPopularity() {
        return popularity;
    }


    // hashCode и equals необходимы для корректной работы JGraphT
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(id, track.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Track{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artists=" + artists +
                '}';
    }
} 