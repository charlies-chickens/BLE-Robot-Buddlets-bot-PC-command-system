package com.example.bluetoothlibrary.listener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/* JADX INFO: loaded from: classes.dex */
public interface OnBleConnectListener {
    void onConnectFailure(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, String str, int i);

    void onConnectSuccess(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, int i);

    void onConnecting(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice);

    void onDisConnectSuccess(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, int i);

    void onDisConnecting(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice);

    void onMTUSetFailure(String str);

    void onMTUSetSuccess(String str, int i);

    void onReadRssi(BluetoothGatt bluetoothGatt, int i, int i2);

    void onReceiveError(String str);

    void onReceiveMessage(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr);

    void onServiceDiscovery(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, int i);

    void onWriteFailure(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, byte[] bArr, String str);

    void onWriteSuccess(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, byte[] bArr);
}