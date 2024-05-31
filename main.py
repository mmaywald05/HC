import numpy as np
import matplotlib.pyplot as plt
import soundfile as sf
import tracemalloc

def read_wav(file_path):
    data, samplerate = sf.read(file_path)
    return data, samplerate

def perform_fft(data, block_size, shift_size, samplerate):
    tracemalloc.start()
    num_blocks = (len(data) - block_size) // shift_size + 1
    frequencies = np.fft.rfftfreq(block_size, d=1/samplerate)
    #spectrogram = (np.zeros((num_blocks, len(frequencies))))
    spectrogram = []
    main_frequencies = []
    main_amplitudes = []

    for i in range(num_blocks):
        start = i * shift_size
        end = start + block_size
        block = data[start:end]
        windowed_block = block * np.hanning(block_size)
        spectrum = np.abs(np.fft.rfft(windowed_block))
        #spectrogram[i, :] = spectrum

        # Find main frequency and amplitude
        main_index = np.argmax(spectrum)
        main_frequency = frequencies[main_index]
        main_amplitude = spectrum[main_index]
        main_frequencies.append(main_frequency)
        main_amplitudes.append(main_amplitude)

    print(tracemalloc.get_traced_memory())
    tracemalloc.stop()

    return frequencies, spectrogram, main_frequencies, main_amplitudes

def plot_spectrogram(frequencies, spectrogram, samplerate, shift_size):
    plt.figure(figsize=(12, 6))
    plt.imshow(
        np.log(spectrogram.T + 1e-7),
        aspect='auto',
        origin='lower',
        extent=[0, len(spectrogram) * shift_size / samplerate, frequencies[0], frequencies[-1]]
    )
    plt.colorbar(format='%+2.0f dB')
    plt.xlabel('Time [s]')
    plt.ylabel('Frequency [Hz]')
    plt.title('Spectrogram')
    plt.show()

def main(file_path, block_size, shift_size):
    data, samplerate = read_wav(file_path)
    if len(data.shape) == 2:  # Check if stereo and convert to mono
        data = data.mean(axis=1)
    frequencies, spectrogram, main_frequencies, main_amplitudes = perform_fft(data, block_size, shift_size, samplerate)
    #plot_spectrogram(frequencies, spectrogram, samplerate, shift_size)

    #print("Angabe der Hauptfrequenzen und deren Amplitude:")
    #for i, (freq, amp) in enumerate(zip(main_frequencies, main_amplitudes)):
     #   print(f"Block {i+1}: Hauptfrequenz = {freq:.2f} Hz, Amplitude = {amp:.2f}")

# Example usage
file_path = 'nicht_zu_laut_abspielen.wav'
block_size = 1024  # Example block size
shift_size = block_size // 64
main(file_path, block_size, shift_size)