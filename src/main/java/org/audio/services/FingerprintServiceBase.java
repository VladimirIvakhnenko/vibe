package org.audio.services;

import org.audio.models.Peak;

import java.util.List;

public abstract class FingerprintServiceBase {
    List<Long> generateFingerprints(double[] audioSamples) {
        return List.of();
    };

    List<Peak> extractPeaks(double[] audioSamples) {
        return null;
    }
}
