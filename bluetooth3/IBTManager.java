package com.example.bluetoothlibrary.bluetooth3;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import com.example.bluetoothlibrary.listener.OnBTConnectListener;
import com.example.bluetoothlibrary.listener.OnBindStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBtWithDeviceConStateListener;
import com.example.bluetoothlibrary.listener.OnDeviceSearchListener;
import com.example.bluetoothlibrary.listener.OnRemoteDeviceConStateListener;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IBTManager {
    boolean boundDevice(BluetoothDevice bluetoothDevice);

    boolean boundDeviceAPI(BluetoothDevice bluetoothDevice);

    void clearConnectedThread();

    void closeBluetooth();

    boolean disBoundDevice(BluetoothDevice bluetoothDevice);

    BluetoothAdapter getBluetoothAdapter();

    boolean getBluetoothState();

    List<BluetoothDevice> getBoundDeviceList();

    boolean getDeviceBoundState(BluetoothDevice bluetoothDevice);

    BluetoothDevice getDeviceByAddress(String str);

    void initBluetooth(Context context);

    void openBluetooth(Context context, boolean z);

    boolean sendData(String str, boolean z);

    boolean sendData(byte[] bArr);

    void setOnBindStateChangeListener(OnBindStateChangeListener onBindStateChangeListener);

    void setOnBluetoothStateChangeListener(OnBluetoothStateChangeListener onBluetoothStateChangeListener);

    void setOnBtWithDeviceConStateListener(OnBtWithDeviceConStateListener onBtWithDeviceConStateListener);

    void setOnRemoteDeviceConStateListener(OnRemoteDeviceConStateListener onRemoteDeviceConStateListener);

    void startConnectDevice(boolean z, BluetoothDevice bluetoothDevice, String str, int i, boolean z2, long j, OnBTConnectListener onBTConnectListener);

    void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener);

    void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener, long j);

    void stopDiscoveryDevice();

    void unRegisterBluetoothReceiver(Context context);
}