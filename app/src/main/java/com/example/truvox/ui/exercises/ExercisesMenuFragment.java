package com.example.truvox.ui.exercises;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.truvox.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.truvox.SettingsActivity;
import com.example.truvox.databinding.FragmentExercisesMenuBinding;

public class ExercisesMenuFragment extends Fragment {

    private FragmentExercisesMenuBinding binding;

    public ExercisesMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExercisesMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);

        binding.settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        binding.pitchButton.setOnClickListener(v ->
                navController.navigate(R.id.action_navigation_exercises_to_navigation_pitch)
        );

        binding.pitchVolumeButton.setOnClickListener(v ->
                navController.navigate(R.id.action_navigation_exercises_to_navigation_pitchvol)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}