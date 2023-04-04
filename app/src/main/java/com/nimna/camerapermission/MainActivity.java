package com.nimna.camerapermission;

import static com.nimna.camerapermission.R.*;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    Button mainActivity_next_button,view_capture_photo_button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        mainActivity_next_button = findViewById(R.id.button_validateUsername);
        mainActivity_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG,"Next button clicked! Move to login view..... ");
                Intent moveSignInIntent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(moveSignInIntent);
            }
        });

        view_capture_photo_button = findViewById(R.id.button_view_capture_photo);
        view_capture_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG,"View Photo button clicked! Move to capture photo view..... ");
                Intent moveSignInIntent = new Intent(MainActivity.this,DisplayPhotoActivity.class);
                startActivity(moveSignInIntent);
            }
        });
    }
}

/*
**
**
**
Resources :

        https://github.com/android/permissions-samples/tree/c4573f1218cef81295b519592718ecef3b144095
 */
 
