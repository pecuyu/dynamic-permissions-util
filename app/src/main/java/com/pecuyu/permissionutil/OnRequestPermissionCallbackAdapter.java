package com.pecuyu.permissionutil;

import android.support.annotation.NonNull;

/**
 * Author: pecuyu
 * Email: yu.qin@ck-telecom.com
 * Date: 2017/8/25
 * TODO:
 */
public class OnRequestPermissionCallbackAdapter implements PermUtil.OnRequestPermissionCallback {

    /**
     * {@inheritDoc}
     * @param permission
     * @param requestCode
     */
    @Override
    public void onCheckedAlreadyGranted(String permission, int requestCode) {

    }

    @Override
    public void onCheckedAlreadyGranted(String[] permissions) {

    }

    @Override
    public void onSuccess(int requestCode, @NonNull String[] permissions) {

    }

    @Override
    public void onFailed(int requestCode, @NonNull String[] permissions) {

    }
}
