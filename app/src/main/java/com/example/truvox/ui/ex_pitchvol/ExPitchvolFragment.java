package com.example.truvox.ui.ex_pitchvol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.truvox.R;

public class ExPitchvolFragment extends Fragment {

    public ExPitchvolFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ex_pitchvol, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button constantButton = view.findViewById(R.id.constantPitchButton);
        constantButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_navigation_pitchvol_to_navigation_constant_exercise));

        Button stairButton = view.findViewById(R.id.stairPitchButton);
        stairButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_navigation_pitchvol_to_navigation_stair_exercise));

        Button heteronymsButton = view.findViewById(R.id.heteronymsPitchButton);
        heteronymsButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_navigation_pitchvol_to_navigation_heteronyms_exercise));
    }
}
