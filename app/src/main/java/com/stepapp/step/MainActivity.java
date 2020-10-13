package com.stepapp.step;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.customview.widget.Openable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.stepapp.step.repos.MainRepository;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MainActivity.super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkMenuToNavCnt();
        linkToolbarToNavCnt();
        setupRepo();
    }

    public void linkMenuToNavCnt() {
        Fragment mainFragHost = getSupportFragmentManager().findFragmentById(R.id.main_frag_holder);
        NavigationUI.setupWithNavController((BottomNavigationView) findViewById(R.id.bottom_nav_menu), NavHostFragment.findNavController(mainFragHost));
    }

    public void linkToolbarToNavCnt() {
        Fragment mainFragHost = getSupportFragmentManager().findFragmentById(R.id.main_frag_holder);
        HashSet<Integer> topLevelDestinations = new HashSet<>();

        topLevelDestinations.add(Integer.valueOf(R.id.mapFragment));
        topLevelDestinations.add(Integer.valueOf(R.id.savedFragment));
        topLevelDestinations.add(Integer.valueOf(R.id.preferencesFragment));

        NavigationUI.setupWithNavController(
                (Toolbar) findViewById(R.id.toolbar),
                NavHostFragment.findNavController(mainFragHost),
                new AppBarConfiguration.Builder(topLevelDestinations).build());
    }

    public void setupRepo() {
        MainRepository.getInstance().initRepo(getApplicationContext(), getPreferences(MODE_PRIVATE));
    }
}