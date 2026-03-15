package com.example.bluetoothlibrary.bluetooth4;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;
import com.example.bluetoothlibrary.utils.LogUtil;
import com.iflytek.cloud.SpeechConstant;

/* JADX INFO: loaded from: classes.dex */
public class SystemBleCheck {
    private static final String TAG = "SystemBleCheck";
    public BluetoothAdapter bluetooth4Adapter;
    public BluetoothLeAdvertiser bluetoothLeAdvertiser;
    public BluetoothManager bluetoothManager;

    private static class SystemBleCheckHolder {
        private static SystemBleCheck systemBleCheck = new SystemBleCheck();

        private SystemBleCheckHolder() {
        }
    }

    public static SystemBleCheck getInstance() {
        return SystemBleCheckHolder.systemBleCheck;
    }

    public void initBle(Context context) {
        if (checkBle(context)) {
            return;
        }
        LogUtil.showLogE(TAG, "该设备不支持低功耗蓝牙");
        Toast.makeText(context, "该设备不支持低功耗蓝牙", 0).show();
    }

    public void openBluetooth(Context context, boolean z) {
        if (isEnable()) {
            LogUtil.showLogD(TAG, "手机蓝牙状态已开");
        } else if (z) {
            LogUtil.showLogD(TAG, "直接打开手机蓝牙");
            this.bluetooth4Adapter.enable();
        } else {
            LogUtil.showLogD(TAG, "提示用户去打开手机蓝牙");
            context.startActivity(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"));
        }
    }

    public void closeBluetooth() {
        BluetoothAdapter bluetoothAdapter = this.bluetooth4Adapter;
        if (bluetoothAdapter == null) {
            return;
        }
        bluetoothAdapter.disable();
    }

    private boolean checkBle(Context context) {
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(SpeechConstant.BLUETOOTH);
        this.bluetoothManager = bluetoothManager;
        if (bluetoothManager == null) {
            return false;
        }
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        this.bluetooth4Adapter = adapter;
        if (adapter == null) {
            return false;
        }
        LogUtil.showLogD(TAG, "该设备支持蓝牙4.0");
        return true;
    }

    public BluetoothLeAdvertiser checkBleAdvertiser() {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        BluetoothLeAdvertiser bluetoothLeAdvertiser = this.bluetooth4Adapter.getBluetoothLeAdvertiser();
        this.bluetoothLeAdvertiser = bluetoothLeAdvertiser;
        if (bluetoothLeAdvertiser != null) {
            return bluetoothLeAdvertiser;
        }
        LogUtil.showLogE(TAG, "不支持BLE蓝牙外围模式--作为从设备通讯");
        return null;
    }

    private boolean isEnable() {
        BluetoothAdapter bluetoothAdapter = this.bluetooth4Adapter;
        if (bluetoothAdapter == null) {
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }
}