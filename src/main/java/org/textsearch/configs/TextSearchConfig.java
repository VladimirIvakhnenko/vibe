package org.textsearch.configs;

import org.textsearch.services.TextSearchService;
import org.textsearch.utils.SynonymManager;

/**
 * Конфигурация для модуля текстового поиска
 */
public class TextSearchConfig {
    
    // Константы конфигурации
    public static final int DEFAULT_MAX_RESULTS = 20;
    public static final int DEFAULT_MAX_SUGGESTIONS = 10;
    public static final double DEFAULT_FUZZY_THRESHOLD = 0.3;
    public static final int MIN_WORD_LENGTH = 2;
    public static final int MIN_SUGGESTION_LENGTH = 3;
    
    // Веса релевантности для разных полей
    public static final float TITLE_WEIGHT = 10.0f;
    public static final float ARTIST_WEIGHT = 8.0f;
    public static final float ALBUM_WEIGHT = 6.0f;
    public static final float GENRE_WEIGHT = 4.0f;
    public static final float LYRICS_WEIGHT = 2.0f;
} 