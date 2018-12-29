package com.example.camerademo1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private CameraPreview mPreview;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]);
            int l = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]);
            int m = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[2]);
            int n = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[3]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED || l != PackageManager.PERMISSION_GRANTED || m != PackageManager.PERMISSION_GRANTED ||
                    n != PackageManager.PERMISSION_GRANTED) {
                startRequestPermission();
            }
        }else {

            initCamera();

            final ImageView mediaPreview=(ImageView)findViewById(R.id.media_preview);
            final Button buttonCapturePhoto=(Button)findViewById(R.id.button_capture_photo);

            buttonCapturePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPreview.takePicture(mediaPreview);
                }
            });

            final Button buttonCaptureVideo=(Button)findViewById(R.id.button_capture_video);

            buttonCaptureVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPreview.isRecording()){
                        mPreview.stopRecording(mediaPreview);
                        buttonCaptureVideo.setText("录像");
                    }else {
                        if (mPreview.startRecording()){
                            buttonCaptureVideo.setText("停止");
                        }
                    }
                }
            });

            mediaPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,ShowPhotoVideo.class);
                    intent.setDataAndType(mPreview.getOutputMediaFileUri(),mPreview.getOutputMediaFileType());
                    startActivityForResult(intent,0);
                }
            });

        }



        Button buttonSetting=findViewById(R.id.button_settings);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.camera_preview,new SettingFragment()).addToBackStack(null).commit();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview=null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            //添加自定义View

            initCamera();
            final ImageView mediaPreview=(ImageView)findViewById(R.id.media_preview);

            final Button buttonCapturePhoto=(Button)findViewById(R.id.button_capture_photo);

            buttonCapturePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPreview.takePicture(mediaPreview);
                }
            });

            final Button buttonCaptureVideo=(Button)findViewById(R.id.button_capture_video);

            buttonCaptureVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPreview.isRecording()){
                        mPreview.stopRecording(mediaPreview);
                        buttonCaptureVideo.setText("录像");
                    }else {
                        if (mPreview.startRecording()){
                            buttonCaptureVideo.setText("停止");
                        }
                    }
                }
            });

            mediaPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,ShowPhotoVideo.class);
                    intent.setDataAndType(mPreview.getOutputMediaFileUri(),mPreview.getOutputMediaFileType());
                    startActivityForResult(intent,0);
                }
            });

        }

        }




    private void initCamera(){

        mPreview = new CameraPreview(this);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);





        SettingFragment.passCamera(mPreview.getCameraInstance());
        PreferenceManager.setDefaultValues(this,R.xml.preference,false);
        SettingFragment.setDefault(PreferenceManager.getDefaultSharedPreferences(this));
        SettingFragment.init(PreferenceManager.getDefaultSharedPreferences(this));


    }





}