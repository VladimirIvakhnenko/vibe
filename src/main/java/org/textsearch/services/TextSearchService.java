package org.textsearch.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.textsearch.indexing.InvertedIndex;
import org.textsearch.models.TextSearchResult;
import org.textsearch.models.TrackMetadata;
import org.textsearch.utils.SynonymManager;

/**
 * Реализация сервиса текстового поиска
 */
@Service
public class TextSearchService implements ITextSearchService {
    
    private final InvertedIndex invertedIndex;
    private final SynonymManager synonymManager;
    private final Set<String> suggestions;
    
    public TextSearchService() {
        this.invertedIndex = new InvertedIndex();
        this.synonymManager = new SynonymManager();
        this.suggestions = new HashSet<>();
    }
    
    @Override
    public List<TextSearchResult> searchTracks(String query, int maxResults, boolean fuzzySearch) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<TextSearchResult> results = new ArrayList<>();
        
        if (fuzzySearch) {
            // Новый вариант: поиск по автомату Левенштейна
            List<String> trackIds = invertedIndex.fuzzyAutomatonSearch(query, 2, maxResults); // 2 - порог ошибок
            for (String trackId : trackIds) {
                TrackMetadata metadata = invertedIndex.getTrackMetadata(trackId);
                if (metadata != null) {
                    results.add(new TextSearchResult(
                        trackId,
                        metadata.getTitle(),
                        metadata.getArtist(),
                        metadata.getAlbum(),
                        1.0f,
                        "levenshtein_automaton",
                        query
                    ));
                }
            }
        } else {
            // Точный поиск
            List<String> trackIds = invertedIndex.search(query, maxResults);
            
            for (String trackId : trackIds) {
                TrackMetadata metadata = invertedIndex.getTrackMetadata(trackId);
                if (metadata != null) {
                    float score = calculateRelevanceScore(query, metadata);
                    results.add(new TextSearchResult(
                        trackId,
                        metadata.getTitle(),
                        metadata.getArtist(),
                        metadata.getAlbum(),
                        score,
                        "exact_match",
                        query
                    ));
                }
            }
        }
        
        // Сортируем по релевантности
        results.sort(Collections.reverseOrder());
        return results;
    }
    
    @Override
    public void registerTrack(TrackMetadata metadata) {
        if (metadata == null) return;
        
        invertedIndex.addTrack(metadata);
        
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
        invertedIndex.removeTrack(trackId);
    }
    
    @Override
    public List<String> getSuggestions(String prefix, int maxSuggestions) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowerPrefix = prefix.toLowerCase();
        
        return suggestions.stream()
                .filter(suggestion -> suggestion.toLowerCase().startsWith(lowerPrefix))
                .sorted()
                .limit(maxSuggestions)
                .collect(Collectors.toList());
    }
    
    @Override
    public void updateTrack(TrackMetadata metadata) {
        if (metadata == null) return;
        
        // Удаляем старые данные
        removeTrack(metadata.getTrackId());
        
        // Добавляем новые данные
        registerTrack(metadata);
    }
    
    /**
     * Добавляет слова в предложения для автодополнения
     * @param text текст для разбора
     */
    private void addToSuggestions(String text) {
        if (text == null || text.trim().isEmpty()) return;
        
        String[] words = text.toLowerCase()
                .replaceAll("[^a-zA-Zа-яА-Я0-9\\s]", " ")
                .split("\\s+");
        
        for (String word : words) {
            if (word.length() >= 3) { // Минимальная длина для предложений
                suggestions.add(word);
            }
        }
    }
    
    /**
     * Вычисляет оценку релевантности для точного поиска
     * @param query поисковый запрос
     * @param metadata метаданные трека
     * @return оценка релевантности
     */
    private float calculateRelevanceScore(String query, TrackMetadata metadata) {
        String normalizedQuery = synonymManager.normalizeText(query.toLowerCase());
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
        return invertedIndex.getStats();
    }
} 