import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.sound.sampled.*;

public class WavFileReader {
    public static float[] readWavFile(String filePath) throws IOException, UnsupportedAudioFileException {
        File file = new File(filePath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();

        if (format.getSampleRate() != 44100.0f || format.getSampleSizeInBits() != 16 || format.getChannels() != 2) {
            throw new IllegalArgumentException("Unsupported audio format. Must be 44.1kHz, 16-bit, stereo.");
        }

        byte[] bytes = audioInputStream.readAllBytes();
        int totalFrames = bytes.length / format.getFrameSize();
        float[] samples = new float[totalFrames];

        int sampleIndex = 0;
        for (int t = 0; t < bytes.length; t += format.getFrameSize()) {
            int low = bytes[t];
            int high = bytes[t + 1];
            int sample = (high << 8) + (low & 0xff);
            samples[sampleIndex++] = sample / 32768.0f;
        }

        audioInputStream.close();
        return samples;
    }
}
