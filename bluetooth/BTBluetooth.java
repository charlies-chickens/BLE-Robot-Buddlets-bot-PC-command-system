package com.example.bluetoothlibrary.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.example.bluetoothlibrary.bluetooth3.BTManager;
import com.example.bluetoothlibrary.bluetooth3.IBTManager;
import com.example.bluetoothlibrary.bluetooth4.AdDataModel;
import com.example.bluetoothlibrary.bluetooth4.AdSetting;
import com.example.bluetoothlibrary.bluetooth4.AdvertiseStateListener;
import com.example.bluetoothlibrary.bluetooth4.BLEManager;
import com.example.bluetoothlibrary.bluetooth4.IBLEManager;
import com.example.bluetoothlibrary.listener.OnBTConnectListener;
import com.example.bluetoothlibrary.listener.OnBindStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBleConnectListener;
import com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBtWithDeviceConStateListener;
import com.example.bluetoothlibrary.listener.OnDeviceSearchListener;
import com.example.bluetoothlibrary.listener.OnRemoteDeviceConStateListener;
import com.example.bluetoothlibrary.model.SearchDevice;
import com.example.bluetoothlibrary.utils.HidConnectUtil;
import com.example.bluetoothlibrary.utils.LogUtil;
import com.example.bluetoothlibrary.utils.TypeConversion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BTBluetooth implements IBTBluetooth {
    private static final String TAG = "BTBluetooth";
    private IBLEManager bleManager;
    private BluetoothGattService bluetoothGattService;
    private IBTManager btManager;
    private BluetoothDevice curBluetoothDevice;
    private BluetoothGatt curBluetoothGatt;
    private Context mContext;
    private OnConnectListener onConnectListener;
    private OnSearchListener onSearchListener;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private String serviceUUID = null;
    private String readUUID = null;
    private String writeUUID = null;
    private List<BluetoothDevice> curSearchDeviceList = new ArrayList();
    private boolean isConnected = false;
    private boolean isBLE = false;
    private boolean isGetUUID = false;
    Handler mHandler = new Handler() { // from class: com.example.bluetoothlibrary.bluetooth.BTBluetooth.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
        }
    };
    private OnDeviceSearchListener onDeviceSearchListener = new OnDeviceSearchListener() { // from class: com.example.bluetoothlibrary.bluetooth.BTBluetooth.2
        @Override // com.example.bluetoothlibrary.listener.OnDeviceSearchListener
        public void onDiscoveryStart() {
            LogUtil.showLogD(BTBluetooth.TAG, "开始扫描...");
            if (BTBluetooth.this.onSearchListener != null) {
                BTBluetooth.this.onSearchListener.onDiscoveryStart();
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnDeviceSearchListener
        public void onDiscoveryStop() {
            LogUtil.showLogD(BTBluetooth.TAG, "扫描结束");
            if (BTBluetooth.this.onSearchListener != null) {
                BTBluetooth.this.onSearchListener.onDiscoveryStop();
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnDeviceSearchListener
        public void onDeviceFound(SearchDevice searchDevice) {
            if (searchDevice != null && searchDevice.bluetoothDevice != null) {
                if (BTBluetooth.this.curSearchDeviceList.contains(searchDevice.bluetoothDevice)) {
                    return;
                }
                BTBluetooth.this.curSearchDeviceList.add(searchDevice.bluetoothDevice);
                LogUtil.showLogD(BTBluetooth.TAG, "扫描到设备name:" + searchDevice.bluetoothDevice.getName() + "-->address:" + searchDevice.bluetoothDevice.getAddress());
                if (BTBluetooth.this.onSearchListener != null) {
                    BTBluetooth.this.onSearchListener.onDeviceFound(searchDevice.bluetoothDevice, searchDevice.rssi, searchDevice.scanRecord);
                    return;
                }
                return;
            }
            LogUtil.showLogE(BTBluetooth.TAG, "onDeviceSearchListener-->searchDevice == null");
        }

        @Override // com.example.bluetoothlibrary.listener.OnDeviceSearchListener
        public void onDiscoveryOutTime() {
            if (BTBluetooth.this.onSearchListener != null) {
                BTBluetooth.this.onSearchListener.onDeviceSearchOutTime();
            }
        }
    };
    private boolean isMoreReadUUIDs = false;
    private String[] readUUIDs = null;
    private OnRemoteDeviceConStateListener onRemoteDeviceConStateListener = new OnRemoteDeviceConStateListener() { // from class: com.example.bluetoothlibrary.bluetooth.BTBluetooth.3
        @Override // com.example.bluetoothlibrary.listener.OnRemoteDeviceConStateListener
        public void onConnected(BluetoothDevice bluetoothDevice) {
        }

        @Override // com.example.bluetoothlibrary.listener.OnRemoteDeviceConStateListener
        public void onDisConnected(BluetoothDevice bluetoothDevice) {
            LogUtil.showLogW(BTBluetooth.TAG, "OnRemoteDeviceConStateListener-->断开连接");
            BTBluetooth.this.isConnected = false;
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onDisConnectSuccess(0);
            }
        }
    };
    private OnBTConnectListener onBTConnectListener = new OnBTConnectListener() { // from class: com.example.bluetoothlibrary.bluetooth.BTBluetooth.4
        @Override // com.example.bluetoothlibrary.listener.OnBTConnectListener
        public void onStartConnect() {
            if (BTBluetooth.this.curBluetoothDevice != null) {
                LogUtil.showLogD(BTBluetooth.TAG, "onBTConnectListener-->开始连接：" + BTBluetooth.this.curBluetoothDevice.getName() + "-->" + BTBluetooth.this.curBluetoothDevice.getAddress());
            }
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onStartConnect();
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBTConnectListener
        public void onConnectSuccess() {
            LogUtil.showLogW(BTBluetooth.TAG, "onBTConnectListener-->连接成功");
            BTBluetooth.this.isConnected = true;
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onConnectSuccess();
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBTConnectListener
        public void onConnectFailure() {
            BTBluetooth.this.isConnected = false;
            LogUtil.showLogE(BTBluetooth.TAG, "onBTConnectListener-->连接失败！");
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onConnectFailure("连接失败", -1);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBTConnectListener
        public void onConnectOutTime() {
            BTBluetooth.this.isConnected = false;
            LogUtil.showLogE(BTBluetooth.TAG, "onBTConnectListener-->连接超时！");
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onConnectFailure("连接超时", -1);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBTConnectListener
        public void onDisConnectSuccess() {
            LogUtil.showLogW(BTBluetooth.TAG, "onBTConnectListener-->断开连接");
            BTBluetooth.this.isConnected = false;
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onDisConnectSuccess(0);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBTConnectListener
        public void onSendSuccess(byte[] bArr) {
            LogUtil.showLogW(BTBluetooth.TAG, "onBTConnectListener-->发送数据成功,长度" + bArr.length + "->" + TypeConversion.bytes2HexString(bArr, bArr.length));
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onSendSuccess(bArr);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBTConnectListener
        public void onSendError(byte[] bArr, String str) {
            LogUtil.showLogE(BTBluetooth.TAG, "onBTConnectListener-->发送数据出错,长度" + bArr.length + "->" + TypeConversion.bytes2HexString(bArr, bArr.length));
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onSendError(bArr, str);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBTConnectListener
        public void onReceiveSuccess(byte[] bArr) {
            LogUtil.showLogW(BTBluetooth.TAG, "onBTConnectListener-->成功收到数据,长度" + bArr.length + "->" + TypeConversion.bytes2HexString(bArr, bArr.length));
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onReceiveSuccess(null, bArr);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBTConnectListener
        public void onReceiveError(String str) {
            LogUtil.showLogE(BTBluetooth.TAG, "onBTConnectListener-->接收数据出错：" + str);
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onReceiveError(str);
            }
        }
    };
    private OnBleConnectListener onBleConnectListener = new OnBleConnectListener() { // from class: com.example.bluetoothlibrary.bluetooth.BTBluetooth.5
        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onReadRssi(BluetoothGatt bluetoothGatt, int i, int i2) {
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onConnecting(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice) {
            if (BTBluetooth.this.curBluetoothDevice != null) {
                LogUtil.showLogD(BTBluetooth.TAG, "onBleConnectListener-->正在连接：" + BTBluetooth.this.curBluetoothDevice.getName() + "-->" + BTBluetooth.this.curBluetoothDevice.getAddress());
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onConnectSuccess(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, int i) {
            LogUtil.showLogW(BTBluetooth.TAG, "onBleConnectListener-->连接成功");
            BTBluetooth.this.isConnected = true;
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onConnectFailure(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, String str, int i) {
            BTBluetooth.this.isConnected = false;
            LogUtil.showLogE(BTBluetooth.TAG, "onBleConnectListener-->" + str + "!-- status = " + i);
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onConnectFailure(str, i);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onDisConnecting(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice) {
            LogUtil.showLogD(BTBluetooth.TAG, "onBleConnectListener-->正在断开...");
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onDisConnectSuccess(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, int i) {
            LogUtil.showLogW(BTBluetooth.TAG, "onBleConnectListener-->断开连接");
            BTBluetooth.this.isConnected = false;
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onDisConnectSuccess(i);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onServiceDiscovery(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, int i) {
            BTBluetooth.this.isConnected = true;
            LogUtil.showLogW(BTBluetooth.TAG, "onBleConnectListener-->连接成功且发现服务！");
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onConnectSuccess();
            }
            BTBluetooth.this.curBluetoothGatt = bluetoothGatt;
            if (BTBluetooth.this.isGetUUID) {
                Log.d(BTBluetooth.TAG, "已知UUID");
                if (!BTBluetooth.this.isMoreReadUUIDs) {
                    BTBluetooth.this.setupService(bluetoothGatt);
                    return;
                } else {
                    BTBluetooth.this.setupServiceWithMoreRead(bluetoothGatt);
                    return;
                }
            }
            Log.d(BTBluetooth.TAG, "UUID未知");
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onReceiveMessage(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr) {
            LogUtil.showLogW(BTBluetooth.TAG, "onBleConnectListener-->成功收到数据,长度" + bArr.length + "->" + TypeConversion.bytes2HexString(bArr, bArr.length));
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onReceiveSuccess(bluetoothGattCharacteristic, bArr);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onReceiveError(String str) {
            LogUtil.showLogE(BTBluetooth.TAG, "onBTConnectListener-->接收数据出错：" + str);
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onReceiveError(str);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onWriteSuccess(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, byte[] bArr) {
            LogUtil.showLogW(BTBluetooth.TAG, "onBleConnectListener-->发送数据成功,长度" + bArr.length + "->" + TypeConversion.bytes2HexString(bArr, bArr.length));
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onSendSuccess(bArr);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onWriteFailure(BluetoothGatt bluetoothGatt, BluetoothDevice bluetoothDevice, byte[] bArr, String str) {
            LogUtil.showLogE(BTBluetooth.TAG, "onBleConnectListener-->发送数据失败,长度" + bArr.length + "->" + TypeConversion.bytes2HexString(bArr, bArr.length));
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onSendError(bArr, str);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onMTUSetSuccess(String str, int i) {
            LogUtil.showLogW(BTBluetooth.TAG, "onBleConnectListener-->MTU设置-->" + str);
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onMTUSetSuccess(str, i);
            }
        }

        @Override // com.example.bluetoothlibrary.listener.OnBleConnectListener
        public void onMTUSetFailure(String str) {
            LogUtil.showLogE(BTBluetooth.TAG, "onBleConnectListener-->MTU设置-->" + str);
            if (BTBluetooth.this.onConnectListener != null) {
                BTBluetooth.this.onConnectListener.onMTUSetFailure(str);
            }
        }
    };

    private static class BTBluetoothHolder {
        private static final BTBluetooth bluetooth = new BTBluetooth();

        private BTBluetoothHolder() {
        }
    }

    public static BTBluetooth getInstance() {
        return BTBluetoothHolder.bluetooth;
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public boolean isConnected() {
        return this.isConnected;
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void setConnected(boolean z) {
        this.isConnected = z;
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void openBluetooth(Context context, boolean z) {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "openBluetooth()-->btManager == null");
                return;
            } else {
                iBTManager.openBluetooth(context, z);
                return;
            }
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "openBluetooth()-->bleManager == null");
        } else {
            iBLEManager.openBluetooth(context, z);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void closeBluetooth() {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "closeBluetooth()-->btManager == null");
                return;
            } else {
                iBTManager.closeBluetooth();
                return;
            }
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "closeBluetooth()-->bleManager == null");
        } else {
            iBLEManager.closeBluetooth();
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void init(Context context, boolean z) {
        this.mContext = context;
        if (z) {
            this.isBLE = true;
        } else {
            this.isBLE = false;
        }
        if (!this.isBLE) {
            BTManager bTManager = BTManager.getInstance();
            this.btManager = bTManager;
            bTManager.initBluetooth(context);
        } else {
            BLEManager bLEManager = BLEManager.getInstance();
            this.bleManager = bLEManager;
            bLEManager.initBluetooth(context);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void unRegisterBTBluetoothReceiver() {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "unRegisterBTBluetoothReceiver()-->btManager == null");
                return;
            } else {
                iBTManager.unRegisterBluetoothReceiver(this.mContext);
                return;
            }
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "unRegisterBTBluetoothReceiver()-->bleManager == null");
        } else {
            iBLEManager.unRegisterBluetoothReceiver(this.mContext);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public boolean getDeviceBondState(BluetoothDevice bluetoothDevice) {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "getDeviceBondState()-->btManager == null");
                return false;
            }
            return iBTManager.getDeviceBoundState(bluetoothDevice);
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "getDeviceBondState()-->bleManager == null");
            return false;
        }
        return iBLEManager.getDeviceBoundState(bluetoothDevice);
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public BluetoothAdapter getBluetoothAdapter() {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "getBluetoothAdapter()-->btManager == null");
                return null;
            }
            return iBTManager.getBluetoothAdapter();
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "getBluetoothAdapter()-->bleManager == null");
            return null;
        }
        return iBLEManager.getBluetoothAdapter();
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public List<BluetoothDevice> getBoundDeviceList() {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "getBoundDeviceList()-->btManager == null");
                return null;
            }
            return iBTManager.getBoundDeviceList();
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "getBoundDeviceList()-->bleManager == null");
            return null;
        }
        return iBLEManager.getBoundDeviceList();
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public boolean boundDevice(BluetoothDevice bluetoothDevice) {
        if (!this.isBLE) {
            if (this.btManager == null) {
                LogUtil.showLogE(TAG, "boundDevice()-->btManager == null");
                return false;
            }
            if (Build.VERSION.SDK_INT >= 19) {
                return this.btManager.boundDeviceAPI(bluetoothDevice);
            }
            return this.btManager.boundDevice(bluetoothDevice);
        }
        if (this.bleManager == null) {
            LogUtil.showLogE(TAG, "boundDevice()-->bleManager == null");
            return false;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            return this.bleManager.boundDeviceAPI(bluetoothDevice);
        }
        return this.bleManager.boundDevice(bluetoothDevice);
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public boolean disBoundDevice(BluetoothDevice bluetoothDevice) {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "disBoundDevice()-->btManager == null");
                return false;
            }
            return iBTManager.disBoundDevice(bluetoothDevice);
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "disBoundDevice()-->bleManager == null");
            return false;
        }
        return iBLEManager.disBoundDevice(bluetoothDevice);
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void discoveryDevice(OnSearchListener onSearchListener, long j) {
        openBluetooth(this.mContext, false);
        List<BluetoothDevice> list = this.curSearchDeviceList;
        if (list == null) {
            this.curSearchDeviceList = new ArrayList();
        } else {
            list.clear();
        }
        if (!this.isBLE) {
            this.onSearchListener = onSearchListener;
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "discoveryDevice()-->btManager == null");
                return;
            } else {
                iBTManager.startDiscoveryDevice(this.onDeviceSearchListener, j);
                return;
            }
        }
        this.onSearchListener = onSearchListener;
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "discoveryDevice()-->bleManager == null");
        } else {
            iBLEManager.startDiscoveryDevice(this.onDeviceSearchListener, j);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void discoveryDevice(UUID[] uuidArr, OnSearchListener onSearchListener, long j) {
        openBluetooth(this.mContext, false);
        List<BluetoothDevice> list = this.curSearchDeviceList;
        if (list == null) {
            this.curSearchDeviceList = new ArrayList();
        } else {
            list.clear();
        }
        if (this.isBLE) {
            this.onSearchListener = onSearchListener;
            IBLEManager iBLEManager = this.bleManager;
            if (iBLEManager == null) {
                Log.d("BLEManager", "bleManager == null");
            } else {
                iBLEManager.startDiscoveryDevice(uuidArr, this.onDeviceSearchListener, j);
            }
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void stopDiscoveryDevice() {
        openBluetooth(this.mContext, false);
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "stopDiscoveryDevice()-->btManager == null");
                return;
            } else {
                iBTManager.stopDiscoveryDevice();
                return;
            }
        }
        if (this.bleManager == null) {
            LogUtil.showLogE(TAG, "stopDiscoveryDevice()-->bleManager == null");
        } else if (Build.VERSION.SDK_INT >= 18) {
            this.bleManager.stopDiscoveryDevice();
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void connectBT3Device(boolean z, int i, String str, boolean z2, BluetoothDevice bluetoothDevice, long j, OnConnectListener onConnectListener) {
        openBluetooth(this.mContext, false);
        if (this.isBLE) {
            return;
        }
        this.onConnectListener = onConnectListener;
        this.curBluetoothDevice = bluetoothDevice;
        IBTManager iBTManager = this.btManager;
        if (iBTManager == null) {
            LogUtil.showLogE(TAG, "connectBT3Device()-->btManager == null");
        } else {
            iBTManager.startConnectDevice(z, bluetoothDevice, str, i, z2, j, this.onBTConnectListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void connectBLEDevice(String str, String str2, String str3, BluetoothDevice bluetoothDevice, long j, OnConnectListener onConnectListener) {
        openBluetooth(this.mContext, false);
        if (this.isBLE) {
            if (str != null && str2 != null && str3 != null) {
                this.isGetUUID = true;
                this.serviceUUID = str;
                this.readUUID = str2;
                this.writeUUID = str3;
            } else {
                this.isGetUUID = false;
            }
            this.onConnectListener = onConnectListener;
            this.curBluetoothDevice = bluetoothDevice;
            if (this.bleManager == null) {
                LogUtil.showLogE(TAG, "connectBLEDevice()-->bleManager == null");
                return;
            }
            if (Build.VERSION.SDK_INT >= 18) {
                Log.d(TAG, "准备连接设备：" + bluetoothDevice.getName());
                this.bleManager.addBLEConnectDevice(this.mContext, bluetoothDevice, j, this.onBleConnectListener);
            }
        }
    }

    public void connectBLEDevice(boolean z, String str, String[] strArr, String str2, BluetoothDevice bluetoothDevice, long j, OnConnectListener onConnectListener) {
        openBluetooth(this.mContext, false);
        if (this.isBLE) {
            if (str != null && strArr != null && str2 != null) {
                this.isGetUUID = true;
                this.isMoreReadUUIDs = z;
                this.serviceUUID = str;
                this.readUUIDs = strArr;
                this.writeUUID = str2;
            } else {
                this.isGetUUID = false;
            }
            this.onConnectListener = onConnectListener;
            this.curBluetoothDevice = bluetoothDevice;
            if (this.bleManager == null) {
                LogUtil.showLogE(TAG, "connectBLEDevice()-->bleManager == null");
            } else if (Build.VERSION.SDK_INT >= 18) {
                this.bleManager.addBLEConnectDevice(this.mContext, bluetoothDevice, j, this.onBleConnectListener);
            }
        }
    }

    public List<BluetoothGattService> getServiceList() {
        BluetoothGatt bluetoothGatt = this.curBluetoothGatt;
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "getServiceList()-->curBluetoothGatt == null");
            return null;
        }
        return bluetoothGatt.getServices();
    }

    public void setupService(String str, String str2, String str3, String str4) {
        if (this.curBluetoothGatt == null) {
            LogUtil.showLogE(TAG, "setupService()-->curBluetoothGatt == null");
            return;
        }
        if (this.bleManager == null) {
            LogUtil.showLogE(TAG, "setupService()-->bleManager == null");
            return;
        }
        if (str == null) {
            LogUtil.showLogE(TAG, "setupService()-->readServiceUUID == null");
            return;
        }
        if (str3 == null) {
            LogUtil.showLogE(TAG, "setupService()-->writeServiceUUID == null");
            return;
        }
        BluetoothGattService bluetoothGattService = null;
        BluetoothGattService bluetoothGattService2 = null;
        for (BluetoothGattService bluetoothGattService3 : getServiceList()) {
            if (bluetoothGattService3.getUuid().toString().equals(str)) {
                bluetoothGattService = bluetoothGattService3;
            }
            if (bluetoothGattService3.getUuid().toString().equals(str3)) {
                bluetoothGattService2 = bluetoothGattService3;
            }
        }
        if (bluetoothGattService == null) {
            LogUtil.showLogE(TAG, "setupService()-->readService == null");
            return;
        }
        if (bluetoothGattService2 == null) {
            LogUtil.showLogE(TAG, "setupService()-->writeService == null");
            return;
        }
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
            if (bluetoothGattCharacteristic.getUuid().toString().equals(str2)) {
                this.readCharacteristic = bluetoothGattCharacteristic;
            }
        }
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic2 : bluetoothGattService2.getCharacteristics()) {
            if (bluetoothGattCharacteristic2.getUuid().toString().equals(str4)) {
                this.writeCharacteristic = bluetoothGattCharacteristic2;
            }
        }
        this.bleManager.enableNotification(true, this.curBluetoothGatt, this.readCharacteristic);
        for (BluetoothGattDescriptor bluetoothGattDescriptor : this.writeCharacteristic.getDescriptors()) {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.curBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth.BTBluetooth.6
            @Override // java.lang.Runnable
            public void run() {
            }
        }, 2000L);
        Log.d(TAG, "已获取特征111");
        Log.d(TAG, "读特征值：" + this.readCharacteristic.getValue());
    }

    public void setupService() {
        BluetoothGatt bluetoothGatt = this.curBluetoothGatt;
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "setupService()-->curBluetoothGatt == null");
        } else {
            setupService(bluetoothGatt);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setupServiceWithMoreRead(BluetoothGatt bluetoothGatt) {
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "setupServiceWithMoreRead()-->bluetoothGatt == null");
            return;
        }
        if (this.bleManager == null) {
            LogUtil.showLogE(TAG, "setupServiceWithMoreRead()-->bleManager == null");
            return;
        }
        if (this.serviceUUID == null) {
            LogUtil.showLogE(TAG, "setupServiceWithMoreRead()-->serviceUUID == null");
            return;
        }
        for (BluetoothGattService bluetoothGattService : bluetoothGatt.getServices()) {
            if (bluetoothGattService.getUuid().toString().equals(this.serviceUUID)) {
                this.bluetoothGattService = bluetoothGattService;
            }
        }
        if (this.bluetoothGattService == null) {
            this.bleManager.disConnectDevice(bluetoothGatt, null);
            LogUtil.showLogE(TAG, "setupServiceWithMoreRead()-->找不到该服务bluetoothGattService == null");
            return;
        }
        LogUtil.showLogD(TAG, "setupService-->bluetoothGattService = " + this.bluetoothGattService.toString());
        ArrayList arrayList = new ArrayList();
        for (String str : this.readUUIDs) {
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : this.bluetoothGattService.getCharacteristics()) {
                if (bluetoothGattCharacteristic.getUuid().toString().toLowerCase().equals(str.toLowerCase())) {
                    arrayList.add(bluetoothGattCharacteristic);
                }
            }
        }
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic2 : this.bluetoothGattService.getCharacteristics()) {
            if (bluetoothGattCharacteristic2.getUuid().toString().equals(this.writeUUID)) {
                this.writeCharacteristic = bluetoothGattCharacteristic2;
            }
        }
        if (arrayList.size() != this.readUUIDs.length) {
            LogUtil.showLogE(TAG, "setupServiceWithMoreRead()-->readCharacteristicList.size() != readUUIDs.length");
            return;
        }
        if (this.writeCharacteristic == null) {
            LogUtil.showLogE(TAG, "setupServiceWithMoreRead()-->writeCharacteristic == null");
            return;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            this.bleManager.enableNotification(true, bluetoothGatt, (BluetoothGattCharacteristic) it.next());
        }
        for (BluetoothGattDescriptor bluetoothGattDescriptor : this.writeCharacteristic.getDescriptors()) {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth.BTBluetooth.7
            @Override // java.lang.Runnable
            public void run() {
            }
        }, 2000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setupService(BluetoothGatt bluetoothGatt) {
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "setupService()-->bluetoothGatt == null");
            return;
        }
        if (this.bleManager == null) {
            LogUtil.showLogE(TAG, "setupService()-->bleManager == null");
            return;
        }
        if (this.serviceUUID == null) {
            LogUtil.showLogE(TAG, "setupService()-->serviceUUID == null");
            return;
        }
        for (BluetoothGattService bluetoothGattService : bluetoothGatt.getServices()) {
            if (bluetoothGattService.getUuid().toString().equals(this.serviceUUID)) {
                this.bluetoothGattService = bluetoothGattService;
            }
        }
        if (this.bluetoothGattService == null) {
            this.bleManager.disConnectDevice(bluetoothGatt, null);
            LogUtil.showLogE(TAG, "setupService()-->找不到该服务bluetoothGattService == null");
            return;
        }
        LogUtil.showLogD(TAG, "setupService-->bluetoothGattService = " + this.bluetoothGattService.toString());
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : this.bluetoothGattService.getCharacteristics()) {
            if (bluetoothGattCharacteristic.getUuid().toString().equals(this.readUUID)) {
                this.readCharacteristic = bluetoothGattCharacteristic;
            } else if (bluetoothGattCharacteristic.getUuid().toString().equals(this.writeUUID)) {
                this.writeCharacteristic = bluetoothGattCharacteristic;
            }
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic2 = this.readCharacteristic;
        if (bluetoothGattCharacteristic2 == null) {
            LogUtil.showLogE(TAG, "setupService()-->readCharacteristic == null");
            return;
        }
        if (this.writeCharacteristic == null) {
            LogUtil.showLogE(TAG, "setupService()-->writeCharacteristic == null");
            return;
        }
        this.bleManager.enableNotification(true, bluetoothGatt, bluetoothGattCharacteristic2);
        for (BluetoothGattDescriptor bluetoothGattDescriptor : this.writeCharacteristic.getDescriptors()) {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth.BTBluetooth.8
            @Override // java.lang.Runnable
            public void run() {
            }
        }, 2000L);
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void disConnectDevice() {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "disConnectDevice()-->btManager == null");
                return;
            } else {
                iBTManager.clearConnectedThread();
                return;
            }
        }
        if (this.bleManager == null) {
            LogUtil.showLogE(TAG, "disConnectDevice()-->bleManager == null");
        } else if (Build.VERSION.SDK_INT >= 18) {
            this.bleManager.disConnectDevice(this.curBluetoothGatt, null);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public boolean sendData(byte[] bArr) {
        openBluetooth(this.mContext, false);
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "sendData()-->btManager == null");
                return false;
            }
            return iBTManager.sendData(bArr);
        }
        if (this.bleManager == null) {
            LogUtil.showLogE(TAG, "sendData()-->bleManager == null");
            return false;
        }
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        boolean zSendMessage = this.bleManager.sendMessage(this.curBluetoothGatt, this.writeCharacteristic, bArr);
        Log.d("BLEManager", "4.0发送结果：" + zSendMessage);
        return zSendMessage;
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public boolean sendData(String str, boolean z) {
        openBluetooth(this.mContext, false);
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "sendData()-->btManager == null");
                return false;
            }
            return iBTManager.sendData(str, z);
        }
        if (this.bleManager == null) {
            LogUtil.showLogE(TAG, "sendData()-->bleManager == null");
            return false;
        }
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        boolean zSendMessage = this.bleManager.sendMessage(this.curBluetoothGatt, this.writeCharacteristic, str, z);
        Log.d("BLEManager", "4.0发送结果：" + zSendMessage);
        return zSendMessage;
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public boolean getBluetoothState() {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "getBluetoothState()-->btManager == null");
                return false;
            }
            return iBTManager.getBluetoothState();
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "getBluetoothState()-->bleManager == null");
            return false;
        }
        return iBLEManager.getBluetoothState();
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void setOnBluetoothStateChangeListener(OnBluetoothStateChangeListener onBluetoothStateChangeListener) {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "setOnBluetoothStateChangeListener()-->btManager == null");
                return;
            } else {
                iBTManager.setOnBluetoothStateChangeListener(onBluetoothStateChangeListener);
                return;
            }
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "setOnBluetoothStateChangeListener()-->bleManager == null");
        } else {
            iBLEManager.setOnBluetoothStateChangeListener(onBluetoothStateChangeListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void setOnBindStateChangeListener(OnBindStateChangeListener onBindStateChangeListener) {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "setOnBindStateChangeListener()-->btManager == null");
                return;
            } else {
                iBTManager.setOnBindStateChangeListener(onBindStateChangeListener);
                return;
            }
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "setOnBindStateChangeListener()-->bleManager == null");
        } else {
            iBLEManager.setOnBindStateChangeListener(onBindStateChangeListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void setOnBtWithDeviceConStateListener(OnBtWithDeviceConStateListener onBtWithDeviceConStateListener) {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "setOnBtWithDeviceConStateListener()-->btManager == null");
                return;
            } else {
                iBTManager.setOnBtWithDeviceConStateListener(onBtWithDeviceConStateListener);
                return;
            }
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "setOnBtWithDeviceConStateListener()-->bleManager == null");
        } else {
            iBLEManager.setOnBtWithDeviceConStateListener(onBtWithDeviceConStateListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void setOnRemoteDeviceConStateListener(OnRemoteDeviceConStateListener onRemoteDeviceConStateListener) {
        if (!this.isBLE) {
            IBTManager iBTManager = this.btManager;
            if (iBTManager == null) {
                LogUtil.showLogE(TAG, "setOnRemoteDeviceConStateListener()-->btManager == null");
                return;
            } else {
                iBTManager.setOnRemoteDeviceConStateListener(onRemoteDeviceConStateListener);
                return;
            }
        }
        IBLEManager iBLEManager = this.bleManager;
        if (iBLEManager == null) {
            LogUtil.showLogE(TAG, "setOnRemoteDeviceConStateListener()-->bleManager == null");
        } else {
            iBLEManager.setOnRemoteDeviceConStateListener(onRemoteDeviceConStateListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public boolean setMtuValue(int i) {
        if (this.isBLE) {
            Log.d(TAG, "设置修改MTU = " + i);
            IBLEManager iBLEManager = this.bleManager;
            if (iBLEManager == null) {
                LogUtil.showLogE(TAG, "setMtuValue()-->bleManager == null");
                return false;
            }
            if (iBLEManager.setMtuValue(i)) {
                LogUtil.showLogW(TAG, "setMtuValue()-->设置修改MTU操作成功！");
                return true;
            }
            LogUtil.showLogE(TAG, "setMtuValue()-->设置修改MTU操作失败！");
        }
        return false;
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void connectHidDevice(BluetoothDevice bluetoothDevice) {
        HidConnectUtil hidConnectUtil = new HidConnectUtil(this.mContext);
        hidConnectUtil.pair(bluetoothDevice);
        hidConnectUtil.connect(bluetoothDevice);
    }

    @Override // com.example.bluetoothlibrary.bluetooth.IBTBluetooth
    public void disConnectHidDevice(BluetoothDevice bluetoothDevice) {
        HidConnectUtil hidConnectUtil = new HidConnectUtil(this.mContext);
        hidConnectUtil.disConnect(bluetoothDevice);
        hidConnectUtil.unPair(bluetoothDevice);
    }

    public void startAdvertising(AdSetting adSetting, AdDataModel adDataModel, AdvertiseStateListener advertiseStateListener) {
        if (this.isBLE) {
            IBLEManager iBLEManager = this.bleManager;
            if (iBLEManager == null) {
                LogUtil.showLogE(TAG, "startAdvertising()-->bleManager == null");
            } else {
                iBLEManager.startAdvertising(adSetting, adDataModel, advertiseStateListener);
            }
        }
    }

    public void stopAdvertising() {
        if (this.isBLE) {
            IBLEManager iBLEManager = this.bleManager;
            if (iBLEManager == null) {
                LogUtil.showLogE(TAG, "stopAdvertising()-->bleManager == null");
            } else {
                iBLEManager.stopAdvertising();
            }
        }
    }

    public void startServer(String str, String str2, String str3) {
        if (this.isBLE) {
            IBLEManager iBLEManager = this.bleManager;
            if (iBLEManager == null) {
                LogUtil.showLogE(TAG, "startServer()-->bleManager == null");
            } else {
                iBLEManager.startBLEServer(str, str2, str3);
            }
        }
    }
}