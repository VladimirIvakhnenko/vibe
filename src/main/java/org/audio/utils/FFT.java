package org.audio.utils;

public class FFT {
    public static Complex[] fft(Complex[] x) {
        int n = x.length;

        // Base case
        if (n == 1) return new Complex[]{x[0]};

        // Check if n is a power of 2
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // Split into even and odd
        Complex[] even = new Complex[n / 2];
        Complex[] odd = new Complex[n / 2];  // Separate array for odd elements
        for (int k = 0; k < n / 2; k++) {
            even[k] = x[2 * k];
            odd[k] = x[2 * k + 1];
        }

        // Recursive calls
        Complex[] q = fft(even);
        Complex[] r = fft(odd);

        // Combine
        Complex[] y = new Complex[n];
        for (int k = 0; k < n / 2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + n / 2] = q[k].minus(wk.times(r[k]));
        }
        return y;
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

        public double abs() {
            return Math.hypot(re, im);
        }
    }
}