package com.example.bluetoothlibrary.broadcastreceiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.example.bluetoothlibrary.listener.OnBindStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBtWithDeviceConStateListener;
import com.example.bluetoothlibrary.listener.OnDeviceSearchListener;
import com.example.bluetoothlibrary.listener.OnRemoteDeviceConStateListener;
import com.example.bluetoothlibrary.model.SearchDevice;
import com.example.bluetoothlibrary.utils.LogUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.ShortCompanionObject;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothBroadcastReceiver";
    private List<OnBindStateChangeListener> onBindStateChangeListenerList;
    private List<OnBluetoothStateChangeListener> onBluetoothStateChangeListenerList;
    private OnBtWithDeviceConStateListener onBtWithDeviceConStateListener;
    private OnDeviceSearchListener onDeviceSearchListener;
    private OnRemoteDeviceConStateListener onRemoteDeviceConStateListener;

    public void setOnDeviceSearchListener(OnDeviceSearchListener onDeviceSearchListener) {
        this.onDeviceSearchListener = onDeviceSearchListener;
    }

    public void setOnRemoteDeviceConStateListener(OnRemoteDeviceConStateListener onRemoteDeviceConStateListener) {
        this.onRemoteDeviceConStateListener = onRemoteDeviceConStateListener;
    }

    public void setOnBtWithDeviceConStateListener(OnBtWithDeviceConStateListener onBtWithDeviceConStateListener) {
        this.onBtWithDeviceConStateListener = onBtWithDeviceConStateListener;
    }

    public void setOnBluetoothStateChangeListener(OnBluetoothStateChangeListener onBluetoothStateChangeListener) {
        if (this.onBluetoothStateChangeListenerList == null) {
            this.onBluetoothStateChangeListenerList = new ArrayList();
        }
        if (this.onBluetoothStateChangeListenerList.contains(onBluetoothStateChangeListener)) {
            return;
        }
        this.onBluetoothStateChangeListenerList.add(onBluetoothStateChangeListener);
    }

    public void removeOnBluetoothStateChangeListener(OnBluetoothStateChangeListener onBluetoothStateChangeListener) {
        List<OnBluetoothStateChangeListener> list = this.onBluetoothStateChangeListenerList;
        if (list != null) {
            list.remove(onBluetoothStateChangeListener);
        }
    }

    public void setOnBindStateChangeListener(OnBindStateChangeListener onBindStateChangeListener) {
        if (this.onBindStateChangeListenerList == null) {
            this.onBindStateChangeListenerList = new ArrayList();
        }
        if (this.onBindStateChangeListenerList.contains(onBindStateChangeListener)) {
            return;
        }
        this.onBindStateChangeListenerList.add(onBindStateChangeListener);
    }

    public void removeOnBindStateChangeListener(OnBindStateChangeListener onBindStateChangeListener) {
        List<OnBindStateChangeListener> list = this.onBindStateChangeListenerList;
        if (list != null) {
            list.remove(onBindStateChangeListener);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.equals(action, "android.bluetooth.adapter.action.STATE_CHANGED")) {
            switch (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0)) {
                case 10:
                    LogUtil.showLogD(TAG, "蓝牙已关闭");
                    List<OnBluetoothStateChangeListener> list = this.onBluetoothStateChangeListenerList;
                    if (list != null) {
                        Iterator<OnBluetoothStateChangeListener> it = list.iterator();
                        while (it.hasNext()) {
                            it.next().onBluetoothClose();
                        }
                    }
                    break;
                case 11:
                    LogUtil.showLogD(TAG, "蓝牙正在打开...");
                    List<OnBluetoothStateChangeListener> list2 = this.onBluetoothStateChangeListenerList;
                    if (list2 != null) {
                        Iterator<OnBluetoothStateChangeListener> it2 = list2.iterator();
                        while (it2.hasNext()) {
                            it2.next().onBluetoothOpening();
                        }
                    }
                    break;
                case 12:
                    LogUtil.showLogD(TAG, "蓝牙已打开");
                    List<OnBluetoothStateChangeListener> list3 = this.onBluetoothStateChangeListenerList;
                    if (list3 != null) {
                        Iterator<OnBluetoothStateChangeListener> it3 = list3.iterator();
                        while (it3.hasNext()) {
                            it3.next().onBluetoothOpen();
                        }
                    }
                    break;
                case 13:
                    LogUtil.showLogD(TAG, "蓝牙正在关闭...");
                    List<OnBluetoothStateChangeListener> list4 = this.onBluetoothStateChangeListenerList;
                    if (list4 != null) {
                        Iterator<OnBluetoothStateChangeListener> it4 = list4.iterator();
                        while (it4.hasNext()) {
                            it4.next().onBluetoothClosing();
                        }
                    }
                    break;
            }
            return;
        }
        if (TextUtils.equals(action, "android.bluetooth.device.action.BOND_STATE_CHANGED")) {
            switch (((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE")).getBondState()) {
                case 10:
                    LogUtil.showLogD(TAG, "已解绑");
                    List<OnBindStateChangeListener> list5 = this.onBindStateChangeListenerList;
                    if (list5 != null) {
                        Iterator<OnBindStateChangeListener> it5 = list5.iterator();
                        while (it5.hasNext()) {
                            it5.next().onBondNone();
                        }
                    }
                    break;
                case 11:
                    LogUtil.showLogD(TAG, "正在绑定...");
                    List<OnBindStateChangeListener> list6 = this.onBindStateChangeListenerList;
                    if (list6 != null) {
                        Iterator<OnBindStateChangeListener> it6 = list6.iterator();
                        while (it6.hasNext()) {
                            it6.next().onBonding();
                        }
                    }
                    break;
                case 12:
                    LogUtil.showLogD(TAG, "已绑定");
                    List<OnBindStateChangeListener> list7 = this.onBindStateChangeListenerList;
                    if (list7 != null) {
                        Iterator<OnBindStateChangeListener> it7 = list7.iterator();
                        while (it7.hasNext()) {
                            it7.next().onBonded();
                        }
                    }
                    break;
            }
            return;
        }
        if (TextUtils.equals(action, "android.bluetooth.adapter.action.DISCOVERY_STARTED")) {
            Log.d("广播", "开始扫描");
            OnDeviceSearchListener onDeviceSearchListener = this.onDeviceSearchListener;
            if (onDeviceSearchListener != null) {
                onDeviceSearchListener.onDiscoveryStart();
                return;
            }
            return;
        }
        if (TextUtils.equals(action, "android.bluetooth.adapter.action.DISCOVERY_FINISHED")) {
            Log.d("广播", "完成扫描");
            OnDeviceSearchListener onDeviceSearchListener2 = this.onDeviceSearchListener;
            if (onDeviceSearchListener2 != null) {
                onDeviceSearchListener2.onDiscoveryStop();
                return;
            }
            return;
        }
        if (TextUtils.equals(action, "android.bluetooth.device.action.FOUND")) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            SearchDevice searchDevice = new SearchDevice(bluetoothDevice, intent.getShortExtra("android.bluetooth.device.extra.RSSI", ShortCompanionObject.MIN_VALUE), null);
            LogUtil.showLogD("BTManager", "扫描到设备：" + bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
            OnDeviceSearchListener onDeviceSearchListener3 = this.onDeviceSearchListener;
            if (onDeviceSearchListener3 != null) {
                onDeviceSearchListener3.onDeviceFound(searchDevice);
                return;
            }
            return;
        }
        if (TextUtils.equals(action, "android.bluetooth.device.action.PAIRING_REQUEST")) {
            BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            LogUtil.showLogD("BTManager", "请求配对 -->" + bluetoothDevice2.getName() + "-->" + bluetoothDevice2.getAddress());
            return;
        }
        if (TextUtils.equals(action, "android.bluetooth.device.action.ACL_CONNECTED")) {
            BluetoothDevice bluetoothDevice3 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            OnRemoteDeviceConStateListener onRemoteDeviceConStateListener = this.onRemoteDeviceConStateListener;
            if (onRemoteDeviceConStateListener != null) {
                onRemoteDeviceConStateListener.onConnected(bluetoothDevice3);
                return;
            }
            return;
        }
        if (TextUtils.equals(action, "android.bluetooth.device.action.ACL_DISCONNECTED")) {
            BluetoothDevice bluetoothDevice4 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            OnRemoteDeviceConStateListener onRemoteDeviceConStateListener2 = this.onRemoteDeviceConStateListener;
            if (onRemoteDeviceConStateListener2 != null) {
                onRemoteDeviceConStateListener2.onDisConnected(bluetoothDevice4);
                return;
            }
            return;
        }
        if (!TextUtils.equals(action, "android.bluetooth.adapter.action.SCAN_MODE_CHANGED") && "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
            int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.CONNECTION_STATE", -1);
            BluetoothDevice bluetoothDevice5 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            if (intExtra == 0) {
                Log.d("CallMusicActivity", "有设备与手机连断开连接");
                OnBtWithDeviceConStateListener onBtWithDeviceConStateListener = this.onBtWithDeviceConStateListener;
                if (onBtWithDeviceConStateListener != null) {
                    onBtWithDeviceConStateListener.onBtWithDeviceDisConnected(bluetoothDevice5);
                    return;
                }
                return;
            }
            if (intExtra != 2) {
                return;
            }
            Log.d("CallMusicActivity", "有设备与手机连接成功");
            OnBtWithDeviceConStateListener onBtWithDeviceConStateListener2 = this.onBtWithDeviceConStateListener;
            if (onBtWithDeviceConStateListener2 != null) {
                onBtWithDeviceConStateListener2.onBtWithDeviceConnected(bluetoothDevice5);
            }
        }
    }
}