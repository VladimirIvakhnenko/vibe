package org.audio.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AudioProcessingResultTest {
    @Test
    void testSuccess() {
        TrackMatch match = new TrackMatch("id", "t", 1, 0.5f, 0);
        AudioProcessingResult result = AudioProcessingResult.success(match, 100, 2000);
        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
        assertEquals(1, result.getMatches().length);
        assertEquals(100, result.getSamplesProcessed());
        assertEquals(2000, result.getAudioDurationMs());
        assertTrue(result.hasMatches());
    }

    @Test
    void testMultipleMatches() {
        TrackMatch[] matches = {
            new TrackMatch("id1", "t1", 1, 0.5f, 0),
            new TrackMatch("id2", "t2", 2, 0.7f, 10)
        };
        AudioProcessingResult result = AudioProcessingResult.multipleMatches(matches, 200, 3000);
        assertTrue(result.isSuccess());
        assertEquals(2, result.getMatches().length);
    }

    @Test
    void testNoMatch() {
        AudioProcessingResult result = AudioProcessingResult.noMatch();
        assertTrue(result.isSuccess());
        assertEquals(0, result.getMatches().length);
        assertFalse(result.hasMatches());
    }

    @Test
    void testError() {
        AudioProcessingResult result = AudioProcessingResult.error("fail");
        assertFalse(result.isSuccess());
        assertEquals("fail", result.getErrorMessage());
    }
} 