package org.audio.db;

import org.audio.models.TrackMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFingerprintDatabaseTest {
    private InMemoryFingerprintDatabase db;

    @BeforeEach
    void setUp() {
        db = new InMemoryFingerprintDatabase();
    }

    @Test
    void addAndFindBestMatch() {
        db.addTrack("id1", "track1", List.of(1L, 2L, 3L, 4L, 5L));
        Optional<TrackMatch> match = db.findBestMatch(List.of(1L, 2L, 3L, 4L, 5L));
        assertTrue(match.isPresent());
        assertEquals("id1", match.get().getTrackId());
    }

    @Test
    void bestMatchesReturnsMultiple() {
        db.addTrack("id1", "track1", List.of(1L, 2L, 3L));
        db.addTrack("id2", "track2", List.of(2L, 3L, 4L));
        TrackMatch[] matches = db.bestMatches(List.of(2L, 3L, 4L), 2, 0.1f);
        assertTrue(matches.length > 0);
    }

    @Test
    void findBestMatchReturnsEmptyIfNoMatch() {
        db.addTrack("id1", "track1", List.of(1L, 2L, 3L));
        Optional<TrackMatch> match = db.findBestMatch(List.of(10L, 20L));
        assertTrue(match.isEmpty());
    }
} 