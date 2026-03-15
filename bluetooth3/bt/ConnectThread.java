package com.example.bluetoothlibrary.bluetooth3.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.example.bluetoothlibrary.utils.LogUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class ConnectThread extends Thread {
    private static final String TAG = "BTManager";
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    private OnBluetoothConnectListener onBluetoothConnectListener;

    public interface OnBluetoothConnectListener {
        void onConnFailure(String str);

        void onConnSuccess(BluetoothSocket bluetoothSocket);

        void onStartConn();
    }

    public ConnectThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice, boolean z, String str, int i, boolean z2) {
        BluetoothSocket bluetoothSocketCreateInsecureRfcommSocketToServiceRecord;
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mmDevice = bluetoothDevice;
        if (this.mmSocket != null) {
            LogUtil.showLogE(TAG, "ConnectThread-->mmSocket != null先去释放");
            try {
                this.mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.showLogD(TAG, "ConnectThread-->mmSocket != null已释放");
        if (z) {
            if (str == null) {
                LogUtil.showLogE(TAG, "ConnectThread-->SPP连接：uuid == null");
                return;
            }
            try {
                if (z2) {
                    bluetoothSocketCreateInsecureRfcommSocketToServiceRecord = this.mmDevice.createRfcommSocketToServiceRecord(UUID.fromString(str));
                } else {
                    bluetoothSocketCreateInsecureRfcommSocketToServiceRecord = this.mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(str));
                }
            } catch (IOException e2) {
                LogUtil.showLogE(TAG, "ConnectThread-->SPP：获取BluetoothSocket异常!" + e2.getMessage());
                bluetoothSocketCreateInsecureRfcommSocketToServiceRecord = null;
            }
        } else {
            if (i < 0) {
                LogUtil.showLogE(TAG, "ConnectThread-->串口号连接：channel < 0");
                return;
            }
            try {
                bluetoothSocketCreateInsecureRfcommSocketToServiceRecord = z2 ? (BluetoothSocket) this.mmDevice.getClass().getMethod("createRfcommSocket", Integer.TYPE).invoke(this.mmDevice, Integer.valueOf(i)) : (BluetoothSocket) this.mmDevice.getClass().getMethod("createInsecureRfcommSocket", Integer.TYPE).invoke(this.mmDevice, Integer.valueOf(i));
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
                LogUtil.showLogE(TAG, "ConnectThread-->channel：获取BluetoothSocket异常1!" + e3.getMessage());
                bluetoothSocketCreateInsecureRfcommSocketToServiceRecord = null;
            } catch (NoSuchMethodException e4) {
                e4.printStackTrace();
                LogUtil.showLogE(TAG, "ConnectThread-->channel：获取BluetoothSocket异常3!" + e4.getMessage());
                bluetoothSocketCreateInsecureRfcommSocketToServiceRecord = null;
            } catch (InvocationTargetException e5) {
                e5.printStackTrace();
                LogUtil.showLogE(TAG, "ConnectThread-->channel：获取BluetoothSocket异常2!" + e5.getMessage());
                bluetoothSocketCreateInsecureRfcommSocketToServiceRecord = null;
            }
        }
        this.mmSocket = bluetoothSocketCreateInsecureRfcommSocketToServiceRecord;
        if (bluetoothSocketCreateInsecureRfcommSocketToServiceRecord != null) {
            LogUtil.showLogW(TAG, "ConnectThread-->已获取BluetoothSocket");
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            LogUtil.showLogE(TAG, "ConnectThread:run-->mBluetoothAdapter == null");
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
        if (this.mmSocket == null) {
            LogUtil.showLogE(TAG, "ConnectThread:run-->mmSocket == null");
            return;
        }
        try {
            LogUtil.showLogD(TAG, "ConnectThread:run-->去连接...");
            if (this.onBluetoothConnectListener != null) {
                this.onBluetoothConnectListener.onStartConn();
            }
            this.mmSocket.connect();
            if (this.onBluetoothConnectListener != null) {
                this.onBluetoothConnectListener.onConnSuccess(this.mmSocket);
                LogUtil.showLogW(TAG, "ConnectThread:run-->连接成功");
            }
        } catch (IOException e) {
            LogUtil.showLogE(TAG, "ConnectThread:run-->连接异常！" + e.getMessage());
            OnBluetoothConnectListener onBluetoothConnectListener = this.onBluetoothConnectListener;
            if (onBluetoothConnectListener != null) {
                onBluetoothConnectListener.onConnFailure("连接异常：" + e.getMessage());
            }
            cancel();
        }
    }

    public void cancel() {
        try {
            if (this.mmSocket != null && this.mmSocket.isConnected()) {
                LogUtil.showLogD(TAG, "ConnectThread:cancel-->mmSocket.isConnected() = " + this.mmSocket.isConnected());
                this.mmSocket.close();
                this.mmSocket = null;
                return;
            }
            if (this.mmSocket != null) {
                this.mmSocket.close();
                this.mmSocket = null;
            }
            LogUtil.showLogD(TAG, "ConnectThread:cancel-->关闭已连接的套接字释放资源");
        } catch (IOException e) {
            LogUtil.showLogE(TAG, "ConnectThread:cancel-->关闭已连接的套接字释放资源异常!" + e.getMessage());
        }
    }

    public void setOnBluetoothConnectListener(OnBluetoothConnectListener onBluetoothConnectListener) {
        this.onBluetoothConnectListener = onBluetoothConnectListener;
    }
}