package com.example.truvox.ui.ex_pitchvol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.truvox.R;

public class ExPitchvolFragment extends Fragment {

    public ExPitchvolFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ex_pitchvol, container, false);
    }
}
