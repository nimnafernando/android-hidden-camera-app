package com.nimna.camerapermission;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class CameraPreviewActivity extends Activity {

    private static final String LOG_TAG = CameraPreviewActivity.class.getSimpleName();

    private static final int CAMERA_ID = 0;

    private Camera mCamera;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Open an instance of the first camera and retrieve its info.
        mCamera = getCameraInstance(CAMERA_ID);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(CAMERA_ID, cameraInfo);

        if (mCamera == null) {
            // if camera is not available -> display an error message
            Toast.makeText(CameraPreviewActivity.this,
                    R.string.camera_not_available,Toast.LENGTH_LONG).show();
        } else {
            setContentView(R.layout.activity_camera);

            // Get the rotation of the screen to adjust the preview image accordingly.
            int displayRotation = getDisplay().getRotation();

            // Create the Preview view and set it as the content of this Activity.
            CameraPreview cameraPreview = new CameraPreview(this, null,
                    0, mCamera, cameraInfo, displayRotation);
            FrameLayout preview = findViewById(R.id.camera_preview);
            preview.addView(cameraPreview);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera access
        releaseCamera();
    }

    /**
     * Release the camera for other applications.
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera getCameraInstance(int cameraId) {
        Camera camera = null;

        try {
            camera = Camera.open(cameraId);
        } catch (Exception exception) {
            Toast.makeText(CameraPreviewActivity.this,
                    R.string.camera_not_available,Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Camera " + cameraId + " is not available: " + exception.getMessage());
        }
        return camera;
    }


    @Override
    protected void onDestroy() {
        if (mCamera != null) {
            mCamera.release();
        }
        super.onDestroy();
    }

}
