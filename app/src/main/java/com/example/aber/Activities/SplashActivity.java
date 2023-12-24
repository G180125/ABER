package com.example.aber.Activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aber.R;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.splashScreen_ProgressBar);

        final int durationMillis = 3500;


        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(durationMillis);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                progressBar.setProgress(animatedValue);
            }
        });


        animator.start();


        progressBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LandingPageActivity.class));
                finish();
            }
        }, durationMillis);
    }
}
