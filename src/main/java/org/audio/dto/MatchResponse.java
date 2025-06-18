package org.audio.dto;

import org.audio.models.TrackMatch;

import java.util.List;

/**
 * DTO для результата поиска треков по аудио
 */

import org.audio.models.AudioProcessingResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MatchResponse {
    private final boolean success;
    private final String message;
    private final List<TrackMatchDto> matches;
    private final ProcessingStatsDto stats;

    public MatchResponse(boolean success, String message,
                         List<TrackMatchDto> matches, ProcessingStatsDto stats) {
        this.success = success;
        this.message = message;
        this.matches = matches;
        this.stats = stats;
    }

    public static MatchResponse fromProcessingResult(AudioProcessingResult result) {
        return new MatchResponse(
                true,
                result.hasMatches() ? "Matches found" : "No matches found",
                result.hasMatches() ?
                        Arrays.stream(result.getMatches())
                                .map(TrackMatchDto::fromTrackMatch)
                                .collect(Collectors.toList()) :
                        null,
                new ProcessingStatsDto(
                        result.getSamplesProcessed(),
                        result.getAudioDurationMs()
                )
        );
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<TrackMatchDto> getMatches() { return matches; }
    public ProcessingStatsDto getStats() { return stats; }

    // Nested DTOs
    public static class TrackMatchDto {
        private final String trackId;
        private final String title;
        private final float confidence;
        private final int matchScore;
        private final long offsetMs;

        public TrackMatchDto(String trackId, String title,
                             float confidence, int matchScore, long offsetMs) {
            this.trackId = trackId;
            this.title = title;
            this.confidence = confidence;
            this.matchScore = matchScore;
            this.offsetMs = offsetMs;
        }

        public static TrackMatchDto fromTrackMatch(TrackMatch match) {
            return new TrackMatchDto(
                    match.getTrackId(),
                    match.getTrackTitle(),
                    match.getConfidence(),
                    match.getMatchScore(),
                    match.getOffsetMs()
            );
        }

        // Getters
        public String getTrackId() { return trackId; }
        public String getTitle() { return title; }
        public float getConfidence() { return confidence; }
        public int getMatchScore() { return matchScore; }
        public long getOffsetMs() { return offsetMs; }
    }

    public static class ProcessingStatsDto {
        private final int samplesProcessed;
        private final int durationMs;

        public ProcessingStatsDto(int samplesProcessed, int durationMs) {
            this.samplesProcessed = samplesProcessed;
            this.durationMs = durationMs;
        }

        // Getters
        public int getSamplesProcessed() { return samplesProcessed; }
        public int getDurationMs() { return durationMs; }
    }
}