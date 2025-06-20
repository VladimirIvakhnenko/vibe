package org.audio.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FingerprintServiceTest {
    @Test
    void generateFingerprints_returnsNotNull() {
        FingerprintService service = new FingerprintService();
        double[] audio = new double[4096];
        assertNotNull(service.generateFingerprints(audio));
    }

    @Test
    void extractPeaks_returnsNotNull() {
        FingerprintService service = new FingerprintService();
        double[] audio = new double[4096];
        assertNotNull(service.extractPeaks(audio));
    }

} 