package com.pecuyu.permissionutil;

/**
 * Created by pecuyu on 2017/8/31.
 */


import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: pecuyu
 * Email:  pecuyu@outloook.com
 * Date: 2017/8/25
 * TODO:动态权限申请工具类
 */

public class PermUtil {
    private Activity mActivity;
    List<String> list = new ArrayList<>();
    private Map<Integer, String> requestInfo = new HashMap<>();
    private Map<Integer, OnRequestPermissionCallback> callbacks = new HashMap<>();
    private static PermUtil permUtil;

    /**
     * @param activity 依赖Activity
     */
    private PermUtil(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 获取PermUtil实例
     *
     * @return permUtil
     * <br/> call this method after the PermUtil has been install {@link #install(Activity)}
     */
    public static PermUtil getInstance() {
        return permUtil;
    }

    /**
     * 将工具安装到依赖Activity
     *
     * @param activity 依赖Activity
     * @return PermUtil
     */
    public static PermUtil install(Activity activity) {
        if (permUtil == null) {
            permUtil = new PermUtil(activity);
        }
        return permUtil;
    }

    /**
     * 检查并申请权限
     *
     * @param permission
     * @param requestCode
     * @param callback
     */
    public void checkAndRequestPermission(String permission, final int requestCode, OnRequestPermissionCallback callback) {
        if (permission == null) {
            return;
        }
        // 检查权限是否已经申请
        if (ActivityCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED) {
            if (callback != null) callback.onCheckedAlreadyGranted(permission, requestCode);
            return;
        }

        requestInfo.put(requestCode, permission);
        callbacks.put(requestCode, callback);

        scheduleNext(permission, requestCode, callback);
    }

    /**
     * 请求多个权限
     *
     * @param permissions
     * @param requestCode
     * @param callback
     */
    public void checkAndRequestPermissions(String[] permissions, int requestCode, OnRequestPermissionCallback callback) {
        if (permissions == null) {
            return;
        }

        if (checkGrantedPermissions(permissions) && callback != null) {
            callback.onCheckedAlreadyGranted(permissions);
            return;
        }

        String permsArray = list2String(getDeniedPermissions(permissions));
        requestInfo.put(requestCode, permsArray);
        callbacks.put(requestCode, callback);
        scheduleNext(permsArray, requestCode, callback);
    }

    /**
     * 依次申请权限
     *
     * @param permission
     * @param requestCode
     * @param callback
     */
    private void scheduleNext(String permission, final int requestCode, OnRequestPermissionCallback callback) {
        // 判断请求的权限是否在头部
        if (requestInfo.size() <= 0 || !isFirstElement(requestInfo.keySet(),requestCode)) {
            return;
        }

        if (permission.contains(",")) {  // 请求多个权限
            List<String> deniedPermissions = getDeniedPermissions(permission.split(","));
            String[] perms = list2Array(deniedPermissions);
            ActivityCompat.requestPermissions(mActivity, perms, requestCode);
        } else {  // 请求一个权限
            ActivityCompat.requestPermissions(mActivity, new String[]{permission}, requestCode);
        }
    }

    public boolean isFirstElement(Set<Integer> set, Integer key) {
        Iterator<Integer> iterator = set.iterator();
        return iterator.hasNext() && iterator.next().equals(key);
    }

    /**
     * list<string> 转 string[]
     *
     * @param list
     * @return
     */
    public String[] list2Array(List<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }

    /**
     * list转string，以，分隔
     *
     * @param list
     * @return
     */
    public String list2String(List<String> list) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size()) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    public String array2String(String[] strings) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            if (i != strings.length) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 处理权限请求
     * <br/>do not call this method directly
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void dealRequestPermission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Set<Integer> requestCodes = requestInfo.keySet();
        if (requestCodes.size() > 0 && requestCode == requestCodes.iterator().next()) {
            OnRequestPermissionCallback callback = callbacks.get(requestCode);
            if (callback == null) {  //没有callback则直接准备申请下一个权限
                prepareScheduleNext(requestCode, null);
                return;
            }

            if (grantResults.length > 0) {
                List<String> grantedPerms = new ArrayList<>();
                List<String> deniedPerms = new ArrayList<>();

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grantedPerms.add(permissions[i]);
                    } else {
                        deniedPerms.add(permissions[i]);
                    }
                }

                // 发布权限结果
                callback.onSuccess(requestCode, list2Array(grantedPerms));
                callback.onFailed(requestCode, list2Array(deniedPerms));
                prepareScheduleNext(requestCode, callback);
            }
        }
    }

    /**
     * 为下一次申请权限做准备
     *
     * @param requestCode
     * @param callback
     */

    private void prepareScheduleNext(Integer requestCode, OnRequestPermissionCallback callback) {
        // 移除已经申请过的信息
        callbacks.remove(requestCode);
        requestInfo.remove(requestCode);
        Set<Integer> requestCodes = requestInfo.keySet(); // 请求码集合
        if (requestCodes.size() > 0) {
            Integer nextCode = requestCodes.iterator().next();
            String nextPerm = requestInfo.get(nextCode); // 获取权限名
            OnRequestPermissionCallback nextCall = callbacks.get(nextCode);
            if (nextPerm == null) {
                callbacks.remove(nextCode);
                requestInfo.remove(nextCode);
                return;
            }

            // 获取Callback
            scheduleNext(nextPerm, nextCode, nextCall);
        }
    }

    /**
     * 检查所给权限组是否都已经授权了
     *
     * @param permissions
     * @return
     */
    public boolean checkGrantedPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取还没有授权的权限
     *
     * @param permissions
     * @return
     */
    public List<String> getDeniedPermissions(String[] permissions) {
        if (permissions == null) return null;
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }

        return deniedPermissions;
    }


    public interface OnRequestPermissionCallback {
        /**
         * 检查发现已经授权
         *
         * @param permission
         * @param requestCode
         */
        void onCheckedAlreadyGranted(String permission, int requestCode);

        /**
         * 检查发现已经授权的权限s
         *
         * @param permissions
         */
        void onCheckedAlreadyGranted(String[] permissions);

        /**
         * 当申请权限成功时
         */
        void onSuccess(int requestCode, @NonNull String[] permissions);

        /**
         * 当申请权限失败时
         */
        void onFailed(int requestCode, @NonNull String[] permissions);
    }

    /**
     * 取消安装，防止内存泄漏
     */
    public void uninstall() {
        callbacks = null;
        requestInfo = null;
        mActivity = null;
        permUtil = null;
    }
}
