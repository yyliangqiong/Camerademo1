package com.example.camerademo1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG="CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    public static final int MEDIA_TYPE_IMAGE=1;
    public static final int MEDIA_TYPE_VIDEO=2;
    private Uri outputMediaFileUri;
    private String outputMediaFileType;
    long time = 0;


    public CameraPreview(Context context) {
        super(context);
        mHolder=getHolder();
        mHolder.addCallback(this);
    }

    public Camera getCameraInstance(){
        if(mCamera==null)
        try {
            mCamera=Camera.open();
        } catch (Exception e) {

            Log.d(TAG, "camera is not available ");
        }
        return mCamera;
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

    private File getOutputMediaFile(int type){
        File mediaStorageDir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),TAG);
        if (!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdir()){
                Log.d(TAG, "failed to create directory ");
                return null;
            }
        }

        String timeStamp =new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type==MEDIA_TYPE_IMAGE){
            mediaFile=new File(mediaStorageDir.getPath()+File.separator+"IMG_"+timeStamp+".jpg");
            outputMediaFileType="image/*";
        }else if (type==MEDIA_TYPE_VIDEO){
            mediaFile=new File(mediaStorageDir.getPath()+File.separator+"VID_"+timeStamp+".mp4");
            outputMediaFileType="video/*";
        }else {
            return null;
        }
        outputMediaFileUri=Uri.fromFile(mediaFile);
        return mediaFile;


    }

    public Uri getOutputMediaFileUri() {
        return outputMediaFileUri;
    }

    public String getOutputMediaFileType() {
        return outputMediaFileType;
    }

    public void takePicture(final ImageView view){
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile=getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if(pictureFile==null){
                    Log.d(TAG, "Error creating media file,check storage permissions");
                    return;
                }
                try {
                    FileOutputStream fos=new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    view.setImageURI(outputMediaFileUri);
                    camera.startPreview();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: "+e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file"+e.getMessage());
                }
            }
        });
    }


    private MediaRecorder mMediaRecorder;
    public boolean startRecording(){
        if (prepareVideoRecorder()){
            mMediaRecorder.start();
            time = System.currentTimeMillis();
            return true;
        }else {
            releaseMediaRecorder();
        }
        return false;
    }

    public void stopRecording(final ImageView view){
        long spanTime = System.currentTimeMillis()-time;
        if(spanTime<1000){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(mMediaRecorder!=null){
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap thumbnail= ThumbnailUtils.createVideoThumbnail(outputMediaFileUri.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
            view.setImageBitmap(thumbnail);
        }
             releaseMediaRecorder();
    }

    public boolean isRecording(){
        return mMediaRecorder!=null;
    }

    private boolean prepareVideoRecorder(){
        mCamera=getCameraInstance();
        mMediaRecorder=new MediaRecorder();

        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        String prefVideoSize= prefs.getString("video_size","");
        String [] split=prefVideoSize.split("x");
        mMediaRecorder.setVideoSize(Integer.parseInt(split[0]),Integer.parseInt(split[1]));

        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

        try {
            mMediaRecorder.prepare();
        }catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder:"+e.getMessage());
            releaseMediaRecorder();
            return  false;
        }

        return true;

    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder!=null){
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder=null;
            mCamera.lock();
        }

    }






}
