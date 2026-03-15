package com.example.bluetoothlibrary.listener;

import com.example.bluetoothlibrary.model.SearchDevice;

/* JADX INFO: loaded from: classes.dex */
public interface OnBt3DeviceStateListener {
    void onConDeviceState(int i, SearchDevice searchDevice);

    void onScanDeviceState(int i, SearchDevice searchDevice);
}