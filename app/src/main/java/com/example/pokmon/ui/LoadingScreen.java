package com.example.pokmon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.pokmon.R;

public class LoadingScreen extends BaseActivity {

    private static final int SPLASH_TIMEOUT = 4000; // 4 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        ImageView splashGif = findViewById(R.id.gif);
        Glide.with(this).asGif().load(R.drawable.splash_animation).into(splashGif);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(LoadingScreen.this, RegionActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIMEOUT);
    }
}