package com.example.xuyuan.trinity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private TextView textView;
    private Context context;
    private SurfaceHolder mHolder;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.activity_main_text_view);
        textView.setText("111 mm");

        final ImageView imageView = (ImageView)findViewById(R.id.image);
        imageView.setImageResource(R.drawable.hptg);
        //hptgInstantiate();
        //startTimerThread();
        SurfaceView videoPrev = (SurfaceView) findViewById(R.id.camera_preview);
        mHolder = videoPrev.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /*
    private void startTimerThread() {

        System.out.println("Size 1");
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                {
                    while (true) {
                        try {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.post(new Runnable(){
                            public void run() {
                                String result = hptgGetOlivia();
                                if(result.equals(""))
                                {
                                    textView.setText(result);
                                }
                                else
                                {
                                    textView.setText(result + " mm");
                                }
                            }
                        });
                    }
                }
            }
        };
        new Thread(runnable).start();
    }
    */

    private void oldOpenCamera() {
        try {
            mCamera = Camera.open();
            this.setCameraParameters(mCamera);

        }
        catch (RuntimeException e) {
            //Log.e(LOG_TAG, "failed to open front camera");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        oldOpenCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            mCamera.setPreviewCallbackWithBuffer(null);

            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            //int width = size.width;
            //int height = size.height;
            //System.out.println("Size %d"+ size.width);
            //System.out.println(size.height);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewCallback(this);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(270);
            mCamera.startPreview();
        } catch (Exception e){
            //Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {

    }

    public void setCameraParameters(Camera camera)
    {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            camera.setParameters(parameters);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        return;
    }

    /*
    static
    {
        System.loadLibrary("hello-android-jni");
    }
    public native String hptgInstantiate();
    public native String hptgGetOlivia();
    }
    */
}
