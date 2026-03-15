package com.example.bluetoothlibrary.listener;

/* JADX INFO: loaded from: classes.dex */
public interface OnBTConnectListener {
    void onConnectFailure();

    void onConnectOutTime();

    void onConnectSuccess();

    void onDisConnectSuccess();

    void onReceiveError(String str);

    void onReceiveSuccess(byte[] bArr);

    void onSendError(byte[] bArr, String str);

    void onSendSuccess(byte[] bArr);

    void onStartConnect();
}