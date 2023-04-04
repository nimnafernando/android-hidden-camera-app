package com.nimna.camerapermission;

import android.content.Context;
import android.hardware.Camera;
import android.media.Image;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String LOG_TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private int mDisplayOrientation;

    private Camera.Parameters parameters;


    private String IMAGE_FILE_LOCATION = "/data/user/0/com.nimna.camerapermission/files/";

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null, null, 0);
    }
    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr,
                         Camera camera, Camera.CameraInfo cameraInfo, int displayOrientation) {
        super(context, attrs, defStyleAttr);

        // Do not initialise if no camera has been set
        if (camera == null || cameraInfo == null) {
            return;
        }
        mCamera = camera;
        mCameraInfo = cameraInfo;
        mDisplayOrientation = displayOrientation;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public static int calculatePreviewOrientation(Camera.CameraInfo info, int rotation) {
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }





    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        try{

            mCamera.setPreviewDisplay(holder);
            //mCamera.startPreview();
            Log.d(LOG_TAG, "Camera preview started.");
        } catch (IOException exception) {
            Log.d(LOG_TAG, "Error setting camera preview.");
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            Log.d(LOG_TAG, "Preview surface does not exist");
            return;
        }

//         stop preview before making changes
        try {
            mCamera.stopPreview();
            Log.d(LOG_TAG, "Preview stopped.");


        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.d(LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }

        int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);
        mCamera.setDisplayOrientation(orientation);

        try {
            //mCamera.setPreviewDisplay(mHolder);

            //get camera parameters
            parameters = mCamera.getParameters();

            //set camera parameters
            mCamera.setParameters(parameters);
            mCamera.startPreview();

            Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    System.out.println("  ********* Save Image ********** ");
                    saveImage(data);

                    try {
                        mCamera.stopPreview();
                        Log.d(LOG_TAG, "Preview stopped.");

                        mCamera.release();
                        //unbind the camera from this object
//                        mCamera = null;

                    } catch (Exception e) {
                        // ignore: tried to stop a non-existent preview
                        Log.d(LOG_TAG, "Error starting camera preview: " + e.getMessage());
                    }
                }

            };
            mCamera.takePicture(null,null,pictureCallback);

            Log.d(LOG_TAG, "Camera preview started.");
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        //stop the preview
        mCamera.stopPreview();
        //release the camera
        mCamera.release();
        //unbind the camera from this object
        mCamera = null;

    }

    private void saveImage(byte[] bytes) {

        File imageFile = new File(IMAGE_FILE_LOCATION, "image.jpg");
        if (imageFile.exists()) {
            imageFile.delete();
        }

        try {

            FileOutputStream fos=new FileOutputStream(imageFile.getPath());
            Log.d(LOG_TAG,"Image saved to : "+ imageFile);

            System.out.println("**********************************");
            System.out.println("Image saved to : "+ imageFile);
            System.out.println("**********************************");

            fos.write(bytes);
            fos.close();
            System.out.println(imageFile);
        } catch (IOException exception) {
            Log.e(LOG_TAG,"Exception in photoCallback");
        }

    }


}
