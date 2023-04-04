package com.nimna.camerapermission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private View loginLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        loginLayout = findViewById(R.id.login_layout);

        // Add a onClick listener for the "login_button"
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomePage();
                showCameraPreview();

            }
        });

    }
    private void launchHomePage() {
        System.out.println("  ****** ****** ****** ");
        System.out.println("Display Launch screen");
    }


    private void showCameraPreview() {
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available -> start camera
            Toast.makeText(LoginActivity.this,
                    R.string.camera_permission_available,Toast.LENGTH_LONG).show();

            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void startCamera(){
//        Intent intent = new Intent(this, CameraPreviewActivity.class);
//        startActivity(intent);

        Intent intent = new Intent(this, TakePictureActivity.class);
        startActivity(intent);
    }

    private void requestCameraPermission(){
        // Permission has not provided -> request permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(loginLayout, R.string.camera_permission_requested,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            //Camera could not be opened. This occurs when the camera is not available
            // (for example it is already in use) or if the system has denied access
            // (for example when camera access has been disabled).
            Snackbar.make(loginLayout, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(loginLayout, R.string.camera_permission_granted,
                                Snackbar.LENGTH_SHORT)
                        .show();
                startCamera();
            } else {
                // Permission request was denied.
                Snackbar.make(loginLayout, R.string.camera_permission_denied,
                                Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }


}
