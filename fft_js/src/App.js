import React, { useState } from 'react';

function App() {
  const [file, setFile] = useState(null);

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };


  const handleFileUpload = () => {
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        const audioData = new DataView(reader.result);
        const { leftChannel, rightChannel } = extractChannels(audioData);

        const blockSize = 1024; // Example block size n
        const shiftSize = 512; // Shift Size 1 braucht viel zu lange
        //const initial = performance.memory.usedJSHeapSize;
        console.log("Performing fft with profiler...")
        if(performance.memory){
          const memoryBefore = performance.memory.usedJSHeapSize;
          processFFTBlocks(leftChannel, blockSize, shiftSize);
          processFFTBlocks(rightChannel, blockSize, shiftSize);
          console.log("fft done.")
          const memoryAfter = performance.memory.usedJSHeapSize;
          console.log(`Memory used by FFT function: ${memoryAfter - memoryBefore} bytes`);
        }
        //const after = performance.memory.usedJSHeapSize;
        //console.log(`Memory used by FFT: ${initial - after} bytes`);
      };
      reader.readAsArrayBuffer(file);
    }
  };

  const extractChannels = (dataView) => {
    const leftChannel = [];
    const rightChannel = [];

    for (let i = 44; i < dataView.byteLength; i += 4) {
      leftChannel.push(dataView.getInt16(i, true) / 32768.0);
      rightChannel.push(dataView.getInt16(i + 2, true) / 32768.0);
    }

    return { leftChannel, rightChannel };
  };

  const processFFTBlocks = (channelData, blockSize, shiftSize) => {
    for (let i = 0; i < channelData.length; i += shiftSize) {
      const block = channelData.slice(i, i + blockSize);
      const fftResult = fft(block);
    }
  };

  const fft = (input) => {
    const n = input.length;
    if (n <= 1) return input;

    const even = fft(input.filter((_, index) => index % 2 === 0));
    const odd = fft(input.filter((_, index) => index % 2 !== 0));

    const result = new Array(n).fill(0).map((_, index) => {
      const t = Math.exp(-2 * Math.PI * index / n) * odd[index % (n / 2)];
      return even[index % (n / 2)] + t;
    });

    return result;
  };

  return (
      <div>
        <input type="file" accept=".wav" onChange={handleFileChange} />
        <button onClick={handleFileUpload}>Upload and Process</button>
      </div>
  );
}

export default App;
