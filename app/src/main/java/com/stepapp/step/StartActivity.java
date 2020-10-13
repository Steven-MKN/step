package com.stepapp.step;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.stepapp.step.repos.MainRepository;

public class StartActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        StartActivity.super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void onStart() {
        super.onStart();
        if (MainRepository.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
