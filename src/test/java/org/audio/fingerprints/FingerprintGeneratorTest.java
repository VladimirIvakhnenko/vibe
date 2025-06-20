package org.audio.fingerprints;

import org.audio.models.Peak;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FingerprintGeneratorTest {
    @Test
    void generateFingerprints_emptyAudio_returnsEmptyList() {
        double[] audio = new double[0];
        List<Long> fingerprints = FingerprintGenerator.generateFingerprints(audio);
        assertNotNull(fingerprints);
    }

    @Test
    void extractPeaks_emptyAudio_returnsEmptyList() {
        double[] audio = new double[0];
        List<Peak> peaks = FingerprintGenerator.extractPeaks(audio);
        assertNotNull(peaks);
        assertTrue(peaks.isEmpty());
    }

    @Test
    void generateFingerprints_and_extractPeaks_workOnSimpleSignal() {
        double[] audio = new double[4096];
        for (int i = 0; i < audio.length; i++) {
            audio[i] = Math.sin(2 * Math.PI * 440 * i / 44100.0); // 440 Hz синус
        }
        List<Long> fingerprints = FingerprintGenerator.generateFingerprints(audio);
        List<Peak> peaks = FingerprintGenerator.extractPeaks(audio);
        assertNotNull(fingerprints);
        assertNotNull(peaks);
        assertFalse(peaks.isEmpty());
    }
} 