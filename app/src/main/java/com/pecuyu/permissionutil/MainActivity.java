package com.pecuyu.permissionutil;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();


    }

    private void requestPermissions() {
        PermUtil.getInstance().checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1, new OnRequestPermissionCallbackAdapter() {
            @Override
            public void onSuccess(int requestCode, @NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "读写权限申请成功", Toast.LENGTH_LONG).show();
            }
        });

        PermUtil.getInstance().checkAndRequestPermission(Manifest.permission.RECORD_AUDIO, 1, new OnRequestPermissionCallbackAdapter() {
            @Override
            public void onSuccess(int requestCode, @NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "录音权限申请成功", Toast.LENGTH_LONG).show();
            }
        });
    }

}