package org.textsearch.indexing;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.textsearch.models.TrackMetadata;
import org.textsearch.utils.SynonymManager;

/**
 * Инвертированный индекс для ускорения текстового поиска
 * Строит индекс: слово -> список треков, содержащих это слово
 */
public class InvertedIndex {
    
    private final Map<String, Set<String>> wordToTracks;
    private final Map<String, TrackMetadata> trackMetadata;
    private final SynonymManager synonymManager;
    
    public InvertedIndex() {
        this.wordToTracks = new HashMap<>();
        this.trackMetadata = new HashMap<>();
        this.synonymManager = new SynonymManager();
    }
    
    /**
     * Добавляет трек в индекс
     * @param metadata метаданные трека
     */
    public void addTrack(TrackMetadata metadata) {
        if (metadata == null) return;
        
        String trackId = metadata.getTrackId();
        trackMetadata.put(trackId, metadata);
        
        // Индексируем все поля
        indexField(trackId, metadata.getTitle(), "title");
        indexField(trackId, metadata.getArtist(), "artist");
        
        if (metadata.getAlbum() != null) {
            indexField(trackId, metadata.getAlbum(), "album");
        }
        
        if (metadata.getLyrics() != null) {
            indexField(trackId, metadata.getLyrics(), "lyrics");
        }
        
        // Индексируем жанры
        for (String genre : metadata.getGenres()) {
            indexField(trackId, genre, "genre");
        }
    }
    
    /**
     * Индексирует поле трека
     * @param trackId ID трека
     * @param text текст для индексации
     * @param fieldType тип поля
     */
    private void indexField(String trackId, String text, String fieldType) {
        if (text == null || text.trim().isEmpty()) return;
        
        // Нормализуем текст
        String[] normalizedWords = synonymManager.normalizeText(text);
        
        // Добавляем каждое слово в индекс
        for (String word : normalizedWords) {
            if (word.length() < 2) continue; // Игнорируем слишком короткие слова
            
            wordToTracks.computeIfAbsent(word, k -> new HashSet<>()).add(trackId);
        }
    }
    
    /**
     * Ищет треки по запросу
     * @param query поисковый запрос
     * @param maxResults максимальное количество результатов
     * @return список ID треков
     */
    public List<String> search(String query, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        // Нормализуем запрос
        String[] queryWords = synonymManager.normalizeText(query);
        
        // Находим треки для каждого слова
        Map<String, Integer> trackScores = new HashMap<>();
        
        for (String word : queryWords) {
            if (word.length() < 2) continue;
            
            Set<String> tracks = wordToTracks.get(word);
            if (tracks != null) {
                for (String trackId : tracks) {
                    trackScores.merge(trackId, 1, Integer::sum);
                }
            }
        }
        
        // Сортируем по количеству совпадений
        return trackScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(maxResults)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Ищет треки с нечетким поиском
     * @param query поисковый запрос
     * @param maxResults максимальное количество результатов
     * @param fuzzyThreshold порог для нечеткого поиска
     * @return список ID треков с оценками релевантности
     */
    public Map<String, Float> fuzzySearch(String query, int maxResults, double fuzzyThreshold) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyMap();
        }
        
        String[] queryWords = synonymManager.normalizeText(query);
        
        Map<String, Float> trackScores = new HashMap<>();
        
        for (String queryWord : queryWords) {
            if (queryWord.length() < 2) continue;
            
            // Ищем точные совпадения
            Set<String> exactMatches = wordToTracks.get(queryWord);
            if (exactMatches != null) {
                for (String trackId : exactMatches) {
                    trackScores.merge(trackId, 1.0f, Float::sum);
                }
            }
            
            // Ищем нечеткие совпадения
            for (Map.Entry<String, Set<String>> entry : wordToTracks.entrySet()) {
                String indexedWord = entry.getKey();
                double distance = org.textsearch.utils.LevenshteinDistance.calculateNormalized(
                    queryWord, indexedWord);
                
                if (distance <= fuzzyThreshold && distance > 0) {
                    float score = (float) (1.0 - distance);
                    for (String trackId : entry.getValue()) {
                        trackScores.merge(trackId, score, Float::sum);
                    }
                }
            }
        }
        
        // Сортируем и ограничиваем результаты
        return trackScores.entrySet().stream()
                .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                .limit(maxResults)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    /**
     * Получает метаданные трека
     * @param trackId ID трека
     * @return метаданные или null
     */
    public TrackMetadata getTrackMetadata(String trackId) {
        return trackMetadata.get(trackId);
    }
    
    /**
     * Удаляет трек из индекса
     * @param trackId ID трека
     */
    public void removeTrack(String trackId) {
        TrackMetadata metadata = trackMetadata.remove(trackId);
        if (metadata == null) return;
        
        // Удаляем все упоминания трека из индекса
        for (Set<String> tracks : wordToTracks.values()) {
            tracks.remove(trackId);
        }
        
        // Удаляем пустые записи
        wordToTracks.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
    
    /**
     * Получает статистику индекса
     * @return статистика
     */
    public IndexStats getStats() {
        return new IndexStats(
            trackMetadata.size(),
            wordToTracks.size(),
            wordToTracks.values().stream().mapToInt(Set::size).sum()
        );
    }
    
    /**
     * Поиск с использованием автомата Левенштейна
     * @param query поисковый запрос
     * @param maxEdits максимальное число ошибок
     * @param maxResults максимальное количество результатов
     * @return список ID треков
     */
    public List<String> fuzzyAutomatonSearch(String query, int maxEdits, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Разбиваем запрос на слова
        String[] queryWords = query.toLowerCase().split("\\s+");
        Set<String> resultTrackIds = new LinkedHashSet<>();
        for (String queryWord : queryWords) {
            if (queryWord.length() < 2) continue;
            org.textsearch.utils.LevenshteinAutomaton automaton = new org.textsearch.utils.LevenshteinAutomaton(queryWord, maxEdits);
            for (String word : wordToTracks.keySet()) {
                if (automaton.accept(word)) {
                    resultTrackIds.addAll(wordToTracks.get(word));
                    if (resultTrackIds.size() >= maxResults) break;
                }
            }
            if (resultTrackIds.size() >= maxResults) break;
        }
        return resultTrackIds.stream().limit(maxResults).collect(Collectors.toList());
    }
    
    /**
     * Статистика индекса
     */
    public static class IndexStats {
        private final int trackCount;
        private final int uniqueWords;
        private final int totalIndexEntries;
        
        public IndexStats(int trackCount, int uniqueWords, int totalIndexEntries) {
            this.trackCount = trackCount;
            this.uniqueWords = uniqueWords;
            this.totalIndexEntries = totalIndexEntries;
        }
        
        public int getTrackCount() { return trackCount; }
        public int getUniqueWords() { return uniqueWords; }
        public int getTotalIndexEntries() { return totalIndexEntries; }
        
        @Override
        public String toString() {
            return String.format("IndexStats{tracks=%d, words=%d, entries=%d}",
                    trackCount, uniqueWords, totalIndexEntries);
        }
    }
    
    public Set<String> getWords() {
        return Collections.unmodifiableSet(wordToTracks.keySet());
    }
} 