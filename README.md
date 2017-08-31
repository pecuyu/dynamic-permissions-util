# dynamic-permissions-util
动态申请权限工具类

##使用方法：
1.使在需要申请权限的Activity继承BaseActivity
2.在manifest文件声明要动态申请的权限

            <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
            <uses-permission android:name="android.permission.RECORD_AUDIO"/>
            
3.使用PermUtil.getInstance()获取util实例，并进行权限申请
4.处理权限申请结果



##示例：
// 申请一个权限

            PermUtil.getInstance().checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1, new OnRequestPermissionCallbackAdapter() {
                        @Override
                        public void onSuccess(int requestCode, @NonNull String[] permissions) {
                            Toast.makeText(MainActivity.this, "读写权限申请成功", Toast.LENGTH_LONG).show();
                        }
                    });


// 申请多个权限

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
