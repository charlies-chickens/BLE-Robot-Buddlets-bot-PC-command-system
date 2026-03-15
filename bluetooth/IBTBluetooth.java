package com.example.bluetoothlibrary.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import com.example.bluetoothlibrary.listener.OnBindStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBtWithDeviceConStateListener;
import com.example.bluetoothlibrary.listener.OnRemoteDeviceConStateListener;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public interface IBTBluetooth {
    boolean boundDevice(BluetoothDevice bluetoothDevice);

    void closeBluetooth();

    void connectBLEDevice(String str, String str2, String str3, BluetoothDevice bluetoothDevice, long j, OnConnectListener onConnectListener);

    void connectBT3Device(boolean z, int i, String str, boolean z2, BluetoothDevice bluetoothDevice, long j, OnConnectListener onConnectListener);

    void connectHidDevice(BluetoothDevice bluetoothDevice);

    boolean disBoundDevice(BluetoothDevice bluetoothDevice);

    void disConnectDevice();

    void disConnectHidDevice(BluetoothDevice bluetoothDevice);

    void discoveryDevice(OnSearchListener onSearchListener, long j);

    void discoveryDevice(UUID[] uuidArr, OnSearchListener onSearchListener, long j);

    BluetoothAdapter getBluetoothAdapter();

    boolean getBluetoothState();

    List<BluetoothDevice> getBoundDeviceList();

    boolean getDeviceBondState(BluetoothDevice bluetoothDevice);

    void init(Context context, boolean z);

    boolean isConnected();

    void openBluetooth(Context context, boolean z);

    boolean sendData(String str, boolean z);

    boolean sendData(byte[] bArr);

    void setConnected(boolean z);

    boolean setMtuValue(int i);

    void setOnBindStateChangeListener(OnBindStateChangeListener onBindStateChangeListener);

    void setOnBluetoothStateChangeListener(OnBluetoothStateChangeListener onBluetoothStateChangeListener);

    void setOnBtWithDeviceConStateListener(OnBtWithDeviceConStateListener onBtWithDeviceConStateListener);

    void setOnRemoteDeviceConStateListener(OnRemoteDeviceConStateListener onRemoteDeviceConStateListener);

    void stopDiscoveryDevice();

    void unRegisterBTBluetoothReceiver();
}