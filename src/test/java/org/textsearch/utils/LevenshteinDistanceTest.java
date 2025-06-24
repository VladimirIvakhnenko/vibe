package org.textsearch.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class LevenshteinDistanceTest {
    @Test
    void testCalculate() {
        assertEquals(0, LevenshteinDistance.calculate("abc", "abc"));
        assertEquals(1, LevenshteinDistance.calculate("abc", "ab"));
        assertEquals(1, LevenshteinDistance.calculate("abc", "adc"));
    }

    @Test
    void testCalculateNormalized() {
        assertEquals(0.0, LevenshteinDistance.calculateNormalized("abc", "abc"));
        assertEquals(1.0, LevenshteinDistance.calculateNormalized("a", "b"));
        assertEquals(0.5, LevenshteinDistance.calculateNormalized("ab", "a"));
    }

    @Test
    void testIsSimilar() {
        assertTrue(LevenshteinDistance.isSimilar("abc", "ab", 1));
        assertFalse(LevenshteinDistance.isSimilar("abc", "ab", 0));
    }

    @Test
    void testIsSimilarNormalized() {
        assertTrue(LevenshteinDistance.isSimilarNormalized("abc", "ab", 0.5));
        assertFalse(LevenshteinDistance.isSimilarNormalized("abc", "ab", 0.1));
    }

    @Test
    void testEmptyStrings() {
        assertEquals(0, LevenshteinDistance.calculate("", ""));
        assertEquals(1, LevenshteinDistance.calculate("a", ""));
        assertEquals(1, LevenshteinDistance.calculate("", "a"));
    }

    @Test
    void testLongStrings() {
        String s1 = "a".repeat(100);
        String s2 = "a".repeat(99) + "b";
        assertEquals(1, LevenshteinDistance.calculate(s1, s2));
    }

    @Test
    void testNonAscii() {
        assertEquals(1, LevenshteinDistance.calculate("привет", "привед"));
    }

    @Test
    void testSymmetry() {
        assertEquals(LevenshteinDistance.calculate("abc", "cba"), LevenshteinDistance.calculate("cba", "abc"));
    }

    @Test
    void testSelfSimilarity() {
        assertTrue(LevenshteinDistance.isSimilar("test", "test", 0));
        assertTrue(LevenshteinDistance.isSimilarNormalized("test", "test", 0.0));
    }

    @Test
    void testSingleCharWords() {
        assertEquals(1, LevenshteinDistance.calculate("a", "b"));
        assertEquals(1.0, LevenshteinDistance.calculateNormalized("a", "b"));
    }
} 