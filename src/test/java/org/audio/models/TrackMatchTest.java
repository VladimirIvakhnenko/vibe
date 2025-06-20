package org.audio.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrackMatchTest {
    @Test
    void testGetters() {
        TrackMatch match = new TrackMatch("id", "title", 10, 0.9f, 1000L);
        assertEquals("id", match.getTrackId());
        assertEquals("title", match.getTrackTitle());
        assertEquals(10, match.getMatchScore());
        assertEquals(0.9f, match.getConfidence());
        assertEquals(1000L, match.getOffsetMs());
    }

    @Test
    void testCompareTo() {
        TrackMatch m1 = new TrackMatch("id", "t", 10, 0.9f, 0);
        TrackMatch m2 = new TrackMatch("id", "t", 5, 0.95f, 0);
        assertTrue(m1.compareTo(m2) < 0);
        assertTrue(m2.compareTo(m1) > 0);
    }

    @Test
    void testIsValid() {
        TrackMatch m = new TrackMatch("id", "t", 1, 0.5f, 0);
        assertTrue(m.isValid(0.5f));
        assertFalse(m.isValid(0.6f));
    }

    @Test
    void testCreate() {
        TrackMatch m = TrackMatch.create("id", "t", 5, 10, 100);
        assertEquals(0.5f, m.getConfidence());
    }

    @Test
    void testEquals() {
        TrackMatch m1 = new TrackMatch("id", "t", 1, 0.5f, 0);
        TrackMatch m2 = new TrackMatch("id", "t", 1, 0.5f, 0);
        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }
} 