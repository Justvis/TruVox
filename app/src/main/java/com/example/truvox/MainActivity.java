package com.example.truvox;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        navView = findViewById(R.id.nav_view);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        // Default bottom nav setup
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_exercises) {
                navController.popBackStack(R.id.navigation_exercises, false);
                navController.navigate(R.id.navigation_exercises);
                return true;
            } else if (itemId == R.id.navigation_home) {
                navController.popBackStack(R.id.navigation_home, false);
                navController.navigate(R.id.navigation_home);
                return true;
            } else if (itemId == R.id.navigation_assessment) {
                navController.popBackStack(R.id.navigation_assessment, false);
                navController.navigate(R.id.navigation_assessment);
                return true;
            }
            return false;
        });

        // Hide nav bar on ConstantExerciseFragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_constant_exercise) {
                navView.setVisibility(View.GONE);
            } else {
                navView.setVisibility(View.VISIBLE);
            }
        });
    }
}
