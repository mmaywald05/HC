public class FFTAnalyzer {
    public static void main(String[] args) {
        String path = "/Users/maywald/IdeaProjects/HC_Git/nicht_zu_laut_abspielen.wav";
        try {
            int blockSize = 2048;
            int shiftSize = 1024;
            float[][] samples = WavReader.readWav(path);
            System.out.println("samples: ");
            System.out.println(samples[0].length);

            int numBlocks = (samples[0].length - blockSize) / shiftSize + 1;
            double[] real = new double[blockSize];
            double[] imag = new double[blockSize];


            Runtime runtime = Runtime.getRuntime();
            long before = runtime.totalMemory() - runtime.freeMemory();
            for (int block = 0; block < numBlocks; block++) {
                int offset = block * shiftSize;
                for (int i = 0; i < blockSize; i++) {
                    if (offset + i < samples[0].length) {
                        real[i] = samples[0][offset + i];
                    } else {
                        real[i] = 0.0;
                    }
                    imag[i] = 0.0;
                }
                FFT.fft(real, imag);
                // Process FFT results here if needed.
            }
            long after = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("before: " + before);
            System.out.println("after: " + after);
            System.out.println("Memory used by FFT execution: " + (before - after) + " bytes");
            System.out.println("allocated doubles: " + FFT.doubles);
            System.out.println("allocated ints: " + FFT.ints);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
