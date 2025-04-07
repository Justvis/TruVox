package com.example.truvox.ui.exercises;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.truvox.R;
import com.example.truvox.SettingsActivity;
import com.example.truvox.audio.PitchDetector;
import com.example.truvox.databinding.ExercisePageBinding;

import android.content.pm.ActivityInfo;

public class ConstantExerciseFragment extends Fragment {

    private static final int RECORD_AUDIO_REQUEST_CODE = 123;

    private ExercisePageBinding binding;
    private PitchDetector pitchDetector;

    public ConstantExerciseFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = ExercisePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Settings button
        binding.settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        // Menu button
        binding.imageButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_home);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        checkMicrophonePermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (pitchDetector != null) {
            pitchDetector.stop();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_REQUEST_CODE
            );
        } else {
            startPitchDetection();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startPitchDetection();
        }
    }

    private void startPitchDetection() {
        pitchDetector = new PitchDetector();
        pitchDetector.start(pitchInHz -> {
            Log.d("Pitch", "Detected pitch: " + pitchInHz);
            // You can update your UI or chart here
        });
    }
}
