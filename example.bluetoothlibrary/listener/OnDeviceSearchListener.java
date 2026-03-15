package com.example.bluetoothlibrary.listener;

import com.example.bluetoothlibrary.model.SearchDevice;

/* JADX INFO: loaded from: classes.dex */
public interface OnDeviceSearchListener {
    void onDeviceFound(SearchDevice searchDevice);

    void onDiscoveryOutTime();

    void onDiscoveryStart();

    void onDiscoveryStop();
}