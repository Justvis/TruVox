package com.example.truvox.ui.ex_pitch;

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

public class ExPitchFragment extends Fragment {

    public ExPitchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ex_pitch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button constantButton = view.findViewById(R.id.constantPitchButton);
        constantButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_navigation_pitch_to_navigation_constant_exercise));
    }
}
