package org.audio.models;

import java.util.Objects;

/**
 * Пик в спектрограмме
 */

public class Peak {
    private final float frequency;  // Hz
    private final float amplitude; // [0..1]
    private final float time;      // в секундах

    public Peak(float frequency, float amplitude, float time) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.time = time;
    }


    public float getFrequency() { return frequency; }
    public float getAmplitude() { return amplitude; }
    public float getTime() { return time; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peak peak = (Peak) o;
        return Float.compare(peak.frequency, frequency) == 0 &&
                Float.compare(peak.amplitude, amplitude) == 0 &&
                Float.compare(peak.time, time) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(frequency, amplitude, time);
    }
}