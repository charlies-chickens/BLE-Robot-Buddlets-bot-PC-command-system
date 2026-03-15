package com.example.bluetoothlibrary.bluetooth3.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import com.example.bluetoothlibrary.utils.LogUtil;
import java.io.IOException;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class AcceptThread extends Thread {
    private static final String TAG = "BTManager";
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread(BluetoothAdapter bluetoothAdapter, String str) {
        BluetoothServerSocket bluetoothServerSocketListenUsingRfcommWithServiceRecord;
        this.mBluetoothAdapter = bluetoothAdapter;
        try {
            bluetoothServerSocketListenUsingRfcommWithServiceRecord = bluetoothAdapter.listenUsingRfcommWithServiceRecord("blue", UUID.fromString(str));
        } catch (IOException e) {
            LogUtil.showLogE(TAG, "AcceptThread-->获取BluetoothServerSocket异常!" + e.getMessage());
            bluetoothServerSocketListenUsingRfcommWithServiceRecord = null;
        }
        this.mmServerSocket = bluetoothServerSocketListenUsingRfcommWithServiceRecord;
        if (bluetoothServerSocketListenUsingRfcommWithServiceRecord != null) {
            LogUtil.showLogW(TAG, "AcceptThread-->已获取BluetoothServerSocket");
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (this.mmServerSocket != null) {
            try {
                if (this.mmServerSocket.accept() != null) {
                    LogUtil.showLogW(TAG, "AcceptThread:run-->服务器成功接受连接请求");
                    cancel();
                }
            } catch (IOException e) {
                LogUtil.showLogE(TAG, "AcceptThread:run-->服务器接受连接请求异常!" + e.getMessage());
                return;
            }
        }
        LogUtil.showLogE(TAG, "AcceptThread:run-->mmServerSocket == null");
    }

    private void cancel() {
        try {
            if (this.mmServerSocket == null) {
                LogUtil.showLogE(TAG, "AcceptThread:cancel-->mmServerSocket == null");
                return;
            }
            this.mmServerSocket.close();
            interrupt();
            LogUtil.showLogW(TAG, "AcceptThread:cancel-->释放服务器套接字及其所有资源");
        } catch (IOException e) {
            LogUtil.showLogE(TAG, "AcceptThread:cancel-->释放服务器套接字及其所有资源异常！" + e.getMessage());
        }
    }
}