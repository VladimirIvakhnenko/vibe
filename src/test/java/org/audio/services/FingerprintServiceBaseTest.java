package org.audio.services;

import org.audio.models.Peak;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class FingerprintServiceBaseTest {
    @Test
    void generateFingerprints_throwsIfNotImplemented() {
        FingerprintServiceBase base = new FingerprintServiceBase() {
            @Override
            public List<Long> generateFingerprints(double[] audioSamples) {
                throw new UnsupportedOperationException();
            }
            @Override
            public List<Peak> extractPeaks(double[] audioSamples) {
                throw new UnsupportedOperationException();
            }
        };
        assertThrows(UnsupportedOperationException.class, () -> base.generateFingerprints(new double[0]));
        assertThrows(UnsupportedOperationException.class, () -> base.extractPeaks(new double[0]));
    }
} 