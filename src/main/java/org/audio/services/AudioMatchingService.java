package org.audio.services;

import org.audio.db.FingerprintDatabase;
import org.audio.models.AudioProcessingResult;
import org.audio.models.TrackMatch;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AudioMatchingService implements  IAudioMatchingService {
    private static final int SAMPLE_RATE = 44100; // 44.1 kHz
    private static final int BYTES_PER_SAMPLE = 2; // 16-bit samples

    private final FingerprintDatabase fingerprintDatabase;
    private final FingerprintServiceBase fingerprintService;

    public AudioMatchingService(FingerprintDatabase fingerprintDatabase,
                                FingerprintServiceBase fingerprintService) {
        this.fingerprintDatabase = fingerprintDatabase;
        this.fingerprintService = fingerprintService;
    }

    /**
     * Идентифицирует трек по аудиоданным.
     */
    @Override
    public AudioProcessingResult identifyTrack(byte[] audioData) {
        if (audioData == null || audioData.length == 0) {
            return AudioProcessingResult.error("Audio data is empty");
        }

        try {
            double[] samples = transformAudioToSamples(audioData);
            List<Long> queryHashes = fingerprintService.generateFingerprints(samples);

            Optional<TrackMatch> match = fingerprintDatabase.findBestMatch(queryHashes);

            if (match.isPresent()) {
                int sampleCount = audioData.length / BYTES_PER_SAMPLE;
                int durationMs = calculateAudioDurationMs(sampleCount);
                return AudioProcessingResult.success(match.get(), sampleCount, durationMs);
            }

            return AudioProcessingResult.noMatch();
        } catch (Exception e) {
            return AudioProcessingResult.error("Error processing audio: " + e.getMessage());
        }
    }

    /**
     * Находит лучшие совпадения по аудиоданным.
     */
    @Override
    public AudioProcessingResult findBestMatches(byte[] audioData, int maxResults, float minConfidence) {
        if (audioData == null || audioData.length == 0) {
            return AudioProcessingResult.error("Audio data is empty");
        }

        try {
            double[] samples = transformAudioToSamples(audioData);
            List<Long> queryHashes = fingerprintService.generateFingerprints(samples);

            TrackMatch[] matches = fingerprintDatabase.bestMatches(queryHashes, maxResults, minConfidence);
            int sampleCount = audioData.length / BYTES_PER_SAMPLE;
            int durationMs = calculateAudioDurationMs(sampleCount);

            return AudioProcessingResult.multipleMatches(matches, sampleCount, durationMs);
        } catch (Exception e) {
            return AudioProcessingResult.error("Error processing audio: " + e.getMessage());
        }
    }

    /**
     * Регистрирует новый трек в базе по аудиоданным.
     */
    @Override
    public void registerTrack(String trackId, String title, byte[] audioData) {
        if (trackId == null || title == null || audioData == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        double[] samples = transformAudioToSamples(audioData);
        List<Long> fingerprints = fingerprintService.generateFingerprints(samples);
        fingerprintDatabase.addTrack(trackId, title, fingerprints);
    }

    private double[] transformAudioToSamples(byte[] audioData) {
        double[] samples = new double[audioData.length / 2];

        for (int i = 0; i < samples.length; i++) {
            short sample = (short) ((audioData[2*i+1] << 8) | (audioData[2*i] & 0xff));
            samples[i] = sample / 32768.0;
        }

        return samples;
    }

    private int calculateAudioDurationMs(int sampleCount) {
        return (int) ((sampleCount * 1000L) / SAMPLE_RATE);
    }
}