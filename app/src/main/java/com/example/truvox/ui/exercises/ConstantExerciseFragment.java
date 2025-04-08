
package com.example.truvox.ui.exercises;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
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
    private static final float TOLERANCE = 10f;
    private static final int EXERCISE_DURATION_MS = 15_000; // Set to 15 seconds

    private boolean isRecording = false;
    private boolean isExerciseRunning = false;

    private Handler stopHandler = new Handler();
    private Runnable stopRunnable;

    private Handler countdownHandler = new Handler(Looper.getMainLooper());
    private Handler progressHandler = new Handler(Looper.getMainLooper());
    private Runnable progressRunnable;

    private int score = 0;
    private int totalPoints = 0;

    private final ArrayList<Float> pitchBuffer = new ArrayList<>();
    private static final int PITCH_SMOOTHING_WINDOW = 5; // You can tune this value

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

        binding.settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        binding.imageButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_home);
        });

        binding.PlayIcon.setOnClickListener(v -> {
            if (!isExerciseRunning) {
                isExerciseRunning = true;
                binding.PlayIcon.setEnabled(false);
                binding.PlayIcon.setAlpha(0.5f);
                startCountdownBeforeExercise();
            }
        });

        binding.RetryIcon.setOnClickListener(v -> resetExercise());

        pitchChart = binding.pitchChart;
        initChart();
        drawTargetLine(targetPitch);
        pitchBall = binding.ball;

        pitchChart.post(() -> {
            chartTopY = pitchChart.getTop();
            chartBottomY = pitchChart.getBottom();
        });

        binding.verticalSeekBar.setMax(600);
        binding.verticalSeekBar.setProgress((int) targetPitch);
        binding.verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 50) progress = 50;
                targetPitch = progress;
                drawTargetLine(targetPitch);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void startCountdownBeforeExercise() {
        TextView countdownText = binding.countdownText;
        countdownText.setVisibility(View.VISIBLE);
        final String[] countdownVals = {"3", "2", "1"};

        for (int i = 0; i < countdownVals.length; i++) {
            final int index = i;
            countdownHandler.postDelayed(() -> countdownText.setText(countdownVals[index]), i * 1000L);
        }

        countdownHandler.postDelayed(() -> countdownText.setText("Go!"), 3000L);
        countdownHandler.postDelayed(() -> {
            countdownText.setVisibility(View.GONE);
            startPitchDetection();
        }, 4000L);
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

        binding.exerciseProgressBar.setVisibility(View.GONE);
        binding.exerciseProgressBar.setProgress(0);
        binding.PlayIcon.setImageResource(R.drawable.play_icon);
        pitchBall.setY(pitchChart.getBottom() - pitchBall.getHeight() / 2f);
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

        ProgressBar progressBar = binding.exerciseProgressBar;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(EXERCISE_DURATION_MS);
        progressBar.setProgress(0);

        long startTime = System.currentTimeMillis();
        progressRunnable = new Runnable() {
            @Override public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= EXERCISE_DURATION_MS) {
                    progressBar.setProgress(EXERCISE_DURATION_MS);
                } else {
                    progressBar.setProgress((int) elapsed);
                    progressHandler.postDelayed(this, 50);
                }
            }
        };
        progressHandler.post(progressRunnable);

        stopRunnable = this::stopPitchDetection;
        stopHandler.postDelayed(stopRunnable, EXERCISE_DURATION_MS);
    }

    private void stopPitchDetection() {
        if (!isRecording) return;

        isRecording = false;
        if (pitchDetector != null) pitchDetector.stop();
        stopHandler.removeCallbacks(stopRunnable);
        progressHandler.removeCallbacks(progressRunnable);

        binding.PlayIcon.setEnabled(true);
        binding.PlayIcon.setAlpha(1f);
        binding.exerciseProgressBar.setVisibility(View.GONE);

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

        pitchChart.setData(new LineData(pitchDataSet));
        pitchChart.getDescription().setEnabled(false);
        pitchChart.getLegend().setEnabled(false);

        YAxis leftAxis = pitchChart.getAxisLeft();
        leftAxis.setAxisMinimum(50f);
        leftAxis.setAxisMaximum(600f);
        leftAxis.setTextColor(Color.BLACK);

        pitchChart.getAxisRight().setEnabled(false);

        XAxis xAxis = pitchChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(0.5f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override public String getAxisLabel(float value, AxisBase axis) {
                return String.format("%.1f", value);
            }
        });
    }

    private void drawTargetLine(float targetPitch) {
        LimitLine targetLine = new LimitLine(targetPitch, "Target: " + targetPitch + " Hz");
        targetLine.setLineColor(Color.parseColor("#6C6879"));
        targetLine.setLineWidth(2f);
        targetLine.setTextColor(Color.parseColor("#6C6879"));
        targetLine.setTextSize(14f);

        YAxis leftAxis = pitchChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(targetLine);
        pitchChart.invalidate();
    }

    private void updatePitchChart(float pitchInHz) {
        if (pitchInHz <= 0 || !isRecording) return;

        pitchEntries.add(new Entry(time, pitchInHz));
        time += 0.1f;

        pitchDataSet.notifyDataSetChanged();
        pitchChart.getData().notifyDataChanged();
        pitchChart.notifyDataSetChanged();
        pitchChart.setVisibleXRangeMaximum(10f);
        pitchChart.moveViewToX(time);

        // Smoothing buffer
        pitchBuffer.add(pitchInHz);
        if (pitchBuffer.size() > PITCH_SMOOTHING_WINDOW) {
            pitchBuffer.remove(0);
        }

        // Score only if buffer is full
        if (pitchBuffer.size() == PITCH_SMOOTHING_WINDOW) {
            float sum = 0f;
            for (float val : pitchBuffer) sum += val;
            float smoothedPitch = sum / pitchBuffer.size();

            movePitchBall(smoothedPitch);

            // Scoring: use dynamic tolerance (e.g., ±5% of target pitch)
            float dynamicTolerance = targetPitch * 0.05f;
            totalPoints++;
            if (Math.abs(smoothedPitch - targetPitch) <= dynamicTolerance) {
                score++;
            }
        }
    }

    private void movePitchBall(float pitchInHz) {
        if (pitchChart == null || pitchBall == null) return;

        float minPitch = 50f;
        float maxPitch = 600f;
        pitchInHz = Math.max(minPitch, Math.min(maxPitch, pitchInHz));

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
