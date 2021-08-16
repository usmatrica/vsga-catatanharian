package com.vsga.catatanharian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(
                () -> startActivity(
                        new Intent(
                                SplashScreenActivity.this,
                                MainActivity.class
                        )
                ),
                3000);
    }
}