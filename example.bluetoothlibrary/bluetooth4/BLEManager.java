package com.example.bluetoothlibrary.bluetooth4;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;
import com.example.bluetoothlibrary.bluetooth4.AdDataModel;
import com.example.bluetoothlibrary.broadcastreceiver.BluetoothBroadcastReceiver;
import com.example.bluetoothlibrary.listener.OnBindStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBleConnectListener;
import com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBtWithDeviceConStateListener;
import com.example.bluetoothlibrary.listener.OnDeviceSearchListener;
import com.example.bluetoothlibrary.listener.OnRemoteDeviceConStateListener;
import com.example.bluetoothlibrary.model.SearchDevice;
import com.example.bluetoothlibrary.utils.ClsUtils;
import com.example.bluetoothlibrary.utils.LogUtil;
import com.example.bluetoothlibrary.utils.TypeConversion;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BLEManager implements IBLEManager {
    private static final long MAX_SERVICE_DISCOVER_TIME = 10000;
    private static final String TAG = "BLEManager";
    private AdvertiseCallback advertiseCallback;
    private AdvertiseStateListener advertiseStateListener;
    private int advertiseTimeout;
    private BluetoothAdapter bluetooth4Adapter;
    private BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    private BluetoothGattServer bluetoothGattServer;
    private BluetoothGattServerCallback bluetoothGattServerCallback;
    private BluetoothGattService bluetoothGattService;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothManager bluetoothManager;
    private BluetoothGattCharacteristic characteristicRead;
    private BluetoothGattCharacteristic characteristicWrite;
    private BluetoothDevice curConnDevice;
    private Context mContext;
    private OnBleConnectListener onBleConnectListener;
    private OnDeviceSearchListener onDeviceSearchListener;
    private SystemBleCheck systemBleCheck;
    private BluetoothGatt mBluetoothGatt = null;
    private boolean isConnectIng = false;
    private List<BluetoothDevice> bluetoothDeviceConnectList = new ArrayList();
    private Set<BluetoothGatt> bluetoothGattSet = new HashSet();
    private Handler mHandler = new Handler() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
        }
    };
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.2
        @Override // android.bluetooth.BluetoothAdapter.LeScanCallback
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            if (bluetoothDevice == null) {
                return;
            }
            if (bluetoothDevice.getName() != null) {
                LogUtil.showLogD(BLEManager.TAG, bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
            } else {
                LogUtil.showLogD(BLEManager.TAG, "null-->" + bluetoothDevice.getAddress());
            }
            SearchDevice searchDevice = new SearchDevice(bluetoothDevice, i, bArr);
            if (BLEManager.this.onDeviceSearchListener != null) {
                BLEManager.this.onDeviceSearchListener.onDeviceFound(searchDevice);
            }
        }
    };
    private Runnable stopScanRunnable = new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.3
        @Override // java.lang.Runnable
        public void run() {
            BLEManager.this.cancelDiscoveryDevice();
            if (BLEManager.this.onDeviceSearchListener != null) {
                BLEManager.this.onDeviceSearchListener.onDiscoveryOutTime();
            }
        }
    };
    private ScanCallback scanCallback = new ScanCallback() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.4
        @Override // android.bluetooth.le.ScanCallback
        public void onScanResult(int i, ScanResult scanResult) {
            super.onScanResult(i, scanResult);
            if (Build.VERSION.SDK_INT >= 21) {
                BluetoothDevice device = scanResult.getDevice();
                int rssi = scanResult.getRssi();
                byte[] bytes = scanResult.getScanRecord().getBytes();
                Log.d(BLEManager.TAG, "根据UUID搜索device：" + device.getAddress());
                SearchDevice searchDevice = new SearchDevice(device, rssi, bytes);
                if (BLEManager.this.onDeviceSearchListener != null) {
                    BLEManager.this.onDeviceSearchListener.onDeviceFound(searchDevice);
                }
            }
        }

        @Override // android.bluetooth.le.ScanCallback
        public void onScanFailed(int i) {
            super.onScanFailed(i);
            Log.e(BLEManager.TAG, "搜索失败");
            if (BLEManager.this.onDeviceSearchListener != null) {
                BLEManager.this.onDeviceSearchListener.onDiscoveryOutTime();
            }
        }
    };
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.5
        @Override // android.bluetooth.BluetoothGattCallback
        public void onPhyUpdate(BluetoothGatt bluetoothGatt, int i, int i2, int i3) {
            super.onPhyUpdate(bluetoothGatt, i, i2, i3);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onPhyRead(BluetoothGatt bluetoothGatt, int i, int i2, int i3) {
            super.onPhyRead(bluetoothGatt, i, i2, i3);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
            super.onConnectionStateChange(bluetoothGatt, i, i2);
            LogUtil.showLogD(BLEManager.TAG, "status:" + i);
            LogUtil.showLogD(BLEManager.TAG, "newState:" + i2);
            if (i == 0) {
                LogUtil.showLogW(BLEManager.TAG, "BluetoothGatt.GATT_SUCCESS");
            } else if (i == 2) {
                LogUtil.showLogW(BLEManager.TAG, "BluetoothGatt.GATT_READ_NOT_PERMITTED");
            } else if (i == 15) {
                LogUtil.showLogW(BLEManager.TAG, "BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION");
            } else if (i == 143) {
                LogUtil.showLogW(BLEManager.TAG, "BluetoothGatt.GATT_CONNECTION_CONGESTED");
            } else if (i == 257) {
                LogUtil.showLogW(BLEManager.TAG, "BluetoothGatt.GATT_FAILURE");
            } else if (i == 5) {
                LogUtil.showLogW(BLEManager.TAG, "BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION");
            } else if (i == 6) {
                LogUtil.showLogW(BLEManager.TAG, "BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED");
            } else if (i == 7) {
                LogUtil.showLogW(BLEManager.TAG, "BluetoothGatt.GATT_INVALID_OFFSET");
            }
            BluetoothDevice device = bluetoothGatt.getDevice();
            LogUtil.showLogD(BLEManager.TAG, "连接的设备：" + device.getName() + "  " + device.getAddress());
            BLEManager.this.isConnectIng = false;
            BLEManager.this.mHandler.removeCallbacks(BLEManager.this.connectOutTimeRunnable);
            if (i2 == 2) {
                LogUtil.showLogW(BLEManager.TAG, "连接成功");
                bluetoothGatt.discoverServices();
                BLEManager.this.mHandler.postDelayed(BLEManager.this.serviceDiscoverOutTimeRunnable, 10000L);
                if (!BLEManager.this.bluetoothGattSet.contains(bluetoothGatt)) {
                    if (BLEManager.this.onBleConnectListener != null) {
                        BLEManager.this.onBleConnectListener.onConnectSuccess(bluetoothGatt, device, i);
                        return;
                    }
                    return;
                }
                LogUtil.showLogE(BLEManager.TAG, "bluetoothGattSet.contains(gatt)!");
                return;
            }
            if (i2 != 0) {
                if (i2 == 1) {
                    LogUtil.showLogD(BLEManager.TAG, "正在连接...");
                    if (BLEManager.this.onBleConnectListener != null) {
                        BLEManager.this.onBleConnectListener.onConnecting(bluetoothGatt, device);
                        return;
                    }
                    return;
                }
                if (i2 == 3) {
                    LogUtil.showLogD(BLEManager.TAG, "正在断开...");
                    if (BLEManager.this.onBleConnectListener != null) {
                        BLEManager.this.onBleConnectListener.onDisConnecting(bluetoothGatt, device);
                        return;
                    }
                    return;
                }
                return;
            }
            BLEManager.this.mHandler.removeCallbacks(BLEManager.this.codeDisconnectFailure);
            ClsUtils.refreshDeviceCache(bluetoothGatt);
            LogUtil.showLogE(BLEManager.TAG, "断开连接status:" + i);
            bluetoothGatt.close();
            BLEManager.this.bluetoothGattSet.remove(bluetoothGatt);
            if (i == 133) {
                if (BLEManager.this.onBleConnectListener != null) {
                    bluetoothGatt.close();
                    BLEManager.this.onBleConnectListener.onConnectFailure(bluetoothGatt, device, "连接异常！", i);
                    LogUtil.showLogE(BLEManager.TAG, "连接失败status：" + i + "  " + device.getAddress());
                    return;
                }
                return;
            }
            if (i == 62) {
                if (BLEManager.this.onBleConnectListener != null) {
                    bluetoothGatt.close();
                    BLEManager.this.onBleConnectListener.onConnectFailure(bluetoothGatt, device, "连接成功服务未发现断开！", i);
                    LogUtil.showLogE(BLEManager.TAG, "连接成功服务未发现断开status:" + i);
                    return;
                }
                return;
            }
            if (i == 0) {
                if (BLEManager.this.onBleConnectListener != null) {
                    BLEManager.this.onBleConnectListener.onDisConnectSuccess(bluetoothGatt, device, i);
                }
            } else if (i == 8) {
                if (BLEManager.this.onBleConnectListener != null) {
                    BLEManager.this.onBleConnectListener.onDisConnectSuccess(bluetoothGatt, device, i);
                }
            } else if (i == 34) {
                if (BLEManager.this.onBleConnectListener != null) {
                    BLEManager.this.onBleConnectListener.onDisConnectSuccess(bluetoothGatt, device, i);
                }
            } else if (BLEManager.this.onBleConnectListener != null) {
                BLEManager.this.onBleConnectListener.onDisConnectSuccess(bluetoothGatt, device, i);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
            super.onServicesDiscovered(bluetoothGatt, i);
            BLEManager.this.mHandler.removeCallbacks(BLEManager.this.serviceDiscoverOutTimeRunnable);
            LogUtil.showLogD(BLEManager.TAG, "移除发现服务超时");
            LogUtil.showLogD(BLEManager.TAG, "发现服务");
            if (BLEManager.this.onBleConnectListener != null) {
                BLEManager.this.onBleConnectListener.onServiceDiscovery(bluetoothGatt, bluetoothGatt.getDevice(), i);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            super.onCharacteristicRead(bluetoothGatt, bluetoothGattCharacteristic, i);
            LogUtil.showLogD(BLEManager.TAG, "读status: " + i);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
            if (bluetoothGattCharacteristic.getValue() == null) {
                LogUtil.showLogE(BLEManager.TAG, "characteristic.getValue() == null");
                return;
            }
            String strBytes2HexString = TypeConversion.bytes2HexString(bluetoothGattCharacteristic.getValue(), bluetoothGattCharacteristic.getValue().length);
            if (i == 0) {
                LogUtil.showLogW(BLEManager.TAG, "写入成功：" + strBytes2HexString);
                if (BLEManager.this.onBleConnectListener != null) {
                    BLEManager.this.onBleConnectListener.onWriteSuccess(bluetoothGatt, bluetoothGatt.getDevice(), bluetoothGattCharacteristic.getValue());
                    return;
                }
                return;
            }
            if (i != 257) {
                if (i == 3) {
                    LogUtil.showLogE(BLEManager.TAG, "没有权限！");
                    return;
                }
                return;
            }
            LogUtil.showLogE(BLEManager.TAG, "写入失败：" + strBytes2HexString);
            if (BLEManager.this.onBleConnectListener != null) {
                BLEManager.this.onBleConnectListener.onWriteFailure(bluetoothGatt, bluetoothGatt.getDevice(), bluetoothGattCharacteristic.getValue(), "写入失败");
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            super.onCharacteristicChanged(bluetoothGatt, bluetoothGattCharacteristic);
            byte[] value = bluetoothGattCharacteristic.getValue();
            LogUtil.showLogW("TAG", "收到数据str:" + TypeConversion.bytes2HexString(value, value.length));
            if (BLEManager.this.onBleConnectListener != null) {
                BLEManager.this.onBleConnectListener.onReceiveMessage(bluetoothGatt, bluetoothGatt.getDevice(), bluetoothGattCharacteristic, bluetoothGattCharacteristic.getValue());
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            super.onDescriptorRead(bluetoothGatt, bluetoothGattDescriptor, i);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            super.onDescriptorWrite(bluetoothGatt, bluetoothGattDescriptor, i);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onReliableWriteCompleted(BluetoothGatt bluetoothGatt, int i) {
            super.onReliableWriteCompleted(bluetoothGatt, i);
            LogUtil.showLogD(BLEManager.TAG, "onReliableWriteCompleted");
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, int i, int i2) {
            super.onReadRemoteRssi(bluetoothGatt, i, i2);
            if (i2 != 0) {
                if (i2 == 257) {
                    LogUtil.showLogW(BLEManager.TAG, "读取RSSI值失败，status：" + i2);
                    return;
                }
                return;
            }
            LogUtil.showLogW(BLEManager.TAG, "读取RSSI值成功，RSSI值：" + i + ",status" + i2);
            if (BLEManager.this.onBleConnectListener != null) {
                BLEManager.this.onBleConnectListener.onReadRssi(bluetoothGatt, i, i2);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onMtuChanged(BluetoothGatt bluetoothGatt, int i, int i2) {
            super.onMtuChanged(bluetoothGatt, i, i2);
            if (i2 == 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("设置MTU成功，新的MTU值：");
                int i3 = i - 3;
                sb.append(i3);
                sb.append(",status");
                sb.append(i2);
                LogUtil.showLogW(BLEManager.TAG, sb.toString());
                if (BLEManager.this.onBleConnectListener != null) {
                    BLEManager.this.onBleConnectListener.onMTUSetSuccess("设置后新的MTU值 = " + i3 + "   status = " + i2, i3);
                    return;
                }
                return;
            }
            if (i2 == 257) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("设置MTU值失败：");
                int i4 = i - 3;
                sb2.append(i4);
                sb2.append(",status");
                sb2.append(i2);
                LogUtil.showLogE(BLEManager.TAG, sb2.toString());
                if (BLEManager.this.onBleConnectListener != null) {
                    BLEManager.this.onBleConnectListener.onMTUSetFailure("设置MTU值失败：" + i4 + "   status：" + i2);
                }
            }
        }
    };
    private Runnable connectOutTimeRunnable = new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.6
        @Override // java.lang.Runnable
        public void run() {
            if (BLEManager.this.mBluetoothGatt != null) {
                BLEManager.this.isConnectIng = false;
                BLEManager.this.mBluetoothGatt.disconnect();
                if (BLEManager.this.onBleConnectListener != null) {
                    BLEManager.this.onBleConnectListener.onConnectFailure(BLEManager.this.mBluetoothGatt, BLEManager.this.curConnDevice, "连接超时！", -1);
                    return;
                }
                return;
            }
            LogUtil.showLogE(BLEManager.TAG, "connectOutTimeRunnable-->mBluetoothGatt == null");
        }
    };
    private Runnable serviceDiscoverOutTimeRunnable = new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.7
        @Override // java.lang.Runnable
        public void run() {
            if (BLEManager.this.mBluetoothGatt != null) {
                BLEManager.this.isConnectIng = false;
                BLEManager.this.mBluetoothGatt.disconnect();
                if (BLEManager.this.onBleConnectListener != null) {
                    BLEManager.this.onBleConnectListener.onConnectFailure(BLEManager.this.mBluetoothGatt, BLEManager.this.curConnDevice, "发现服务超时！", -1);
                    return;
                }
                return;
            }
            LogUtil.showLogE(BLEManager.TAG, "serviceDiscoverOutTimeRunnable-->mBluetoothGatt == null");
        }
    };
    private Runnable codeDisconnectFailure = new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.8
        @Override // java.lang.Runnable
        public void run() {
            if (BLEManager.this.mBluetoothGatt != null) {
                BLEManager.this.mBluetoothGatt.disconnect();
            }
        }
    };
    private boolean isEnableNotifiIsBusy = false;
    private Runnable advertiseStopRunnable = new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.11
        @Override // java.lang.Runnable
        public void run() {
            String str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            LogUtil.showLogW(BLEManager.TAG, "广播已结束：" + str);
            if (BLEManager.this.advertiseStateListener != null) {
                BLEManager.this.advertiseStateListener.onStopAdvertise(str);
            }
        }
    };

    private static class BLEManagerHolder {
        private static BLEManager bleManager = new BLEManager();

        private BLEManagerHolder() {
        }
    }

    public static BLEManager getInstance() {
        return BLEManagerHolder.bleManager;
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void setOnBluetoothStateChangeListener(OnBluetoothStateChangeListener onBluetoothStateChangeListener) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver == null) {
            LogUtil.showLogE(TAG, "setOnBluetoothStateChangeListener-->bluetoothBroadcastReceiver == null");
        } else {
            bluetoothBroadcastReceiver.setOnBluetoothStateChangeListener(onBluetoothStateChangeListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void setOnBindStateChangeListener(OnBindStateChangeListener onBindStateChangeListener) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver == null) {
            LogUtil.showLogE(TAG, "setOnBindStateChangeListener-->bluetoothBroadcastReceiver == null");
        } else {
            bluetoothBroadcastReceiver.setOnBindStateChangeListener(onBindStateChangeListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void setOnBtWithDeviceConStateListener(OnBtWithDeviceConStateListener onBtWithDeviceConStateListener) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver == null) {
            LogUtil.showLogE(TAG, "setOnBtWithDeviceConStateListener-->bluetoothBroadcastReceiver == null");
        } else {
            bluetoothBroadcastReceiver.setOnBtWithDeviceConStateListener(onBtWithDeviceConStateListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void setOnRemoteDeviceConStateListener(OnRemoteDeviceConStateListener onRemoteDeviceConStateListener) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver == null) {
            LogUtil.showLogE(TAG, "setOnRemoteDeviceConStateListener-->bluetoothBroadcastReceiver == null");
        } else {
            bluetoothBroadcastReceiver.setOnRemoteDeviceConStateListener(onRemoteDeviceConStateListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void initBluetooth(Context context) {
        this.mContext = context;
        SystemBleCheck systemBleCheck = SystemBleCheck.getInstance();
        this.systemBleCheck = systemBleCheck;
        systemBleCheck.initBle(this.mContext);
        this.bluetoothManager = this.systemBleCheck.bluetoothManager;
        this.bluetooth4Adapter = this.systemBleCheck.bluetooth4Adapter;
        if (this.systemBleCheck.checkBleAdvertiser() == null) {
            LogUtil.showLogE(TAG, "当前设备不支持BLE蓝牙外围模式--作为从设备通讯");
        } else {
            BluetoothLeAdvertiser bluetoothLeAdvertiserCheckBleAdvertiser = this.systemBleCheck.checkBleAdvertiser();
            this.bluetoothLeAdvertiser = bluetoothLeAdvertiserCheckBleAdvertiser;
            if (bluetoothLeAdvertiserCheckBleAdvertiser != null) {
                defineAdvertiseCallback();
            }
        }
        if (this.bluetoothBroadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
            intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
            intentFilter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
            BluetoothBroadcastReceiver bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
            this.bluetoothBroadcastReceiver = bluetoothBroadcastReceiver;
            this.mContext.registerReceiver(bluetoothBroadcastReceiver, intentFilter);
        }
        this.systemBleCheck.openBluetooth(this.mContext, false);
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void openBluetooth(Context context, boolean z) {
        SystemBleCheck systemBleCheck = this.systemBleCheck;
        if (systemBleCheck == null) {
            LogUtil.showLogE(TAG, "openBluetooth-->systemBleCheck == null");
        } else {
            systemBleCheck.openBluetooth(context, z);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void closeBluetooth() {
        SystemBleCheck systemBleCheck = this.systemBleCheck;
        if (systemBleCheck == null) {
            LogUtil.showLogE(TAG, "closeBluetooth-->systemBleCheck == null");
        } else {
            systemBleCheck.closeBluetooth();
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void unRegisterBluetoothReceiver(Context context) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver != null) {
            context.unregisterReceiver(bluetoothBroadcastReceiver);
            this.bluetoothBroadcastReceiver = null;
        }
    }

    private void initStartDiscovery(OnDeviceSearchListener onDeviceSearchListener) {
        if (this.bluetooth4Adapter == null) {
            LogUtil.showLogE(TAG, "initStartDiscovery()-->bluetooth4Adapter == null");
            return;
        }
        this.onDeviceSearchListener = onDeviceSearchListener;
        if (onDeviceSearchListener != null) {
            onDeviceSearchListener.onDiscoveryStart();
        }
        if (Build.VERSION.SDK_INT >= 18) {
            LogUtil.showLogD(TAG, "开始扫描设备");
            this.bluetooth4Adapter.startLeScan(this.leScanCallback);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener) {
        initStartDiscovery(onDeviceSearchListener);
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener, long j) {
        initStartDiscovery(onDeviceSearchListener);
        this.mHandler.postDelayed(this.stopScanRunnable, j);
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void startDiscoveryDevice(UUID[] uuidArr, OnDeviceSearchListener onDeviceSearchListener, long j) {
        if (this.bluetooth4Adapter == null) {
            LogUtil.showLogE(TAG, "startDiscoveryDevice-->bluetooth4Adapter == null");
            return;
        }
        this.onDeviceSearchListener = onDeviceSearchListener;
        if (onDeviceSearchListener != null) {
            onDeviceSearchListener.onDiscoveryStart();
        }
        if (Build.VERSION.SDK_INT >= 18) {
            LogUtil.showLogD(TAG, "开始扫描设备");
            if (Build.VERSION.SDK_INT < 21) {
                Log.d(TAG, "搜索所有device....");
                this.bluetooth4Adapter.startLeScan(uuidArr, this.leScanCallback);
            } else {
                Log.d(TAG, "根据UUID搜索device....");
                BluetoothLeScanner bluetoothLeScanner = this.bluetooth4Adapter.getBluetoothLeScanner();
                ArrayList arrayList = new ArrayList();
                ScanFilter.Builder builder = new ScanFilter.Builder();
                builder.setServiceUuid(ParcelUuid.fromString(uuidArr[0].toString()));
                arrayList.add(builder.build());
                bluetoothLeScanner.startScan(arrayList, new ScanSettings.Builder().build(), this.scanCallback);
            }
        }
        this.mHandler.postDelayed(this.stopScanRunnable, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelDiscoveryDevice() {
        if (this.bluetooth4Adapter == null) {
            LogUtil.showLogE(TAG, "cancelDiscoveryDevice-->bluetooth4Adapter == null");
            return;
        }
        if (this.leScanCallback == null) {
            LogUtil.showLogE(TAG, "cancelDiscoveryDevice-->leScanCallback == null");
            return;
        }
        LogUtil.showLogD(TAG, "停止扫描设备");
        this.bluetooth4Adapter.stopLeScan(this.leScanCallback);
        OnDeviceSearchListener onDeviceSearchListener = this.onDeviceSearchListener;
        if (onDeviceSearchListener != null) {
            onDeviceSearchListener.onDiscoveryStop();
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void stopDiscoveryDevice() {
        this.mHandler.removeCallbacks(this.stopScanRunnable);
        cancelDiscoveryDevice();
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean boundDevice(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "boundDevice-->bluetoothDevice == null");
            return false;
        }
        try {
            return ClsUtils.createBond(BluetoothDevice.class, bluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean boundDeviceAPI(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "boundDeviceAPI-->bluetoothDevice == null");
            return false;
        }
        return bluetoothDevice.createBond();
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean disBoundDevice(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "disBoundDevice-->bluetoothDevice == null");
            return false;
        }
        try {
            return ClsUtils.removeBond(BluetoothDevice.class, bluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void addBLEConnectDevice(Context context, BluetoothDevice bluetoothDevice, long j, OnBleConnectListener onBleConnectListener) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "addBLEConnectDevice-->bluetoothDevice == null");
            return;
        }
        BluetoothAdapter bluetoothAdapter = this.bluetooth4Adapter;
        if (bluetoothAdapter == null) {
            LogUtil.showLogE(TAG, "addBLEConnectDevice-->bluetooth4Adapter == null");
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            this.bluetooth4Adapter.cancelDiscovery();
        }
        this.onBleConnectListener = onBleConnectListener;
        this.curConnDevice = bluetoothDevice;
        LogUtil.showLogD(TAG, "开始准备连接：" + bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
        try {
            this.mBluetoothGatt = bluetoothDevice.connectGatt(context, false, this.bluetoothGattCallback);
        } catch (Exception e) {
            LogUtil.showLogE(TAG, "e:" + e.getMessage());
        }
        this.mHandler.postDelayed(this.connectOutTimeRunnable, j);
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void disConnectDevice(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "disConnectDevice-->bluetoothGatt == null");
        } else {
            if (bluetoothGattCharacteristic == null) {
                bluetoothGatt.disconnect();
                return;
            }
            bluetoothGattCharacteristic.setValue(TypeConversion.hexString2Bytes("AA55FF"));
            bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
            this.mHandler.postDelayed(this.codeDisconnectFailure, 2000L);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void removeConnectDevice(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "removeConnectDevice-->bluetoothDevice == null");
            return;
        }
        for (BluetoothGatt bluetoothGatt : this.bluetoothGattSet) {
            if (bluetoothGatt.getDevice() == bluetoothDevice) {
                bluetoothGatt.disconnect();
                this.bluetoothGattSet.remove(bluetoothGatt);
            }
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void enableNotification(boolean z, BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "enableNotification-->gatt == null");
        } else if (bluetoothGattCharacteristic == null) {
            LogUtil.showLogE(TAG, "enableNotification-->characteristic == null");
        } else {
            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, z);
        }
    }

    private class BluetoothIn {
        public BluetoothGatt bluetoothGatt;
        public BluetoothGattCharacteristic characteristic;

        private BluetoothIn() {
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void toEnableAllNotification(BluetoothGatt bluetoothGatt) {
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "toEnableAllNotifi-->bluetoothGatt == null");
            return;
        }
        if (this.isEnableNotifiIsBusy) {
            LogUtil.showLogE(TAG, "toEnableAllNotifi-->isEnableNotifiIsBusy = true");
            return;
        }
        this.isEnableNotifiIsBusy = true;
        final ArrayList arrayList = new ArrayList();
        Iterator<BluetoothGattService> it = bluetoothGatt.getServices().iterator();
        while (it.hasNext()) {
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : it.next().getCharacteristics()) {
                int properties = bluetoothGattCharacteristic.getProperties();
                if ((properties & 16) == 16) {
                    LogUtil.showLogD(TAG, "NOTIFY:" + properties + " UUID:" + bluetoothGattCharacteristic.getUuid().toString());
                    BluetoothIn bluetoothIn = new BluetoothIn();
                    bluetoothIn.bluetoothGatt = bluetoothGatt;
                    bluetoothIn.characteristic = bluetoothGattCharacteristic;
                    arrayList.add(bluetoothIn);
                }
                if ((properties & 32) == 32) {
                    LogUtil.showLogD(TAG, "INDICATE:" + properties + " UUID:" + bluetoothGattCharacteristic.getUuid().toString());
                    BluetoothIn bluetoothIn2 = new BluetoothIn();
                    bluetoothIn2.bluetoothGatt = bluetoothGatt;
                    bluetoothIn2.characteristic = bluetoothGattCharacteristic;
                    arrayList.add(bluetoothIn2);
                }
            }
        }
        new Thread(new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.9
            @Override // java.lang.Runnable
            public void run() {
                for (BluetoothIn bluetoothIn3 : arrayList) {
                    LogUtil.showLogD(BLEManager.TAG, "正在打开：" + bluetoothIn3.characteristic.getUuid().toString());
                    bluetoothIn3.bluetoothGatt.setCharacteristicNotification(bluetoothIn3.characteristic, true);
                    for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothIn3.characteristic.getDescriptors()) {
                        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        bluetoothIn3.bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
                        try {
                            Thread.currentThread();
                            Thread.sleep(200L);
                        } catch (InterruptedException e) {
                            BLEManager.this.isEnableNotifiIsBusy = false;
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.currentThread();
                        Thread.sleep(200L);
                    } catch (InterruptedException e2) {
                        BLEManager.this.isEnableNotifiIsBusy = false;
                        e2.printStackTrace();
                    }
                }
                BLEManager.this.isEnableNotifiIsBusy = false;
            }
        }).start();
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean sendMessage(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr) {
        if (bluetoothGattCharacteristic == null) {
            LogUtil.showLogE(TAG, "sendMessage(byte[])-->writeGattCharacteristic == null");
            return false;
        }
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "sendMessage(byte[])-->bluetoothGatt == null");
            return false;
        }
        Log.d(TAG, "写特征设置值结果：" + bluetoothGattCharacteristic.setValue(bArr));
        return bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean sendMessage(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, String str, boolean z) {
        byte[] bytes;
        if (str == null || str.length() == 0) {
            LogUtil.showLogE(TAG, "sendMessage(string)-->msg == null");
            return false;
        }
        if (bluetoothGattCharacteristic == null) {
            LogUtil.showLogE(TAG, "sendMessage(string)-->writeGattCharacteristic == null");
            return false;
        }
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "sendMessage(string)-->bluetoothGatt == null");
            return false;
        }
        if (z) {
            LogUtil.showLogD(TAG, "System.currentTimeMillis():" + System.currentTimeMillis());
            bytes = TypeConversion.hexString2Bytes(str);
            LogUtil.showLogD(TAG, "System.currentTimeMillis():" + System.currentTimeMillis());
        } else {
            bytes = str.getBytes();
        }
        LogUtil.showLogD(TAG, "将要发送数据长度：" + str.length());
        bluetoothGattCharacteristic.setValue(bytes);
        return bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean setMtuValue(int i) {
        LogUtil.showLogD(TAG, "正在设置mtu:" + i);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "setMtuValue-->mBluetoothGatt == null");
            return false;
        }
        int i2 = i + 3;
        if (i2 >= 23) {
            return bluetoothGatt.requestMtu(i2);
        }
        return false;
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean getRemoteRSSI() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "getRemoteRSSI-->bluetoothGatt == null");
            return false;
        }
        return bluetoothGatt.readRemoteRssi();
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public BluetoothGattService getBluetoothGattService(BluetoothGatt bluetoothGatt, String str) {
        if (str == null) {
            LogUtil.showLogE(TAG, "getBluetoothGattService-->serviceUUID == null");
            return null;
        }
        if (bluetoothGatt == null) {
            LogUtil.showLogE(TAG, "getBluetoothGattService-->gatt == null");
            return null;
        }
        return bluetoothGatt.getService(UUID.fromString(str));
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public BluetoothAdapter getBluetoothAdapter() {
        BluetoothAdapter bluetoothAdapter = this.bluetooth4Adapter;
        if (bluetoothAdapter != null) {
            return bluetoothAdapter;
        }
        return null;
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean getBluetoothState() {
        BluetoothAdapter bluetoothAdapter = this.bluetooth4Adapter;
        if (bluetoothAdapter == null) {
            LogUtil.showLogE(TAG, "getBluetoothState-->bluetooth4Adapter == null");
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public BluetoothDevice getBluetoothDeviceByAddress(String str) {
        if (str == null || str.length() == 0) {
            LogUtil.showLogE(TAG, "getBluetoothDeviceByAddress-->macAddress == null");
            return null;
        }
        BluetoothAdapter bluetoothAdapter = this.bluetooth4Adapter;
        if (bluetoothAdapter == null) {
            LogUtil.showLogE(TAG, "getBluetoothDeviceByAddress-->bluetooth4Adapter == null");
            return null;
        }
        return bluetoothAdapter.getRemoteDevice(str);
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public List<BluetoothDevice> getBoundDeviceList() {
        if (this.bluetooth4Adapter == null) {
            LogUtil.showLogE(TAG, "getBoundDeviceLists-->bluetooth4Adapter == null");
            return null;
        }
        return new ArrayList(this.bluetooth4Adapter.getBondedDevices());
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean getDeviceBoundState(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "getDeviceBoundState-->bluetoothDevice == null");
            return false;
        }
        Iterator<BluetoothDevice> it = getBoundDeviceList().iterator();
        while (it.hasNext()) {
            if (it.next().getAddress().equals(bluetoothDevice.getAddress())) {
                return true;
            }
        }
        return false;
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public boolean getDeviceConnectState(BluetoothDevice bluetoothDevice) {
        BluetoothManager bluetoothManager = this.bluetoothManager;
        if (bluetoothManager == null) {
            LogUtil.showLogE(TAG, "getDeviceConnectState(device)-->bluetoothManager == null");
            return false;
        }
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "getDeviceConnectState(device)-->bluetoothDevice == null");
            return false;
        }
        int connectionState = bluetoothManager.getConnectionState(bluetoothDevice, 7);
        if (connectionState == 2) {
            return true;
        }
        if (connectionState == 0) {
        }
        return false;
    }

    public AdvertiseSettings createAdvSettings(AdSetting adSetting) {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        this.advertiseTimeout = adSetting.getTimeout();
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(adSetting.getAdvertiseMode());
        builder.setConnectable(adSetting.isConnectable());
        builder.setTimeout(adSetting.getTimeout());
        builder.setTxPowerLevel(adSetting.getTxPowerLevel());
        return builder.build();
    }

    public AdvertiseData createAdvertiseData(AdDataModel adDataModel) {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        if (adDataModel.getAdDataList() != null && adDataModel.getAdDataList().size() > 0) {
            for (AdDataModel.AdData adData : adDataModel.getAdDataList()) {
                if (adData.getType() == 0) {
                    builder.addManufacturerData(Integer.parseInt(adData.getId(), 16), TypeConversion.hexString2Bytes(adData.getData()));
                } else if (adData.getType() == 1) {
                    builder.addServiceData(ParcelUuid.fromString(adData.getId()), TypeConversion.hexString2Bytes(adData.getData()));
                }
            }
        }
        builder.setIncludeDeviceName(adDataModel.isIncludeDeviceName());
        builder.setIncludeTxPowerLevel(adDataModel.isIncludeTxPowerLevel());
        return builder.build();
    }

    private void defineAdvertiseCallback() {
        if (Build.VERSION.SDK_INT >= 21) {
            this.advertiseCallback = new AdvertiseCallback() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.10
                @Override // android.bluetooth.le.AdvertiseCallback
                public void onStartSuccess(AdvertiseSettings advertiseSettings) {
                    super.onStartSuccess(advertiseSettings);
                    if (advertiseSettings != null) {
                        String str = "ble广播打开成功！\n 广播模式 = " + advertiseSettings.getMode() + "\n,信号强度 = " + advertiseSettings.getTxPowerLevel() + "\n,广播可否连接 = " + advertiseSettings.isConnectable() + "\n,广播时间 = " + advertiseSettings.getTimeout() + "ms";
                        LogUtil.showLogW(BLEManager.TAG, str);
                        if (BLEManager.this.advertiseStateListener != null) {
                            BLEManager.this.advertiseStateListener.onStartSuccess(str);
                        }
                    } else {
                        LogUtil.showLogE(BLEManager.TAG, "onStartSuccess()-->settingsInEffect == null");
                        if (BLEManager.this.advertiseStateListener != null) {
                            BLEManager.this.advertiseStateListener.onStartSuccess("ble广播打开成功！但广播设置AdvertiseSettings == null");
                        }
                    }
                    String str2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    LogUtil.showLogW(BLEManager.TAG, "广播已开始：" + str2);
                    if (BLEManager.this.advertiseTimeout != 0) {
                        BLEManager.this.mHandler.postDelayed(BLEManager.this.advertiseStopRunnable, BLEManager.this.advertiseTimeout);
                    }
                    if (BLEManager.this.advertiseStateListener != null) {
                        BLEManager.this.advertiseStateListener.onStartAdvertise(str2);
                    }
                }

                @Override // android.bluetooth.le.AdvertiseCallback
                public void onStartFailure(int i) {
                    super.onStartFailure(i);
                    String str = "ble广播打开失败！errorCode = " + i;
                    LogUtil.showLogE(BLEManager.TAG, str);
                    if (i == 4) {
                        LogUtil.showLogE(BLEManager.TAG, "由于内部错误操作失败");
                        str = str + "由于内部错误操作失败";
                    } else if (i == 3) {
                        LogUtil.showLogE(BLEManager.TAG, "正在连接的，无法再次连接");
                        str = str + "正在连接的，无法再次连接";
                    } else if (i == 1) {
                        LogUtil.showLogE(BLEManager.TAG, "广播开启错误，数据大于31字节");
                        str = str + "广播开启错误，数据大于31字节";
                    } else if (i == 5) {
                        LogUtil.showLogE(BLEManager.TAG, "在这个平台上不支持此功能");
                        str = str + "在这个平台上不支持此功能";
                    } else if (i == 2) {
                        LogUtil.showLogE(BLEManager.TAG, "广播广播泄露");
                        str = str + "广播广播泄露";
                    }
                    if (BLEManager.this.advertiseStateListener != null) {
                        BLEManager.this.advertiseStateListener.onStartFailure(str);
                    }
                }
            };
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void startAdvertising(AdSetting adSetting, AdDataModel adDataModel, AdvertiseStateListener advertiseStateListener) {
        if (this.bluetoothLeAdvertiser == null) {
            LogUtil.showLogE(TAG, "startAdvertising()-->bluetoothLeAdvertiser == null");
            return;
        }
        if (!getBluetoothState()) {
            LogUtil.showLogE(TAG, "startAdvertising()-->当前蓝牙状态未开启");
            if (advertiseStateListener != null) {
                advertiseStateListener.onStartFailure("当前蓝牙已关闭!");
                return;
            }
            return;
        }
        if (adSetting == null) {
            LogUtil.showLogE(TAG, "startAdvertising()-->adSetting == null");
            return;
        }
        if (adDataModel == null) {
            LogUtil.showLogE(TAG, "startAdvertising()-->adData == null");
            return;
        }
        AdvertiseSettings advertiseSettingsCreateAdvSettings = createAdvSettings(adSetting);
        AdvertiseData advertiseDataCreateAdvertiseData = createAdvertiseData(adDataModel);
        if (advertiseSettingsCreateAdvSettings == null) {
            LogUtil.showLogE(TAG, "startAdvertising()-->advertiseSettings == null");
            return;
        }
        if (advertiseDataCreateAdvertiseData == null) {
            LogUtil.showLogE(TAG, "startAdvertising()-->advertiseData == null");
            return;
        }
        if (advertiseStateListener != null) {
            this.advertiseStateListener = advertiseStateListener;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.bluetoothLeAdvertiser.startAdvertising(advertiseSettingsCreateAdvSettings, advertiseDataCreateAdvertiseData, this.advertiseCallback);
        } else {
            Toast.makeText(this.mContext, "系统不支持ble广播数据！", 0).show();
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void stopAdvertising() {
        if (this.bluetoothLeAdvertiser == null) {
            LogUtil.showLogE(TAG, "stopAdvertising()-->bluetoothLeAdvertiser == null");
        } else if (Build.VERSION.SDK_INT >= 21) {
            this.bluetoothLeAdvertiser.stopAdvertising(this.advertiseCallback);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth4.IBLEManager
    public void startBLEServer(String str, String str2, String str3) {
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        UUID uuidFromString3 = UUID.fromString(str3);
        this.bluetoothGattService = new BluetoothGattService(uuidFromString, 0);
        BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuidFromString2, 18, 1);
        this.characteristicRead = bluetoothGattCharacteristic;
        this.bluetoothGattService.addCharacteristic(bluetoothGattCharacteristic);
        BluetoothGattCharacteristic bluetoothGattCharacteristic2 = new BluetoothGattCharacteristic(uuidFromString3, 8, 16);
        this.characteristicWrite = bluetoothGattCharacteristic2;
        this.bluetoothGattService.addCharacteristic(bluetoothGattCharacteristic2);
        BluetoothGattServer bluetoothGattServer = this.bluetoothGattServer;
        if (bluetoothGattServer != null) {
            bluetoothGattServer.addService(this.bluetoothGattService);
        }
    }

    private void defineBluetoothGattServerCallback() {
        if (Build.VERSION.SDK_INT >= 18) {
            this.bluetoothGattServerCallback = new BluetoothGattServerCallback() { // from class: com.example.bluetoothlibrary.bluetooth4.BLEManager.12
                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onConnectionStateChange(BluetoothDevice bluetoothDevice, int i, int i2) {
                    super.onConnectionStateChange(bluetoothDevice, i, i2);
                    if (i2 == 2) {
                        Log.w(BLEManager.TAG, String.format("BLE服务器端--已连接：device name = %s, address = %s", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                        Log.w(BLEManager.TAG, String.format("BLE服务器端--已连接：status = %s, newState =%s ", Integer.valueOf(i), Integer.valueOf(i2)));
                    } else if (i2 == 0) {
                        Log.w(BLEManager.TAG, String.format("BLE服务器端--已断开：device name = %s, address = %s", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                        Log.w(BLEManager.TAG, String.format("BLE服务器端--已断开：status = %s, newState =%s ", Integer.valueOf(i), Integer.valueOf(i2)));
                    }
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onServiceAdded(int i, BluetoothGattService bluetoothGattService) {
                    super.onServiceAdded(i, bluetoothGattService);
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--已添加服务：status = %s", Integer.valueOf(i)));
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onCharacteristicReadRequest(BluetoothDevice bluetoothDevice, int i, int i2, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                    super.onCharacteristicReadRequest(bluetoothDevice, i, i2, bluetoothGattCharacteristic);
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--发送数据：device name = %s, address = %s", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--发送数据：requestId = %s, offset = %s", Integer.valueOf(i), Integer.valueOf(i2)));
                    BLEManager.this.bluetoothGattServer.sendResponse(bluetoothDevice, i, 0, i2, bluetoothGattCharacteristic.getValue());
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onCharacteristicWriteRequest(BluetoothDevice bluetoothDevice, int i, BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean z, boolean z2, int i2, byte[] bArr) {
                    super.onCharacteristicWriteRequest(bluetoothDevice, i, bluetoothGattCharacteristic, z, z2, i2, bArr);
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--接收数据：device name = %s, address = %s", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--接收数据：requestId = %s, preparedWrite=%s, responseNeeded=%s, offset=%s, value=%s", Integer.valueOf(i), Boolean.valueOf(z), Boolean.valueOf(z2), Integer.valueOf(i2), bArr.toString()));
                    BLEManager.this.bluetoothGattServer.sendResponse(bluetoothDevice, i, 0, i2, bArr);
                    BLEManager.this.onResponseToClient(bArr, bluetoothDevice, i, bluetoothGattCharacteristic);
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onDescriptorReadRequest(BluetoothDevice bluetoothDevice, int i, int i2, BluetoothGattDescriptor bluetoothGattDescriptor) {
                    super.onDescriptorReadRequest(bluetoothDevice, i, i2, bluetoothGattDescriptor);
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--发送描述符：device name = %s, address = %s", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--发送描述符：requestId = %s", Integer.valueOf(i)));
                    BLEManager.this.bluetoothGattServer.sendResponse(bluetoothDevice, i, 0, i2, null);
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onDescriptorWriteRequest(BluetoothDevice bluetoothDevice, int i, BluetoothGattDescriptor bluetoothGattDescriptor, boolean z, boolean z2, int i2, byte[] bArr) {
                    super.onDescriptorWriteRequest(bluetoothDevice, i, bluetoothGattDescriptor, z, z2, i2, bArr);
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--接收描述符：device name = %s, address = %s", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--接收描述符：requestId = %s, preparedWrite = %s, responseNeeded = %s, offset = %s, value = %s,", Integer.valueOf(i), Boolean.valueOf(z), Boolean.valueOf(z2), Integer.valueOf(i2), bArr.toString()));
                    BLEManager.this.bluetoothGattServer.sendResponse(bluetoothDevice, i, 0, i2, bArr);
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onExecuteWrite(BluetoothDevice bluetoothDevice, int i, boolean z) {
                    super.onExecuteWrite(bluetoothDevice, i, z);
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--执行写操作：device name = %s,requestId = %s", bluetoothDevice.getName(), Integer.valueOf(i)));
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onNotificationSent(BluetoothDevice bluetoothDevice, int i) {
                    super.onNotificationSent(bluetoothDevice, i);
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--发送通知或指示：device name = %s, address = %s", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--发送通知或指示：status = %s", Integer.valueOf(i)));
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onMtuChanged(BluetoothDevice bluetoothDevice, int i) {
                    super.onMtuChanged(bluetoothDevice, i);
                    Log.w(BLEManager.TAG, String.format("BLE服务器端--MTU已更改：device name = %s, mtu = %s", bluetoothDevice.getName(), Integer.valueOf(i)));
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onPhyUpdate(BluetoothDevice bluetoothDevice, int i, int i2, int i3) {
                    super.onPhyUpdate(bluetoothDevice, i, i2, i3);
                }

                @Override // android.bluetooth.BluetoothGattServerCallback
                public void onPhyRead(BluetoothDevice bluetoothDevice, int i, int i2, int i3) {
                    super.onPhyRead(bluetoothDevice, i, i2, i3);
                }
            };
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onResponseToClient(byte[] bArr, BluetoothDevice bluetoothDevice, int i, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        Log.w(TAG, String.format("BLE服务器端--接收数据回应客户端：device name = %s, address = %s", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
        Log.w(TAG, String.format("BLE服务器端--接收数据回应客户端：requestId = %s", Integer.valueOf(i)));
        Log.w(TAG, "4.收到：" + TypeConversion.bytes2HexString(bArr));
        String str = TypeConversion.bytes2HexString(bArr) + "FFFFFFFF";
        this.characteristicRead.setValue(TypeConversion.hexString2Bytes(str));
        this.bluetoothGattServer.notifyCharacteristicChanged(bluetoothDevice, this.characteristicRead, false);
        Log.i(TAG, "4.响应：" + str);
    }
}