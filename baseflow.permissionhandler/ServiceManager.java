package com.baseflow.permissionhandler;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
final class ServiceManager {

    @FunctionalInterface
    interface SuccessCallback {
        void onSuccess(int i);
    }

    ServiceManager() {
    }

    void checkServiceStatus(int i, Context context, SuccessCallback successCallback, ErrorCallback errorCallback) {
        if (context == null) {
            Log.d("permissions_handler", "Context cannot be null.");
            errorCallback.onError("PermissionHandler.ServiceManager", "Android context cannot be null.");
            return;
        }
        if (i == 3 || i == 4 || i == 5) {
            successCallback.onSuccess(isLocationServiceEnabled(context) ? 1 : 0);
            return;
        }
        if (i == 21) {
            successCallback.onSuccess(isBluetoothServiceEnabled() ? 1 : 0);
        }
        if (i != 8) {
            if (i == 16) {
                successCallback.onSuccess(Build.VERSION.SDK_INT < 23 ? 2 : 1);
                return;
            } else {
                successCallback.onSuccess(2);
                return;
            }
        }
        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature("android.hardware.telephony")) {
            successCallback.onSuccess(2);
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null || telephonyManager.getPhoneType() == 0) {
            successCallback.onSuccess(2);
            return;
        }
        Intent intent = new Intent("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:123123"));
        if (packageManager.queryIntentActivities(intent, 0).isEmpty()) {
            successCallback.onSuccess(2);
        } else if (telephonyManager.getSimState() != 5) {
            successCallback.onSuccess(0);
        } else {
            successCallback.onSuccess(1);
        }
    }

    private boolean isLocationServiceEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= 28) {
            LocationManager locationManager = (LocationManager) context.getSystemService(LocationManager.class);
            if (locationManager == null) {
                return false;
            }
            return locationManager.isLocationEnabled();
        }
        if (Build.VERSION.SDK_INT >= 19) {
            return isLocationServiceEnabledKitKat(context);
        }
        return isLocationServiceEnablePreKitKat(context);
    }

    private static boolean isLocationServiceEnabledKitKat(Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        try {
            return Settings.Secure.getInt(context.getContentResolver(), "location_mode") != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isLocationServiceEnablePreKitKat(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            return false;
        }
        return !TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), "location_providers_allowed"));
    }

    private boolean isBluetoothServiceEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }
}