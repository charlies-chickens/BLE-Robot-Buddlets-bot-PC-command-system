package com.example.bluetoothlibrary.listener;

import android.bluetooth.BluetoothDevice;

/* JADX INFO: loaded from: classes.dex */
public interface OnRemoteDeviceConStateListener {
    void onConnected(BluetoothDevice bluetoothDevice);

    void onDisConnected(BluetoothDevice bluetoothDevice);
}