package com.example.xuyuan.trinity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private TextView textView;
    private Context context;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private ImageView imageView;
    public static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.activity_main_text_view);
        textView.setText("666 mm");

        ImageView imageView_logo = (ImageView)findViewById(R.id.logo);
        imageView_logo.setImageResource(R.drawable.hptg);

        imageView = (ImageView)findViewById(R.id.image);
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
        instance = this;

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

        // start preview with new settings
        try {

            mCamera.setPreviewCallback(this);
            mCamera.setPreviewDisplay(mHolder);

            int rotation = instance.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case 0:
                    degrees = 0;
                    break;
                case 1:
                    degrees = 270;
                    break;
                case 2:
                    degrees = 180;
                    break;
                case 3:
                    degrees = 270 ;
            }

            mCamera.setDisplayOrientation(degrees);
            mCamera.startPreview();
        } catch (Exception e){
            //Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
    }

    public void setCameraParameters(Camera camera)
    {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            int width = parameters.getPreviewSize().width;
            int height = parameters.getPreviewSize().height;
            parameters.setPreviewSize(640, 480);
            camera.setParameters(parameters);
        }
    }


    private void drawSquare(byte[] data, int x, int y, int width, int height,int circle, int thickness)
    {
        int x1 = x -  circle;
        int y1 = y -  circle;
        int x2 = x -  circle;
        int y2 = y +  circle;

        int x3 = x +  circle;
        int y3 = y -  circle;
        int x4 = x +  circle;
        int y4 = y +  circle;
        for(int i = -1 * thickness; i <= thickness; ++i)
        //int i = 0;
        {
            for(int j = y1; j <= y2; ++j)
            {
                int tempx = i + x1;
                int tempy =j;
                data[tempx * width + tempy] = (byte)128;
                tempx = i + x3;
                tempy =j;
                data[tempx * width + tempy] = (byte)128;
            }

            for(int j = x1; j <= x3; ++j)
            {
                int tempx = j;
                int tempy = i + y1;;
                data[tempx * width + tempy] = (byte)128;
                tempx = j;
                tempy = i + y2;
                data[tempx * width + tempy] = (byte)128;
            }
        }

    }
    private void drawCircler(byte[] data, int x, int y, int width, int height,int circle , int thickness)
    {
        for(int i = -1 * circle; i <= circle; ++i)
        {
            for(int j = -1 * circle; j <= circle; ++j)
            {
                if((x + i>=0) && (x + i< height) && (y + j>=0) && (y + j< width) && (Math.abs(i * i + j * j  - circle * circle) < thickness ))
                {
                    int temp_x = x + i ;
                    int temp_y = y + j;
                    data[temp_x * width + temp_y] = (byte) 128;
                }
            }
        }

    }

    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight)
    {
        byte [] yuv = new byte[imageWidth*imageHeight*3/2];
        // Rotate the Y luma
        int i = 0;
        for(int x = 0;x < imageWidth;x++)
        {
            for(int y = imageHeight-1;y >= 0;y--)
            {
                yuv[i] = data[y*imageWidth+x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth*imageHeight*3/2-1;
        for(int x = imageWidth-1;x > 0;x=x-2)
        {
            for(int y = 0;y < imageHeight/2;y++)
            {
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+x];
                i--;
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
                i--;
            }
        }
        return yuv;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;
        int rotation = instance.getWindowManager().getDefaultDisplay().getRotation();

        if(rotation == 3)
        {
            data = rotateYUV420Degree90(data, width, height);
            int temp = height;
            height = width;
            width = temp;
        }

        int circle = 60;
        int thickness = 2;

        drawSquare(data ,height / 2, width / 2,width, height, circle, thickness);
        drawSquare(data ,height / 2, width / 2 - 2 * circle,width, height, circle, thickness);
        drawSquare(data ,height / 2, width / 2 + 2 * circle,width, height, circle, thickness);
        drawSquare(data ,height / 2 + 2 * circle, width / 2,width, height, circle, thickness);
        drawSquare(data ,height / 2 - 2 * circle, width / 2,width, height, circle, thickness);
        drawSquare(data ,height / 2 - 2 * circle, width / 2 - 2 * circle,width, height, circle, thickness);
        drawSquare(data ,height / 2 + 2 * circle, width / 2 - 2 * circle,width, height, circle, thickness);
        drawSquare(data ,height / 2 - 2 * circle, width / 2 + 2 * circle,width, height, circle, thickness);
        drawSquare(data ,height / 2 + 2 * circle, width / 2 + 2 * circle,width, height, circle, thickness);

        /*
        drawCircler(data ,height / 2, width / 2,width, height, circle, thickness);
        drawCircler(data ,height / 2, width / 2 - 2 * circle,width, height, circle, thickness);
        drawCircler(data ,height / 2, width / 2 + 2 * circle,width, height, circle, thickness);
        drawCircler(data ,height / 2 + 2 * circle, width / 2,width, height, circle, thickness);
        drawCircler(data ,height / 2 - 2 * circle, width / 2,width, height, circle, thickness);
        drawCircler(data ,height / 2 - 2 * circle, width / 2 - 2 * circle,width, height, circle, thickness);
        drawCircler(data ,height / 2 + 2 * circle, width / 2 - 2 * circle,width, height, circle, thickness);
        drawCircler(data ,height / 2 - 2 * circle, width / 2 + 2 * circle,width, height, circle, thickness);
        drawCircler(data ,height / 2 + 2 * circle, width / 2 + 2 * circle,width, height, circle, thickness);
        */
        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);




        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

        byte[] bytes = out.toByteArray();
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        imageView.setImageBitmap(bitmap);


        /*
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                {
                    handler.post(new Runnable(){
                        public void run() {
                            {
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });

                }
            }
        };
        new Thread(runnable).start();
        */
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
