package com.example.bluetoothlibrary.bluetooth3;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.bluetoothlibrary.utils.LogUtil;

/* JADX INFO: loaded from: classes.dex */
public class SystemBtCheck {
    private static final String TAG = "SystemBtCheck";
    public BluetoothAdapter bluetooth3Adapter;

    private static class SystemBtCheckHolder {
        private static SystemBtCheck systemBtCheck = new SystemBtCheck();

        private SystemBtCheckHolder() {
        }
    }

    public static SystemBtCheck getInstance() {
        return SystemBtCheckHolder.systemBtCheck;
    }

    public void initBle(Context context) {
        if (checkBt3()) {
            return;
        }
        LogUtil.showLogE(TAG, "该设备不支持蓝牙");
        Toast.makeText(context, "该设备不支持蓝牙", 0).show();
    }

    public void openBluetooth(Context context, boolean z) {
        if (isEnable()) {
            LogUtil.showLogD(TAG, "手机蓝牙状态已开");
        } else if (z) {
            LogUtil.showLogD(TAG, "直接打开手机蓝牙");
            this.bluetooth3Adapter.enable();
        } else {
            LogUtil.showLogD(TAG, "提示用户去打开手机蓝牙");
            context.startActivity(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"));
        }
    }

    public void closeBluetooth() {
        BluetoothAdapter bluetoothAdapter = this.bluetooth3Adapter;
        if (bluetoothAdapter == null) {
            return;
        }
        bluetoothAdapter.disable();
    }

    private boolean checkBt3() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetooth3Adapter = defaultAdapter;
        if (defaultAdapter == null) {
            return false;
        }
        LogUtil.showLogD(TAG, "该设备支持蓝牙3.0");
        return true;
    }

    public boolean isEnable() {
        BluetoothAdapter bluetoothAdapter = this.bluetooth3Adapter;
        if (bluetoothAdapter == null) {
            LogUtil.showLogE(TAG, "isEnable-->bluetooth3Adapter == null");
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }
}