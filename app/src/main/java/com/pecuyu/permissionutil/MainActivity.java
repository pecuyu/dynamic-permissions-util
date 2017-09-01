package com.pecuyu.permissionutil;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();


    }

    private void requestPermissions() {
//        PermUtil.getInstance().checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1, new OnRequestPermissionCallbackAdapter() {
//            @Override
//            public void onSuccess(int requestCode, @NonNull String[] permissions) {
//                Toast.makeText(MainActivity.this, "读写权限申请成功", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        PermUtil.getInstance().checkAndRequestPermission(Manifest.permission.RECORD_AUDIO, 2, new OnRequestPermissionCallbackAdapter() {
//            @Override
//            public void onSuccess(int requestCode, @NonNull String[] permissions) {
//                Toast.makeText(MainActivity.this, "录音权限申请成功", Toast.LENGTH_LONG).show();
//            }
//        });

        PermUtil.getInstance().checkAndRequestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 3, new OnRequestPermissionCallbackAdapter() {
            @Override
            public void onSuccess(int requestCode, @NonNull String[] permissions) {
                super.onSuccess(requestCode, permissions);
                String result = Arrays.toString(permissions); // 申请成功权限
                if (result.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "读写权限申请成功", Toast.LENGTH_LONG).show();
                }
                if (result.contains(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(MainActivity.this, "录音权限申请成功", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(int requestCode, @NonNull String[] permissions) {
                super.onFailed(requestCode, permissions);
                // 处理申请失败
            }
        });
    }

}