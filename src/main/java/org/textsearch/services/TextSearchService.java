package org.textsearch.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.textsearch.db.TextSearchDatabase;
import org.textsearch.indexing.InvertedIndex;
import org.textsearch.models.TextSearchResult;
import org.textsearch.models.TrackMetadata;
import org.textsearch.utils.SynonymManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.io.InputStream;

/**
 * Реализация сервиса текстового поиска
 */
@Service
public class TextSearchService implements ITextSearchService {
    
    private final TextSearchDatabase db;
    private final SynonymManager synonymManager;
    private final Set<String> suggestions = new HashSet<>();
    private final Set<String> trackTitles = new HashSet<>();
    
    @Autowired
    public TextSearchService(TextSearchDatabase db) {
        this.db = db;
        this.synonymManager = new SynonymManager();
        // loadTracksFromJson("spotify_1000_tracks_20250618_153243.json");
    }
    
    @Override
    public List<TextSearchResult> searchTracks(String query, int maxResults, boolean fuzzySearch) {
        return db.searchTracks(query, maxResults, fuzzySearch);
    }
    
    @Override
    public void registerTrack(TrackMetadata metadata) {
        db.addTrack(metadata);
        
        // Добавляем слова в предложения для автодополнения
        addToSuggestions(metadata.getTitle());
        addToSuggestions(metadata.getArtist());
        if (metadata.getAlbum() != null) {
            addToSuggestions(metadata.getAlbum());
        }
        for (String genre : metadata.getGenres()) {
            addToSuggestions(genre);
        }
        // Добавляем полное название трека для фразовых подсказок
        if (metadata.getTitle() != null && !metadata.getTitle().isBlank()) {
            trackTitles.add(metadata.getTitle().toLowerCase());
        }
    }
    
    @Override
    public void removeTrack(String trackId) {
        db.removeTrack(trackId);
    }
    
    @Override
    public List<String> getSuggestions(String prefix, int maxSuggestions) {
        Set<String> suggestions = new HashSet<>();
        // Слова из индекса (старое поведение)
        suggestions.addAll(db.getSuggestions(prefix, maxSuggestions));
        // Фразовые подсказки по полному названию трека
        String lowerPrefix = prefix.toLowerCase();
        for (String title : trackTitles) {
            if (title.startsWith(lowerPrefix)) {
                suggestions.add(title);
                if (suggestions.size() >= maxSuggestions) break;
            }
        }
        return new java.util.ArrayList<>(suggestions).subList(0, Math.min(suggestions.size(), maxSuggestions));
    }
    
    @Override
    public void updateTrack(TrackMetadata metadata) {
        db.updateTrack(metadata);
    }
    
    /**
     * Добавляет слова в предложения для автодополнения
     * @param text текст для разбора
     */
    private void addToSuggestions(String text) {
        if (text == null || text.trim().isEmpty()) return;
        String[] words = synonymManager.normalizeText(text.toLowerCase());
        for (String word : words) {
            suggestions.add(word);
        }
    }
    
    /**
     * Вычисляет оценку релевантности для точного поиска
     * @param query поисковый запрос
     * @param metadata метаданные трека
     * @return оценка релевантности
     */
    private float calculateRelevanceScore(String query, TrackMetadata metadata) {
        String[] normalizedQueryArr = synonymManager.normalizeText(query.toLowerCase());
        String normalizedQuery = String.join(" ", normalizedQueryArr);
        String searchableText = metadata.getSearchableText();
        
        float score = 0.0f;
        
        // Проверяем совпадения в названии (высший приоритет)
        if (metadata.getTitle().toLowerCase().contains(normalizedQuery)) {
            score += 10.0f;
        }
        
        // Проверяем совпадения в исполнителе
        if (metadata.getArtist().toLowerCase().contains(normalizedQuery)) {
            score += 8.0f;
        }
        
        // Проверяем совпадения в альбоме
        if (metadata.getAlbum() != null && metadata.getAlbum().toLowerCase().contains(normalizedQuery)) {
            score += 6.0f;
        }
        
        // Проверяем совпадения в жанрах
        for (String genre : metadata.getGenres()) {
            if (genre.toLowerCase().contains(normalizedQuery)) {
                score += 4.0f;
                break;
            }
        }
        
        // Проверяем совпадения в тексте песни
        if (metadata.getLyrics() != null && metadata.getLyrics().toLowerCase().contains(normalizedQuery)) {
            score += 2.0f;
        }
        
        return score;
    }
    
    /**
     * Получает статистику поискового индекса
     * @return статистика
     */
    public InvertedIndex.IndexStats getIndexStats() {
        return db.getStats();
    }

    public TrackMetadata getTrack(String trackId) {
        return db.getTrack(trackId);
    }

    /**
     * Загрузка треков из JSON-файла (например, resources/spotify_1000_tracks_20250618_153243.json)
     */
    public void loadTracksFromJson(String resourcePath) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) throw new RuntimeException("Resource not found: " + resourcePath);
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            JsonArray tracks = root.getAsJsonArray("tracks");
            if (tracks != null) {
                for (var elem : tracks) {
                    JsonObject node = elem.getAsJsonObject();
                    String id = node.get("id").getAsString();
                    String title = node.get("name").getAsString();
                    String artist = node.getAsJsonArray("artists").size() > 0 ? node.getAsJsonArray("artists").get(0).getAsString() : "";
                    String album = node.has("album") ? node.get("album").getAsString() : null;
                    String lyrics = null;
                    java.util.HashSet<String> genres = new java.util.HashSet<>();
                    if (node.has("genres") && node.getAsJsonArray("genres").size() > 0) {
                        for (var g : node.getAsJsonArray("genres")) genres.add(g.getAsString());
                    }
                    int year = 0;
                    if (node.has("release_date")) {
                        String date = node.get("release_date").getAsString();
                        if (date.length() >= 4) {
                            try { year = Integer.parseInt(date.substring(0, 4)); } catch (Exception ignore) {}
                        }
                    }
                    TrackMetadata meta = new TrackMetadata(id, title, artist, album, lyrics, genres, year);
                    registerTrack(meta);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки треков из json: " + e.getMessage(), e);
        }
    }
} 