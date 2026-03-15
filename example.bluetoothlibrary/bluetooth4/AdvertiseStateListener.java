package com.example.bluetoothlibrary.bluetooth4;

/* JADX INFO: loaded from: classes.dex */
public interface AdvertiseStateListener {
    void onStartAdvertise(String str);

    void onStartFailure(String str);

    void onStartSuccess(String str);

    void onStopAdvertise(String str);
}