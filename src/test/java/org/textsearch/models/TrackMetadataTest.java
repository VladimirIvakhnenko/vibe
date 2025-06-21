package org.textsearch.models;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TrackMetadataTest {
    @Test
    void testGettersAndToString() {
        TrackMetadata meta = new TrackMetadata("id1", "Title", "Artist", "Album", "Lyrics", Set.of("pop", "rock"), 2022);
        assertEquals("id1", meta.getTrackId());
        assertEquals("Title", meta.getTitle());
        assertEquals("Artist", meta.getArtist());
        assertEquals("Album", meta.getAlbum());
        assertEquals("Lyrics", meta.getLyrics());
        assertEquals(Set.of("pop", "rock"), meta.getGenres());
        assertEquals(2022, meta.getYear());
        assertTrue(meta.toString().contains("id1"));
    }

    @Test
    void testEqualsAndHashCode() {
        TrackMetadata m1 = new TrackMetadata("id1", "T", "A", null, null, Set.of(), 1);
        TrackMetadata m2 = new TrackMetadata("id1", "T", "A", null, null, Set.of(), 1);
        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void testGetSearchableText() {
        TrackMetadata meta = new TrackMetadata("id2", "T", "A", "Al", "Ly", Set.of("g1"), 2020);
        String text = meta.getSearchableText();
        assertTrue(text.contains("t"));
        assertTrue(text.contains("a"));
        assertTrue(text.contains("al"));
        assertTrue(text.contains("ly"));
        assertTrue(text.contains("g1"));
    }
} 