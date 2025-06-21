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

/**
 * Реализация сервиса текстового поиска
 */
@Service
public class TextSearchService implements ITextSearchService {
    
    private final TextSearchDatabase db;
    private final SynonymManager synonymManager;
    private final Set<String> suggestions;
    
    @Autowired
    public TextSearchService(TextSearchDatabase db) {
        this.db = db;
        this.synonymManager = new SynonymManager();
        this.suggestions = new HashSet<>();
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
    }
    
    @Override
    public void removeTrack(String trackId) {
        db.removeTrack(trackId);
    }
    
    @Override
    public List<String> getSuggestions(String prefix, int maxSuggestions) {
        return db.getSuggestions(prefix, maxSuggestions);
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
} 