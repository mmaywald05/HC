public class FFT {
    public static void fft(float[] inputReal, float[] inputImag) {
        int n = inputReal.length;

        if (n == 1) return;

        int halfSize = n / 2;
        float[] evenReal = new float[halfSize];
        float[] evenImag = new float[halfSize];
        float[] oddReal = new float[halfSize];
        float[] oddImag = new float[halfSize];

        for (int i = 0; i < halfSize; i++) {
            evenReal[i] = inputReal[i * 2];
            evenImag[i] = inputImag[i * 2];
            oddReal[i] = inputReal[i * 2 + 1];
            oddImag[i] = inputImag[i * 2 + 1];
        }

        fft(evenReal, evenImag);
        fft(oddReal, oddImag);

        for (int k = 0; k < halfSize; k++) {
            double angle = -2 * Math.PI * k / n;
            float cosine = (float) Math.cos(angle);
            float sine = (float) Math.sin(angle);
            float tempReal = cosine * oddReal[k] - sine * oddImag[k];
            float tempImag = cosine * oddImag[k] + sine * oddReal[k];

            inputReal[k] = evenReal[k] + tempReal;
            inputImag[k] = evenImag[k] + tempImag;
            inputReal[k + halfSize] = evenReal[k] - tempReal;
            inputImag[k + halfSize] = evenImag[k] - tempImag;
        }
    }

    public static void processFFTInBlocks(float[] inputReal, float[] inputImag, int blockSize, int shiftSize) {
        int length = inputReal.length;
        for (int start = 0; start + blockSize <= length; start += shiftSize) {
            float[] blockReal = new float[blockSize];
            float[] blockImag = new float[blockSize];

            // Copy data to the block arrays
            System.arraycopy(inputReal, start, blockReal, 0, blockSize);
            System.arraycopy(inputImag, start, blockImag, 0, blockSize);

            // Compute FFT on the block
            fft(blockReal, blockImag);

            // Copy the transformed data back to the input arrays
            System.arraycopy(blockReal, 0, inputReal, start, blockSize);
            System.arraycopy(blockImag, 0, inputImag, start, blockSize);
        }
    }
}
