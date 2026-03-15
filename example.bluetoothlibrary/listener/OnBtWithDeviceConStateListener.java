package com.example.bluetoothlibrary.listener;

import android.bluetooth.BluetoothDevice;

/* JADX INFO: loaded from: classes.dex */
public interface OnBtWithDeviceConStateListener {
    void onBtWithDeviceConnected(BluetoothDevice bluetoothDevice);

    void onBtWithDeviceDisConnected(BluetoothDevice bluetoothDevice);
}