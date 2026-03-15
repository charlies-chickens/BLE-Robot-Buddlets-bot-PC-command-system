package com.example.bluetoothlibrary.model;

import android.bluetooth.BluetoothDevice;

/* JADX INFO: loaded from: classes.dex */
public class SearchDevice {
    public BluetoothDevice bluetoothDevice;
    public int rssi;
    public byte[] scanRecord;

    public SearchDevice(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = i;
        this.scanRecord = bArr;
    }
}