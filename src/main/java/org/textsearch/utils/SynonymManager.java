package org.textsearch.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;

/**
 * Менеджер синонимов для обработки альтернативных названий
 * Например: "The Beatles" -> "Beatles", "Metallica" -> "Metalica"
 */
public class SynonymManager {
    
    private final Analyzer analyzer;
    
    public SynonymManager() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("synonyms", "/synonyms.txt"); // файл должен лежать в resources
        filterArgs.put("expand", "true");
        filterArgs.put("ignoreCase", "true");
        SynonymGraphFilterFactory factory = new SynonymGraphFilterFactory(filterArgs);
        try {
            factory.inform(new ClasspathResourceLoader(getClass()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load synonyms.txt", e);
        }
        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                org.apache.lucene.analysis.standard.StandardTokenizer tokenizer = new org.apache.lucene.analysis.standard.StandardTokenizer();
                TokenStream filtered = factory.create(tokenizer);
                return new TokenStreamComponents(tokenizer, filtered);
            }
        };
    }
    
    public String[] normalizeText(String text) {
        if (text == null || text.isEmpty()) return new String[0];
        try (TokenStream ts = analyzer.tokenStream("", new StringReader(text))) {
            CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            java.util.List<String> tokens = new java.util.ArrayList<>();
            while (ts.incrementToken()) {
                tokens.add(termAttr.toString());
            }
            ts.end();
            return tokens.toArray(new String[0]);
        } catch (IOException e) {
            throw new RuntimeException("Error normalizing text", e);
        }
    }
} 