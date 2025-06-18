package org.audio.services;

import org.audio.models.AudioProcessingResult;

public interface IAudioMatchingService {
    AudioProcessingResult identifyTrack(byte[] audioData);
    AudioProcessingResult findBestMatches(byte[] audioData, int maxResults, float minConfidence);
    void registerTrack(String trackId, String title, byte[] audioData);
}
