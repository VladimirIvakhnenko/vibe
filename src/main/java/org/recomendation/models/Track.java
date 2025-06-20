package org.recomendation.models;

/**
 * Класс Track представляет музыкальный трек в системе рекомендаций.
 * Каждый трек имеет уникальный идентификатор, название, жанр и исполнителя.
 * Используется как вершина графа связей между треками.
 */
public class Track {
    /** Уникальный идентификатор трека (например, UUID или строковый id из БД) */
    private final String id;
    /** Название трека */
    private final String title;
    /** Жанр трека (например, Rock, Pop, Jazz) */
    private final String genre;
    /** Имя исполнителя */
    private final String artist;

    /**
     * Конструктор для создания трека.
     * @param id уникальный идентификатор
     * @param title название трека
     * @param genre жанр трека
     * @param artist исполнитель
     */
    public Track(String id, String title, String genre, String artist) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.artist = artist;
    }

    /**
     * Получить уникальный идентификатор трека.
     */
    public String getId() { return id; }
    /**
     * Получить название трека.
     */
    public String getTitle() { return title; }
    /**
     * Получить жанр трека.
     */
    public String getGenre() { return genre; }
    /**
     * Получить исполнителя трека.
     */
    public String getArtist() { return artist; }
} 