public class FFT {
    static int doubles = 0;
    static int ints = 0;

    public static void fft(double[] real, double[] imag) {
        Runtime runtime = Runtime.getRuntime();
        int n = real.length;
        FFT.ints++;
        if (n != imag.length || Integer.bitCount(n) != 1) {
            throw new IllegalArgumentException("Lengths of real and imaginary parts must be powers of 2 and equal.");
        }
        int logN = Integer.numberOfTrailingZeros(n);
        FFT.ints++;
        for (int i = 0; i < n; i++) {
            FFT.ints++;
            int j = Integer.reverse(i) >>> (32 - logN);
            if (j > i) {
                FFT.doubles++;
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }
        for (int size = 2; size <= n; size *= 2) {
            double angle = 2 * Math.PI / size;
            double wReal = Math.cos(angle);
            double wImag = Math.sin(angle);
            FFT.doubles += 3;
            for (int i = 0; i < n; i += size) {
                double uReal = 1.0;
                double uImag = 0.0;
                for (int j = 0; j < size / 2; j++) {
                    int evenIndex = i + j;
                    int oddIndex = i + j + size / 2;
                    double tReal = uReal * real[oddIndex] - uImag * imag[oddIndex];
                    double tImag = uReal * imag[oddIndex] + uImag * real[oddIndex];
                    real[oddIndex] = real[evenIndex] - tReal;
                    imag[oddIndex] = imag[evenIndex] - tImag;
                    real[evenIndex] += tReal;
                    imag[evenIndex] += tImag;
                    double tempReal = uReal * wReal - uImag * wImag;
                    uImag = uReal * wImag + uImag * wReal;
                    uReal = tempReal;
                    FFT.doubles +=3;
                    FFT.ints += 2;
                }
            }
        }
    }
}
