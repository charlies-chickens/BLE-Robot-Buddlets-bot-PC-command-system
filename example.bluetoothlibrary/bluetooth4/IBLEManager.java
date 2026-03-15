package com.example.bluetoothlibrary.bluetooth4;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import com.example.bluetoothlibrary.listener.OnBindStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBleConnectListener;
import com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBtWithDeviceConStateListener;
import com.example.bluetoothlibrary.listener.OnDeviceSearchListener;
import com.example.bluetoothlibrary.listener.OnRemoteDeviceConStateListener;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public interface IBLEManager {
    void addBLEConnectDevice(Context context, BluetoothDevice bluetoothDevice, long j, OnBleConnectListener onBleConnectListener);

    boolean boundDevice(BluetoothDevice bluetoothDevice);

    boolean boundDeviceAPI(BluetoothDevice bluetoothDevice);

    void closeBluetooth();

    boolean disBoundDevice(BluetoothDevice bluetoothDevice);

    void disConnectDevice(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic);

    void enableNotification(boolean z, BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic);

    BluetoothAdapter getBluetoothAdapter();

    BluetoothDevice getBluetoothDeviceByAddress(String str);

    BluetoothGattService getBluetoothGattService(BluetoothGatt bluetoothGatt, String str);

    boolean getBluetoothState();

    List<BluetoothDevice> getBoundDeviceList();

    boolean getDeviceBoundState(BluetoothDevice bluetoothDevice);

    boolean getDeviceConnectState(BluetoothDevice bluetoothDevice);

    boolean getRemoteRSSI();

    void initBluetooth(Context context);

    void openBluetooth(Context context, boolean z);

    void removeConnectDevice(BluetoothDevice bluetoothDevice);

    boolean sendMessage(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, String str, boolean z);

    boolean sendMessage(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr);

    boolean setMtuValue(int i);

    void setOnBindStateChangeListener(OnBindStateChangeListener onBindStateChangeListener);

    void setOnBluetoothStateChangeListener(OnBluetoothStateChangeListener onBluetoothStateChangeListener);

    void setOnBtWithDeviceConStateListener(OnBtWithDeviceConStateListener onBtWithDeviceConStateListener);

    void setOnRemoteDeviceConStateListener(OnRemoteDeviceConStateListener onRemoteDeviceConStateListener);

    void startAdvertising(AdSetting adSetting, AdDataModel adDataModel, AdvertiseStateListener advertiseStateListener);

    void startBLEServer(String str, String str2, String str3);

    void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener);

    void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener, long j);

    void startDiscoveryDevice(UUID[] uuidArr, OnDeviceSearchListener onDeviceSearchListener, long j);

    void stopAdvertising();

    void stopDiscoveryDevice();

    void toEnableAllNotification(BluetoothGatt bluetoothGatt);

    void unRegisterBluetoothReceiver(Context context);
}