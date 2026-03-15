package com.example.bluetoothlibrary.permission;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.bluetoothlibrary.utils.LogUtil;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class PermissionRequest extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final String TAG = "PermissionRequest";
    private PermissionListener mListener;

    public void requestRuntimePermission(Context context, String[] strArr, PermissionListener permissionListener) {
        this.mListener = permissionListener;
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            if (ContextCompat.checkSelfPermission(context, str) != 0 && !arrayList.contains(str)) {
                arrayList.add(str);
            }
        }
        if (!arrayList.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, (String[]) arrayList.toArray(new String[arrayList.size()]), 1);
            return;
        }
        PermissionListener permissionListener2 = this.mListener;
        if (permissionListener2 != null) {
            permissionListener2.onGranted();
            LogUtil.showLogD(TAG, "权限都授予了");
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1 && iArr.length > 0) {
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < iArr.length; i2++) {
                String str = strArr[i2];
                if (iArr[i2] != 0 && !arrayList.contains(str)) {
                    arrayList.add(str);
                }
            }
            if (arrayList.isEmpty()) {
                PermissionListener permissionListener = this.mListener;
                if (permissionListener != null) {
                    permissionListener.onGranted();
                    LogUtil.showLogD(TAG, "权限都授予了");
                    return;
                }
                return;
            }
            PermissionListener permissionListener2 = this.mListener;
            if (permissionListener2 != null) {
                permissionListener2.onDenied(arrayList);
                LogUtil.showLogE(TAG, "有权限被拒绝了");
            }
        }
    }
}