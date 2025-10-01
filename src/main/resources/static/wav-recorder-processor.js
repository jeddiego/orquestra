class WavRecorderProcessor extends AudioWorkletProcessor {
  process(inputs, outputs, parameters) {
    const inputChannelData = inputs[0][0];
    if (inputChannelData) {
      // Post a copy of the raw PCM data back to the main thread.
      // This is a Float32Array.
      this.port.postMessage(inputChannelData);
    }
    // Return true to keep the processor alive.
    return true;
  }
}

registerProcessor('wav-recorder-processor', WavRecorderProcessor);