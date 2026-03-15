package com.example.bluetoothlibrary.bluetooth;

import android.bluetooth.BluetoothDevice;

/* JADX INFO: loaded from: classes.dex */
public interface OnSearchListener {
    void onDeviceFound(BluetoothDevice bluetoothDevice, int i, byte[] bArr);

    void onDeviceSearchOutTime();

    void onDiscoveryStart();

    void onDiscoveryStop();
}