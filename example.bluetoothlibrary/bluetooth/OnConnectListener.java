package com.example.bluetoothlibrary.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;

/* JADX INFO: loaded from: classes.dex */
public interface OnConnectListener {
    void onConnectFailure(String str, int i);

    void onConnectSuccess();

    void onDisConnectSuccess(int i);

    void onMTUSetFailure(String str);

    void onMTUSetSuccess(String str, int i);

    void onReceiveError(String str);

    void onReceiveSuccess(BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr);

    void onSendError(byte[] bArr, String str);

    void onSendSuccess(byte[] bArr);

    void onStartConnect();
}