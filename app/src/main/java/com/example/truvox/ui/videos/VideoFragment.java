package com.example.truvox.ui.videos;

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

public class VideoFragment extends Fragment{

    public VideoFragment () {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button warmupButton = view.findViewById(R.id.warmupButton);
        warmupButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_navigation_video_to_navigation_warmup_videos));

        Button tutorialButton = view.findViewById(R.id.tutorialsButton);
        tutorialButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_navigation_video_to_navigation_tutorials));
    }

}
