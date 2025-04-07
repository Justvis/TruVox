package com.example.truvox.audio;

import android.util.Log;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class PitchDetector {

    private static final String TAG = "PitchDetector";
    private AudioDispatcher dispatcher;
    private Thread audioThread;
    private PitchListener pitchListener;

    public interface PitchListener {
        void onPitchDetected(float pitchInHz);
    }

    public void start(PitchListener listener) {
        stop(); // Ensure previous session is closed

        this.pitchListener = listener;
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        PitchProcessor.PitchEstimationAlgorithm algo = PitchProcessor.PitchEstimationAlgorithm.YIN;

        dispatcher.addAudioProcessor(new PitchProcessor(algo, 22050, 1024, new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                if (pitchListener != null) {
                    pitchListener.onPitchDetected(pitchInHz);
                }
            }
        }));

        audioThread = new Thread(dispatcher, "Audio Dispatcher");
        audioThread.start();
        Log.d(TAG, "Pitch detection started.");
    }

    public void stop() {
        if (dispatcher != null) {
            dispatcher.stop();
            dispatcher = null;
        }
        if (audioThread != null) {
            audioThread.interrupt();
            audioThread = null;
        }
        Log.d(TAG, "Pitch detection stopped.");
    }
}
