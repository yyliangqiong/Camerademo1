package com.example.camerademo1;

import android.Manifest;
import android.app.Activity;
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
import android.widget.Toast;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION};

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
            CameraPreview mPreview = new CameraPreview(this);
            FrameLayout preview = findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            SettingFragment.passCamera(mPreview.getCameraInstance());
            PreferenceManager.setDefaultValues(this,R.xml.preference,false);
            SettingFragment.setDefault(PreferenceManager.getDefaultSharedPreferences(this));
            SettingFragment.init(PreferenceManager.getDefaultSharedPreferences(this));

        }

        Button buttonSetting=findViewById(R.id.button_settings);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.camera_preview,new SettingFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            //添加自定义View
            CameraPreview mPreview = new CameraPreview(this);
            FrameLayout preview = findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            SettingFragment.passCamera(mPreview.getCameraInstance());
            PreferenceManager.setDefaultValues(this,R.xml.preference,false);
            SettingFragment.setDefault(PreferenceManager.getDefaultSharedPreferences(this));
            SettingFragment.init(PreferenceManager.getDefaultSharedPreferences(this));

        }
    }



}