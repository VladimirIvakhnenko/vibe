package org.audio.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PeakTest {
    @Test
    void testGetters() {
        Peak peak = new Peak(440.0f, 0.8f, 1.2f);
        assertEquals(440.0f, peak.getFrequency());
        assertEquals(0.8f, peak.getAmplitude());
        assertEquals(1.2f, peak.getTime());
    }

    @Test
    void testEqualsAndHashCode() {
        Peak p1 = new Peak(440.0f, 0.8f, 1.2f);
        Peak p2 = new Peak(440.0f, 0.8f, 1.2f);
        Peak p3 = new Peak(441.0f, 0.8f, 1.2f);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
    }
} 