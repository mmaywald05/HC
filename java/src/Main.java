import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String filePath = "/Users/maywald/IdeaProjects/HC/HC1/src/nicht_zu_laut_abspielen.wav"; // Specify the path to your .wav file

        try {


            // Read the WAV fileq
            float[] audioSignal = WavFileReader.readWavFile(filePath);

            // Truncate to the next smallest power of 2
            int n = (int) Math.pow(2, Math.floor(Math.log(audioSignal.length) / Math.log(2)));
            float[] truncatedSignal = new float[n];
            System.arraycopy(audioSignal, 0, truncatedSignal, 0, n);

            // Prepare the imaginary part (all zeros)
            float[] imaginary = new float[n];

            // Perform FFT
            Runtime runtime =Runtime.getRuntime();
            long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
            FFT.processFFTInBlocks(truncatedSignal, imaginary, 64, 16);
            long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Memory increased:" + (usedMemoryAfter-usedMemoryBefore));

            // Calculate the number of frequencies to display (lowest 10%)
            int lower10PercentN = (int) (n * 0.1);
            double[] frequencies = new double[lower10PercentN];
            double[] magnitudes = new double[lower10PercentN];
            for (int i = 0; i < lower10PercentN; i++) {
                magnitudes[i] = Math.sqrt(truncatedSignal[i] * truncatedSignal[i] + imaginary[i] * imaginary[i]);
                frequencies[i] = i * 44100.0 / n;
            }

            // Display the plot
            JFrame frame = new JFrame("Frequency Spectrum");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600); // Adjusted height for better visibility
            frame.add(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int width = getWidth();
                    int height = getHeight();
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, width, height);

                    g.setColor(Color.BLACK);
                    g.drawLine(50, height - 50, width - 50, height - 50);
                    g.drawLine(50, height - 50, 50, 50);

                    double maxFrequency = frequencies[frequencies.length - 1];
                    double maxMagnitude = Double.NEGATIVE_INFINITY;
                    for (double magnitude : magnitudes) {
                        if (magnitude > maxMagnitude) {
                            maxMagnitude = magnitude;
                        }
                    }

                    // Plot the frequencies and magnitudes
                    for (int i = 0; i < frequencies.length; i++) {
                        int x = (int) (50 + (frequencies[i] / maxFrequency) * (width - 100));
                        int y = (int) (height - 50 - (magnitudes[i] / maxMagnitude) * (height - 100));
                        g.fillOval(x, y, 3, 3);
                    }

                    // Draw x-axis labels
                    g.setColor(Color.BLACK);
                    for (int i = 0; i <= 10; i++) {
                        int x = 50 + i * (width - 100) / 10;
                        int frequencyLabel = (int) (i * maxFrequency / 10);
                        g.drawLine(x, height - 50, x, height - 45);
                        g.drawString(frequencyLabel + " Hz", x - 15, height - 30);
                    }

                    // Draw y-axis label
                    g.drawString("Frequency (Hz)", width / 2 - 30, height - 20);
                    g.drawString("Magnitude", 10, height / 2);
                }
            });
            frame.setVisible(true);

        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
}
