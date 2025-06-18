package org.textsearch.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер синонимов для обработки альтернативных названий
 * Например: "The Beatles" -> "Beatles", "Metallica" -> "Metalica"
 */
public class SynonymManager {
    
    private final Map<String, Set<String>> synonymGroups;
    private final Map<String, String> canonicalForms;
    
    public SynonymManager() {
        this.synonymGroups = new HashMap<>();
        this.canonicalForms = new HashMap<>();
        initializeDefaultSynonyms();
    }
    
    /**
     * Инициализация стандартных синонимов
     */
    private void initializeDefaultSynonyms() {
        // Группы синонимов
        addSynonymGroup("beatles", "the beatles", "beatles");
        addSynonymGroup("metallica", "metallica", "metalica");
        addSynonymGroup("led zeppelin", "led zeppelin", "zeppelin");
        addSynonymGroup("pink floyd", "pink floyd", "floyd");
        addSynonymGroup("rolling stones", "rolling stones", "stones");
        addSynonymGroup("queen", "queen");
        addSynonymGroup("nirvana", "nirvana");
        addSynonymGroup("radiohead", "radiohead");
        
        // Жанры
        addSynonymGroup("rock", "rock", "rock music");
        addSynonymGroup("pop", "pop", "pop music", "popular");
        addSynonymGroup("jazz", "jazz", "jazz music");
        addSynonymGroup("classical", "classical", "classical music");
        addSynonymGroup("electronic", "electronic", "electronic music", "edm");
        addSynonymGroup("hip hop", "hip hop", "hip-hop", "rap");
    }
    
    /**
     * Добавляет группу синонимов
     * @param canonicalForm каноническая форма
     * @param synonyms синонимы
     */
    public void addSynonymGroup(String canonicalForm, String... synonyms) {
        Set<String> group = new HashSet<>();
        group.add(canonicalForm);
        group.addAll(Arrays.asList(synonyms));
        
        synonymGroups.put(canonicalForm, group);
        
        // Добавляем маппинг для каждого синонима
        for (String synonym : group) {
            canonicalForms.put(synonym.toLowerCase(), canonicalForm);
        }
    }
    
    /**
     * Получает каноническую форму для слова
     * @param word слово
     * @return каноническая форма или исходное слово
     */
    public String getCanonicalForm(String word) {
        if (word == null) return null;
        return canonicalForms.getOrDefault(word.toLowerCase(), word);
    }
    
    /**
     * Получает все синонимы для слова
     * @param word слово
     * @return множество синонимов
     */
    public Set<String> getSynonyms(String word) {
        if (word == null) return Collections.emptySet();
        
        String canonical = getCanonicalForm(word);
        return synonymGroups.getOrDefault(canonical, Collections.singleton(word));
    }
    
    /**
     * Проверяет, являются ли два слова синонимами
     * @param word1 первое слово
     * @param word2 второе слово
     * @return true если слова синонимы
     */
    public boolean areSynonyms(String word1, String word2) {
        if (word1 == null || word2 == null) return false;
        
        String canonical1 = getCanonicalForm(word1);
        String canonical2 = getCanonicalForm(word2);
        
        return canonical1.equals(canonical2);
    }
    
    /**
     * Нормализует текст, заменяя синонимы на канонические формы
     * @param text исходный текст
     * @return нормализованный текст
     */
    public String normalizeText(String text) {
        if (text == null) return null;
        
        String[] words = text.toLowerCase().split("\\s+");
        List<String> normalizedWords = new ArrayList<>();
        
        for (String word : words) {
            String canonical = getCanonicalForm(word);
            normalizedWords.add(canonical);
        }
        
        return String.join(" ", normalizedWords);
    }
    
    /**
     * Расширяет поисковый запрос синонимами
     * @param query поисковый запрос
     * @return расширенный запрос с синонимами
     */
    public String expandQueryWithSynonyms(String query) {
        if (query == null) return null;
        
        String[] words = query.toLowerCase().split("\\s+");
        List<String> expandedWords = new ArrayList<>();
        
        for (String word : words) {
            Set<String> synonyms = getSynonyms(word);
            expandedWords.addAll(synonyms);
        }
        
        return String.join(" ", expandedWords);
    }
} 