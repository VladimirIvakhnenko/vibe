package org.audio.fingerprints;

import org.audio.AudioConfig;
import org.audio.utils.FFT;

import org.audio.models.Peak;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class FingerprintGenerator {
    private static final int WINDOW_SIZE = 4096;
    private static final int OVERLAP = 2048;
    private static final int TARGET_ZONE_SIZE = 5;
    private static final int NUM_PEAKS = 5;
    private static final double MIN_MAGNITUDE_THRESHOLD = 0.02;
    private static final int FREQ_BINS = 30;
    private static final double MIN_FREQ = 20;
    private static final double MAX_FREQ = 5000.0;
    private static final double SILENCE_THRESHOLD = 0.02;
    private static final double TARGET_RMS = 0.15;

    private static final ExecutorService executor = Executors.newWorkStealingPool();

    public static List<Long> generateFingerprints(double[] audioData) {
        normalizeVolume(audioData);
        boolean[] activeRegions = detectActiveRegions(audioData);
        return processAudioWindows(audioData, activeRegions);
    }

    private static void normalizeVolume(double[] audioData) {
        double sum = 0;
        for (double sample : audioData) {
            sum += sample * sample;
        }
        double rms = Math.sqrt(sum / audioData.length);

        if (rms < 0.001) return;

        double gain = TARGET_RMS / rms;
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.min(Math.max(audioData[i] * gain, -1.0), 1.0);
        }
    }

    private static boolean[] detectActiveRegions(double[] audioData) {
        boolean[] active = new boolean[audioData.length];
        int regionSize = WINDOW_SIZE / 2;
        int numRegions = (audioData.length + regionSize - 1) / regionSize;

        IntStream.range(0, numRegions).parallel().forEach(i -> {
            int start = i * regionSize;
            int end = Math.min(start + regionSize, audioData.length);
            double sum = 0;

            for (int j = start; j < end; j++) {
                sum += Math.abs(audioData[j]);
            }

            boolean isActive = (sum / (end - start)) > SILENCE_THRESHOLD;
            Arrays.fill(active, start, end, isActive);
        });

        return active;
    }

    private static List<Long> processAudioWindows(double[] audioData, boolean[] activeRegions) {
        double[] hannWindow = precomputeHannWindow(WINDOW_SIZE);

        return IntStream.range(0, (audioData.length + OVERLAP - 1) / OVERLAP)
                .parallel()
                .mapToObj(i -> {
                    int start = i * OVERLAP;
                    int end = Math.min(start + WINDOW_SIZE, audioData.length);

                    if (!isRegionActive(activeRegions, start, end)) {
                        return Collections.<Long>emptyList();
                    }

                    return processWindow(Arrays.copyOfRange(audioData, start, end), hannWindow, start);
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static boolean isRegionActive(boolean[] activeRegions, int start, int end) {
        for (int i = start; i < end && i < activeRegions.length; i++) {
            if (activeRegions[i]) {
                return true;
            }
        }
        return false;
    }

    private static List<Long> processWindow(double[] window, double[] hannWindow, int windowOffset) {
        applyWindow(window, hannWindow);
        FFT.Complex[] fftOutput = computeFFT(window);
        List<Peak> peaks = findSignificantPeaks(fftOutput, windowOffset);
        return generateHashesFromPeaks(peaks);
    }

    private static FFT.Complex[] computeFFT(double[] window) {
        FFT.Complex[] fftInput = new FFT.Complex[window.length];
        for (int i = 0; i < window.length; i++) {
            fftInput[i] = new FFT.Complex(window[i], 0);
        }
        return FFT.fft(fftInput);
    }

    private static List<Peak> findSignificantPeaks(FFT.Complex[] spectrum, int windowOffset) {
        List<Peak>[] bandPeaks = new List[FREQ_BINS];
        Arrays.setAll(bandPeaks, i -> new ArrayList<>());

        for (int i = 1; i < spectrum.length / 2 - 1; i++) {
            double freq = i * (double) AudioConfig.SAMPLE_RATE / spectrum.length;
            if (freq < MIN_FREQ || freq > MAX_FREQ) continue;

            double mag = spectrum[i].abs();
            if (mag < MIN_MAGNITUDE_THRESHOLD) continue;

            if (mag > spectrum[i-1].abs() && mag > spectrum[i+1].abs()) {
                int band = (int)((freq - MIN_FREQ) / (MAX_FREQ - MIN_FREQ) * FREQ_BINS);
                band = Math.min(Math.max(band, 0), FREQ_BINS - 1);


                float timeInSeconds = (float)(windowOffset + i) / AudioConfig.SAMPLE_RATE;
                bandPeaks[band].add(new Peak((float)freq, (float)mag, timeInSeconds));
            }
        }

        List<Peak> significantPeaks = new ArrayList<>();
        for (List<Peak> band : bandPeaks) {
            if (!band.isEmpty()) {
                band.sort(Comparator.comparingDouble(Peak::getAmplitude).reversed());
                significantPeaks.addAll(band.subList(0, Math.min(2, band.size())));
            }
        }

        significantPeaks.sort(Comparator.comparingDouble(Peak::getAmplitude).reversed());
        return significantPeaks.subList(0, Math.min(NUM_PEAKS, significantPeaks.size()));
    }

    private static List<Long> generateHashesFromPeaks(List<Peak> peaks) {
        List<Long> hashes = new ArrayList<>();
        int peakCount = peaks.size();

        for (int i = 0; i < peakCount; i++) {
            Peak anchor = peaks.get(i);
            int end = Math.min(i + TARGET_ZONE_SIZE, peakCount);

            for (int j = i + 1; j < end; j++) {
                Peak point = peaks.get(j);
                long hash = ((long)(anchor.getFrequency() / 10) & 0xFFFF) << 32 |
                        ((long)(point.getFrequency() / 10) & 0xFFFF) << 16 |
                        ((long)((point.getTime() - anchor.getTime()) * 1000) & 0xFFFF);
                hashes.add(hash);
            }
        }

        return hashes;
    }

    private static double[] precomputeHannWindow(int size) {
        double[] window = new double[size];
        for (int i = 0; i < size; i++) {
            window[i] = 0.5 * (1 - Math.cos(2 * Math.PI * i / (size - 1)));
        }
        return window;
    }

    private static void applyWindow(double[] window, double[] hannWindow) {
        for (int i = 0; i < window.length; i++) {
            window[i] *= hannWindow[i];
        }
    }
}