package org.audio.models;


import java.util.Arrays;

public class AudioProcessingResult {
    private final boolean success;
    private final String errorMessage;
    private final TrackMatch[] matches;
    private final int samplesProcessed;
    private final int audioDurationMs;

    private AudioProcessingResult(boolean success, String errorMessage,
                                  TrackMatch[] matches, int samplesProcessed,
                                  int audioDurationMs) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.matches = matches;
        this.samplesProcessed = samplesProcessed;
        this.audioDurationMs = audioDurationMs;
    }

    public static AudioProcessingResult success(TrackMatch match, int samplesProcessed, int durationMs) {
        return new AudioProcessingResult(true, null, new TrackMatch[]{match}, samplesProcessed, durationMs);
    }

    public static AudioProcessingResult multipleMatches(TrackMatch[] matches, int samplesProcessed, int durationMs) {
        return new AudioProcessingResult(true, null, matches, samplesProcessed, durationMs);
    }

    public static AudioProcessingResult noMatch() {
        return new AudioProcessingResult(true, null, new TrackMatch[0], 0, 0);
    }

    public static AudioProcessingResult error(String message) {
        return new AudioProcessingResult(false, message, null, 0, 0);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public TrackMatch[] getMatches() { return matches; }
    public int getSamplesProcessed() { return samplesProcessed; }
    public int getAudioDurationMs() { return audioDurationMs; }
    public boolean hasMatches() { return matches != null && matches.length > 0; }
}