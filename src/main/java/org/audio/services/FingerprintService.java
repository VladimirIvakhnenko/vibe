package org.audio.services;

import org.audio.fingerprints.FingerprintGenerator;
import org.audio.models.Peak;

import java.util.List;

public class FingerprintService extends FingerprintServiceBase {
    private final FingerprintGenerator fingerprintGenerator;

    public FingerprintService() {
        this.fingerprintGenerator = new FingerprintGenerator();
    }

    /**
     * Генерирует аудиоотпечатки для массива сэмплов.
     */
    @Override
    public List<Long> generateFingerprints(double[] audioSamples) {
        return FingerprintGenerator.generateFingerprints(audioSamples);
    }

    /**
     * Извлекает пики из аудиосигнала.
     */
    @Override
    public List<Peak> extractPeaks(double[] audioSamples) {
        return FingerprintGenerator.extractPeaks(audioSamples);
    }
}