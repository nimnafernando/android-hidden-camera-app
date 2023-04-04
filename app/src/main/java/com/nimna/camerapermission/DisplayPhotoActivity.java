package com.nimna.camerapermission;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DisplayPhotoActivity extends AppCompatActivity {

    private static final String LOG_TAG = TakePictureActivity.class.getSimpleName();

    private String IMAGE_FILE_LOCATION = "/data/user/0/com.nimna.camerapermission/files/";

    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_photo);
        imageView = (ImageView) findViewById(R.id.image_view_user_face);

        displayCaptureImage();

    }

    private void displayCaptureImage() {
        File imageFile = new File(IMAGE_FILE_LOCATION, "image.jpg");
        if (imageFile.exists()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {

                Log.e(LOG_TAG,"Exception in photo loading");
            }
        }

    }
}
