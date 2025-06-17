package org.audio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.audio.models.TrackMatch;

import java.util.List;

/**
 * DTO для результата поиска треков по аудио
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchResponse {
    private boolean success;
    private String errorMessage;
    private List<TrackMatchDto> matches;
    private ProcessingStatsDto stats;

    // Конструкторы
    public MatchResponse() {}

    public MatchResponse(List<TrackMatchDto> matches, ProcessingStatsDto stats) {
        this.success = true;
        this.matches = matches;
        this.stats = stats;
    }

    public MatchResponse(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
    }

    // Геттеры и сеттеры
    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<TrackMatchDto> getMatches() {
        return matches;
    }

    public ProcessingStatsDto getStats() {
        return stats;
    }



    /**
     * DTO для информации о совпадении трека
     */
    public static class TrackMatchDto {
        private String trackId;
        private String trackTitle;
        private String artist;
        private double confidence;
        private int matchScore;
        private long offsetMs;
        private String albumCoverUrl;

        public TrackMatchDto() {}

        public TrackMatchDto(String trackId, String trackTitle, String artist,
                             double confidence, int matchScore, long offsetMs) {
            this.trackId = trackId;
            this.trackTitle = trackTitle;
            this.artist = artist;
            this.confidence = confidence;
            this.matchScore = matchScore;
            this.offsetMs = offsetMs;
        }

        public String getTrackId() {
            return trackId;
        }

        public String getTrackTitle() {
            return trackTitle;
        }

        public String getArtist() {
            return artist;
        }

        public double getConfidence() {
            return confidence;
        }

        public int getMatchScore() {
            return matchScore;
        }

        public long getOffsetMs() {
            return offsetMs;
        }

        public String getAlbumCoverUrl() {
            return albumCoverUrl;
        }

        public void setAlbumCoverUrl(String albumCoverUrl) {
            this.albumCoverUrl = albumCoverUrl;
        }
    }

    /**
     * DTO для статистики обработки аудио
     */
    public static class ProcessingStatsDto {
        private long processingTimeMs;
        private int audioLengthMs;
        private int samplesProcessed;

        public ProcessingStatsDto() {}

        public ProcessingStatsDto(long processingTimeMs, int audioLengthMs, int samplesProcessed) {
            this.processingTimeMs = processingTimeMs;
            this.audioLengthMs = audioLengthMs;
            this.samplesProcessed = samplesProcessed;
        }

        public long getProcessingTimeMs() {
            return processingTimeMs;
        }

        public int getAudioLengthMs() {
            return audioLengthMs;
        }

        public int getSamplesProcessed() {
            return samplesProcessed;
        }
    }
}