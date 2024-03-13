package com.ugwebstudio.plasticwastemanagementapp.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ugwebstudio.plasticwastemanagementapp.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int ANIMATION_DURATION = 2000; // Animation duration in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Find the ImageView
        ImageView imageView = findViewById(R.id.imageViewLogo);

        // Create an ObjectAnimator to animate the ImageView
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(ANIMATION_DURATION); // Set animation duration
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat indefinitely

        // Start the animation
        animator.start();

        // Example: Delay for splash screen and then transition to main activity
        // You may replace this with your actual splash screen logic
        new android.os.Handler().postDelayed(
                () -> {
                    // Start your main activity
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    // Finish splash screen activity
                    finish();
                },
                ANIMATION_DURATION // Delay duration same as animation duration
        );
    }
}
