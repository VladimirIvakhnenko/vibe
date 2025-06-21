package org.audio.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FFTTest {
    @Test
    void testFftWithNull() {
        assertThrows(IllegalArgumentException.class, () -> FFT.fft(null));
    }

    @Test
    void testFftPowerOfTwo() {
        FFT.Complex[] input = new FFT.Complex[4];
        for (int i = 0; i < 4; i++) input[i] = new FFT.Complex(i, 0);
        FFT.Complex[] output = FFT.fft(input);
        assertEquals(4, output.length);
    }

    @Test
    void testFftWithPadding() {
        FFT.Complex[] input = new FFT.Complex[3];
        for (int i = 0; i < 3; i++) input[i] = new FFT.Complex(i, 0);
        FFT.Complex[] output = FFT.fftWithPadding(input);
        assertEquals(4, output.length); // padded to 4
    }

    @Test
    void testFftThrowsOnNonPowerOfTwo() {
        FFT.Complex[] input = new FFT.Complex[3];
        for (int i = 0; i < 3; i++) input[i] = new FFT.Complex(i, 0);
        assertThrows(IllegalArgumentException.class, () -> FFT.fft(input));
    }

    @Test
    void testComplexMath() {
        FFT.Complex a = new FFT.Complex(1, 2);
        FFT.Complex b = new FFT.Complex(3, 4);
        assertEquals(new FFT.Complex(4, 6), a.plus(b));
        assertEquals(new FFT.Complex(-2, -2), a.minus(b));
        assertEquals(new FFT.Complex(-5, 10), a.times(b));
        assertEquals(new FFT.Complex(2, 4), a.times(2));
        assertEquals(Math.hypot(1, 2), a.abs());
    }

    @Test
    void testFftWithPadding_nullInput() {
        FFT.Complex[] output = FFT.fftWithPadding(null);
        assertNotNull(output);
        assertEquals(0, output.length);
    }

    @Test
    void testFftWithPadding_emptyInput() {
        FFT.Complex[] output = FFT.fftWithPadding(new FFT.Complex[0]);
        assertNotNull(output);
        assertEquals(0, output.length);
    }

    @Test
    void testFftWithPadding_powerOfTwoInput() {
        FFT.Complex[] input = new FFT.Complex[4];
        for (int i = 0; i < 4; i++) input[i] = new FFT.Complex(i, 0);
        FFT.Complex[] output = FFT.fftWithPadding(input);
        assertEquals(4, output.length);
    }

    @Test
    void testFftWithPadding_nonPowerOfTwoInput() {
        FFT.Complex[] input = new FFT.Complex[3];
        for (int i = 0; i < 3; i++) input[i] = new FFT.Complex(i, 0);
        FFT.Complex[] output = FFT.fftWithPadding(input);
        assertEquals(4, output.length); // padded до 4
    }
} 