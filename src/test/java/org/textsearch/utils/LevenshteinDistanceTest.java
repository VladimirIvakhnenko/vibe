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
} 