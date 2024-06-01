import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WavReader {
    private static final int BUFFER_SIZE = 4096;

    public static float[][] readWav(String filePath) throws UnsupportedAudioFileException, IOException {
        File wavFile = new File(filePath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
        AudioFormat format = audioInputStream.getFormat();

        if (format.getChannels() != 2 || format.getSampleRate() != 44100.0 || format.getSampleSizeInBits() != 16) {
            throw new IllegalArgumentException("WAV file must be stereo, 44.1kHz, 16 bit.");
        }

        long frameLength = audioInputStream.getFrameLength();
        int numSamples = (int) frameLength;

        float[][] samples = new float[2][numSamples];
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        int sampleIndex = 0;

        while ((bytesRead = audioInputStream.read(buffer)) != -1) {
            for (int i = 0; i < bytesRead; i += format.getFrameSize()) {
                for (int channel = 0; channel < format.getChannels(); channel++) {
                    int sample = 0;
                    sample |= buffer[i + channel * 2] & 0xFF;
                    sample |= buffer[i + channel * 2 + 1] << 8;
                    samples[channel][sampleIndex] = sample / 32768.0f; // Normalize to [-1, 1]
                }
                sampleIndex++;
            }
        }

        audioInputStream.close();
        return samples;
    }
}
