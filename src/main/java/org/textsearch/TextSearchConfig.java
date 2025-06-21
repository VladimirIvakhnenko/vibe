package org.textsearch;

/**
 * Основная конфигурация модуля текстового поиска
 */
public class TextSearchConfig {
    public static final int MAX_SEARCH_RESULTS = 50;
    public static final float DEFAULT_MIN_RELEVANCE = 0.1f;
    public static final int SEARCH_TIMEOUT_MS = 3000;
    public static final boolean ENABLE_FUZZY_SEARCH = true;
    public static final boolean ENABLE_SYNONYMS = true;
} 