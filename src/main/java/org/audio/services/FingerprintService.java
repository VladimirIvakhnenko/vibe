package org.audio.services;


import org.audio.fingerprints.FingerprintGenerator;
import org.audio.models.Peak;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FingerprintService extends FingerprintServiceBase {
    private final FingerprintGenerator fingerprintGenerator;

    public FingerprintService() {
        this.fingerprintGenerator = new FingerprintGenerator();
    }


    public List<Long> generateFingerprints(double[] audioSamples) {
        return fingerprintGenerator.generateFingerprints(audioSamples);
    }


    public List<Peak> extractPeaks(double[] audioSamples) {
        fingerprintGenerator.generateFingerprints(audioSamples);
        return List.of();
    }
}