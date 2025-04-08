package com.example.truvox.functions;

public class getPitch {
/*    int sampleRate = 44100;
    int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);

    AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize);

    FxAuto fxAuto = new FxAuto(75, 100, 300, 500, sampleRate);
    short[] audioBuffer = new short[bufferSize];
    float[] floatBuffer = new float[bufferSize];

recorder.startRecording();

new Thread(() -> {
        while (true) {
            int read = recorder.read(audioBuffer, 0, bufferSize);
            for (int i = 0; i < read; i++) {
                floatBuffer[i] = audioBuffer[i] / 32768.0f; // Convert to float [-1, 1]
            }

            FxAuto.FxResult result = fxAuto.calculateFx(floatBuffer, read);
            float pitch = result.fx;

            runOnUiThread(() -> {
                // Update UI with pitch
                Log.d("Pitch", "Detected pitch: " + pitch + " Hz");
            });
        }
    }).start(); */
}
