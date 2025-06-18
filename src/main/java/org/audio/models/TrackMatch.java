package org.audio.models;

import java.util.Objects;

/**
 * Audio fingerprint match result
 */
public class TrackMatch implements Comparable<TrackMatch> {
    private final String trackId;
    private final String trackTitle;
    private final int matchScore;
    private final float confidence;
    private final long offsetMs;

    public TrackMatch(String trackId, String trackTitle,
                      int matchScore, float confidence, long offsetMs) {
        if (confidence < 0 || confidence > 1) {
            throw new IllegalArgumentException("Confidence must be between 0 and 1");
        }
        this.trackId = Objects.requireNonNull(trackId);
        this.trackTitle = Objects.requireNonNull(trackTitle);
        this.matchScore = matchScore;
        this.confidence = confidence;
        this.offsetMs = offsetMs;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public float getConfidence() {
        return confidence;
    }

    public long getOffsetMs() {
        return offsetMs;
    }

    @Override
    public int compareTo(TrackMatch other) {
        int scoreComparison = Integer.compare(other.matchScore, this.matchScore);
        if (scoreComparison != 0) {
            return scoreComparison;
        }
        return Float.compare(other.confidence, this.confidence);
    }

    public boolean isValid(float minConfidence) {
        return confidence >= minConfidence;
    }

    public static TrackMatch create(String trackId, String trackTitle,
                                    int matches, int totalHashes, long offset) {
        float confidence = totalHashes > 0 ? (float) matches / totalHashes : 0;
        return new TrackMatch(trackId, trackTitle, matches, confidence, offset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackMatch that = (TrackMatch) o;
        return matchScore == that.matchScore &&
                Float.compare(that.confidence, confidence) == 0 &&
                offsetMs == that.offsetMs &&
                trackId.equals(that.trackId) &&
                trackTitle.equals(that.trackTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, trackTitle, matchScore, confidence, offsetMs);
    }

    @Override
    public String toString() {
        return String.format(
                "TrackMatch{id=%s, title=%s, score=%d, confidence=%.2f, offset=%dms}",
                trackId, trackTitle, matchScore, confidence, offsetMs
        );
    }
}