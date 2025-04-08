package com.example.truvox.ui.exercises;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.truvox.R;
import com.example.truvox.SettingsActivity;
import com.example.truvox.audio.PitchDetector;
import com.example.truvox.databinding.ExercisePageBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class ConstantExerciseFragment extends Fragment {

    private ExercisePageBinding binding;
    private PitchDetector pitchDetector;
    private LineChart pitchChart;
    private LineDataSet pitchDataSet;
    private ArrayList<Entry> pitchEntries;
    private float time = 0f;

    private View pitchBall;
    private float chartTopY;
    private float chartBottomY;

    private float targetPitch = 220f;

    private boolean isRecording = false;

    private boolean isExerciseRunning = false;

    private static final float TOLERANCE = 10f;

    private android.os.Handler stopHandler = new android.os.Handler();
    private Runnable stopRunnable;

    private int score = 0;
    private int totalPoints = 0;

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

        // Settings button → opens settings screen
        binding.settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        // Menu button → navigates to home
        binding.imageButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_home);
        });

        // Play button → starts exercise
        binding.PlayIcon.setOnClickListener(v -> {
            if (!isExerciseRunning) {
                isExerciseRunning = true;
                startPitchDetection();
                binding.PlayIcon.setEnabled(false); // Disable to prevent re-clicks
                binding.PlayIcon.setAlpha(0.5f);    // Visually dim it
            }
        });

        // Retry button → resets chart & state
        binding.RetryIcon.setOnClickListener(v -> {
            resetExercise();
        });

        // Setup chart and line
        pitchChart = binding.pitchChart;
        initChart();
        drawTargetLine(targetPitch); // Initial horizontal line

        pitchBall = binding.ball;

        // Wait for layout to be drawn to capture Y bounds
        pitchChart.post(() -> {
            chartTopY = pitchChart.getTop();
            chartBottomY = pitchChart.getBottom();
        });

        // Setup SeekBar to control target pitch
        binding.verticalSeekBar.setMax(600); // Range: 50 Hz – 600 Hz
        binding.verticalSeekBar.setProgress((int) targetPitch); // Set to initial value

        binding.verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 50) {
                    progress = 50; // Clamp to minimum useful pitch
                }
                targetPitch = progress;
                drawTargetLine(targetPitch);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void resetExercise() {
        stopPitchDetection();
        isExerciseRunning = false;
        time = 0f;
        score = 0;
        totalPoints = 0;
        pitchEntries.clear();
        pitchDataSet.notifyDataSetChanged();
        pitchChart.getData().notifyDataChanged();
        pitchChart.notifyDataSetChanged();
        pitchChart.invalidate();

        binding.PlayIcon.setImageResource(R.drawable.play_icon);
        pitchBall.setY(pitchChart.getBottom() - pitchBall.getHeight() / 2f); // Reset ball to bottom
    }

    private void startPitchDetection() {
        if (isRecording) return;

        isRecording = true;
        time = 0f;
        pitchEntries.clear();
        pitchDataSet.notifyDataSetChanged();
        pitchChart.getData().notifyDataChanged();
        pitchChart.notifyDataSetChanged();
        pitchChart.invalidate();

        pitchDetector = new PitchDetector();
        pitchDetector.start(pitchInHz -> {
            Log.d("Pitch", "Detected pitch: " + pitchInHz);
            requireActivity().runOnUiThread(() -> updatePitchChart(pitchInHz));
        });

        // Automatically stop after 30 seconds
        stopRunnable = this::stopPitchDetection;
        stopHandler.postDelayed(stopRunnable, 30_000);
    }

    private void stopPitchDetection() {
        if (!isRecording) return;

        isRecording = false;
        if (pitchDetector != null) {
            pitchDetector.stop();
        }

        stopHandler.removeCallbacks(stopRunnable);

        // Reset button state
        binding.PlayIcon.setEnabled(true);
        binding.PlayIcon.setAlpha(1f);

        if (totalPoints > 0) {
            int percentage = (int) ((score / (float) totalPoints) * 100);
            Toast.makeText(requireContext(), "Score: " + score + "/" + totalPoints + " (" + percentage + "%)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(requireContext(), "No valid pitch data recorded.", Toast.LENGTH_SHORT).show();
        }

    }

    private void initChart() {
        pitchEntries = new ArrayList<>();
        pitchDataSet = new LineDataSet(pitchEntries, "Pitch");
        pitchDataSet.setColor(Color.TRANSPARENT);
        pitchDataSet.setLineWidth(2f);
        pitchDataSet.setDrawCircles(false);
        pitchDataSet.setDrawValues(false);

        LineData lineData = new LineData(pitchDataSet);
        pitchChart.setData(lineData);

        pitchChart.getDescription().setEnabled(false);
        pitchChart.getLegend().setEnabled(false);

        YAxis leftAxis = pitchChart.getAxisLeft();
        leftAxis.setAxisMinimum(50f);
        leftAxis.setAxisMaximum(600f);
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = pitchChart.getAxisRight();
        rightAxis.setEnabled(false);

        XAxis xAxis = pitchChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(0.5f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return String.format("%.1f", value);
            }
        });
    }

    private void drawTargetLine(float targetPitch) {
        LimitLine targetLine = new LimitLine(targetPitch, "Target: " + targetPitch + " Hz");
        targetLine.setLineColor(Color.parseColor("#6C6879")); // Purple-ish
        targetLine.setLineWidth(2f);
        targetLine.setTextColor(Color.parseColor("#6C6879"));
        targetLine.setTextSize(14f);

        YAxis leftAxis = pitchChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(targetLine);
        pitchChart.invalidate();
    }

    private void updatePitchChart(float pitchInHz) {
        if (pitchInHz > 0 && isRecording) {
            pitchEntries.add(new Entry(time, pitchInHz));
            time += 0.1f;

            pitchDataSet.notifyDataSetChanged();
            pitchChart.getData().notifyDataChanged();
            pitchChart.notifyDataSetChanged();
            pitchChart.setVisibleXRangeMaximum(10f);
            pitchChart.moveViewToX(time);

            movePitchBall(pitchInHz);

            totalPoints++;
            if (Math.abs(pitchInHz - targetPitch) <= TOLERANCE) {
                score++;
            }
        }
    }

    private void movePitchBall(float pitchInHz) {
        if (pitchChart == null || pitchBall == null) return;

        float minPitch = 50f;
        float maxPitch = 600f;

        // Clamp pitch to valid range
        pitchInHz = Math.max(minPitch, Math.min(maxPitch, pitchInHz));

        //Calculate vertical position in chart
        float chartHeight = chartBottomY - chartTopY;
        float relativePitch = (pitchInHz - minPitch) / (maxPitch - minPitch);
        float ballY = chartBottomY - (relativePitch * chartHeight);

        pitchBall.setY(ballY - pitchBall.getHeight() / 2f);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        stopPitchDetection();

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
}
