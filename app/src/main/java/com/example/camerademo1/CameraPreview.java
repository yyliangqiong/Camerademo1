package com.example.camerademo1;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG="CamerPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;


    public CameraPreview(Context context) {
        super(context);
        mHolder=getHolder();
        mHolder.addCallback(this);
    }

    private static Camera getCameraInstance(){
        Camera c=null;
        try {
            c=Camera.open();
        } catch (Exception e) {

            Log.d(TAG, "camera is not available ");
        }
        return c;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera=getCameraInstance();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview"+e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;

    }
}
