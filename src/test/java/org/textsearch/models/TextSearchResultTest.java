package org.textsearch.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TextSearchResultTest {
    @Test
    void testGettersAndToString() {
        TextSearchResult r = new TextSearchResult("id", "t", "a", "al", 0.9f, "field", "text");
        assertEquals("id", r.getTrackId());
        assertEquals("t", r.getTitle());
        assertEquals("a", r.getArtist());
        assertEquals("al", r.getAlbum());
        assertEquals(0.9f, r.getRelevanceScore());
        assertEquals("field", r.getMatchedField());
        assertEquals("text", r.getMatchedText());
        assertTrue(r.toString().contains("id"));
    }

    @Test
    void testEqualsAndHashCode() {
        TextSearchResult r1 = new TextSearchResult("id", "t", "a", "al", 1.0f, "f", "txt");
        TextSearchResult r2 = new TextSearchResult("id", "t", "a", "al", 1.0f, "f", "txt");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testCompareTo() {
        TextSearchResult r1 = new TextSearchResult("id1", "t", "a", "al", 2.0f, "f", "txt");
        TextSearchResult r2 = new TextSearchResult("id2", "t", "a", "al", 1.0f, "f", "txt");
        assertTrue(r1.compareTo(r2) < 0);
        assertTrue(r2.compareTo(r1) > 0);
    }
} 