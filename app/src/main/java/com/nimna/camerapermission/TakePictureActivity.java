package com.nimna.camerapermission;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class TakePictureActivity extends Activity implements SurfaceHolder.Callback {
    private static final String LOG_TAG = TakePictureActivity.class.getSimpleName();

//    private final String IP_ADDRESS = "192.168.8.136:5000";
    private final String IP_ADDRESS = "127.0.0.1:5000";

    private static final int CAMERA_PERMISSION_CODE = 1;

    //a variable to store a reference to the Image View at the main.xml file
    private ImageView iv_image;
    //a variable to store a reference to the Surface View at the main.xml file
    private SurfaceView sv;

    //a bitmap to display the captured image
    private Bitmap bmp;

    //Camera variables
    //a surface holder
    private SurfaceHolder sHolder;
    //a variable to control the camera
    private Camera mCamera;
    //the camera parameters
    private Camera.Parameters parameters;

    private String IMAGE_FILE_LOCATION = "/data/user/0/com.nimna.camerapermission/files/";


    /**
     * Called when the activity is first created.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        //get the Image View at the main.xml file
        iv_image = (ImageView) findViewById(R.id.imageView);

        //get the Surface View at the main.xml file
        sv = (SurfaceView) findViewById(R.id.surfaceView);

        //Get a surface
        sHolder = sv.getHolder();

        //add the callback interface methods defined below as the Surface View callbacks
        sHolder.addCallback(this);

        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        //get camera parameters
        parameters = mCamera.getParameters();

        //set camera parameters
        mCamera.setParameters(parameters);
        mCamera.startPreview();

        //sets what code should be executed after the picture is taken
        Camera.PictureCallback mCall = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //decode the data obtained by the camera into a Bitmap
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                //set the iv_image
                iv_image.setImageBitmap(bmp);

                saveImage(data);
                //detectUserEmotion();

                File imageFile = new File(IMAGE_FILE_LOCATION, "image.jpg");
                try {
                    System.out.println("****** Image Send ******");
                    //detectEmotion(imageFile); // Unable to fix the issue
                    //detectUserEmotion(); // detectUserEmotion() -> execute the catch block : android.os.NetworkOnMainThreadException

                    connectApi();
                } catch (Exception e) {
                    System.out.println("EXCEPTION");
                    System.out.println(e);

                }
            }
        };

        mCamera.takePicture(null, null, mCall);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw the preview.
        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(holder);

        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //stop the preview
        mCamera.stopPreview();
        //release the camera
        mCamera.release();
        //unbind the camera from this object
        mCamera = null;

    }

    @Override
    protected void onDestroy() {
        if (mCamera != null) {
            mCamera.release();
        }
        super.onDestroy();
    }

    private void saveImage(byte[] bytes) {

        File imageFile = new File(IMAGE_FILE_LOCATION, "image.jpg");
        if (imageFile.exists()) {
            imageFile.delete();
        }

        try {

            FileOutputStream fos = new FileOutputStream(imageFile.getPath());
            Log.d(LOG_TAG, "Image saved to : " + imageFile);

            System.out.println("**********************************");
            System.out.println("Image saved to : " + imageFile);
            System.out.println("**********************************");

            fos.write(bytes);
            fos.close();
            System.out.println(imageFile);
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Exception in photoCallback");
        }

    }

    private void detectUserEmotion() {

        String url = "http://"+IP_ADDRESS+"/detect-emotion";
        try {
            URL emotionDetectionUrl = new URL(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new NameValuePair("avatar", IMAGE_FILE_LOCATION));
            MultiPartServer.postData(emotionDetectionUrl, nameValuePairs);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    // no use
    public void detectEmotion(File imageFile) throws IOException {
        // Create an HTTP client
        CloseableHttpClient client = HttpClients.createDefault(); //  'org.apache.http.conn.ssl.AllowAllHostnameVerifier'

        // Create an HTTP POST request to the Flask endpoint
        HttpPost post = new HttpPost("http://"+IP_ADDRESS+"/detect-emotion");

        // Create a multipart entity to send the image file
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", imageFile, ContentType.DEFAULT_BINARY, imageFile.getName());
        HttpEntity entity = builder.build();

        // Set the request entity
        post.setEntity(entity);

        // Execute the request and get the response
        CloseableHttpResponse response = client.execute(post);
        HttpEntity responseEntity = response.getEntity();

        // Process the response entity
        if (responseEntity != null) {
            // Do something with the response entity
            String responseString = EntityUtils.toString(responseEntity);
            System.out.println("***** RESPONSE ENTITY ******");
            System.out.println(responseString);
        }

        // Close the response and HTTP client
        response.close();
        client.close();
    }

    public void connectApi() {
        String url = "http://"+IP_ADDRESS+"/test-post";
        final String[] result = {""};
        int timeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        try {
                            JSONObject jsonObject = new JSONObject((Map) response);
                            String data = jsonObject.getString("placement");
                            if (data.equals("1")) {
                                result[0] = "SUCCESS";
                            } else {
                                result[0] = "ERROR";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                        System.out.println("AN ERROR OCCURRED");
                    }
                }) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<String,String>();
                params.put("cgpa", "ABCD");
                params.put("iq", "1234");
                params.put("profile_score", "345");
                return params;
            }
        };
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(TakePictureActivity.this);
        queue.add(stringRequest);
    }
}
