package org.audio.services;

import org.audio.db.FingerprintDatabase;
import org.audio.fingerprints.FingerprintGenerator;
import org.audio.models.AudioProcessingResult;
import org.audio.models.TrackMatch;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AudioMatchingService {

    private final FingerprintDatabase fingerprintDatabase;
    private final FingerprintGenerator fingerprintGenerator;

    public AudioMatchingService(FingerprintDatabase fingerprintDatabase,
                                FingerprintGenerator fingerprintGenerator) {
        this.fingerprintDatabase = fingerprintDatabase;
        this.fingerprintGenerator = fingerprintGenerator;
    }

    public AudioProcessingResult identifyTrack(byte[] audioData) {

        List<Long> queryHashes = fingerprintGenerator.generateFingerprints(transformTrack(audioData));


        Optional<TrackMatch> match = fingerprintDatabase.findBestMatch(queryHashes);


        if (match.isPresent()) {
            return new AudioProcessingResult(
                    match.get(),
                    audioData.length / 2,
                    calculateAudioDurationMs(audioData)
            );
        }

        return AudioProcessingResult.noMatch();
    }

    public void registerTrack(String trackId, String title, byte[] audioData) {
        List<Long> fingerprints = fingerprintGenerator.generateFingerprints(transformTrack(audioData));
        fingerprintDatabase.addFingerprints(trackId, fingerprints);
    }

    private double[] transformTrack(byte[] audioData) {
        double[] samples = new double[audioData.length / 2];

        for (int i = 0; i < samples.length; i++) {
            short sample = (short) ((audioData[2*i+1] << 8) | (audioData[2*i] & 0xff));
            samples[i] = sample / 32768.0;
        }

        return samples;
    }

    private int calculateAudioDurationMs(byte[] audioData) {
        // Пример: 16-bit samples, mono, 44100 Hz
        return (audioData.length / 2) * 1000 / 44100;
    }
}