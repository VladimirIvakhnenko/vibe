package org.audio.services;

import org.audio.models.Peak;

import java.util.List;

public abstract class FingerprintServiceBase {
    public abstract List<Long> generateFingerprints(double[] audioSamples);
    public abstract List<Peak> extractPeaks(double[] audioSamples);
}
