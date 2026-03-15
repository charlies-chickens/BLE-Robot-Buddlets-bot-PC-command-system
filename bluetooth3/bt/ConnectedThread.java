package com.example.bluetoothlibrary.bluetooth3.bt;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import com.example.bluetoothlibrary.utils.LogUtil;
import com.example.bluetoothlibrary.utils.TypeConversion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public class ConnectedThread extends Thread {
    private static final String TAG = "BTManager";
    private ConnectThread connectThread;
    private boolean isStop = false;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private BluetoothSocket mmSocket;
    private OnSendReceiveDataListener onSendReceiveDataListener;

    public interface OnSendReceiveDataListener {
        void onReceiveDataError(String str);

        void onReceiveDataSuccess(byte[] bArr);

        void onSendDataError(byte[] bArr, String str);

        void onSendDataSuccess(byte[] bArr);
    }

    public void terminalClose(ConnectThread connectThread) {
        this.isStop = true;
        this.connectThread = connectThread;
    }

    public ConnectedThread(BluetoothSocket bluetoothSocket) {
        InputStream inputStream;
        this.mmSocket = bluetoothSocket;
        OutputStream outputStream = null;
        try {
            inputStream = bluetoothSocket.getInputStream();
        } catch (IOException unused) {
            inputStream = null;
        }
        try {
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException unused2) {
            LogUtil.showLogE(TAG, "ConnectedThread-->获取InputStream 和 OutputStream异常!");
        }
        this.mmInStream = inputStream;
        this.mmOutStream = outputStream;
        if (inputStream != null) {
            LogUtil.showLogD(TAG, "ConnectedThread-->已获取InputStream");
        }
        if (this.mmOutStream != null) {
            LogUtil.showLogD(TAG, "ConnectedThread-->已获取OutputStream");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x000e, code lost:
    
        com.example.bluetoothlibrary.utils.LogUtil.showLogE(com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread.TAG, "ConnectedThread:run-->输入流mmInStream == null");
     */
    @Override // java.lang.Thread, java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void run() {
        /*
            r5 = this;
            r0 = 2048(0x800, float:2.87E-42)
            byte[] r0 = new byte[r0]
        L4:
            boolean r1 = r5.isStop
            java.lang.String r2 = "BTManager"
            if (r1 != 0) goto L93
            java.io.InputStream r1 = r5.mmInStream     // Catch: java.io.IOException -> L53
            if (r1 != 0) goto L15
            java.lang.String r0 = "ConnectedThread:run-->输入流mmInStream == null"
            com.example.bluetoothlibrary.utils.LogUtil.showLogE(r2, r0)     // Catch: java.io.IOException -> L53
            goto L93
        L15:
            java.io.InputStream r1 = r5.mmInStream     // Catch: java.io.IOException -> L53
            int r1 = r1.available()     // Catch: java.io.IOException -> L53
            if (r1 == 0) goto L4
            java.io.InputStream r1 = r5.mmInStream     // Catch: java.io.IOException -> L53
            int r1 = r1.read(r0)     // Catch: java.io.IOException -> L53
            byte[] r1 = java.util.Arrays.copyOf(r0, r1)     // Catch: java.io.IOException -> L53
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.io.IOException -> L53
            r3.<init>()     // Catch: java.io.IOException -> L53
            java.lang.String r4 = "ConnectedThread:run-->收到消息,长度"
            r3.append(r4)     // Catch: java.io.IOException -> L53
            int r4 = r1.length     // Catch: java.io.IOException -> L53
            r3.append(r4)     // Catch: java.io.IOException -> L53
            java.lang.String r4 = "->"
            r3.append(r4)     // Catch: java.io.IOException -> L53
            int r4 = r1.length     // Catch: java.io.IOException -> L53
            java.lang.String r4 = com.example.bluetoothlibrary.utils.TypeConversion.bytes2HexString(r1, r4)     // Catch: java.io.IOException -> L53
            r3.append(r4)     // Catch: java.io.IOException -> L53
            java.lang.String r3 = r3.toString()     // Catch: java.io.IOException -> L53
            com.example.bluetoothlibrary.utils.LogUtil.showLogW(r2, r3)     // Catch: java.io.IOException -> L53
            com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread$OnSendReceiveDataListener r3 = r5.onSendReceiveDataListener     // Catch: java.io.IOException -> L53
            if (r3 == 0) goto L4
            com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread$OnSendReceiveDataListener r3 = r5.onSendReceiveDataListener     // Catch: java.io.IOException -> L53
            r3.onReceiveDataSuccess(r1)     // Catch: java.io.IOException -> L53
            goto L4
        L53:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "ConnectedThread:run-->接收消息异常！"
            r1.append(r3)
            java.lang.String r3 = r0.getMessage()
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            com.example.bluetoothlibrary.utils.LogUtil.showLogE(r2, r1)
            com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread$OnSendReceiveDataListener r1 = r5.onSendReceiveDataListener
            if (r1 == 0) goto L88
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "接收消息异常:"
            r3.append(r4)
            java.lang.String r0 = r0.getMessage()
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1.onReceiveDataError(r0)
        L88:
            boolean r0 = r5.cancel()
            if (r0 == 0) goto L93
            java.lang.String r0 = "ConnectedThread:run-->接收消息异常,成功断开连接！"
            com.example.bluetoothlibrary.utils.LogUtil.showLogE(r2, r0)
        L93:
            boolean r0 = r5.cancel()
            if (r0 == 0) goto L9e
            java.lang.String r0 = "ConnectedThread:run-->接收消息结束,断开连接！"
            com.example.bluetoothlibrary.utils.LogUtil.showLogD(r2, r0)
        L9e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread.run():void");
    }

    public boolean write(byte[] bArr) {
        try {
            if (this.mmOutStream == null) {
                Log.e(TAG, "mmOutStream == null");
                return false;
            }
            this.mmOutStream.write(bArr);
            this.mmOutStream.flush();
            Log.d(TAG, "ConnectedThread:write-->写入成功：" + TypeConversion.bytes2HexString(bArr, bArr.length));
            if (this.onSendReceiveDataListener == null) {
                return true;
            }
            this.onSendReceiveDataListener.onSendDataSuccess(bArr);
            return true;
        } catch (IOException unused) {
            Log.e("TAG", "ConnectedThread:write-->写入失败：" + TypeConversion.bytes2HexString(bArr, bArr.length));
            OnSendReceiveDataListener onSendReceiveDataListener = this.onSendReceiveDataListener;
            if (onSendReceiveDataListener != null) {
                onSendReceiveDataListener.onSendDataError(bArr, "写入失败");
            }
            return false;
        }
    }

    public boolean cancel() {
        try {
            if (this.mmInStream != null) {
                this.mmInStream.close();
            }
            if (this.mmOutStream != null) {
                this.mmOutStream.close();
            }
            if (this.mmSocket != null) {
                this.mmSocket.close();
            }
            if (this.connectThread != null) {
                this.connectThread.cancel();
            }
            this.connectThread = null;
            this.mmInStream = null;
            this.mmOutStream = null;
            this.mmSocket = null;
            LogUtil.showLogW(TAG, "ConnectedThread:cancel-->成功断开连接");
            return true;
        } catch (IOException e) {
            this.mmInStream = null;
            this.mmOutStream = null;
            this.mmSocket = null;
            LogUtil.showLogE(TAG, "ConnectedThread:cancel-->断开连接异常！" + e.getMessage());
            return false;
        }
    }

    public void setOnSendReceiveDataListener(OnSendReceiveDataListener onSendReceiveDataListener) {
        this.onSendReceiveDataListener = onSendReceiveDataListener;
    }
}