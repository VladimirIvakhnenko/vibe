package org.audio.utils;

public class FFT {
    /**
     * Вычисляет БПФ (FFT) комплексного массива.
     * Длина массива должна быть степенью двойки.
     *
     * @param x входной комплексный массив
     * @return преобразованный массив
     * @throws IllegalArgumentException если длина массива не степень двойки
     */
    public static Complex[] fft(Complex[] x) {
        if (x == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }

        int n = x.length;


        if ((n & (n - 1)) != 0) {
            throw new IllegalArgumentException("Array length must be a power of 2");
        }


        if (n == 1) {
            return new Complex[]{x[0]};
        }


        Complex[] even = new Complex[n/2];
        Complex[] odd = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
            odd[k] = x[2*k + 1];
        }


        Complex[] evenFFT = fft(even);
        Complex[] oddFFT = fft(odd);


        Complex[] result = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double angle = -2 * Math.PI * k / n;
            Complex wk = new Complex(Math.cos(angle), Math.sin(angle)); // Поворачивающий множитель

            Complex product = wk.times(oddFFT[k]);
            result[k] = evenFFT[k].plus(product);
            result[k + n/2] = evenFFT[k].minus(product);
        }

        return result;
    }

    public static Complex[] fftWithPadding(Complex[] x) {
        if (x == null) {
            return new Complex[0];
        }

        if (x.length == 0) {
            return new Complex[0];
        }

        int nextPowerOfTwo = nextPowerOfTwo(x.length);

        if (nextPowerOfTwo != x.length) {
            Complex[] padded = new Complex[nextPowerOfTwo];
            System.arraycopy(x, 0, padded, 0, x.length);
            for (int i = x.length; i < padded.length; i++) {
                padded[i] = new Complex(0, 0);
            }
            return fft(padded);
        }

        return fft(x);
    }

    private static int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power <<= 1;
        }
        return power;
    }

    public static class Complex {
        public final double re;
        public final double im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public Complex plus(Complex b) {
            return new Complex(this.re + b.re, this.im + b.im);
        }

        public Complex minus(Complex b) {
            return new Complex(this.re - b.re, this.im - b.im);
        }

        public Complex times(Complex b) {
            return new Complex(
                    this.re * b.re - this.im * b.im,
                    this.re * b.im + this.im * b.re
            );
        }

        public Complex times(double scalar) {
            return new Complex(this.re * scalar, this.im * scalar);
        }

        public double abs() {
            return Math.hypot(re, im);
        }

        @Override
        public String toString() {
            return String.format("(%.3f, %.3fi)", re, im);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Complex complex = (Complex) o;
            return Double.compare(complex.re, re) == 0 &&
                   Double.compare(complex.im, im) == 0;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(re, im);
        }
    }
}