package org.textsearch.services;

import org.textsearch.models.TextSearchResult;
import org.textsearch.models.TrackMetadata;

import java.util.List;

/**
 * Интерфейс сервиса текстового поиска
 */
public interface ITextSearchService {
    
    /**
     * Ищет треки по текстовому запросу
     * @param query поисковый запрос
     * @param maxResults максимальное количество результатов
     * @param fuzzySearch использовать нечеткий поиск
     * @return список результатов поиска
     */
    List<TextSearchResult> searchTracks(String query, int maxResults, boolean fuzzySearch);
    
    /**
     * Регистрирует метаданные трека для поиска
     * @param metadata метаданные трека
     */
    void registerTrack(TrackMetadata metadata);
    
    /**
     * Удаляет трек из поискового индекса
     * @param trackId ID трека
     */
    void removeTrack(String trackId);
    
    /**
     * Получает предложения для автодополнения
     * @param prefix префикс для поиска
     * @param maxSuggestions максимальное количество предложений
     * @return список предложений
     */
    List<String> getSuggestions(String prefix, int maxSuggestions);
    
    /**
     * Обновляет метаданные трека
     * @param metadata новые метаданные
     */
    void updateTrack(TrackMetadata metadata);
} 