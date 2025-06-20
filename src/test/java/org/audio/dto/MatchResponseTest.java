package org.audio.dto;

import org.audio.models.AudioProcessingResult;
import org.audio.models.TrackMatch;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchResponseTest {
    @Test
    void fromProcessingResult_withMatches() {
        TrackMatch match = new TrackMatch("id", "title", 1, 0.9f, 100);
        AudioProcessingResult result = AudioProcessingResult.success(match, 100, 2000);
        MatchResponse response = MatchResponse.fromProcessingResult(result);
        assertTrue(response.isSuccess());
        assertEquals("Matches found", response.getMessage());
        assertNotNull(response.getMatches());
        assertEquals(1, response.getMatches().size());
        assertNotNull(response.getStats());
    }

    @Test
    void fromProcessingResult_noMatches() {
        AudioProcessingResult result = AudioProcessingResult.noMatch();
        MatchResponse response = MatchResponse.fromProcessingResult(result);
        assertTrue(response.isSuccess());
        assertEquals("No matches found", response.getMessage());
        assertNull(response.getMatches());
        assertNotNull(response.getStats());
    }

    @Test
    void testTrackMatchDto() {
        MatchResponse.TrackMatchDto dto = new MatchResponse.TrackMatchDto("id", "t", 0.5f, 10, 100);
        assertEquals("id", dto.getTrackId());
        assertEquals("t", dto.getTitle());
        assertEquals(0.5f, dto.getConfidence());
        assertEquals(10, dto.getMatchScore());
        assertEquals(100, dto.getOffsetMs());
    }

    @Test
    void testProcessingStatsDto() {
        MatchResponse.ProcessingStatsDto stats = new MatchResponse.ProcessingStatsDto(100, 2000);
        assertEquals(100, stats.getSamplesProcessed());
        assertEquals(2000, stats.getDurationMs());
    }
} 