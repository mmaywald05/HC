

public class DFT {
    public static Complex[] discreteFourierTransform(float[] signal) {
        int N = signal.length;
        Complex[] result = new Complex[N];

        // Perform DFT
        for (int k = 0; k < N; k++) {
            if(k%100 == 0){
                System.out.println("iteration:" +k);
            }

            double real = 0;
            double imag = 0;
            for (int n = 0; n < N; n++) {
                double theta = 2 * Math.PI * k * n / N;
                real += signal[n] * Math.cos(theta);
                imag -= signal[n] * Math.sin(theta);
            }
            result[k] = new Complex(real, imag);
        }

        return result;
    }
}
