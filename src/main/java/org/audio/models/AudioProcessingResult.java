package org.audio.models;


import java.util.List;

public class AudioProcessingResult {
    private final TrackMatch match;
    private final int samplesProcessed;
    private final int audioDurationMs;
    private final boolean hasMatch;

    public AudioProcessingResult(TrackMatch match, int samplesProcessed, int audioDurationMs) {
        this.match = match;
        this.samplesProcessed = samplesProcessed;
        this.audioDurationMs = audioDurationMs;
        this.hasMatch = true;
    }

    public static AudioProcessingResult noMatch() {
        return new AudioProcessingResult();
    }

    private AudioProcessingResult() {
        this.match = null;
        this.samplesProcessed = 0;
        this.audioDurationMs = 0;
        this.hasMatch = false;
    }

    // Геттеры
    public TrackMatch getMatch() {
        return match;
    }

    public int getSamplesProcessed() {
        return samplesProcessed;
    }

    public int getAudioDurationMs() {
        return audioDurationMs;
    }

    public boolean hasMatch() {
        return hasMatch;
    }

    public List<TrackMatch> getMatches() {
        return hasMatch ? List.of(match) : List.of();
    }
}